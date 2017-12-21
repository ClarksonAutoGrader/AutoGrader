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

import org.jasig.cas.client.util.AssertionHolder;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.objects.Problem;

/**
 * JDBC database class. Each instance of this class manages one active
 * transaction. <br>
 * <br>
 * The client should implement specific query methods which call
 * {@link Database#query(String)} to obtain a {@link java.sql.ResultSet},
 * process the ResultSet, and finally {@link Database#closeConnection()}.
 */
public class Database {

	// Console logging for debugging
	private static ConsoleHandler LOG = new ConsoleHandler();

	// private static final String DEFAULT_USERNAME = "null";
	private static final String DEFAULT_USERNAME = "clappdj";

	// Database parameters
	private static final String url = "jdbc:mysql://autograder.clarkson.edu:3306/autograder_db";
	private static final String user = "autograder_dev";
	private static final String password = "292.2K16";

	// Instanced database connection
	private Connection conn;

	/**
	 * Returns user's username (lowercase) from the authentication server, or
	 * DEFAULT_USERNAME if the authentication server cannot provide one.
	 */
	static String getUsername() {
		String username = null;
		try {
			username = AssertionHolder.getAssertion().getPrincipal().getName().toLowerCase();
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO,
			        "Database#getUsername - failed to retrieve username from CAS: " + exception));

			username = DEFAULT_USERNAME;
		}
		LOG.publish(new LogRecord(Level.INFO, "Database#getUsername - user=" + username));
		return username;
	}

	/**
	 * Create a new instance. This object allows for one instanced connection
	 * and the constructor automatically establishes a connection.
	 */
	Database() {
		establishConnection();
	}

	/**
	 * Ensure an existing connection is still valid or create a new one. Logs
	 * any errors.
	 */
	private void establishConnection() {
		LOG.publish(new LogRecord(Level.INFO, "Database#establishConn - begin"));

		try {
			if (conn != null && !conn.isClosed()) {
				LOG.publish(new LogRecord(Level.INFO, "Database#establishConn - connection already active"));
			}
		} catch (SQLException e1) {
			LOG.publish(new LogRecord(Level.INFO, "Database#establishConn - existing connection corrupt"));
		}

		LOG.publish(new LogRecord(Level.INFO, "Database#establishConn - attempting to connect"));

		try {
			Class.forName("com.mysql.jdbc.Driver");
			// create connection to database
			conn = DriverManager.getConnection(url, user, password);
			LOG.publish(new LogRecord(Level.INFO, "Database#establishConnection - DB Connection Successful"));
		} catch (CommunicationsException e) {
			LOG.publish(new LogRecord(Level.INFO,
			        "Database#establishConnection Failed (do you need a VPN?)- " + e.toString()));
		} catch (SQLException | ClassNotFoundException e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#establishConnection Failed - " + e.toString()));
		}

		LOG.publish(new LogRecord(Level.INFO, "Database#establishConn - end"));
	}

	/**
	 * Uses an existing connection to query the database.
	 * 
	 * @param SQL
	 * @return ResultSet
	 */
	private final ResultSet query(final String SQL) {
		// uniquely identify separate calls to Database#query in the log.
		final String LOG_LOCATION = "Database#query" + SQL.hashCode() + " ";
		LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "- begin " + SQL));

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
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "unexpected exception " + exception));
		}

		LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "- end"));
		return resultSet;
	}

	private void closeConnection() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database failed to close connection " + exception));
		}
	}

	Map<Assignment, List<Problem>> queryAssignmentProblemTreeData(int courseId) {
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
			// put resultSet into map
			Assignment assign = null;
			List<Problem> problemSet = new ArrayList<Problem>();
			int currentAssignId = -1;
			int previousAssignId = -1;
			while (rs.next()) {
				currentAssignId = Integer.parseInt(rs.getString("a.assignment_id"));
				final Problem currentProb = new Problem(Integer.parseInt(rs.getString("prob.problem_id")),
				        currentAssignId, rs.getString("prob.problem_title"),
				        Double.parseDouble(rs.getString("prob.points_possible")),
				        Double.parseDouble(rs.getString("uw.points")));

				if (currentAssignId == previousAssignId) {
					// add problem to the problem set for the assignment currently being processed
					problemSet.add(currentProb);

				} else {
					// this must be a new assignment-problem set

					// commit previous set to map, unless its the first assignment processed
					if (!rs.isFirst()) {
						map.put(assign, problemSet);
					}

					// create the new assignment
					assign = new Assignment(Integer.parseInt(rs.getString("a.assignment_id")), courseId,
					        rs.getString("a.assignment_title"), rs.getDate("a.due_date"));
					previousAssignId = currentAssignId;

					// create the new problemSet
					problemSet = new ArrayList<Problem>();
				}
			}
		} catch (NumberFormatException e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - course_id NaN"));
		} catch (SQLException e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - " + e));
		} catch (Exception e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - " + e));
		}

		closeConnection();
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - end"));
		return map;
	}

	List<Course> queryCourses() {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - begin"));

		List<Course> courseList = new ArrayList<Course>();

		final String SQL = "SELECT c.course_id, c.course_title " + "FROM enrollment e LEFT JOIN courses c "
		        + "ON e.enr_cid = c.course_id WHERE e.enr_username = \"" + getUsername() + "\";";
		try {
			ResultSet rs = query(SQL);
			while (rs.next()) {
				courseList.add(new Course(Integer.parseInt(rs.getString("course_id")), rs.getString("course_title")));
				LOG.publish(new LogRecord(Level.INFO, "Course: " + rs.getString("course_title")));
			}
		} catch (NumberFormatException e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - course_id NaN"));
		} catch (SQLException e) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - " + e));
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - unexpected exception " + exception));
		}

		closeConnection();
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - end"));
		return courseList;
	}
}
