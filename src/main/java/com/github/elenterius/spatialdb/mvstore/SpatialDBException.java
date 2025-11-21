package com.github.elenterius.spatialdb.mvstore;

import org.jspecify.annotations.Nullable;

public class SpatialDBException extends Exception {

	public SpatialDBException(String message) {
		super(message);
	}

	public SpatialDBException(String message, @Nullable Throwable cause) {
		super(message, cause);
	}

}
