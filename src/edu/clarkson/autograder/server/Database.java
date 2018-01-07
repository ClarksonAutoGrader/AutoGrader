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

import org.apache.commons.dbutils.DbUtils;
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
 * {@link Database#evaluate(String)} to obtain a {@link java.sql.ResultSet},
 * process the ResultSet, and finally {@link Database#closeConnection()}.
 */
public class Database {

	// Console logging for debugging
	private static ConsoleHandler LOG = new ConsoleHandler();

	private static final String DEFAULT_USERNAME = "woodrj";
	
	// Database parameters
	private static final String url = "jdbc:mysql://autograder.clarkson.edu:3306/autograder_db";
	private static final String user = "autograder_dev";
	private static final String password = "292.2K16";

	// Instanced database connection
	private Connection conn;
	private Statement stmt;

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
		//return username;
		return "woodrj";
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
	private final ResultSet evaluate(final String SQL) {
		// uniquely identify separate calls to Database#query in the log.
		final String LOG_LOCATION = "Database#evaluate" + SQL.hashCode() + " ";
		LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "- begin " + SQL));

		ResultSet resultSet = null;
		try {
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

	private void closeConnection(ResultSet rs) {
		
		DbUtils.closeQuietly(rs);
		DbUtils.closeQuietly(stmt);
		DbUtils.closeQuietly(conn);
		
		LOG.publish(new LogRecord(Level.INFO, "ResultSet, Statement, Connection closed"));

	}

	/*
	 * SQL queries
	 */

	/**
	 * Returns data needed to create
	 * {@link edu.clarkson.autograder.client.objects.ProblemData} object.<br>
	 * <br>
	 * Required inputs are username and problem ID (in that order).
	 */
	final static String selectedProblemDataSql = "SELECT prob.problem_id, prob.problem_aid, prob.problem_title, prob.points_possible, "
	        + "prob.num_check_allowed - COALESCE(uw.num_check_used, 0) AS 'checks_remaining', "
	        + "prob.num_new_questions_allowed - COALESCE(uw.num_new_questions_used, 0) AS 'questions_remaining', "
	        + "b.body_text, COALESCE(uw.points, 0) AS 'uw.points', COALESCE(uw.soln_perm_id, perm.perm_id) AS 'perm.perm_id', perm.num_inputs, perm.num_answers, perm.input_1, perm.input_2, "
	        + "perm.input_3, perm.input_4, perm.input_5, perm.input_6, perm.input_7, perm.input_8, perm.input_9, perm.input_10, perm.answer_1, perm.answer_2, perm.answer_3, perm.answer_4, "
	        + "perm.answer_5, perm.answer_6, perm.answer_7, perm.answer_8, perm.answer_9, perm.answer_10, uw.user_answer_1, uw.user_answer_2, uw.user_answer_3, uw.user_answer_4, "
	        + "uw.user_answer_5, uw.user_answer_6, uw.user_answer_7, uw.user_answer_8, uw.user_answer_9, uw.user_answer_10 "
	        + "FROM permutations perm, body b, problems prob LEFT JOIN user_work uw ON prob.problem_id = uw.soln_prob_id "
	        + "WHERE b.body_prob_id = prob.problem_id AND perm.perm_prob_id = prob.problem_id AND "
	        + "IF((uw.soln_username = '%s' OR uw.soln_username IS NULL), TRUE, FALSE) AND prob.problem_id = %s "
	        + "AND IF((uw.soln_prob_id IS NULL), TRUE, uw.soln_perm_id = perm.perm_id) "
	        + "ORDER BY IF((uw.soln_perm_id IS NOT NULL), uw.soln_perm_id, RAND()) LIMIT 1;";
	
	/**
	 * Returns the user role as String given a username. 
	 */
	final static String userRoleSql = "SELECT user_role FROM users WHERE username = '%s';";

	/**
	 * Returns previous answers as a
	 * {@link edu.clarkson.autograder.client.objects.PreviousAnswersRow}
	 * object.<br>
	 * <br>
	 * Required inputs are answer number, username and permutation ID (in that
	 * order).
	 */
	final static String previousAnswersSql = "SELECT prev_ans_%s FROM previous_answers WHERE prev_ans_username = '%s' AND prev_ans_perm_id = %s;";
	
	/**
	 * Returns data needed to create
	 * {@link edu.clarkson.autograder.client.objects.GradebookData} object. <br>
	 * <br>
	 * Required input is the course ID.
	 */
	final static String gradebookDataSql = "SELECT c.course_title, e.enr_username, a.assignment_title, SUM(COALESCE(uw.points, 0)) AS 'uw.points', "
		    + "SUM(COALESCE(prob.points_possible, 0)) AS 'prob.points_possible' FROM enrollment e RIGHT JOIN courses c ON e.enr_cid = c.course_id "
		    + "RIGHT JOIN assignments a ON c.course_id = a.a_cid LEFT JOIN problems prob ON prob.problem_aid = a.assignment_id "
		    + "LEFT JOIN user_work uw ON prob.problem_id = uw.soln_prob_id AND e.enr_username = uw.soln_username "
		    + "WHERE c.course_id = %s GROUP BY e.enr_username, a.assignment_id;";

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
			    COALESCE(uw.points, 0) AS uw.points
			FROM
			    assignments a,
			    problems prob
			        LEFT JOIN
			    user_work uw ON uw.soln_prob_id = prob.problem_id
			WHERE
			    a.assignment_id = prob.problem_aid
			        AND a.a_cid = 1 AND IF((uw.soln_username = 'murphycd' OR uw.soln_username IS NULL), TRUE, FALSE) ORDER BY a.due_date, prob.problem_num;
		 */
		final String SQL = "SELECT a.assignment_id, a.assignment_title, a.due_date, prob.problem_id, prob.problem_title, prob.points_possible, COALESCE(uw.points, 0) AS 'uw.points' "
				+ "FROM assignments a, problems prob LEFT JOIN user_work uw ON uw.soln_prob_id = prob.problem_id "
				+ "WHERE a.assignment_id = prob.problem_aid AND a.a_cid = " + courseId + " AND IF((uw.soln_username = '" + getUsername()
		        + "' OR uw.soln_username IS NULL), TRUE, FALSE) ORDER BY a.due_date, prob.problem_num;";
				
		final ResultSet rs = evaluate(SQL);
		try {
			if (!rs.next()) {
				return map;
			}
			rs.beforeFirst();

			// put resultSet into map
			Assignment assign = null;
			double assignmentPointsPossible = 0;
			double assignmentPointsEarned = 0;
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
					assignmentPointsPossible += currentProb.getTotalPoints();
					assignmentPointsEarned += currentProb.getEarnedPoints();

				} else {
					// this must be a new assignment-problem set

					// commit previous set to map, unless its the first assignment processed
					if (!rs.isFirst()) {
						// create the new assignment
						assign = new Assignment(Integer.parseInt(rs.getString("a.assignment_id")), courseId,
						        rs.getString("a.assignment_title"), rs.getDate("a.due_date"), assignmentPointsEarned, assignmentPointsPossible);
						map.put(assign, problemSet);
					}
					
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

		closeConnection(rs);
		LOG.publish(new LogRecord(Level.INFO, "Database#queryAssignmentProblemTreeData - end"));
		return map;
	}

	List<Course> queryCourses() {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - begin"));

		List<Course> courseList = new ArrayList<Course>();

		final String SQL = "SELECT c.course_id, c.course_title " + "FROM enrollment e LEFT JOIN courses c "
		        + "ON e.enr_cid = c.course_id WHERE e.enr_username = \"" + getUsername() + "\";";
		ResultSet rs = null;
		try {
			rs = evaluate(SQL);
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

		closeConnection(rs);
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - end"));
		return courseList;
	}

	Course queryCourseFromId(int courseId) {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourseFromId - begin"));

		Course course = null;

		final String SQL = "SELECT c.course_id, c.course_title " + "FROM enrollment e LEFT JOIN courses c "
		        + "ON e.enr_cid = c.course_id WHERE e.enr_username = \"" + getUsername() + "\" AND c.course_id = "
		        + courseId + " LIMIT 1;";
		ResultSet rs;
		
		try {
			rs = evaluate(SQL);
			rs.next();
			course = new Course(rs.getInt("course_id"), rs.getString("course_title"));
			LOG.publish(new LogRecord(Level.INFO, "Course: " + rs.getString("course_title")));
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourseFromId - SQLException " + exception));
			throw new RuntimeException(exception);
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourseFromId - unexpected exception " + exception));
			throw new RuntimeException(exception);
		}

		closeConnection(rs);
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourseFromId - end"));
		return course;
	}

	<T> T query(final ProcessResultSetCallback<T> callback, final String parameterizedSql,
	        final Object... sqlParameters) {
		LOG.publish(new LogRecord(Level.INFO, "Database#query - begin"));

		final String SQL = String.format(parameterizedSql, sqlParameters);
		LOG.publish(new LogRecord(Level.INFO, "Database#query - " + parameterizedSql));
		LOG.publish(new LogRecord(Level.INFO, "Database#query - " + SQL));
		ResultSet rs = evaluate(SQL);
		T data = null;
		try {
			data = callback.process(rs);
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#query - SQLException " + exception));
			throw new RuntimeException(exception);
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#query - unexpected exception " + exception));
			throw new RuntimeException(exception);
		}

		closeConnection(rs);
		LOG.publish(new LogRecord(Level.INFO, "Database#query - end"));
		return data;
	}

	String[] queryAnswers(int permutationId) {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryAnswers - begin"));

		String[] answers = new String[10];

		final String SQL = "SELECT answer_1, answer_2, answer_3, answer_4, answer_5, answer_6, answer_7, answer_8, answer_9, answer_10 "
		        + "FROM permutations WHERE perm_id = " + permutationId + ";";
		final ResultSet rs = evaluate(SQL);
		try {
			rs.next();
			
			for (int col = 0; col < 10; ++col) {
				answers[col] = rs.getString("answer_" + (col + 1));
			}

		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryAnswers - SQLException " + exception));
			throw new RuntimeException(exception);
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#queryAnswers - unexpected exception " + exception));
			throw new RuntimeException(exception);
		}

		closeConnection(rs);
		LOG.publish(new LogRecord(Level.INFO, "Database#queryAnswers - end"));
		return answers;
	}
}
