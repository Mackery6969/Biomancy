package com.github.elenterius.spatialdb.mvstore;

import com.github.elenterius.spatialdb.SpatialDBManager;
import org.h2.message.DbException;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreTool;
import org.h2.store.fs.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class BackupUtil {

	public static final String SUFFIX_BACKUP_FILE = ".backup";
	public static final String SUFFIX_RECOVERY_FILE = ".recoveryFile";
	public static final String SUFFIX_NEW_FILE = ".newFile";
	public static final String SUFFIX_TEMP_FILE = ".tempFile";

	private BackupUtil() {}

	public static boolean isDirectoryWritable(Path dir) {
		return Files.isDirectory(dir) && Files.isWritable(dir);
	}

	/**
	 * This will do an online backup when the database is open and a offline backup when it is already closed.
	 */
	public static void backupNow(MVStore store) throws IOException {
		String backupFilePath = getBackupFilePath(store);
		backupNow(store, backupFilePath);
	}

	/**
	 * This will do an online backup when the database is open and a offline backup when it is already closed.
	 */
	public static void backupNow(MVStore store, String backupFilePath) throws IOException {
		String temporaryBackupFilePath = backupFilePath + SUFFIX_TEMP_FILE;
		FileUtils.delete(temporaryBackupFilePath);

		if (!store.isClosed()) {
			onlineBackupNow(store, temporaryBackupFilePath);
		}
		else {
			offlineBackupNow(store, temporaryBackupFilePath);
		}

		MVStoreTool.moveAtomicReplace(temporaryBackupFilePath, backupFilePath);
	}

	private static void offlineBackupNow(MVStore store, String targetFilePath) throws IOException {
		SpatialDBManager.LOGGER.debug(SpatialDBManager.LOG_MARKER, "Starting Offline Backup");
		try (FileOutputStream fos = new FileOutputStream(targetFilePath); ZipOutputStream zos = new ZipOutputStream(fos)) {
			Path path = new File(store.getFileStore().getFileName()).toPath();
			zos.putNextEntry(new ZipEntry(path.getFileName().toString()));
			Files.copy(path, zos);
			zos.closeEntry();
		}
	}

	private static void onlineBackupNow(MVStore store, String targetFilePath) throws IOException {
		SpatialDBManager.LOGGER.debug(SpatialDBManager.LOG_MARKER, "Starting Live Backup");
		try (FileOutputStream fos = new FileOutputStream(targetFilePath); ZipOutputStream zos = new ZipOutputStream(fos)) {
			store.getFileStore().backup(zos);
		}
	}

	public static String getBackupFilePath(MVStore store) {
		return store.getFileStore().getFileName() + SUFFIX_BACKUP_FILE;
	}

	public static Path getBackupFilePath(Path dbFilePath) {
		return Path.of(dbFilePath + SUFFIX_BACKUP_FILE);
	}

	public static Path getRecoveryFilePath(Path dbFilePath) {
		return Path.of(dbFilePath + SUFFIX_RECOVERY_FILE);
	}

	public static void deleteRecoveryFile(String filePath) throws DbException {
		String recoveryFile = filePath + SUFFIX_RECOVERY_FILE;
		if (FileUtils.exists(recoveryFile)) {
			FileUtils.delete(recoveryFile);
		}
	}

	public static void cleanUpFiles(String filePath) throws DbException {
		cleanupTemporaryFiles(filePath);

		cleanupTemporaryFiles(filePath + SUFFIX_BACKUP_FILE);

		cleanupTemporaryFiles(filePath + SUFFIX_RECOVERY_FILE);
		String recoveryFile = filePath + SUFFIX_RECOVERY_FILE;
		if (FileUtils.exists(recoveryFile)) {
			FileUtils.delete(recoveryFile);
		}
	}

	public static void cleanupTemporaryFiles(String filePath) throws DbException {
		String tempFile = filePath + SUFFIX_TEMP_FILE;
		if (FileUtils.exists(tempFile)) {
			FileUtils.delete(tempFile);
		}

		String newFile = filePath + SUFFIX_NEW_FILE;
		if (FileUtils.exists(newFile)) {
			if (FileUtils.exists(filePath)) {
				FileUtils.delete(newFile);
			}
			else {
				FileUtils.move(newFile, filePath);
			}
		}
	}

	public static void extractBackup(Path zipPath, Path extractPath) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath.toFile()))) {
			if (zis.getNextEntry() != null) {
				Files.copy(zis, extractPath, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

}
