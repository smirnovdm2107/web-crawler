package com.github.smirnovdm2107.crawler;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

record Result(Map<URI, Path> downloaded, Map<URI, Exception> fractured) {

}
