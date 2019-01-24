package edu.clarkson.autograder.client.pages;

import java.util.List;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.Autograder;
import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.objects.Problem;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataService;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataServiceAsync;
import edu.clarkson.autograder.client.services.CoursesService;
import edu.clarkson.autograder.client.services.CoursesServiceAsync;
import edu.clarkson.autograder.client.widgets.Content;
import edu.clarkson.autograder.client.widgets.Listing;

public class CourseManagementPage extends Content{

	public static final SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	private FlexTable assignmentTable = new FlexTable();

	public CourseManagementPage() {
		requestAssignmentProblemTreeDataAsync();

		// Create page title
		Label pageTitle = new Label("Assignments");
		pageTitle.addStyleName(getPrimaryStyleName() + "Title");

		// Configure empty listing
		assignmentTable.setCellSpacing(6);

		// Add elements to page
		VerticalPanel layout = new VerticalPanel();
		layout.add(pageTitle);
		layout.add(assignmentTable);

		initWidget(layout);
	}
	
	private void requestAssignmentProblemTreeDataAsync() {
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestAssignmentProblemTreeDataAsync - begin"));

		AssignmentProblemTreeDataServiceAsync treeDataService = GWT.create(AssignmentProblemTreeDataService.class);
		treeDataService.fetchTreeData(courseId, new AsyncCallback<SortedMap<Assignment, List<Problem>>>() {
			@Override
			public void onFailure(Throwable caught) {
				final String failureToLoadAssignments = "Failed to load course assignments.";
				LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestAssignmentProblemTreeDataAsync - onFailure"));
				sideBarAndContent.remove(errorLabel);
				errorLabel.setText(failureToLoadAssignments);
				sideBarAndContent.add(errorLabel);
			}

			@Override
			public void onSuccess(SortedMap<Assignment, List<Problem>> treeData) {
				LOG.publish(new LogRecord(Level.INFO,
				        "CoursePage#requestAssignmentProblemTreeDataAsync - onSuccess"));
				if (treeData.isEmpty()) {
					sideBarAndContent.remove(errorLabel);
					errorLabel.setText("The instructor has not added any assignments to the course.");
					sideBarAndContent.add(errorLabel);
				} else {
					errorLabel.setText("");
					sideBarAndContent.remove(errorLabel);
					loadSideBar(treeData);
				}

			}
		});
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestAssignmentProblemTreeDataAsync - end"));
	}

	private void requestAssignmentListAsync() {
		LOG.publish(new LogRecord(Level.INFO, "CourseManagementPage#requestAssignmentListAsync - begin"));
		AssignmentsServiceAsync assignmentService = GWT.create(AssignmentsService.class);
		assignmentService.fetchAssignments(new AsyncCallback<List<Assignment>>() {
			@Override
			public void onFailure(Throwable caught) {
				LOG.publish(new LogRecord(Level.INFO, "CourseManagementPage#requestAssignmentListAsync - onFailure"));
				Label errorLabel = new Label("Failed to load assignments.");
				errorLabel.addStyleName("errorLabel");
				assignmentTable.setWidget(0, 0, errorLabel);
			}

			@Override
			public void onSuccess(List<Assignment> assignmentList) {
				LOG.publish(new LogRecord(Level.INFO, "CourseManagementPage#requestAssignmentListAsync - onSuccess"));
				assignmentTable.clear();

				if (assignmentList.isEmpty()) {
					Label errorLabel = new Label("No assignments found.");
					errorLabel.addStyleName("errorLabel");
					assignmentTable.setWidget(0, 0, errorLabel);
				} else {

					// populate table of course listings
					for (Assignment assignment : assignmentList) {
						assignmentTable.setWidget(assignmentTable.getRowCount(), 0, new Listing(assignment.getTitle(), Autograder.formatIdToken(assignment.getId())));
					}
				}
			}
		});
	}

	@Override
	public String getPrimaryStyleName() {
		return "courseManagementPage";
	}
}
