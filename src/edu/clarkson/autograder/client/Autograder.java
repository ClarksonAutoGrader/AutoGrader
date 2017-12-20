package edu.clarkson.autograder.client;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Autograder implements EntryPoint {

	public static String tempDebugCourseNameSelected = "ME310 - Thermodynamics";

	/**
	 * The static images used throughout the Autograder.
	 */
	public static final AutograderResources images = GWT.create(AutograderResources.class);

	/**
	 * The html used to show a loading icon.
	 */
	public final String loadingHtml = AbstractImagePrototype.create(Autograder.images.loading()).getHTML();

	public static final int ID_TOKEN_WIDTH = 6;

	public static final SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	public static String formatIdToken(int id) {
		// Attempts to provide identical functionality as:
		// String.format("%0" + ID_TOKEN_WIDTH + "d", id);
		int unpadded_length = ("" + id).length();
		String padding = "";
		for (int i = 0; i < ID_TOKEN_WIDTH - unpadded_length; ++i)
			padding += "0";
		return padding + id;
	}

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
		LOG.publish(new LogRecord(Level.INFO, "EntryPoint"));

        History.addValueChangeHandler(State.getInstance());
        if (History.getToken().isEmpty()) {
            History.newItem("courses");
        }
		// trigger State#onValueChange
        History.fireCurrentHistoryState();
    }
}
