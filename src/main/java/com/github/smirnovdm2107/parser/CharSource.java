package com.github.smirnovdm2107.parser;

import java.io.IOException;
import java.util.function.IntPredicate;

public interface CharSource extends AutoCloseable{
    char next() throws IOException;

    boolean hasNext() throws IOException;

    void skipWhitespaces() throws IOException;

    void skipWhile(final IntPredicate predicate) throws IOException;

    void close() throws IOException;

}
