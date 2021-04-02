package com.awidok.catalog;

import java.util.HashMap;
import java.util.Map;

public class Currencies {
    public enum Names {
        USD,
        RUB,
        EUR
    }

    public static final Names DEFAULT = Names.RUB;

    private static final Map<Names, Integer> rates = new HashMap<>();

    static {
        rates.put(Names.USD, 7609);
        rates.put(Names.EUR, 8949);
        rates.put(Names.RUB, 100);
    }

    public static String convert(long price, Names name) {
        long finalPrice = divideWithCeiling(price * 100, rates.get(name));
        long prefix = finalPrice / 100;
        long suffix = finalPrice % 100;
        return String.format(switch (name) {
            case USD -> "$%d.%d";
            case EUR -> "%d.%d€";
            case RUB -> "%d.%d₽";
        }, prefix, suffix);
    }

    private static long divideWithCeiling(long a, long b) {
        return (a + b - 1) / b;
    }
}
