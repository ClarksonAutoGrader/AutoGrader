package edu.clarkson.autograder.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.pages.CoursePage;
import edu.clarkson.autograder.client.pages.CourseSelectionPage;

public final class State implements ValueChangeHandler<String> {

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
            courseId = Integer.parseInt(historyToken.substring(0, Data.ID_TOKEN_WIDTH));
        } catch (NumberFormatException e) {
            History.replaceItem("courses");
            return;
        }
        Course course = Data.getCourseFor(courseId);
        if (course == null) {
            // no such course exists
            History.replaceItem("courses");
            return;
        }

        // check for assignment token
        int selectedAssignmentId = -1;
        if (historyToken.length() > Data.ID_TOKEN_WIDTH) {
            try {
                selectedAssignmentId = Integer
                        .parseInt(historyToken.substring(Data.ID_TOKEN_WIDTH, 2 * Data.ID_TOKEN_WIDTH));
            } catch (NumberFormatException e) {
                // invalid assignment token
                History.replaceItem(course.getToken());
                return;
            }
        }
        // let CoursePage determine the default assignment selection
        ContentContainer.setContent(new CoursePage(course, selectedAssignmentId));
    }
}
