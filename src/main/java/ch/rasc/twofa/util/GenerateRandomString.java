package ch.rasc.twofa.util;

import java.util.Random;
import java.util.UUID;

public class GenerateRandomString {

    private static final String asciiUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String asciiLowerCase = asciiUpperCase.toLowerCase();
    private static final String digits = "1234567890";
    private static final String asciiChars = asciiUpperCase + asciiLowerCase + digits;
    private static final int len = 25;

    private static String generateRandomString(int length, String seedChars) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        Random rand = new Random();
        while (i < length) {
            sb.append(seedChars.charAt(rand.nextInt(seedChars.length())));
            i++;
        }
        return sb.toString();
    }
    public static String getOne() {
        return generateRandomString(len, asciiChars);
    }
}