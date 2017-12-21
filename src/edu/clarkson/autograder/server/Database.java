package edu.clarkson.autograder.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.objects.Problem;

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
		// String username =
		// AssertionHolder.getAssertion().getPrincipal().getName();
		// LOG.publish(new LogRecord(Level.INFO, "Database#getUsername - user =
		// " + username));
		// if (username == null) {
		// throw new RuntimeException("Could not locate user: null");
		// }
		// return username.toLowerCase();
		return "clappdj";
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

	public static Map<Assignment, List<Problem>> queryAssignmentProblemTreeData(int courseId) {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryAssignmentProblemTreeData - begin: courseId=" + courseId));

		Map<Assignment, List<Problem>> map = new HashMap<Assignment, List<Problem>>();

		final String SQL = "SELECT a.assignment_id, a.assignment_title, a.due_date, prob.problem_id, "
		        + "prob.problem_title, prob.points_possible, uw.points " + "FROM assignments a "
		        + "RIGHT JOIN problems prob ON a.assignment_id = prob.problem_aid "
		        + "LEFT JOIN user_work uw ON uw.soln_prob_id = prob.problem_id " + "WHERE a.a_cid = " + courseId + ";";

		final ResultSet rs = query(SQL);
		try {

			if (!rs.next()) {
				return map;
			}

			int currentAssignId = -1;
			int previousAssignId = -1;

			Assignment assign = null;
			List<Problem> problems = new ArrayList<Problem>();

			// TODO put resultSet into map
			while (rs.next()) {
				currentAssignId = Integer.parseInt(rs.getString("a.assignment_id"));
				if (currentAssignId != previousAssignId) {

					if (!rs.isFirst()) {
						map.put(assign, problems);
					}

					assign = new Assignment(Integer.parseInt(rs.getString("a.assignment_id")), courseId, rs.getString("a.assignment_title"), rs.getDate("a.due_date"));
					previousAssignId = currentAssignId;
				}
				
				final Problem prob = new Problem(Integer.parseInt(rs.getString("prob.problem_id")), currentAssignId,
				        rs.getString("prob.problem_title"), Double.parseDouble(rs.getString("prob.points_possible")),
				        Double.parseDouble(rs.getString("uw.points")));
				problems.add(prob);
				
			}
		} catch (NumberFormatException e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - course_id NaN"));
		} catch (SQLException e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - " + e));
		} catch (Exception e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - " + e));
		}

		// map.put(new Assignment(asdfasdf), list of problems)

		closeConnection();
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - end"));
		return map;
	}

	public static List<Course> queryCourses() {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - begin"));

		List<Course> courseList = new ArrayList<Course>();

		final String SQL = "SELECT c.course_id, c.course_title " + "FROM enrollment e LEFT JOIN courses c "
		        + "ON e.enr_cid = c.course_id WHERE e.enr_username = \"" + getUsername() + "\";";
		ResultSet rs = query(SQL);
		try {
			while (rs.next()) {
				courseList.add(new Course(Integer.parseInt(rs.getString("course_id")), rs.getString("course_title")));
				LOG.publish(new LogRecord(Level.INFO, "Course: " + rs.getString("course_title")));
			}
		} catch (NumberFormatException e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - course_id NaN"));
		} catch (SQLException e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - " + e));
		}

		closeConnection();
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - end"));
		return courseList;
	}

	private static final ResultSet query(final String SQL) {
		final String LOG_LOCATION = "Database#query" + SQL.hashCode() + " ";
		LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "- begin " + SQL));

		conn = establishConnection();

		ResultSet resultSet = null;
		try {
			Statement stmt;
			stmt = conn.createStatement();
			LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "statement created"));
			resultSet = stmt.executeQuery(SQL);
			LOG.publish(new LogRecord(Level.INFO,
			        LOG_LOCATION + "RS returned " + (resultSet.next() ? "not empty" : "empty")));
			resultSet.beforeFirst();
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "failed to return RS " + exception));
		}

		LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "- end"));
		return resultSet;
	}

	private static void closeConnection() {
		try {
			if (!conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database failed to close connection " + exception));
		}
	}
}
