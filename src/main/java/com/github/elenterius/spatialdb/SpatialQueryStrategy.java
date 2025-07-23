package com.github.elenterius.spatialdb;

import com.github.elenterius.spatialdb.geometry.Shape;
import org.h2.mvstore.rtree.MVRTreeMap;

public interface SpatialQueryStrategy {

	MVRTreeMap.RTreeCursor<Long> find(String levelKey, SpatialQuery query, SDBI spatialStorage);

	boolean test(SpatialQuery query, Shape shape);

	SpatialQueryStrategy INTERSECTION = new SpatialQueryStrategy() {
		@Override
		public MVRTreeMap.RTreeCursor<Long> find(String levelKey, SpatialQuery query, SDBI spatialStorage) {
			return spatialStorage.findIntersecting(levelKey, query);
		}

		@Override
		public boolean test(SpatialQuery query, Shape shape) {
			return shape.intersectsCuboid(query.minX(), query.minY(), query.minZ(), query.maxX(), query.maxY(), query.maxZ());
		}
	};

}
