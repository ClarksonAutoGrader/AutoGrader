package edu.clarkson.autograder.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jasig.cas.client.util.AssertionHolder;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.objects.Permutation;
import edu.clarkson.autograder.client.objects.Problem;
import edu.clarkson.autograder.client.objects.ProblemData;

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
	// TODO: remove debug
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

	SortedMap<Assignment, List<Problem>> queryAssignmentProblemTreeData(int courseId) {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryAssignmentProblemTreeData - begin: courseId=" + courseId));

		SortedMap<Assignment, List<Problem>> map = new TreeMap<Assignment, List<Problem>>();

		/*
			SELECT 
				a.assignment_id,
			    a.assignment_title,
			    a.due_date,
			    prob.problem_id,
			    prob.problem_title,
			    prob.points_possible,
			    uw.points
			FROM
			    users u
			        RIGHT JOIN
			    user_work uw ON u.u_id = uw.soln_uid
			        RIGHT JOIN
			    problems prob ON uw.soln_prob_id = prob.problem_id
			        RIGHT JOIN
			    assignments a ON prob.problem_aid = a.assignment_id
			WHERE
			    u.username = 'clappdj' AND a.a_cid = 1;
		 */
		final String SQL = "SELECT a.assignment_id, a.assignment_title, a.due_date, prob.problem_id, "
		        + "prob.problem_title, prob.points_possible, uw.points " + "FROM users u "
				+ "RIGHT JOIN user_work uw ON u.username = uw.soln_username "
		        + "RIGHT JOIN problems prob ON uw.soln_prob_id = prob.problem_id "
		        + "RIGHT JOIN assignments a ON prob.problem_aid = a.assignment_id " + "WHERE u.username = '"
				+ getUsername() + "' AND a.a_cid = " + courseId + " AND uw.soln_perm_id % 2 <> 0;";

		final ResultSet rs = query(SQL);
		try {
			if (!rs.next()) {
				return map;
			}
			rs.beforeFirst();

			// put resultSet into map
			Assignment assign = null;
			List<Problem> problemSet = new ArrayList<Problem>();
			int currentAssignId = -1;
			int previousAssignId = -1;
			while (rs.next()) {
				LOG.publish(new LogRecord(Level.INFO, "Database#queryAssignmentProblemTreeData - aId="
				        + rs.getString("a.assignment_id") + " pId=" + rs.getString("prob.problem_id")));
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
					problemSet.add(currentProb);
				}
			}
			map.put(assign, problemSet);

		} catch (NumberFormatException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryAssignmentProblemTreeData - course_id NaN"));
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryAssignmentProblemTreeData - SQLException " + exception));
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryAssignmentProblemTreeData - unexpected exception " + exception));
			throw new RuntimeException(exception);
		}

		closeConnection();
		LOG.publish(new LogRecord(Level.INFO, "Database#queryAssignmentProblemTreeData - end"));
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
		} catch (NumberFormatException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - course_id NaN"));
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - SQLException " + exception));
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - unexpected exception " + exception));
			throw new RuntimeException(exception);
		}

		closeConnection();
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - end"));
		return courseList;
	}

	ProblemData querySelectedProblemData(int problemId) {
		LOG.publish(new LogRecord(Level.INFO, "Database#querySelectedProblemData - begin"));

		ProblemData problemData = null;

		/*
			SELECT 
		    prob.problem_id,
		    prob.problem_aid,
		    prob.problem_title,
		    prob.points_possible,
		    prob.body,
		    uw.points,
		    perm.perm_id,
		    perm.input_1,
		    perm.input_2,
		    perm.input_3,
		    perm.input_4,
		    perm.input_5,
		    perm.input_6,
		    perm.input_7,
		    perm.input_8,
		    perm.input_9,
		    perm.input_10
		FROM
		    problems prob
		        RIGHT JOIN
		    user_work uw ON prob.problem_id = uw.soln_prob_id
				RIGHT JOIN
		    permutations perm ON perm.perm_id = uw.soln_perm_id
		WHERE
		    uw.soln_username = 'clappdj' AND prob.problem_id = 5
		        AND uw.soln_perm_id % 2 <> 0; //TODO don't do this
		*/
		final String SQL = "SELECT prob.problem_id, prob.problem_aid, prob.problem_title, prob.points_possible, prob.body, uw.points, "
		        + "perm.perm_id, perm.num_inputs, perm.num_answers, perm.input_1, perm.input_2, perm.input_3, perm.input_4, perm.input_5, perm.input_6, perm.input_7, perm.input_8, perm.input_9, perm.input_10 "
				+ "FROM problems prob RIGHT JOIN user_work uw ON prob.problem_id = uw.soln_prob_id RIGHT JOIN permutations perm ON perm.perm_id = uw.soln_perm_id "
		        + "WHERE uw.soln_username = '" + getUsername() + "' AND prob.problem_id = " + problemId + " AND uw.soln_perm_id % 2 <> 0;";
		try {
			// TODO process resultset
			ResultSet rs = query(SQL);

			// get first and only problem
			rs.next();

			// warn if resultset contains more than one entry
			if (rs.isAfterLast()) {
				LOG.publish(new LogRecord(Level.INFO,
				        "Database#querySelectedProblemData - Unexpected number of rows in ResultSet"));
			}

			Problem prob = new Problem(rs.getInt("prob.problem_id"), rs.getInt("prob.problem_aid"),
			        rs.getString("prob.problem_title"), rs.getDouble("prob.points_possible"),
			        rs.getDouble("uw.points"));
			
			String[] inputs = {
					rs.getString("perm.input_1"),
					rs.getString("perm.input_2"),
					rs.getString("perm.input_3"),
					rs.getString("perm.input_4"),
					rs.getString("perm.input_5"),
					rs.getString("perm.input_6"),
					rs.getString("perm.input_7"),
					rs.getString("perm.input_8"),
					rs.getString("perm.input_9"),
					rs.getString("perm.input_10")
			};
			
			Permutation permutation = new Permutation(rs.getInt("perm.perm_id"), rs.getInt("prob.problem_id"),
			        rs.getInt("perm.num_inputs"), rs.getInt("perm.num_answers"), inputs);

			problemData = new ProblemData(prob, rs.getString("prob.body"),
			        5 /* number of new questions (resets) available to user */,
			        3 /* number of attempts (submissions) available to user */,
			        permutation);
				
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#querySelectedProblemData - SQLException " + exception));
			throw new RuntimeException(exception);
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#querySelectedProblemData - unexpected exception " + exception));
			throw new RuntimeException(exception);
		}

		closeConnection();
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - end"));
		return problemData;
	}
}
