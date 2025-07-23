package com.github.elenterius.spatialdb.mvstore;

import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreException;

import java.io.IOException;
import java.nio.file.Path;

public interface MVStoreFactory {
	MVStore open(Path dbFilepath) throws IOException, MVStoreException;
}
