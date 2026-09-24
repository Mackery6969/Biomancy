package com.github.elenterius.spatialdb.type;

import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import com.github.elenterius.biomancy.world.MobSpawnFilterShape;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.github.elenterius.spatialdb.geometry.CuboidShape;
import com.github.elenterius.spatialdb.geometry.OctantEllipsoidShape;
import com.github.elenterius.spatialdb.geometry.Shape;
import com.github.elenterius.spatialdb.geometry.SphereShape;
import net.minecraft.nbt.CompoundTag;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ShapeSerializers {
	private static final String SERIALIZER_KEY = "Serializer";
	private static final Map<String, NBTSerializer<Shape>> SERIALIZERS = new HashMap<>();
	public static final NBTSerializer<Shape> CUBOID_SERIALIZER = register("cuboid", CuboidShape.Serializer::new);
	public static final NBTSerializer<Shape> SPHERE_SERIALIZER = register("sphere", SphereShape.Serializer::new);
	public static final NBTSerializer<Shape> OCTANT_ELLIPSOID_SERIALIZER = register("octant_ellipsoid", OctantEllipsoidShape.Serializer::new);
	public static final NBTSerializer<Shape> MOUND_SERIALIZER = register("mound", MoundShape.Serializer::new);
	public static final NBTSerializer<Shape> MOB_SPAWN_FILTER_SERIALIZER = register("mob_spawn_filter", MobSpawnFilterShape.Serializer::new);

	private ShapeSerializers() {}

	public static <T extends Shape> NBTSerializer<Shape> register(String id, Factory<T> factory) {
		NBTSerializer<Shape> serializer = cast(factory.create(id));
		SERIALIZERS.put(id, serializer);
		return serializer;
	}

	public static @Nullable NBTSerializer<Shape> get(String id) {
		return SERIALIZERS.get(id);
	}

	public static CompoundTag write(Shape shape) {
		NBTSerializer<Shape> serializer = shape.getNBTSerializer();
		CompoundTag tag = serializer.write(shape);
		tag.putString(SERIALIZER_KEY, serializer.id());
		return tag;
	}

	public static Shape read(CompoundTag tag) {
		NBTSerializer<Shape> serializer = get(tag.getString(SERIALIZER_KEY));
		return serializer != null ? serializer.read(tag) : Shape.EMPTY;
	}

	private static NBTSerializer<Shape> cast(NBTSerializer<? extends Shape> serializer) {
		//noinspection unchecked
		return (NBTSerializer<Shape>) serializer;
	}

	@FunctionalInterface
	public interface Factory<T extends Shape> {
		NBTSerializer<T> create(String id);
	}
}
