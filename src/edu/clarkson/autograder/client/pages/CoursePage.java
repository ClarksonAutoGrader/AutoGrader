package edu.clarkson.autograder.client.pages;

import java.util.List;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Problem;
import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataService;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataServiceAsync;
import edu.clarkson.autograder.client.services.SelectedProblemDataServiceAsync;
import edu.clarkson.autograder.client.widgets.AssignmentTreeViewModel;
import edu.clarkson.autograder.client.widgets.Content;
import edu.clarkson.autograder.client.widgets.ProblemView;

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
		treeDataService.fetchTreeData(courseId, new AsyncCallback<SortedMap<Assignment, List<Problem>>>() {
			@Override
			public void onFailure(Throwable caught) {
				LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestAssignmentProblemTreeDataAsync - onFailure"));
				Label errorLabel = new Label("Failed to load course assignments.");
				errorLabel.addStyleName("errorLabel");
				sideBarAndContent.add(errorLabel);
			}

			@Override
			public void onSuccess(SortedMap<Assignment, List<Problem>> treeData) {
				LOG.publish(new LogRecord(Level.INFO,
				        "AssignmentTreeViewModel#requestAssignmentProblemTreeDataAsync - onSuccess"));
				if (treeData.isEmpty()) {
					Label errorLabel = new Label("The instructor has not added any assignments to the course.");
					errorLabel.addStyleName("errorLabel");
					sideBarAndContent.add(errorLabel);
				} else {
					loadSideBar(treeData);
				}

			}
		});
		LOG.publish(new LogRecord(Level.INFO, "AssignmentTreeViewModel#requestAssignmentProblemTreeDataAsync - end"));
	}

	private void loadSideBar(SortedMap<Assignment, List<Problem>> treeData) {

		// Create a side bar for assignment selection.
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#loadSideBar - begin"));
		CellTree sideBar = new CellTree(new AssignmentTreeViewModel(treeData, new ProblemSelectionCallback()), null);
		sideBar.setAnimationEnabled(true);
		sideBar.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		// sideBar.ensureDebugId("sideBar"); // TODO what is debugId?
		sideBar.addStyleName("assignmentSideBar");
		sideBar.getRootTreeNode().setChildOpen(0, true);

		sideBarAndContent.add(sideBar);
	}

	public final class ProblemSelectionCallback {

		private ProblemSelectionCallback() {
		}

		public void requestSelectedProblemDataAsync(int problemId) {

			LOG.publish(new LogRecord(Level.INFO, "CoursePage.ProblemSelectionCallback#requestSelectedProblemDataAsync - begin"));

			SelectedProblemDataServiceAsync problemDataService = GWT.create(SelectedProblemDataServiceAsync.class);
			problemDataService.fetchProblemData(problemId, new AsyncCallback<ProblemData>() {
				@Override
				public void onFailure(Throwable caught) {
					LOG.publish(new LogRecord(Level.INFO,
					        "CoursePage.ProblemSelectionCallback#requestSelectedProblemDataAsync - onFailure"));
					Label errorLabel = new Label("Failed to load the selected problem.");
					errorLabel.addStyleName("errorLabel");
					sideBarAndContent.add(errorLabel);
				}

				@Override
				public void onSuccess(ProblemData data) {
					LOG.publish(new LogRecord(Level.INFO,
					        "CoursePage.ProblemSelectionCallback#requestSelectedProblemDataAsync - onSuccess"));
					loadProblemView(data);

				}
			});
			LOG.publish(new LogRecord(Level.INFO,
			        "CoursePage.ProblemSelectionCallback#requestSelectedProblemDataAsync - end"));
		}

	}

	private void loadProblemView(ProblemData data) {
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#loadProblemView - begin"));
		ProblemView problemView = new ProblemView(data);
		problemView.addStyleName("problemView");
		sideBarAndContent.add(problemView);

		Window.alert("You selected: " + data.getTitle() + ", aId=" + data.getaId() + ", pId=" + data.getpId());
	}
}
