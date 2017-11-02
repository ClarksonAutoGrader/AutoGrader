package edu.clarkson.autograder.client.pages;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
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
		AbstractImagePrototype proto = AbstractImagePrototype.create(Autograder.images.loading());
		loadingHtml = proto.getHTML();

		this.courseId = courseId;
		LOG.publish(new LogRecord(Level.INFO, "Attempt to create course page with coureId=" + courseId));

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

		// Problem content
		ProblemContent problemContent = new ProblemContent();
		
		// Assemble page layout
		DockPanel topLevel = new DockPanel();
		topLevel.add(sideBar, DockPanel.WEST);
		topLevel.add(pageTitle, DockPanel.NORTH);
		topLevel.add(problemContent, DockPanel.CENTER);

		// Add page to app
		// initWidget(uiBinder.createAndBindUi(this));
		initWidget(topLevel);
	}

	@Override
	public String getPrimaryStyleName() {
		return "coursePage";
	};
}
