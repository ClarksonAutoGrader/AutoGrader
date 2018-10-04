package edu.clarkson.autograder.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;


public class Listing extends Composite {

	private FocusPanel panel;

	public Listing(String titleString, final String nextPageToken) {

		Label title = new Label(titleString);
		title.addStyleDependentName("listingTitle");

		// Wrap to capture mouse over
		panel = new FocusPanel();
		panel.add(title);
		panel.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				panel.addStyleName("listingHighlight");
			}
		});
		panel.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				panel.removeStyleName("listingHighlight");
			}
		});
		panel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				History.newItem(nextPageToken);
			}
		});
		panel.setStyleName("listingStyle");

		initWidget(panel);
	}
}