package edu.clarkson.autograder.client.pages;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SingleSelectionModel;

import edu.clarkson.autograder.client.AssignmentTreeViewModel;
import edu.clarkson.autograder.client.Autograder;
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

	/**
	 * The html used to show a loading icon.
	 */
	private final String loadingHtml;

	/**
	 * Attempt to create CoursePage with specified course ID. The course ID
	 * possibly does not exist, or the user cannot access it.
	 */
	public CoursePage(int courseId) {
		LOG.publish(new LogRecord(Level.INFO, "Attempt to create course page with coureId=" + courseId));
		this.courseId = courseId;

		AbstractImagePrototype proto = AbstractImagePrototype.create(Autograder.images.loading());
		loadingHtml = proto.getHTML();

		// Page title
		Label pageTitle = new Label(edu.clarkson.autograder.client.Autograder.tempDebugCourseNameSelected);
		pageTitle.addStyleName("coursePageHeader");

		// Create a side bar for assignment selection.
		final AssignmentTreeViewModel treeModel = new AssignmentTreeViewModel();
		
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#CoursePage - Post Creation of AssignmentTreeViewModel"));
		
		CellTree sideBar = new CellTree(treeModel, null);
		sideBar.setAnimationEnabled(true);
		sideBar.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		sideBar.ensureDebugId("sideBar"); // TODO what is debugId?
		sideBar.addStyleName("assignmentSideBar");
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#CoursePage - Before getRootTreeNode"));
		sideBar.getRootTreeNode().setChildOpen(0, true);
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#CoursePage - Post Creation of Cell Tree"));
		

		// Problem content
		ProblemContent problemContent = new ProblemContent();
		problemContent.addStyleName("problemContent");

		HorizontalPanel sideBarAndContent = new HorizontalPanel();
		sideBarAndContent.addStyleName("sideBarAndContent");
		sideBarAndContent.add(sideBar);
		sideBarAndContent.add(problemContent);
		VerticalPanel topLevel = new VerticalPanel();
		topLevel.add(pageTitle);
		topLevel.add(sideBarAndContent);

		// Add page to app
		initWidget(topLevel);
	}

	@Override
	public String getPrimaryStyleName() {
		return "coursePage";
	};
}
