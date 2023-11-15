package net.quillcraft.build.util;

import javax.annotation.Nullable;

public class FormattedNumber {

    private enum CastNumber {
        SECOND('s', 1),
        MINUTE('m', 60),
        HOUR('h', 3600),
        DAY('d', 86400);

        private final char association;
        private final int second;

        CastNumber(char association, int time) {
            this.association = association;
            this.second = time;
        }

        public int getSecond() {
            return second;
        }
    }

    @Nullable
    private static CastNumber getFormat(String text) {
        if(text.length() < 2) return null;

        final char pass = text.charAt(text.length()-1);
        for(CastNumber castNumber : CastNumber.values()) {
            if(pass == castNumber.association)
                return castNumber;
        }
        return null;
    }


    public static int getValueFromFormattedText(String text) throws Exception {
        final CastNumber format = getFormat(text);
        if (format == null) throw new Exception("Malformatted text exception: Invalid format");

        final int value = Integer.parseInt(text.substring(0, text.length() - 1));
        if (value < 0) throw new Exception("Malformatted text exception: Negative value is not allowed");

        return value * format.getSecond();
    }


}
