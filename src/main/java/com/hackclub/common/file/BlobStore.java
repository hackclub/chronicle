package com.hackclub.common.file;

import org.apache.commons.lang3.NotImplementedException;

import java.io.File;
import java.net.URI;

/**
 * Filesystem abstraction using URIs.
 * Supports:
 *      Basic filesystem
 *      S3 (future)
 */
public class BlobStore {
    public static File load(URI path) {
        String scheme = path.getScheme();

        switch(scheme) {
            case "file": return new File(path.getPath());
            default: throw new NotImplementedException("Not implemented!");
        }
    }
}
