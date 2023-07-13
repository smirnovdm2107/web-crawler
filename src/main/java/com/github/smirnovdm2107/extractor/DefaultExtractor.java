package com.github.smirnovdm2107.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DefaultExtractor implements Extractor{
    @Override
    public List<URI> extractLinks(final Path path) throws ExtractorException {
        try(final BufferedReader reader = Files.newBufferedReader(path)) {

        } catch (final IOException e) {
            throw new ExtractorException(e);
        }
    }
}
