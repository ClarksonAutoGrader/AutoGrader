package edu.clarkson.autograder.client;

import com.google.gwt.user.client.ui.RootPanel;

import edu.clarkson.autograder.client.widgets.Content;

public class ContentContainer {

    public static void setContent(Content content) {
        RootPanel.get("content").clear();
        content.setStylePrimaryName(content.getPrimaryStyleName());
        RootPanel.get("content").add(content);
    }
}
