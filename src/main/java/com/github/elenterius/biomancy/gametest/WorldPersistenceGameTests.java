package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.MobSpawnFilterShape;
import com.github.elenterius.spatialdb.geometry.CuboidShape;
import com.github.elenterius.spatialdb.geometry.Shape;
import com.github.elenterius.spatialdb.geometry.ShapeHierarchy;
import com.github.elenterius.spatialdb.mvstore.BackupUtil;
import com.github.elenterius.spatialdb.mvstore.SpatialDB;
import com.github.elenterius.spatialdb.type.ShapeDataType;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.WriteBuffer;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WorldPersistenceGameTests {

	private WorldPersistenceGameTests() {}

	@GameTest(template = "empty_platform")
	public static void corruptDatabaseRecoversSavedShapes(GameTestHelper helper) throws Exception {
		Path directory = Files.createTempDirectory("biomancy-recovery-test-");
		Path database = directory.resolve("spatial.db");
		try {
			try (MVStore store = new MVStore.Builder().fileName(database.toString()).open()) {
				store.openMap("test").put("shape", "saved shape");
				store.commit();
				BackupUtil.backupNow(store);
			}
			Files.write(database, new byte[8192]);
			SpatialDB recovered = SpatialDB.open(database, "GameTest", "recovery", path -> new MVStore.Builder().fileName(path.toString()).open());
			try {
				helper.assertTrue("saved shape".equals(recovered.getStore().openMap("test").get("shape")), "Recovery must restore data from the valid backup");
			}
			finally {
				recovered.shutdown();
			}
		}
		finally {
			Files.deleteIfExists(BackupUtil.getRecoveryFilePath(database));
			Files.deleteIfExists(BackupUtil.getBackupFilePath(database));
			Files.deleteIfExists(database);
			Files.deleteIfExists(directory);
		}
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void hierarchyKeepsNegativeCoordinateBounds(GameTestHelper helper) {
		CuboidShape shape = new CuboidShape(-100, -50, -80, -90, -40, -70);
		ShapeHierarchy<Shape> hierarchy = new ShapeHierarchy<>(List.of(shape));
		helper.assertTrue(hierarchy.getAABB().equals(shape.getAABB()), "Negative-coordinate bounds must not expand toward zero");
		helper.assertTrue(hierarchy.getCenter().equals(shape.center()), "Negative-coordinate center must stay inside its shape");
		ShapeHierarchy<Shape> empty = new ShapeHierarchy<>(List.of());
		helper.assertTrue(empty.getAABB().equals(new AABB(0, 0, 0, 0, 0, 0)), "An empty hierarchy must have empty bounds");
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void spawnFilterRetainsItsShapeAfterSerialization(GameTestHelper helper) {
		Shape shape = new MobSpawnFilterShape(new CuboidShape(-100, -50, -80, -90, -40, -70));
		ShapeDataType dataType = new ShapeDataType();
		WriteBuffer buffer = new WriteBuffer();
		dataType.write(buffer, shape);
		ByteBuffer bytes = buffer.getBuffer();
		bytes.flip();
		Shape restored = dataType.read(bytes);
		helper.assertTrue(restored instanceof MobSpawnFilterShape, "Spawn filter must retain its type");
		helper.assertTrue(restored.getAABB().equals(shape.getAABB()), "Spawn filter must retain its nested bounds");
		helper.assertTrue(restored.contains(-95, -45, -75), "Restored spawn filter must still contain its protected area");
		helper.succeed();
	}
}
