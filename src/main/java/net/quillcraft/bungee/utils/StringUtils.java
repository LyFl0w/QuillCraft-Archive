package net.quillcraft.bungee.utils;

import reactor.util.annotation.NonNull;

import java.util.List;

public class StringUtils {

    public static boolean containsIgnoreCase(@NonNull List<String> strings, @NonNull String searchStr) {
        if(searchStr.isBlank() || strings.size() == 0) return false;

        return strings.stream().parallel().anyMatch(searchStr::equalsIgnoreCase);
    }

    public static int indexOfStringIngoreCase(String search, List<String> searchList) {
        for(int i = 0; i < searchList.size(); i++) {
            if(search.equalsIgnoreCase(searchList.get(i))) return i;
        }
        return -1;
    }

}
