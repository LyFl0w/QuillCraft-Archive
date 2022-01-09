package org.lumy.api.text;

public sealed interface TextBase permits Text, TextList{

    String getPath();

}
