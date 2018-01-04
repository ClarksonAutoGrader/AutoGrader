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
import com.google.gwt.user.client.ui.VerticalPanel;

public class Listing extends Composite {

    private Listable content;
    private FocusPanel wrapper;

    public Listing(Listable content) {
        this.content = content;

        // Create title
        Label title = new Label(content.getTitle());
        // TODO: listing styles dependent on parent Content primary style name
        title.addStyleDependentName("listingTitle");

        // Create description
        Label descriptionLabel = new Label(content.getDescription());
        descriptionLabel.addStyleDependentName("listingDescrption");

        // Arrange elements
        VerticalPanel layout = new VerticalPanel();
        layout.add(title);
        layout.add(descriptionLabel);
        // TODO: round corners on Listing border

        // Wrap to capture mouse over
        wrapper = new FocusPanel();
        wrapper.add(layout);
        wrapper.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                wrapper.addStyleName("listingHighlight");
            }
        });
        wrapper.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                wrapper.removeStyleName("listingHighlight");
            }
        });
        wrapper.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                History.newItem(getContent().getToken());
            }
        });
        wrapper.setStyleName("listingStyle");

        initWidget(wrapper);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            wrapper.addStyleName(Listable.STYLE_SELECTED);
        } else {
            wrapper.removeStyleName(Listable.STYLE_SELECTED);
        }
    }

    public Listable getContent() {
        return content;
    }
}
