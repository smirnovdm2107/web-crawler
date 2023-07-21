package com.github.smirnovdm2107.extractor;

import com.github.smirnovdm2107.parser.CharSource;
import com.github.smirnovdm2107.parser.FileCharSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DefaultExtractor implements Extractor {
    @Override
    public List<URI> extractLinks(final Path path) throws ExtractorException {
        try(final Parser parser = new Parser(path)) {
            return parser.extractLinks();
        } catch (final MalformedInputException e){
            throw new ExtractorException("Malformed input");
        } catch (final IOException e) {
            throw new ExtractorException(e);
        }
    }

    private static class Parser implements AutoCloseable {
        public static final int MAX_URL_LENGTH = 2048;
        private final CharSource in;
        public Parser(final Path path) {
            this.in = new FileCharSource(path);
        }

        public List<URI> extractLinks() throws IOException {
            final List<URI> links = new ArrayList<>();
            while (true) {
                final URI link = nextLink();
                if (link == null) {
                    break;
                }
                links.add(link);
            }
            return links;
        }

        private URI nextLink() throws IOException {
            final StringBuilder sb = new StringBuilder();
            int letters = 0;
            in.skipWhitespaces();
            while(in.hasNext()) {
                in.skipWhitespaces();
                while (in.hasNext()) {
                    final char ch = in.next();
                    if (Character.isWhitespace(ch)) {
                        break;
                    }
                    sb.append(ch);
                    if (++letters > MAX_URL_LENGTH) {
                        sb.setLength(0);
                        letters = 0;
                        in.skipWhile(value -> !Character.isWhitespace(value));
                    }
                }
                final URI link = toLinkOrNull(sb);
                if (link != null) {
                    return link;
                }
                sb.setLength(0);
                letters = 0;
            }
            return null;
        }

        private URI toLinkOrNull(final StringBuilder sb) {
            try {
                return new URI(sb.toString()).toURL().toURI();
            } catch (final URISyntaxException | MalformedURLException | IllegalArgumentException e) {
                return null;
            }
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }




}
