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
		Label pageTitle = new Label("Course Title (ID = " + courseId + ")");
		pageTitle.getElement().getStyle().setFontSize(50, Unit.PX);
		pageTitle.getElement().getStyle().setBackgroundColor("#3CF");

		// Create a side bar for assignment selection.
		final SingleSelectionModel<ProblemContent> selectionModel = new SingleSelectionModel<ProblemContent>();
		final AssignmentTreeViewModel treeModel = new AssignmentTreeViewModel(selectionModel);
		CellTree sideBar = new CellTree(treeModel, null);
		sideBar.setAnimationEnabled(true);
		sideBar.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		sideBar.ensureDebugId("sideBar"); // TODO what is debugId?
		LOG.publish(new LogRecord(Level.INFO, "Ur style nom is " + sideBar.getStylePrimaryName()));
		sideBar.addStyleDependentName("assignmentSideBar");

		// Problem content
		String content = "";
		for(int i=0; i<500; ++i) content += "Problem ";
		Label problemContent = new Label(content);
		problemContent.getElement().getStyle().setFontSize(15, Unit.PX);
		problemContent.getElement().getStyle().setPadding(1.0, Unit.EM);
		problemContent.addStyleName("problemContent");
		
		// Assemble page layout
		VerticalPanel titleAndContent = new VerticalPanel();
		titleAndContent.addStyleName("assignmentTitleAndContent");
		titleAndContent.add(pageTitle);
		titleAndContent.add(problemContent);
		HorizontalPanel topLevel = new HorizontalPanel();
		topLevel.add(sideBar);
		topLevel.add(titleAndContent);

		// Add page to app
		initWidget(topLevel);
	}

	@Override
	public String getPrimaryStyleName() {
		return "coursePage";
	};
}
