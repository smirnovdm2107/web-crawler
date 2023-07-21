package com.github.smirnovdm2107.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class FileCharSource implements CharSource {
    private final BufferedReader in;
    private int ch = -1;

    public FileCharSource(final Path path) {
        try {
            in = Files.newBufferedReader(path);
            ch = in.read();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void skipWhitespaces() throws IOException {
        skipWhile(Character::isWhitespace);
    }

    @Override
    public void skipWhile(final IntPredicate predicate) throws IOException {
        while(predicate.test(ch) && hasNext()) {
            next();
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public char next() throws IOException {
        if (ch == -1) {
            throw new IOException("No chars left");
        }
        final int result = ch;
        ch = in.read();
        return (char) result;
    }

    @Override
    public boolean hasNext() {
        return ch != -1;
    }


}
