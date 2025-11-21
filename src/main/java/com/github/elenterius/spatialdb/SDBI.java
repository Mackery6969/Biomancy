package com.github.elenterius.spatialdb;

import com.github.elenterius.spatialdb.geometry.Shape;
import com.github.elenterius.spatialdb.mvstore.SpatialDB;
import com.github.elenterius.spatialdb.type.ShapeDataType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.h2.mvstore.rtree.Spatial;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SDBI {

	public static final Marker LOG_MARKER = MarkerManager.getMarker("SDBI");

	private final SpatialDB spatialDB;

	private final Map<String, MVRTreeMap<Long>> levelTrees = new HashMap<>();
	private final Map<String, MVMap<Long, Shape>> levelShapes = new HashMap<>();

	protected SDBI(SpatialDB spatialDB) {
		this.spatialDB = spatialDB;
	}

	protected final SpatialDB getSpatialDB() {
		return spatialDB;
	}

	protected final MVRTreeMap<Long> getTree(String levelKey) {
		return levelTrees.computeIfAbsent(levelKey, this::createTreeForLevel);
	}

	protected final MVMap<Long, Shape> getShapes(String levelKey) {
		return levelShapes.computeIfAbsent(levelKey, this::createMapForLevel);
	}

	protected final MVRTreeMap<Long> createTreeForLevel(String levelKey) {
		MVRTreeMap.Builder<Long> builder = new MVRTreeMap.Builder<Long>().dimensions(3);
		MVRTreeMap<Long> tree = spatialDB.getStore().openMap(levelKey + "_rtree", builder);
		if (!tree.isQuadraticSplit()) {
			tree.setQuadraticSplit(true);
		}
		return tree;
	}

	protected final MVMap<Long, Shape> createMapForLevel(String levelKey) {
		MVMap.Builder<Long, Shape> builder = new MVMap.Builder<Long, Shape>().valueType(new ShapeDataType());
		return spatialDB.getStore().openMap(levelKey + "_shapes", builder);
	}

	protected final String getLevelKey(ServerLevel level) {
		return level.dimension().location().toString();
	}

	protected MVRTreeMap.RTreeCursor<Long> findIntersecting(String levelKey, Spatial boundingBox) {
		return getTree(levelKey).findIntersectingKeys(boundingBox);
	}

	protected MVRTreeMap.RTreeCursor<Long> findContained(String levelKey, Spatial boundingBox) {
		return getTree(levelKey).findContainedKeys(boundingBox);
	}

	protected Shape getOrCreateShape(String levelKey, long shapeId, Supplier<Shape> factory) {
		MVRTreeMap<Long> tree = getTree(levelKey);
		MVMap<Long, Shape> shapes = getShapes(levelKey);

		if (shapes.containsKey(shapeId)) {
			Shape shape = shapes.get(shapeId);
			SpatialDBManager.LOGGER.debug(LOG_MARKER, "Fetched existing shape with id '{}' of type '{}'", shapeId, shape);
			return shape;
		}

		Shape shape = factory.get();
		shapes.put(shapeId, shape);
		tree.add(new SpatialKey(shapeId, shape.getAABB()), shapeId);

		SpatialDBManager.LOGGER.debug(LOG_MARKER, "Created shape with id '{}' of type '{}'", shapeId, shape);

		return shape;
	}

	public Shape getOrCreateShape(ServerLevel level, BlockPos shapeId, Supplier<Shape> factory) {
		return getOrCreateShape(getLevelKey(level), shapeId.asLong(), factory);
	}

	protected void removeShape(String levelKey, long shapeId) {
		MVMap<Long, Shape> shapes = getShapes(levelKey);
		MVRTreeMap<Long> tree = getTree(levelKey);

		Shape shape = shapes.get(shapeId);
		if (shape == null) return;

		Long removed = tree.remove(new SpatialKey(shapeId, shape.getAABB()));
		shapes.remove(shapeId);

		SpatialDBManager.LOGGER.debug(LOG_MARKER, "Removed shape with id '{}' of type '{}'", removed, shape);
	}

	public void removeShape(ServerLevel level, BlockPos shapeId) {
		removeShape(level, shapeId.asLong());
	}

	public void removeShape(ServerLevel level, long shapeId) {
		removeShape(getLevelKey(level), shapeId);
	}

	public @Nullable Shape getClosestShape(ServerLevel level, BlockPos blockPos) {
		return getClosestShape(level, blockPos, shape -> true);
	}

	public @Nullable Shape getClosestShape(ServerLevel level, BlockPos blockPos, Predicate<Shape> predicate) {
		SpatialQuery query = SpatialQuery.of(blockPos);
		String levelKey = getLevelKey(level);

		final float x = Mth.lerp(0.5f, query.minX(), query.maxX());
		final float y = Mth.lerp(0.5f, query.minY(), query.maxY());
		final float z = Mth.lerp(0.5f, query.minZ(), query.maxZ());

		double minDistSqr = Double.MAX_VALUE;
		Shape closestShape = null;

		MVRTreeMap.RTreeCursor<Long> intersectingKeys = findIntersecting(levelKey, query);
		MVMap<Long, Shape> shapes = getShapes(levelKey);

		while (intersectingKeys.hasNext()) {
			long id = intersectingKeys.next().getId();
			Shape shape = shapes.get(id);
			if (shape != null && shape.contains(x, y, z) && predicate.test(shape)) {
				double distSqr = shape.distanceToSqr(x, y, z);
				if (distSqr < minDistSqr) {
					closestShape = shape;
					minDistSqr = distSqr;
				}
			}
		}

		return closestShape;
	}

	public @Nullable Shape getAnyShape(ServerLevel level, Entity entity, SpatialQueryStrategy strategy, Predicate<Shape> predicate) {
		return getAnyShape(level, strategy, SpatialQuery.of(entity), predicate);
	}

	public @Nullable Shape getAnyShape(ServerLevel level, SpatialQueryStrategy strategy, SpatialQuery query, Predicate<Shape> predicate) {
		String levelKey = getLevelKey(level);

		MVRTreeMap.RTreeCursor<Long> foundKeys = strategy.find(levelKey, query, this);
		MVMap<Long, Shape> shapes = getShapes(levelKey);

		while (foundKeys.hasNext()) {
			long id = foundKeys.next().getId();
			Shape shape = shapes.get(id);
			if (shape != null && strategy.test(query, shape) && predicate.test(shape)) {
				return shape;
			}
		}

		return null;
	}

}
