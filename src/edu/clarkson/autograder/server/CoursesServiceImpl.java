package edu.clarkson.autograder.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.services.CoursesService;

@SuppressWarnings("serial")
public class CoursesServiceImpl extends RemoteServiceServlet implements CoursesService {

	public List<Course> fetchCourses() {

		final List<Course> courses = new ArrayList<>();
		{
			/*
			 * Create courses
			 */
			courses.add(new Course(1, "ME310 Thermodynamics", false));
			courses.add(new Course(2, "ES436 - Climate Change", true));
			courses.add(new Course(3, "AA200 - Generic Course", true));
			courses.add(new Course(4, "Course S - Generic Course", false));
			courses.add(new Course(5,
			        "Course C - Generic Course - course description is especially long in this case."
			                + " We just keep going and going and going and going and going and "
			                + "This course description is especially long in this case.",
			        false));
		}

		List<Course> result = new ArrayList<>();
		for (Course course : courses) {
			if (course.isVisible()) {
				result.add(course);
			}
		}
		// return result;
		return courses;
	}

}
