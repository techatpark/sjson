package com.tehatpark.sjson;

import java.util.List;

public abstract class Json<T> {


    protected final List<Tokenizer.Token> tokens;

    private final Tokenizer tokenizer;
    private final char[] charArray;

    public Json(final String jsonText) {
        charArray = jsonText.toCharArray();
        tokenizer = new Tokenizer();
        tokens = tokenizer.getTokens(charArray);
    }


    abstract T value();
}
