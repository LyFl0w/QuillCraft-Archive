package net.lyflow.particles.utils;

import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandUtils{

    public static List<String> completionTable(String arg, List<String> completions){
        final List<String> completion = new ArrayList<>();
        //Copie les correspondances du premier argument de la liste (ex : si le premier argument est 'm', il retournera juste 'minecraft')
        StringUtil.copyPartialMatches(arg, completions, completion);
        //Trie la liste
        Collections.sort(completion);
        return completion;
    }

    public static List<String> completionTable(String arg, String[] completions){
        return completionTable(arg, Arrays.asList(completions));
    }

    public static List<String> completionTable(String arg, Enum[] completions){
        List<String> enumName = new ArrayList<>();
        Arrays.stream(completions).parallel().forEach(anEnum -> {
            enumName.add(anEnum.name());
        });
        return completionTable(arg, enumName);
    }

    public static List<String> completionTable(String arg, Enum[] completions, List<String> subCompletition){
        List<String> enumName = new ArrayList<>();
        Arrays.stream(completions).parallel().forEach(anEnum -> enumName.add(anEnum.name()));
        enumName.addAll(subCompletition);
        return completionTable(arg, enumName);
    }

}
