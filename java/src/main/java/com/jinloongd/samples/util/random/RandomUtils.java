package com.jinloongd.samples.util.random;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author JinLoong.Du
 */
public class RandomUtils {

    public static final String UPPERCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxzy";

    public static final String NUMBERS = "0123456789";

    public static final String SPECIAL_CHARACTERS = "@$!%*#?&.+-=_^`~";

    public static final String DEFAULT_CHARACTERS = UPPERCASE_CHARACTERS + LOWERCASE_CHARACTERS + NUMBERS + SPECIAL_CHARACTERS;

    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    public static SecureRandom getSecureRandom() {
        return getSecureRandom(null);
    }

    public static SecureRandom getSecureRandom(byte[] seed) {
        return (seed == null) ? new SecureRandom() : new SecureRandom(seed);
    }

    public static Random getRandom(boolean isSecure) {
        return isSecure ? getSecureRandom() : getRandom();
    }

    /**
     * Generate a random int value.
     *
     * @return the random int value
     */
    public static int randomInt() {
        return getRandom().nextInt();
    }

    /**
     * Generate a random int value between 0 (inclusive) and the specified value (exclusive).
     *
     * @param bound Limit the range of random int value (exclusive)
     * @return the random int value
     */
    public static int randomInt(int bound) {
        return getRandom().nextInt(bound);
    }

    /**
     * Generate a random int value between the specified minimum value (inclusive)
     * and the specified maximum value (inclusive).
     *
     * @param min the minimum value of random int value (inclusive)
     * @param max the maximum value of random int value (inclusive)
     * @return the random int value
     */
    public static int randomInt(int min, int max) {
        return getRandom().nextInt(min, max + 1);
    }

    /**
     * Generate a random long value
     *
     * @return the random long value
     */
    public static long randomLong() {
        return getRandom().nextLong();
    }

    /**
     * Generate a random long value between zero (inclusive) and the specified value (exclusive).
     *
     * @param bound Limit the range of random long value (exclusive)
     * @return the random long value
     */
    public static long randomLong(long bound) {
        return getRandom().nextLong(bound);
    }

    /**
     * Generate a random long value between the specified minimum value (inclusive).
     * and the specified maximum value (inclusive).
     *
     * @param min the minimum value of random long value (inclusive)
     * @param max the maximum value of random long value (inclusive)
     * @return the random long value
     */
    public static long randomLong(long min, long max) {
        return getRandom().nextLong(min, max + 1);
    }

    /**
     * Generate a random string of specific length consisting of alphanumeric characters.
     *
     * @param length the random string length
     * @return the random string
     */
    public static String randomString(int length) {
        return randomString(DEFAULT_CHARACTERS.toCharArray(), length);
    }

    /**
     * Generate a random string of specific length consisting of the specified symbolic characters.
     *
     * @param characters the specified characters
     * @param length the random string length
     * @return the random string
     */
    public static String randomString(char[] characters, int length) {
        if (characters == null || characters.length == 0
                || length <= 0) {
            throw new IllegalArgumentException("symbols is empty or length is negative");
        }
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = randomInt(characters.length);
            sb.append(characters[randomIndex]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(RandomUtils.randomString(randomInt(16, 32)));
        }
    }
}
