package edu.clarkson.autograder.client;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

import edu.clarkson.autograder.client.services.UsernameService;
import edu.clarkson.autograder.client.services.UsernameServiceAsync;

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
	
	private InlineLabel usernameLabel = new InlineLabel("");

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
		SimpleRemoteLogHandler remoteLog = new SimpleRemoteLogHandler();
		remoteLog.publish(new LogRecord(Level.INFO, "EntryPoint"));
		
		requestUserAsync();
		
		//logout button (INOP)
//		RootPanel.get("info").add(new Button("Log Out", new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				Window.Location.assign("https://cas.clarkson.edu/cas/logout");
//				}	
//    		}));
		
		//Home button
		RootPanel.get("info").add(new Button("Course Selection", new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Window.Location.replace("#courses");
			}	
		}));
		RootPanel.get("info").add(usernameLabel);

        History.addValueChangeHandler(State.getInstance());
        if (History.getToken().isEmpty()) {
            History.newItem("courses");
        }
		// trigger State#onValueChange
        History.fireCurrentHistoryState();
    }

    private void requestUserAsync() {
    	UsernameServiceAsync userService = GWT.create(UsernameService.class);
    	AsyncCallback<String> callback = new AsyncCallback<String>() {
    		public void onFailure(Throwable caught) {
				usernameLabel.setText("Authentication Error (1)");
				ContentContainer.clearContent();
    		}
    		
    		public void onSuccess(String username) {
				if (username.equals("null")) {
					ContentContainer.clearContent();
					usernameLabel.setText("Authentication Error (2)");
				} else {
					usernameLabel.setText(username);
				}
    		}
    	};
    	userService.getCurrentUsername(callback);
    }
}
