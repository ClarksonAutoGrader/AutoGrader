package edu.clarkson.autograder.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.services.CoursesService;

@SuppressWarnings("serial")
public class CoursesServiceImpl extends RemoteServiceServlet implements CoursesService {
	
	private static ConsoleHandler LOG = new ConsoleHandler();
	
	private DatabaseQuery db;
	
	public List<Course> fetchCourses(){
		
		LOG.publish(new LogRecord(Level.INFO, "#fetchCourses"));
		
		db = new DatabaseQuery();
		return db.queryCourses();
		
//		final List<Course> courses = new ArrayList<>();
//		{
//			/*
//			 * Create courses
//			 */
//			courses.add(new Course(1, "ME310", "Thermodynamics - course description", true));
//			courses.add(new Course(2, "ES436", "Climate Change - course description", true));
//			courses.add(new Course(3, "AA100", "Generic Course - course description", true));
//			courses.add(new Course(4, "Course S", "Generic Course - course description", false));
//			courses.add(new Course(5, "Course C",
//			        "Generic Course - course description is especially long in this case."
//			                + " We just keep going and going and going and going and going and "
//			                + "This course description is especially long in this case.",
//			        true));
//		}
//		
//		
//		
//		return courses;
	}

}
