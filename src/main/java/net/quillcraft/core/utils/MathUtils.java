package net.quillcraft.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

    private MathUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static float round(float f, int decimalPlace) {
        return BigDecimal.valueOf(f).setScale(decimalPlace, RoundingMode.HALF_UP).floatValue();
    }

    public static double round(double d, int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace, RoundingMode.HALF_UP).doubleValue();
    }

}
