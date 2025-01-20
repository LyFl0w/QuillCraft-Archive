package net.quillcraft.core.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private final long time;
    private final String pattern;

    public TimeUtils(int time, TimeUnit timeUnit, String pattern) {
        this.time = timeUnit.toSeconds(time);
        this.pattern = pattern;
    }

    //FIXME : SOMETHING IS WRONG WITH i

    public String formatToTimer() {
        final StringBuilder message = new StringBuilder();
        final String[] split = pattern.split(":");
        int i = 0;
        for (String part : split) {
            switch (part) {
                case "h" -> message.append(time / 3600);
                case "hh" -> {
                    final long hour = time / 3600;
                    message.append(((hour < 10) ? "0" + hour : hour));
                }
                case "m" -> message.append(time / 60);
                case "mm" -> {
                    final long minute = time / 60;
                    message.append(((minute < 10) ? "0" + minute : minute));
                }
                case "s" -> message.append(time % 60);
                case "ss" -> {
                    final long second = time % 60;
                    message.append(((second < 10) ? "0" + second : second));
                }
            }
            if ((i += 1) != split.length) message.append(":");
        }
        return message.toString();
    }


}
