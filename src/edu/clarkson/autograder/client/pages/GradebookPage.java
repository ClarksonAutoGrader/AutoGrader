package edu.clarkson.autograder.client.pages;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.ui.Label;

import edu.clarkson.autograder.client.widgets.Content;

public class GradebookPage extends Content {
	
	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();
	
	private int courseID;
	
	public GradebookPage(int courseID) {
		LOG.publish(new LogRecord(Level.INFO, "GradebookPage#<init> - courseId=" + courseID));
		this.courseID = courseID;
		
		Label pageTitle = new Label(edu.clarkson.autograder.client.Autograder.tempDebugCourseNameSelected);
		pageTitle.addStyleName("gradebookPageHeader");
		
		
	}
	
	@Override
	public String getPrimaryStyleName() {
		return "gradebookpage";
	}
	
}
