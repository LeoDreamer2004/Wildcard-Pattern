package org.leodreamer.wildcard_pattern.util;

public class MathUtils {
    public static int saturatedCast(long number) {
        if (number > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (number < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) number;
    }
}
