package edu.clarkson.autograder.client;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.jasig.cas.client.util.AssertionHolder;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Course;

/**
 * Temporary hard-coded database for autograder
 *
 */
public class Data {

	public static final int ID_TOKEN_WIDTH = 6;
	
	public static String formatIdToken(int id) {
		// Attempts to provide identical functionality as:
		// String.format("%0" + ID_TOKEN_WIDTH + "d", id);
		int unpadded_length = ("" + id).length();
		String padding = "";
		for (int i = 0; i < ID_TOKEN_WIDTH - unpadded_length; ++i)
			padding += "0";
		return padding + id;
	}

	/**
	 * List of courses accessible by user
	 * 
	 * <li>ME310
	 * <li>ES436
	 * <li>AA100
	 * <li>Course A <i>(invisible)</i>
	 * <li>Course B
	 */
	private static final List<Course> courses = new ArrayList<>();
	static {
		/*
		 * Create courses
		 */
		courses.add(new Course(1, "ME310", "Thermodynamics - course description", true));
		courses.add(new Course(2, "ES436", "Climate Change - course description", true));
		courses.add(new Course(3, "AA100", "Generic Course - course description", true));
		courses.add(new Course(4, "Course A", "Generic Course - course description", false));
		courses.add(new Course(5, "Course B",
		        "Generic Course - course description is especially long in this case."
		                + " We just keep going and going and going and going and going and "
		                + "This course description is especially long in this case.",
		        true));
	}

	/**
	 * List of accessible assignments
	 */
	private static final List<Assignment> assignments = new ArrayList<>();
	static {
		/*
		 * Create assignments for every course
		 */
		int number_of_assignments = 12;
		int num_current_assignments = 0;
		for (Course c : courses) {
			for (int a_num = 0; a_num < number_of_assignments; a_num++) {
				// open date 1498795200000 is 06/30/2017 04:00 AM GMT
				// One day increment: 86400000
				// close date is between -10 and +2 days from current date
				assignments.add(new Assignment(a_num, c.getId(), true, "Assignment Title " + (a_num + 1),
				        new Date(1498795200000L),
				        new Date(120000 + (86400000L * (c.getId() - 2)) + (new Date()).getTime()
				                - (86400000L * (number_of_assignments - num_current_assignments - a_num)))));
			}
		}
	}

	/*
	 * 
	 * 
	 * Access functions
	 * 
	 * 
	 */

	public static List<Course> getCourses() {
		return courses;
	}

	/**
	 * 
	 * Return Course data associated with the course ID or null if course does
	 * not exist.
	 */
	public static Course getCourseFor(int courseId) {
		for (Course course : courses) {
			if (course.getId() == courseId) {
				return course;
			}
		}
		return null;
	}

	public static List<Assignment> getAssignmentsFor(int courseId) {
		List<Assignment> sublist = new ArrayList<>();
		for (Assignment a : assignments) {
			if (a.getcId() == courseId) {
				sublist.add(a);
			}
		}
		return sublist;
	}
}
