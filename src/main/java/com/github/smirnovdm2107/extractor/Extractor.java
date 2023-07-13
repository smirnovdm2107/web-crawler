package com.github.smirnovdm2107.extractor;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

public interface Extractor {
    List<URI> extractLinks(final Path path) throws ExtractorException;
}
