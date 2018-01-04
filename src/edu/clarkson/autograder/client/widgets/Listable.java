package edu.clarkson.autograder.client.widgets;

public interface Listable {

    String STYLE_SELECTED = "listingSelected";

    String getToken();

    int getId();

    String getTitle();

    String getDescription();
}
