package edu.clarkson.autograder.client.pages;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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

		// Side-bar assignment selection
		VerticalPanel sideBar = new VerticalPanel();
		for (int i = 0; i < 10; ++i) {
			sideBar.add(new Label("SideBar content line=" + (i + 1)));
		}
		sideBar.getElement().getStyle().setFontSize(50, Unit.PX);
		sideBar.getElement().getStyle().setBackgroundColor("#6F6");		
		
		// Assemble page layout
		DockPanel topLevel = new DockPanel();
		topLevel.add(sideBar, DockPanel.WEST);
		topLevel.add(pageTitle, DockPanel.NORTH);
		topLevel.add(new ProblemContent(), DockPanel.CENTER);

		// Add page to app
		// initWidget(uiBinder.createAndBindUi(this));
		initWidget(topLevel);
	}

	@Override
	public String getPrimaryStyleName() {
		return "coursePage";
	};
}
