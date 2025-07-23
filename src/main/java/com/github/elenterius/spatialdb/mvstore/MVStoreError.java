package com.github.elenterius.spatialdb.mvstore;

import org.h2.mvstore.MVStoreException;

import java.util.Arrays;

public enum MVStoreError {

	UNKNOW_ERROR_CODE(-1),

	/// An error occurred while reading from the file.
	ERROR_READING_FAILED(1),

	/// An error occurred when trying to write to the file.
	ERROR_WRITING_FAILED(2),

	/// An internal error occurred. This could be a bug, or a memory corruption (for example caused by out of memory).
	ERROR_INTERNAL(3),

	/// The object is already closed.
	ERROR_CLOSED(4),

	/// The file format is not supported.
	ERROR_UNSUPPORTED_FORMAT(5),

	/// The file is corrupt or (for encrypted files) the encryption key is wrong.
	ERROR_FILE_CORRUPT(6),

	/// The file is locked.
	ERROR_FILE_LOCKED(7),

	/// An error occurred when serializing or de-serializing.
	ERROR_SERIALIZATION(8),

	/// The application was trying to read data from a chunk that is no longer available.
	ERROR_CHUNK_NOT_FOUND(9),

	/// The block in the stream store was not found.
	ERROR_BLOCK_NOT_FOUND(50),

	/// The transaction store is corrupt.
	ERROR_TRANSACTION_CORRUPT(100),

	/// An entry is still locked by another transaction.
	ERROR_TRANSACTION_LOCKED(101),

	/// There are too many open transactions.
	ERROR_TOO_MANY_OPEN_TRANSACTIONS(102),

	/// The transaction store is in an illegal state (for example, not yet initialized).
	ERROR_TRANSACTION_ILLEGAL_STATE(103),

	/// The transaction contains too many changes.
	ERROR_TRANSACTION_TOO_BIG(104),

	/// Deadlock discovered and one of transactions involved chosen as victim and rolled back.
	ERROR_TRANSACTIONS_DEADLOCK(105),

	/// The transaction store can not be initialized because data type is not found in type registry.
	ERROR_UNKNOWN_DATA_TYPE(106);

	private final int errorCode;

	MVStoreError(int errorCode) {
		this.errorCode = errorCode;
	}

	public boolean isUnknownErrorCode() {
		return this == UNKNOW_ERROR_CODE;
	}

	public static MVStoreError parse(MVStoreException exception) {
		return parse(exception.getErrorCode());
	}

	public static MVStoreError parse(int errorCode) {
		return Arrays.stream(values()).filter(x -> x.errorCode == errorCode).findAny().orElse(UNKNOW_ERROR_CODE);
	}

}
