package com.github.elenterius.spatialdb.mvstore;

import org.jetbrains.annotations.Nullable;

public class SpatialDBException extends Exception {

	public SpatialDBException(String message) {
		super(message);
	}

	public SpatialDBException(String message, @Nullable Throwable cause) {
		super(message, cause);
	}

}
