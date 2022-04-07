package net.quillcraft.core.utils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class TimeUtils{

    private final long time;
    private final String pattern;
    public TimeUtils(int time, TimeUnit timeUnit, String pattern){
        this.time = timeUnit.toSeconds(time);
        this.pattern = pattern;
    }

    public String formatToTimer(){
        final StringBuilder message = new StringBuilder();
        Arrays.stream(pattern.split(":")).forEach(pattern -> {
            switch(pattern){
                case "h" -> message.append(time/3600).append(":");
                case "hh" -> {
                    final long hour = time/3600;
                    message.append(((hour < 10) ? "0"+hour : hour)).append(":");
                }
                case "m" -> message.append(time/60).append(":");
                case "mm" -> {
                    final long minute = time/60;
                    message.append(((minute < 10) ? "0"+minute : minute)).append(":");
                }
                case "s" -> message.append(time%60).append(":");
                case "ss" -> {
                    final long second = time%60;
                    message.append(((second < 10) ? "0"+second : second)).append(":");
                }
            }
        });
        return message.toString();
    }



}
