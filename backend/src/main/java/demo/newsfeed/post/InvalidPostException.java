package demo.newsfeed.post;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Input that breaks the post rules; carries one message per offending field. */
public class InvalidPostException extends RuntimeException {

    private final Map<String, String> fieldErrors;

    InvalidPostException(Map<String, String> fieldErrors) {
        super("Some fields need attention.");
        this.fieldErrors = Collections.unmodifiableMap(new LinkedHashMap<>(fieldErrors));
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
