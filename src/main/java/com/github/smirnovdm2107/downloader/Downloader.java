package com.github.smirnovdm2107.downloader;

import java.net.URL;
import java.nio.file.Path;

public interface Downloader {
    void download(final URL url, final Path path);
}
