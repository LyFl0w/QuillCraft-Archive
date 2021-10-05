package net.quillcraft.bungee.utils;

import java.util.List;

public class StringUtils {

    public static boolean containsIgnoreCase(List<String> strings, String searchStr)     {
        if(searchStr.isBlank() || strings.size() == 0) return false;

        return strings.stream().parallel().filter(string -> !string.isBlank()).anyMatch(string -> string.toLowerCase().contains(searchStr.toLowerCase()));
    }

}
