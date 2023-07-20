package com.hackclub.common.file;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class Cache {
    public static void save(String key, String data) throws IOException {
        FileUtils.writeStringToFile(new File(getCachedPath(key)), data, StandardCharsets.UTF_8);
    }

    public static Optional<String> load(String key) {
        String path = getCachedPath(key);
        try {
            return Optional.of(new String(Files.readAllBytes(Paths.get(path))));
        } catch (IOException e) {
            // do nothing
        }
        return Optional.empty();
    }

    @NotNull
    private static String getCachedPath(String key) {
        String md5Hex = DigestUtils.md5Hex(key).toUpperCase();
        return String.format("/var/data/chronicle/cache/%s", md5Hex);
    }
}
