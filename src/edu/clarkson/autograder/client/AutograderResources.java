package edu.clarkson.autograder.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * The images and styles used throughout the Autograder.
 */
public interface AutograderResources extends ClientBundle {

	/**
	 * The static images used throughout the Autograder.
	 */
	public static final AutograderResources INSTANCE = GWT.create(AutograderResources.class);

	ImageResource loading();

	ImageResource greenCheck();

	ImageResource redCross();

	ImageResource info();
}
