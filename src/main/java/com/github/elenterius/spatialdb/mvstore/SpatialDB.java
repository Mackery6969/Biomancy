package com.github.elenterius.spatialdb.mvstore;

import com.github.elenterius.biomancy.util.FormatUtil;
import com.github.elenterius.spatialdb.SpatialDBManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.MVStoreTool;
import org.h2.store.fs.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpatialDB {

	public static final Marker LOG_MARKER = MarkerManager.getMarker("Database");

	private final MVStore store;

	private long lastBackupVersion = -1;

	private SpatialDB(MVStore store) {
		this.store = store;
	}

	public void backup() {
		if (!needsBackup() && FileUtils.exists(BackupUtil.getBackupFilePath(store))) {
			SpatialDBManager.LOGGER.info(LOG_MARKER, "Skipping backup because the backup is already on the latest version");
			return;
		}
		backupNow();
	}

	public void backupNow() {
		SpatialDBManager.LOGGER.info(LOG_MARKER, "Starting backup of database to {}...", BackupUtil.getBackupFilePath(store));
		try {
			long startTime = System.nanoTime();
			BackupUtil.backupNow(store);
			long elapsedNanos = System.nanoTime() - startTime;

			lastBackupVersion = store.getCurrentVersion();

			SpatialDBManager.LOGGER.debug(LOG_MARKER, "Backup took {}", () -> FormatUtil.formatDuration(elapsedNanos));
		}
		catch (ClosedChannelException e) {
			SpatialDBManager.LOGGER.warn(LOG_MARKER, "Failed to create backup because database channel was already closed");
		}
		catch (Exception e) {
			SpatialDBManager.LOGGER.error(LOG_MARKER, "Failed to create backup of database", e);
		}
	}

	public boolean isClosed() {
		return store.isClosed();
	}

	public boolean needsBackup() {
		return lastBackupVersion < store.getCurrentVersion() || store.hasUnsavedChanges();
	}

	public void shutdown() {
		SpatialDBManager.LOGGER.info(LOG_MARKER, "Closing database...");

		if (store.isClosed()) {
			SpatialDBManager.LOGGER.warn(LOG_MARKER, "The database was already closed");
			return;
		}

		try {
			store.close();
		}
		catch (Exception e) {
			SpatialDBManager.LOGGER.fatal(LOG_MARKER, "Failed to close database", e);
		}
	}

	public static SpatialDB open(Path dbFilepath, String dbName, String dbOwnerName, MVStoreFactory mainDBFactory) throws SpatialDBException {
		if (!BackupUtil.isDirectoryWritable(dbFilepath.getParent())) {
			throw new SpatialDBException("Database directory is not writable: " + dbFilepath.getParent());
		}

		boolean isNewDB = !Files.exists(dbFilepath);
		if (!isNewDB && !Files.isWritable(dbFilepath)) {
			throw new SpatialDBException("Database file is not writable: " + dbFilepath);
		}

		try {
			BackupUtil.cleanUpFiles(dbFilepath.toString());
		}
		catch (Exception e) {
			SpatialDBManager.LOGGER.warn("Failed to cleanup temporary files of database '{}' for '{}'", dbName, dbOwnerName, e);
		}

		DBHelper mainDB = tryOpen(dbFilepath, dbName, dbOwnerName, mainDBFactory);

		DBHelper backupDB = tryOpen(dbFilepath, dbName + " Backup", dbOwnerName, path -> {
			Path backupFile = BackupUtil.getBackupFilePath(path);
			Path recoveryFile = BackupUtil.getRecoveryFilePath(path);

			if (!Files.exists(backupFile)) throw new FileNotFoundException(backupFile.toString());
			BackupUtil.extractBackup(backupFile, recoveryFile);

			return new MVStore.Builder().fileName(recoveryFile.toString()).readOnly().open();
		});

		if (!mainDB.isValid() && (!mainDB.status.isRecoveryPossible() || !backupDB.isValid())) {
			mainDB.closeImmediately();
			backupDB.closeImmediately();
			throw new SpatialDBException("Database cannot be recovered because it's in an illegal state: %s".formatted(mainDB.status));
		}

		if (backupDB.isValid()) {
			if (mainDB.storeVersion == SpatialDBManager.STORE_VERSION && backupDB.storeVersion == SpatialDBManager.STORE_VERSION) {
				if (!mainDB.isValid() || mainDB.snapshotVersion < backupDB.snapshotVersion) {

					//noinspection DataFlowIssue - already covered by backupDB.isValid()
					String backupFile = backupDB.mvStore.getFileStore().getFileName();

					backupDB.closeImmediately();
					mainDB.closeImmediately();

					MVStoreTool.moveAtomicReplace(backupFile, dbFilepath.toString());

					try {
						BackupUtil.deleteRecoveryFile(dbFilepath.toString());
					}
					catch (Exception e) {
						SpatialDBManager.LOGGER.warn("Failed to delete temporary recovery file of database '{}' for '{}'", dbName, dbOwnerName, e);
					}

					mainDB = tryOpen(dbFilepath, dbName, dbOwnerName, mainDBFactory);
					if (mainDB.status == DBStatus.OK) {
						return mainDB.toResilientDB();
					}
				}
			}
		}
		else if (!isNewDB) {
			SpatialDBManager.LOGGER.warn("Using database without checking it's validity against a backup. Reason: No valid backup is available");
		}

		backupDB.closeImmediately();

		try {
			BackupUtil.deleteRecoveryFile(dbFilepath.toString());
		}
		catch (Exception e) {
			SpatialDBManager.LOGGER.warn("Failed to delete temporary recovery file of database '{}' for '{}'", dbName, dbOwnerName, e);
		}

		return mainDB.toResilientDB();
	}

	private static DBHelper tryOpen(Path dbFilepath, String dbName, String dbOwnerName, MVStoreFactory factory) {
		try {
			MVStore store = factory.open(dbFilepath);
			return DBHelper.from(store);
		}
		catch (MVStoreException e) {
			MVStoreError error = MVStoreError.parse(e);
			Marker marker = MarkerManager.getMarker(error.name());

			switch (error) {
				case ERROR_FILE_LOCKED -> {
					SpatialDBManager.LOGGER.fatal(marker, "Database '{}' for '{}' is already locked by a different process", dbName, dbOwnerName, e);
					return DBHelper.from(DBStatus.LOCKED, e);
				}
				case ERROR_UNSUPPORTED_FORMAT -> {
					SpatialDBManager.LOGGER.error(marker, "Unsupported format of database '{}' for '{}'", dbName, dbOwnerName, e);
					return DBHelper.from(DBStatus.CORRUPTED, e);
				}
				case ERROR_FILE_CORRUPT, ERROR_READING_FAILED -> {
					SpatialDBManager.LOGGER.error(marker, "Failed to open database '{}' for '{}'", dbName, dbOwnerName, e);
					return DBHelper.from(DBStatus.CORRUPTED, e);
				}
				default -> {
					SpatialDBManager.LOGGER.fatal(marker, "A fatal error occurred while trying to open database '{}' for '{}'", dbName, dbOwnerName, e);
					return DBHelper.from(DBStatus.UNKNOWN_ERROR, e);
				}
			}
		}
		catch (FileNotFoundException e) {
			Marker marker = MarkerManager.getMarker("IO");
			SpatialDBManager.LOGGER.error(marker, "Failed to find database '{}' ({}) for '{}'", dbName, e.getMessage(), dbOwnerName);
			return DBHelper.from(DBStatus.IO_ERROR, e);
		}
		catch (IOException e) {
			Marker marker = MarkerManager.getMarker("IO");
			SpatialDBManager.LOGGER.error(marker, "Failed to open database '{}' for '{}'", dbName, dbOwnerName, e);
			return DBHelper.from(DBStatus.IO_ERROR, e);
		}
		catch (Exception e) {
			SpatialDBManager.LOGGER.fatal("A fatal error occurred while trying to open database '{}' for '{}'", dbName, dbOwnerName, e);
			return DBHelper.from(DBStatus.UNKNOWN_ERROR, e);
		}
	}

	public MVStore getStore() {
		return store;
	}

	private enum DBStatus {
		OK(false, false),
		CORRUPTED(true, false),
		UNKNOWN_ERROR(false, true),
		IO_ERROR(false, true),
		LOCKED(false, true);

		private final boolean allowRecovery;
		private final boolean fatalError;

		DBStatus(boolean allowRecovery, boolean fatalError) {
			this.allowRecovery = allowRecovery;
			this.fatalError = fatalError;
		}

		public boolean isRecoveryPossible() {
			return allowRecovery;
		}

		public boolean isFatalError() {
			return fatalError;
		}
	}

	private static final class DBHelper {

		private final @Nullable MVStore mvStore;
		private final DBStatus status;
		public final int storeVersion;
		public final long snapshotVersion;
		public final long creationTime;
		public final long lastModified;

		private final @Nullable Exception exception;

		private DBHelper(DBStatus status, @Nullable MVStore mvStore, @Nullable Exception exception) {
			this.status = status;
			this.mvStore = mvStore;
			this.exception = exception;

			if (mvStore != null) {
				storeVersion = mvStore.getStoreVersion();
				snapshotVersion = mvStore.getCurrentVersion();
				creationTime = mvStore.getFileStore().getCreationTime();
				lastModified = FileUtils.lastModified(mvStore.getFileStore().getFileName());
			}
			else {
				storeVersion = -1;
				snapshotVersion = -1;
				creationTime = 0;
				lastModified = 0;
			}
		}

		static DBHelper from(MVStore mvStore) {
			return new DBHelper(DBStatus.OK, mvStore, null);
		}

		static DBHelper from(DBStatus status, Exception exception) {
			return new DBHelper(status, null, exception);
		}

		public void closeImmediately() {
			if (mvStore != null) mvStore.closeImmediately();
		}

		public SpatialDB toResilientDB() throws SpatialDBException {
			if (mvStore != null && status == DBStatus.OK) {
				return new SpatialDB(mvStore);
			}
			throw new SpatialDBException("Failed to open database", exception);
		}

		public boolean isValid() {
			return status == DBStatus.OK && mvStore != null;
		}
	}

}

