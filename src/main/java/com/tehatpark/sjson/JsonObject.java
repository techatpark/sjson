package com.tehatpark.sjson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class JsonObject extends Json<Map<String, Object>> {

    public JsonObject(final String jsonText) {
        super(jsonText);
    }

    @Override
    public Map<String, Object> value() {
        Map<String, Object> jsonAsMap = new HashMap<>();

        Stack<Map<String, Object>> objectsStack = new Stack<>();
        Stack<List> arraysStack = new Stack<>();

        objectsStack.push(jsonAsMap);

        int count = tokens.size();

        Tokenizer.Token token;

        for (int index = 0; index < count; index++) {
            token = tokens.get(index);
            switch (token.tokenType()) {
                case STRING ,NUMBER, TRUE, FALSE,NULL,ARRAY,EMPTY_OBJECT,EMPTY_ARRAY -> {
                    objectsStack.peek().put(token.key(),token.value());
                }
                case OBJECT_START -> {
                    objectsStack.peek().put(token.key(),token.value());
                    objectsStack.push((Map<String, Object>) token.value());
                }
                case ARRAY_START -> {
                    objectsStack.peek().put(token.key(),token.value());
                    List theArray = (List) token.value();
                    arraysStack.push(theArray);

                    if(!theArray.isEmpty()
                            && theArray.get(theArray.size()-1) instanceof List theInnerArray) {
                        arraysStack.push(theInnerArray);
                    }

                    if(token.openingTokenTypes()
                            .get(0).equals(Tokenizer.TokenType.OBJECT_START)) {
                        Map<String,Object> newObject = new HashMap<>();
                        arraysStack.peek().add(newObject);
                        objectsStack.push(newObject);
                    }
                }
            }

            token.closingTokenTypes().forEach(closingTokenType  -> {
                switch (closingTokenType.tokenType()) {
                    case OBJECT_END ->  {
                        objectsStack.pop();

                        if(!closingTokenType.items().isEmpty()) {
                            arraysStack.peek().addAll(closingTokenType.items());
                            // TODO: Do we need this
                            closingTokenType.items().clear();
                        }
                    }
                    case ARRAY ->  {
                        arraysStack.peek().addAll(closingTokenType.items());
                    }
                    case ARRAY_START ->  {
                        arraysStack.peek().add(closingTokenType.items());
                        arraysStack.push(closingTokenType.items());
                    }
                    case ARRAY_END ->  {
                        arraysStack.pop();
                        if(!closingTokenType.items().isEmpty()) {
                            arraysStack.peek().addAll(closingTokenType.items());
                            // TODO: Do we need this
                            closingTokenType.items().clear();
                        }
                    }
                    case OBJECT_START ->  {
                        Map<String,Object> newObject = new HashMap<>();
                        arraysStack.peek().add(newObject);
                        objectsStack.push(newObject);
                    }
                }
            });

        }

        if( !arraysStack.isEmpty() || !objectsStack.isEmpty()  ) {
            throw new IllegalArgumentException("Invalid Document");
        }

        return jsonAsMap;
    }


}
