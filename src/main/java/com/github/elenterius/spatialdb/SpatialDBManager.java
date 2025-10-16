package com.github.elenterius.spatialdb;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.mixin.accessor.DimensionDataStorageAccessor;
import com.github.elenterius.spatialdb.mvstore.MVStoreFactory;
import com.github.elenterius.spatialdb.mvstore.SpatialDB;
import com.github.elenterius.spatialdb.mvstore.SpatialDBException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.h2.mvstore.MVStore;
import org.h2.store.fs.FileUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpatialDBManager {

	public static final Logger LOGGER = LogManager.getLogger("SpatialDB");
	public static final Marker LOG_MARKER = MarkerManager.getMarker("Biomancy");

	public static final int STORE_VERSION = 0;
	public static final MVStoreFactory MAIN_DB_FACTORY = path -> new MVStore.Builder().fileName(path.toString()).cacheSize(8).open();

	public static final String FILE_NAME = "biomancy.spatial.db";

	private static final Map<String, SDBI> DATABASES = new HashMap<>();

	private static final ExecutorService EXECUTOR_SERVICE;

	static {
		EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

		if (FMLEnvironment.dist.isClient()) {
			LOGGER.info(LOG_MARKER, "Registering shutdown hook...");
			//this gets called too late on a dedicated server due to forge/mojang
			Runtime.getRuntime().addShutdownHook(new Thread(SpatialDBManager::shutdownAll, "SpatialDB/ShutdownHook"));
		}
	}

	private SpatialDBManager() {}

	private static SpatialDB openDB(MinecraftServer server, String worldName) {
		Path dataFolder = ((DimensionDataStorageAccessor) server.overworld().getDataStorage()).biomancy$getDataFolder().toPath();
		Path dbFilepath = dataFolder.resolve(FILE_NAME);
		String dbName = "Biomancy-SpatialDB";
		String dbOwnerName = "[World/%s]".formatted(worldName);

		try {
			//clean up legacy files
			FileUtils.delete(dataFolder.resolve("biomancy.spatial.dat").toString());
		}
		catch (Exception ignored) {}

		try {
			SpatialDB db = SpatialDB.open(dbFilepath, dbName, dbOwnerName, MAIN_DB_FACTORY);
			LOGGER.info(LOG_MARKER, "Successfully opened database '{}' for '{}'", dbName, dbOwnerName);
			return db;
		}
		catch (SpatialDBException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getWorldName(ServerLevel level) {
		return level.getServer().getWorldData().getLevelName();
	}

	public static SDBI getInstance(final ServerLevel level) {
		String key = getWorldName(level);
		return DATABASES.computeIfAbsent(key, worldName -> new SDBI(openDB(level.getServer(), worldName)));
	}

	private static void backupDB(ServerLevel level) {
		String key = getWorldName(level);
		if (DATABASES.containsKey(key)) {
			SpatialDB db = DATABASES.get(key).getSpatialDB();
			try {
				EXECUTOR_SERVICE.submit(db::backup);
			}
			catch (Exception ignored) {

			}
		}
	}

	/// Close [SpatialDB] if present for the given level and remove it from the cache
	private static void shutdownDB(ServerLevel level) {
		String key = getWorldName(level);
		if (DATABASES.containsKey(key)) {
			SpatialDB db = DATABASES.get(key).getSpatialDB();

			LOGGER.info(LOG_MARKER, "Shutting down database for world {}...", key);
			db.backup();
			db.shutdown();

			DATABASES.remove(key);
		}
	}

	private static void shutdownAll() {
		LOGGER.info(LOG_MARKER, "Shutting down DBManager and Executor Service...");
		EXECUTOR_SERVICE.shutdown();
		try {
			if (!EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS)) {
				LOGGER.warn(LOG_MARKER, "Executor Service failed to terminate cleanly, forcing shutdown!");
				EXECUTOR_SERVICE.shutdownNow();
			}
		}
		catch (InterruptedException e) {
			LOGGER.warn(LOG_MARKER, "Shutdown was interrupted while waiting for Executor Service to terminate, forcing shutdown!");
			EXECUTOR_SERVICE.shutdownNow();
		}

		DATABASES.forEach((world, sdbi) -> {
			LOGGER.info(LOG_MARKER, "Shutting down database for [World/{}]...", world);
			SpatialDB db = sdbi.getSpatialDB();
			db.backup();
			db.shutdown();
		});
		DATABASES.clear();
	}

	@SubscribeEvent
	public static void onLevelLoad(final LevelEvent.Load event) {
		if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel && (serverLevel.dimension() == Level.OVERWORLD)) {
			getInstance(serverLevel); //force init spatial database
		}
	}

	@SubscribeEvent
	public static void onLevelSave(final LevelEvent.Save event) {
		if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel && (serverLevel.dimension() == Level.OVERWORLD)) {
			backupDB(serverLevel);
		}
	}

	@SubscribeEvent
	public static void onLevelUnload(final LevelEvent.Unload event) {
		if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel && (serverLevel.dimension() == Level.OVERWORLD)) {
			shutdownDB(serverLevel);
		}
	}

	@SubscribeEvent
	public static void onServerStop(final ServerStoppingEvent event) {
		if (FMLEnvironment.dist.isDedicatedServer()) {
			shutdownAll(); //explicitly force a shutdown because forge/mojang shutdowns the server in an incompatible way with JVM shutdown hooks
		}
	}

}
