package com.github.grimsa.pokerhands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class ClasspathFileReader implements Supplier<List<String>> {
    private final String path;

    ClasspathFileReader(final String path) {
        this.path = Objects.requireNonNull(path);
    }

    @Override
    public List<String> get() {
        try (final BufferedReader reader = createBufferedReader(getResourceAsStream())) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file " + path, e);
        }
    }

    private InputStream getResourceAsStream() {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    private BufferedReader createBufferedReader(final InputStream resource) {
        return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8));
    }
}
