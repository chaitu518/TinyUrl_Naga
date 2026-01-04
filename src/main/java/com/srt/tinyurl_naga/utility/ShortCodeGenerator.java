package com.srt.tinyurl_naga.utility;

import java.util.concurrent.ThreadLocalRandom;

public class ShortCodeGenerator {

    private static final String BASE62 =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generate(Long length) {
        StringBuilder sb = new StringBuilder(String.valueOf(length));
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            sb.append(BASE62.charAt(random.nextInt(BASE62.length())));
        }
        return sb.toString();
    }
}
