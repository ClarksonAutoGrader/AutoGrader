package edu.clarkson.autograder.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jasig.cas.client.util.AssertionHolder;

import edu.clarkson.autograder.client.objects.Course;

public class Database {

	// Console logging for debugging
	private static ConsoleHandler LOG = new ConsoleHandler();

	// Connection for database
	private static Connection conn = null;

	// Database parameters
	private static final String url = "jdbc:mysql://autograder.clarkson.edu:3306/autograder_db";
	private static final String user = "autograder_dev";
	private static final String password = "292.2K16";

	private static String getUsername() {
		return AssertionHolder.getAssertion().getPrincipal().getName().toLowerCase();
	}

	private static Connection establishConnection() {
		LOG.publish(new LogRecord(Level.INFO, "Database#establishConn - begin"));

		conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// create connection to database
			conn = DriverManager.getConnection(url, user, password);
			LOG.publish(new LogRecord(Level.INFO, "#establishConnection: DB Connection Successful"));
		} catch (SQLException | ClassNotFoundException e) {
			LOG.publish(new LogRecord(Level.INFO, "#Database Failed: " + e.toString()));
		}

		LOG.publish(new LogRecord(Level.INFO, "Database#establishConn - end"));
		return conn;
	}

	public static List<Course> queryCourses() {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - begin"));

		conn = establishConnection();

		List<Course> courseList = new ArrayList<Course>();

		String sql = "SELECT c.course_id, c.course_title " + "FROM enrollment e LEFT JOIN courses c "
		        + "ON e.enr_cid = c.course_id WHERE e.enr_username = \"" + getUsername() + "\";";
		try {
			Statement stmt;
			stmt = conn.createStatement();
			LOG.publish(new LogRecord(Level.INFO, "#queryCourses: statement created"));
			ResultSet rs = stmt.executeQuery(sql);
			LOG.publish(new LogRecord(Level.INFO, "#queryCourses: RS returned"));

			while (rs.next()) {
				courseList.add(new Course(Integer.parseInt(rs.getString("course_id")), rs.getString("course_title")));
				LOG.publish(new LogRecord(Level.INFO, "Course: " + rs.getString("course_title")));
			}
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "#queryCourses: failed to return RS " + exception));
		}

		try {
			if (!conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "#queryCourses: failed to close connection " + exception));
		}

		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - end"));
		return courseList;

	}
}
