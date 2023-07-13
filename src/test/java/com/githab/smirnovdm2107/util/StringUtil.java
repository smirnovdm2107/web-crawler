package com.githab.smirnovdm2107.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Set;

public class StringUtil {
    private static final String ALPHAS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Set<String> domains = Set.of(
        "ru",
        "net",
        "com",
        "org",
        "info"
    );
    public static final Charset STANDARD_CHARSET = StandardCharsets.UTF_8;
    private static final Random random = new Random();
    public static String generateURL() {
        final StringBuilder sb = new StringBuilder(generateScheme() + "://" + generateRandomHost());
        while(Math.random() < 0.5) {
            sb.append("/")
                .append(generateRandomWord(5));
        }
        return sb.toString();
    }

    public static String generateRandomHost() {
        return generateRandomWord(10) + "." + getRandomDomain();
    }


    public static String getRandomDomain() {
        return domains.stream().skip(random.nextInt(domains.size())).findFirst().orElse(null);
    }
    public static String generateScheme() {
        return Math.random() < 0.5 ? "http" : "https";
    }

    public static String generateRandomWord(final int length) {
        final char[] chs = new char[length];
        for (int i = 0; i < length; i++) {
            chs[i] = ALPHAS.charAt(random.nextInt(ALPHAS.length()));
        }
        return new String(chs);
    }
}
