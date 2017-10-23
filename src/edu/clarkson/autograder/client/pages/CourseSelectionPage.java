package edu.clarkson.autograder.client.pages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.Data;
import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.services.CoursesService;
import edu.clarkson.autograder.client.services.CoursesServiceAsync;
import edu.clarkson.autograder.client.widgets.Content;
import edu.clarkson.autograder.client.widgets.Listing;

/**
 * Generate a page listing all courses available for selection.
 */
public class CourseSelectionPage extends Content {

    public CourseSelectionPage() {
        // Create page title
        Label pageTitle = new Label("Courses");
        // TODO content sub-styles dependent on primary style name
        pageTitle.addStyleName("pageTitle");

        // Create table of course listings
        FlexTable table = new FlexTable();
        table.setCellSpacing(6);
        // TODO enter course page if only one course available
        for (Course course : Data.getCourses()) {
        	
            table.setWidget(table.getRowCount(), 0, new Listing(course));
        }

        // Add elements to page
        VerticalPanel layout = new VerticalPanel();
        layout.add(pageTitle);
        layout.add(table);

        initWidget(layout);
    }

    @Override
    public String getPrimaryStyleName() {
        return "courseSelectionPage";
    }
    
}
