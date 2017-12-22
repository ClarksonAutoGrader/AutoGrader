package edu.clarkson.autograder.client.pages;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.services.CoursesService;
import edu.clarkson.autograder.client.services.CoursesServiceAsync;
import edu.clarkson.autograder.client.widgets.Content;

/**
 * Users select a course from among enrolled courses.
 */
public class CourseSelectionPage extends Content {

	public static final SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	public class Listing extends Composite {

		private Course content;
		private FocusPanel panel;

		public Listing(Course content) {
			this.content = content;

			Label title = new Label(content.getTitle());
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
					// TODO (temporary) remove course propagation
					// course page should ask for the course from courseId on
					// load
					// for now, we'll just send the course name over
					edu.clarkson.autograder.client.Autograder.tempDebugCourseNameSelected = getContent().getTitle();

					History.newItem(getContent().getToken());
				}
			});
			panel.setStyleName("listingStyle");

			initWidget(panel);
		}

		private Course getContent() {
			return content;
		}
	}

	private FlexTable courseTable = new FlexTable();

	public CourseSelectionPage() {
		requestCourseListAsync();

		// Create page title
		Label pageTitle = new Label("Enrolled Courses");
		pageTitle.addStyleName("courseSelectionPageTitle");

		// Configure empty course listing courseTable
		courseTable.setCellSpacing(6);

		// Add elements to page
		VerticalPanel layout = new VerticalPanel();
		layout.add(pageTitle);
		layout.add(courseTable);

		initWidget(layout);
	}

	private void requestCourseListAsync() {
		LOG.publish(new LogRecord(Level.INFO, "CourseSelectionPage#requestCourseListAsync - begin"));
		CoursesServiceAsync courseService = GWT.create(CoursesService.class);
		courseService.fetchCourses(new AsyncCallback<List<Course>>() {
			@Override
			public void onFailure(Throwable caught) {
				LOG.publish(new LogRecord(Level.INFO, "CourseSelectionPage#requestCourseListAsync - onFailure"));
				Label errorLabel = new Label("Failed to load enrolled courses.");
				errorLabel.addStyleName("errorLabel");
				courseTable.setWidget(0, 0, errorLabel);
			}

			@Override
			public void onSuccess(List<Course> courseList) {
				LOG.publish(new LogRecord(Level.INFO, "CourseSelectionPage#requestCourseListAsync - onSuccess"));
				courseTable.clear();

				if (courseList.isEmpty()) {
					Label errorLabel = new Label("You are not enrolled in any courses.");
					errorLabel.addStyleName("errorLabel");
					courseTable.setWidget(0, 0, errorLabel);
				} else {

					// populate table of course listings
					for (Course course : courseList) {
						courseTable.setWidget(courseTable.getRowCount(), 0, new Listing(course));
					}
				}
			}
		});
	}

	@Override
	public String getPrimaryStyleName() {
		return "courseSelectionPage";
	}

}
