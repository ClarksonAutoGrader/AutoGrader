package edu.clarkson.autograder.server;

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
	
	@Override
	public List<Course> fetchCourses() {
		
		LOG.publish(new LogRecord(Level.INFO, "#fetchCourses - begin"));
		
		Database db = new Database();
		List<Course> courses = db.queryCourses();

		LOG.publish(new LogRecord(Level.INFO, "#fetchCourses - end"));

		return courses;
		
	}

}
