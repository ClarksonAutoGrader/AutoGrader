package edu.clarkson.autograder.server;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.services.CourseFromIdService;

@SuppressWarnings("serial")
public class CourseFromIdServiceImpl extends RemoteServiceServlet implements CourseFromIdService {
	
	private static ConsoleHandler LOG = new ConsoleHandler();
	
	@Override
	public Course fetchCourseFromId(int courseId) {
		
		LOG.publish(new LogRecord(Level.INFO, "#fetchCourseFromId - begin"));
		
		Database db = new Database();
		Course course = db.queryCourseFromId(courseId);

		if (course == null) {
			throw new RuntimeException("Course was null");
		}

		LOG.publish(new LogRecord(Level.INFO, "#fetchCourseFromId - end"));

		return course;
		
	}

}