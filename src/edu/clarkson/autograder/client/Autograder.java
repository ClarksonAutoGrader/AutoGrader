package edu.clarkson.autograder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Autograder implements EntryPoint {
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        History.addValueChangeHandler(State.getInstance());
        if (History.getToken().isEmpty()) {
            History.newItem("courses");
        }
        History.fireCurrentHistoryState();
    }
}
