package edu.clarkson.autograder.client;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.pages.CoursePage;
import edu.clarkson.autograder.client.pages.CourseSelectionPage;
import edu.clarkson.autograder.client.pages.GradebookPage;
import edu.clarkson.autograder.client.services.UserRoleService;
import edu.clarkson.autograder.client.services.UserRoleServiceAsync;

public final class State implements ValueChangeHandler<String> {

	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	private static final State instance = new State();

	private static Course course;

	private State() {
	}

	public static State getInstance() {
		return instance;
	}

	public static Course getCourse() {
		return course;
	}

	public static void setCourse(Course course) {
		State.course = course;
	}

	private void loadDefault() {
		ContentContainer.setContent(new CourseSelectionPage());
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// TODO clean history of duplicate tokens and do not process
		String historyToken = event.getValue();

		// Check for: course selection page
		if (historyToken.equals("courses")) {
			loadDefault();
			return;
		}

		// Check for: specific course page
		int courseId = -1;
		try {
			courseId = Integer.parseInt(historyToken.substring(0, Autograder.ID_TOKEN_WIDTH));
		} catch (NumberFormatException e) {
			History.replaceItem("courses");
			return;
		}

		// Choose course page based on user role
		requestUserRoleService(courseId);

	}

	private void requestUserRoleService(final int courseId) {
		UserRoleServiceAsync userRoleService = GWT.create(UserRoleService.class);
		userRoleService.fetchUserRole(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				LOG.publish(new LogRecord(Level.INFO, "requestUserRoleService#onFailure"));
			}

			@Override
			public void onSuccess(String role) {
				LOG.publish(new LogRecord(Level.INFO, "requestUserRoleService#onSuccess - role=" + role));
				if (role.equals("instructor")) {
					ContentContainer.setContent(new GradebookPage(courseId));
				} else if (role.equals("student")) {
					ContentContainer.setContent(new CoursePage(courseId));
				} else if (role.equals("developer")) {
					// developer preference :)
					boolean choice = Window.confirm("Welcome, admin. Press \"OK\" to proceed to student view, \"CANCEL\" to load the instructor view instead.");
					if (choice) {
						ContentContainer.setContent(new CoursePage(courseId));
					} else {
						ContentContainer.setContent(new GradebookPage(courseId));
					}
				}
			}
		});
	}
}
