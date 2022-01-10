package org.lumy.api.text;

public sealed interface TextBase permits Text, TextList{

    String getPath();


    default boolean isDefaultText(){
        return ((Enum<?>)this).name().startsWith("SERVER");
    }

    default boolean isNotDefaultText(){
        return !isDefaultText();
    }

}
