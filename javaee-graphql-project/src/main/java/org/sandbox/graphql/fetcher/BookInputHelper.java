package org.sandbox.graphql.fetcher;

import org.sandbox.graphql.dto.BookInput;
import graphql.schema.DataFetchingEnvironment;

import java.util.Map;

public abstract class BookInputHelper {
    protected BookInput fetchBookInput(DataFetchingEnvironment environment) {
        Map<String, Object> inputMap = environment.getArgument("input");
        // Manually convert the Map into a BookInput object
        BookInput input = new BookInput();
        assert inputMap != null;
        input.setTitle((String) inputMap.get("title"));
        input.setAuthor((String) inputMap.get("author"));
        input.setYear((Integer) inputMap.get("year"));
        return input;
    }
}
