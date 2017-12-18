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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Autograder implements EntryPoint {

	public static String tempDebugCourseNameSelected = "ME310 - Thermodynamics";

	/**
	 * The static images used throughout the Autograder.
	 */
	public static final AutograderResources images = GWT.create(AutograderResources.class);

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
		SimpleRemoteLogHandler remoteLog = new SimpleRemoteLogHandler();
		remoteLog.publish(new LogRecord(Level.INFO, "EntryPoint"));
		
		RootPanel.get("header").add(getInfo());

        History.addValueChangeHandler(State.getInstance());
        if (History.getToken().isEmpty()) {
            History.newItem("courses");
        }
        History.fireCurrentHistoryState();
    }
    
    private Widget getInfo() {
    	HorizontalPanel infoPanel = new HorizontalPanel();
    	infoPanel.add(new InlineLabel("Ryan Wood"));
    	
    	Button logoutButton = new Button("Test", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				Window.Location.replace("https://cas.clarkson.edu/cas/logout");
				}	
    		});
    	infoPanel.add(logoutButton);
    	
    	return infoPanel;
    }
}
