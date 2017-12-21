package edu.clarkson.autograder.client.pages;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Problem;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataService;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataServiceAsync;
import edu.clarkson.autograder.client.widgets.AssignmentTreeViewModel;
import edu.clarkson.autograder.client.widgets.Content;
import edu.clarkson.autograder.client.widgets.ProblemContent;

/**
 * Generate a page listing all assignments in the specified course.
 */
public class CoursePage extends Content {

	// interface CoursePageUiBinder extends UiBinder<Widget, CoursePage> {
	// }
	//
	// private static final CoursePageUiBinder uiBinder =
	// GWT.create(CoursePageUiBinder.class);

	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	private int courseId;

	private HorizontalPanel sideBarAndContent;

	/**
	 * Attempt to create CoursePage with specified course ID. The course ID
	 * possibly does not exist, or the user cannot access it.
	 */
	public CoursePage(int courseId) {
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#<init> - courseId=" + courseId));
		this.courseId = courseId;

		// Page title
		Label pageTitle = new Label(edu.clarkson.autograder.client.Autograder.tempDebugCourseNameSelected);
		pageTitle.addStyleName("coursePageHeader");

		// Assemble the course page elements
		sideBarAndContent = new HorizontalPanel();
		sideBarAndContent.addStyleName("sideBarAndContent");
		VerticalPanel topLevel = new VerticalPanel();
		topLevel.add(pageTitle);
		topLevel.add(sideBarAndContent);

		// request data to finish loading sideBarAndContent
		requestAssignmentProblemTreeDataAsync();

		// Add page to app
		initWidget(topLevel);
	}

	@Override
	public String getPrimaryStyleName() {
		return "coursePage";
	}

	private void requestAssignmentProblemTreeDataAsync() {
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestAssignmentProblemTreeDataAsync - begin"));

		AssignmentProblemTreeDataServiceAsync treeDataService = GWT.create(AssignmentProblemTreeDataService.class);
		treeDataService.fetchTreeData(courseId, new AsyncCallback<Map<Assignment, List<Problem>>>() {
			@Override
			public void onFailure(Throwable caught) {
				LOG.publish(new LogRecord(Level.INFO,
				        "CoursePage#requestAssignmentProblemTreeDataAsync - onFailure"));
				Label errorLabel = new Label("Failed to load course assignments.");
				errorLabel.addStyleName("errorLabel");
				sideBarAndContent.add(errorLabel);
			}

			@Override
			public void onSuccess(Map<Assignment, List<Problem>> treeData) {
				LOG.publish(new LogRecord(Level.INFO,
				        "AssignmentTreeViewModel#requestAssignmentProblemTreeDataAsync - onSuccess"));
				loadSideBarAndContent(treeData);
			}
		});
		LOG.publish(new LogRecord(Level.INFO, "AssignmentTreeViewModel#requestAssignmentProblemTreeDataAsync - end"));
	}

	private void loadSideBarAndContent(Map<Assignment, List<Problem>> treeData) {

		// Create a side bar for assignment selection.
		LOG.publish(new LogRecord(Level.INFO,
		        "CoursePage#loadSideBarAndContent - Create sidebar"));
		CellTree sideBar = new CellTree(new AssignmentTreeViewModel(treeData), null);
		sideBar.setAnimationEnabled(true);
		sideBar.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		// sideBar.ensureDebugId("sideBar"); // TODO what is debugId?
		sideBar.addStyleName("assignmentSideBar");
		sideBar.getRootTreeNode().setChildOpen(0, true);

		LOG.publish(new LogRecord(Level.INFO, "CoursePage#loadSideBarAndContent - Create problem view"));
		// Problem content
		ProblemContent problemContent = new ProblemContent();
		problemContent.addStyleName("problemContent");

		sideBarAndContent.add(sideBar);
		sideBarAndContent.add(problemContent);
	}
}
