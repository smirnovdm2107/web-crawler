package com.github.smirnovdm2107.extractor;

public class ExtractorException extends Exception {
    public ExtractorException(final String message) {
        super(message);
    }

    public ExtractorException(final Throwable throwable) {
        super(throwable);
    }
}
