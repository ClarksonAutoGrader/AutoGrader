package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	@Override
	public List<Course> fetchCourses() {
		
		LOG.publish(new LogRecord(Level.INFO, "#fetchCourses - begin"));
		
		Database db = new Database();
		List<Course> courses = db.query(processResultSetCallback, Database.selectCoursesSql, ServerUtils.getUsername());
		db.closeConnection();
		LOG.publish(new LogRecord(Level.INFO, "#fetchCourses - end"));

		return courses;
		
	}

	private ProcessResultSetCallback<List<Course>> processResultSetCallback = new ProcessResultSetCallback<List<Course>>() {

		@Override
		public List<Course> process(ResultSet rs) throws SQLException {

			LOG.publish(new LogRecord(Level.INFO, "CoursesServiceImpl#process - begin"));

			List<Course> courseList = new ArrayList<Course>();

			while (rs.next()) {
				courseList
						.add(new Course(Integer.parseInt(rs.getString("course_id")), rs.getString("course_title")));
				LOG.publish(new LogRecord(Level.INFO, "Course: " + rs.getString("course_title")));
			}

			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - end"));
			return courseList;
		}
	};

}
