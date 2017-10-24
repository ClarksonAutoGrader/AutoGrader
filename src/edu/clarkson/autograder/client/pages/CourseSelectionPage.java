package edu.clarkson.autograder.client.pages;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.services.CoursesService;
import edu.clarkson.autograder.client.services.CoursesServiceAsync;
import edu.clarkson.autograder.client.widgets.Content;
import edu.clarkson.autograder.client.widgets.Listing;

/**
 * Generate a page listing all courses available for selection.
 */
public class CourseSelectionPage extends Content {

	private FlexTable couseTable = new FlexTable();

    public CourseSelectionPage() {
		requestCourseList();

        // Create page title
        Label pageTitle = new Label("Courses");
        // TODO content sub-styles dependent on primary style name
        pageTitle.addStyleName("pageTitle");

		// Configure empty course listing couseTable
		couseTable.setCellSpacing(6);

		// Add elements to page
		VerticalPanel layout = new VerticalPanel();
		layout.add(pageTitle);
		layout.add(couseTable);

		initWidget(layout);
	}


	private void requestCourseList() {
		CoursesServiceAsync courseService = GWT.create(CoursesService.class);
		courseService.fetchCourses(new AsyncCallback<List<Course>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(List<Course> courseList) {
				// populate couseTable of course listings
				for (Course course : courseList) {
					couseTable.setWidget(couseTable.getRowCount(), 0, new Listing(course));
				}
			}
		});
	}

    @Override
    public String getPrimaryStyleName() {
        return "courseSelectionPage";
    }
    
}
