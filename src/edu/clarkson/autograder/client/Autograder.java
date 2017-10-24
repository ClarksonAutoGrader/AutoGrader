package edu.clarkson.autograder.client;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.History;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Autograder implements EntryPoint {



    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
		SimpleRemoteLogHandler remoteLog = new SimpleRemoteLogHandler();
		remoteLog.publish(new LogRecord(Level.INFO, "EntryPoint"));

        History.addValueChangeHandler(State.getInstance());
        if (History.getToken().isEmpty()) {
            History.newItem("courses");
        }
        History.fireCurrentHistoryState();
    }
}
