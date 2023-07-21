package com.github.smirnovdm2107;

import com.github.smirnovdm2107.extractor.DefaultExtractor;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println(new DefaultExtractor().extractLinks(Path.of(args[0])).size());
    }
}