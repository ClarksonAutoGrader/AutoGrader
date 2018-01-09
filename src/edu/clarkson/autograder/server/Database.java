package edu.clarkson.autograder.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.commons.dbutils.DbUtils;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.objects.Problem;

/**
 * JDBC database class. Each instance of this class manages one active
 * transaction. <br>
 * <br>
 * The client should implement specific query methods which call
 * {@link Database#executeQuery(String)} to obtain a {@link java.sql.ResultSet},
 * process the ResultSet, and finally {@link Database#closeConnection()}.
 */
public class Database {

	// Console logging for debugging
	private static ConsoleHandler LOG = new ConsoleHandler();

	// Database parameters
	private static final String url = "jdbc:mysql://autograder.clarkson.edu:3306/autograder_db";
	private static final String user = "autograder_dev";
	private static final String password = "292.2K16";

	// Instanced database connection
	private Connection conn;
	private Statement stmt;

	/**
	 * Create a new instance. This object allows for one instanced connection
	 * and the constructor automatically establishes a connection. Use
	 * {@link #query(String)} or {@link #update(String)} and finally end by
	 * calling {@link #closeConnection()}.
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
	 * Uses an existing connection to query the database. Wrapper for
	 * {@link java.sql.Statement#executeUpdate(String)}.
	 */
	private final int executeUpdate(final String SQL) {
		// uniquely identify separate calls to Database#query in the log.
		final String LOG_LOCATION = "Database#evaluate" + SQL.hashCode() + " ";
		LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "- begin: " + SQL));

		int rowsUpdated = 0;
		try {
			stmt = conn.createStatement();
			LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "statement created"));

			rowsUpdated = stmt.executeUpdate(SQL);

			LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "updated " + rowsUpdated + " rows"));
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + " " + exception));
			throw new RuntimeException(exception);
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "unexpected exception " + exception));
			throw new RuntimeException(exception);
		}

		LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "- end"));
		return rowsUpdated;
	}

	/**
	 * Uses an existing connection to query the database. Wrapper for
	 * {@link java.sql.Statement#executeQuery(String)}.
	 * 
	 * @param SQL
	 * @return ResultSet
	 */
	private final ResultSet executeQuery(final String SQL) {
		// uniquely identify separate calls to Database#query in the log.
		final String LOG_LOCATION = "Database#executeQuery" + SQL.hashCode() + " ";
		LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "- begin: " + SQL));

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
			throw new RuntimeException(exception);
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "unexpected exception " + exception));
			throw new RuntimeException(exception);
		}

		LOG.publish(new LogRecord(Level.INFO, LOG_LOCATION + "- end"));
		return resultSet;
	}

	/*
	 * SQL queries
	 */

	/**
	 * Returns the number of resets remaining for a user and permutation ID.
	 */
	final static String selectResetsRemaining = "SELECT prob.num_new_questions_allowed - COALESCE(uw.num_new_questions_used, 0) AS 'num_new_questions_remaining' "
	        + "FROM problems prob LEFT JOIN user_work uw ON IF(uw.soln_username = '%s' AND prob.problem_id = uw.soln_prob_id, TRUE, FALSE) "
	        + "WHERE prob.problem_id = %s;";

	/**
	 * Returns data needed to create
	 * {@link edu.clarkson.autograder.client.objects.ProblemData} object.<br>
	 * <br>
	 * Required inputs are username and problem ID (in that order).
	 */
	final static String selectProblemDataSql = "SELECT prob.problem_id, prob.problem_aid, prob.problem_title, prob.points_possible, "
	        + "prob.num_check_allowed, COALESCE(uw.num_check_used, 0) AS 'uw.num_check_used', "
	        + "prob.num_new_questions_allowed, COALESCE(uw.num_new_questions_used, %s) AS 'uw.num_new_questions_used', "
	        + "b.body_text, COALESCE(uw.points, 0) AS 'uw.points', COALESCE(uw.soln_perm_id, perm.perm_id) AS 'perm.perm_id', perm.perm_prob_id, perm.num_inputs, perm.num_answers, perm.input_1, perm.input_2, "
	        + "perm.input_3, perm.input_4, perm.input_5, perm.input_6, perm.input_7, perm.input_8, perm.input_9, perm.input_10, perm.answer_1, perm.answer_2, perm.answer_3, perm.answer_4, "
	        + "perm.answer_5, perm.answer_6, perm.answer_7, perm.answer_8, perm.answer_9, perm.answer_10, COALESCE(uw.soln_id, 0) AS 'uw.soln_id', uw.soln_prob_id, uw.soln_perm_id, uw.user_answer_1, uw.user_answer_2, uw.user_answer_3, uw.user_answer_4, "
	        + "uw.user_answer_5, uw.user_answer_6, uw.user_answer_7, uw.user_answer_8, uw.user_answer_9, uw.user_answer_10 "
			+ "FROM permutations perm, body b, problems prob LEFT JOIN user_work uw ON IF(uw.soln_username = '%s' AND prob.problem_id = uw.soln_prob_id, TRUE, FALSE) "
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

	/**
	 * Update user_work table
	 */
	final static String updateUserWorkPointsEarned = "UPDATE user_work SET points = %s WHERE soln_username = '%s' AND soln_perm_id = %s;";

	/**
	 * Requires username and permutation ID
	 */
	final static String deleteUserWorkRecord = "DELETE FROM user_work WHERE soln_username = '%s' AND soln_perm_id = %s;";

	/**
	 * Inserts user work record for submit action. Requires parameters for all
	 * columns being inserted
	 */
	final static String insertSubmittedUserWork = "INSERT INTO user_work (soln_id, soln_prob_id, "
	        + "soln_username, soln_perm_id, num_new_questions_used, num_check_used, "
	        + "user_answer_1, user_answer_2, user_answer_3, user_answer_4, user_answer_5, "
	        + "user_answer_6, user_answer_7, user_answer_8, user_answer_9, user_answer_10) "
	        + "VALUES (%s, %s, '%s', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) "
	        + "ON DUPLICATE KEY UPDATE soln_prob_id = %s, soln_username = '%s', soln_perm_id = %s, "
	        + "num_new_questions_used = %s, num_check_used = %s, user_answer_1 = %s, "
	        + "user_answer_2 = %s, user_answer_3 = %s, user_answer_4 = %s, "
	        + "user_answer_5 = %s, user_answer_6 = %s, user_answer_7 = %s, "
	        + "user_answer_8 = %s, user_answer_9 = null, user_answer_10 = null;";

	/**
	 * Inserts user work record upon loading initial problem data. Requires
	 * parameters for all columns being inserted
	 */
	final static String insertInitialUserWork = "INSERT INTO user_work (soln_prob_id, "
	        + "soln_username, soln_perm_id, num_new_questions_used, num_check_used, "
	        + "user_answer_1, user_answer_2, user_answer_3, user_answer_4, user_answer_5, "
	        + "user_answer_6, user_answer_7, user_answer_8, user_answer_9, user_answer_10) "
	        + "VALUES (%s, '%s', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";

	/*
	 * Methods for package use
	 */

	/**
	 * Typically used for INSERT, UPDATE, or DELETE statements. This is a
	 * wrapper for {@link java.sql.Statement.executeUpdate(String)} but with
	 * parameterized SQL statement. Parameterization is handled by
	 * String#format(String,Object...).
	 * 
	 * @param parameterizedSql
	 * @param sqlParameters
	 * @return number of rows updated
	 */
	int update(final String parameterizedSql, final Object... sqlParameters) {
		LOG.publish(new LogRecord(Level.INFO, "Database#update - begin"));

		int rowsUpdated = -1;
		try {
			final String SQL = String.format(parameterizedSql, sqlParameters);
			rowsUpdated = executeUpdate(SQL);
		} catch (IllegalFormatException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#update - failed to format query " + exception));
			throw new RuntimeException(exception);
		}

		DbUtils.closeQuietly(stmt);

		LOG.publish(new LogRecord(Level.INFO, "Database#update - end"));
		return rowsUpdated;
	}

	/**
	 * Typically used for SELECT statements. This is a wrapper for
	 * {@link java.sql.Statement.executeQuery(String)} but with parameterized
	 * SQL statement. Parameterization is handled by
	 * String#format(String,Object...).
	 * 
	 * @param callback
	 * @param parameterizedSql
	 * @param sqlParameters
	 * @return
	 */
	<T> T query(final ProcessResultSetCallback<T> callback, final String parameterizedSql,
	        final Object... sqlParameters) {
		LOG.publish(new LogRecord(Level.INFO, "Database#query - begin"));

		ResultSet rs;
		try {
			final String SQL = String.format(parameterizedSql, sqlParameters);
			rs = executeQuery(SQL);
		} catch (IllegalFormatException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#update - failed to format query " + exception));
			throw new RuntimeException(exception);
		}
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

		DbUtils.closeQuietly(rs);
		DbUtils.closeQuietly(stmt);

		LOG.publish(new LogRecord(Level.INFO, "Database#query - end"));
		return data;
	}

	void closeConnection() {
		DbUtils.closeQuietly(conn);
	}

	/*
	 * Old methods waiting to be updated
	 */

	/**
	 * @deprecated
	 */
	private void closeConnection(ResultSet rs) {

		DbUtils.closeQuietly(rs);
		DbUtils.closeQuietly(stmt);
		DbUtils.closeQuietly(conn);

		LOG.publish(new LogRecord(Level.INFO, "ResultSet, Statement, Connection closed"));
	}

	/**
	 * @deprecated use
	 *             {@link #query(ProcessResultSetCallback, String, Object...)}
	 */
	SortedMap<Assignment, List<Problem>> queryAssignmentProblemTreeData(int courseId) {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryAssignmentProblemTreeData - begin: courseId=" + courseId));

		SortedMap<Assignment, List<Problem>> map = new TreeMap<Assignment, List<Problem>>();

		/*
		 * SELECT 
		    a.assignment_id,
		    a.assignment_title,
		    a.due_date,
		    prob.problem_id,
		    prob.problem_title,
		    prob.points_possible,
		    prob.num_new_questions_allowed,
    		prob.num_check_allowed,
		    IF(uw.soln_username = 'clappdj',
		        uw.points,
		        0) AS 'uw.points'
		FROM
		    assignments a,
		    problems prob
		        LEFT JOIN
		    user_work uw ON IF(uw.soln_username = 'clappdj', uw.soln_prob_id = prob.problem_id, FALSE)
		WHERE
		    a.assignment_id = prob.problem_aid
		        AND a.a_cid = 1
		ORDER BY a.due_date , prob.problem_num;
		 */
		final String SQL = "SELECT a.assignment_id, a.assignment_title, a.due_date, prob.problem_id, prob.problem_title, prob.points_possible, prob.num_new_questions_allowed, "
				+ "prob.num_check_allowed, IF(uw.soln_username = '" + ServerUtils.getUsername() + "', uw.points, 0) AS 'uw.points' "
		        + "FROM assignments a, problems prob LEFT JOIN user_work uw ON IF(uw.soln_username = '"
		        + ServerUtils.getUsername() + "', uw.soln_prob_id = prob.problem_id, FALSE) "
		        + "WHERE a.assignment_id = prob.problem_aid AND a.a_cid = " + courseId
		        + " ORDER BY a.due_date , prob.problem_num;";
				
		final ResultSet rs = executeQuery(SQL);
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
				currentAssignId = rs.getInt("a.assignment_id");
				final Problem currentProb = new Problem(rs.getInt("prob.problem_id"),
				        currentAssignId, rs.getString("prob.problem_title"),
				        rs.getDouble("prob.points_possible"), rs.getDouble("uw.points"),
				        rs.getInt("prob.num_new_questions_allowed"), rs.getInt("prob.num_check_allowed"));

				if (currentAssignId == previousAssignId) {
					// add problem to the problem set for the assignment currently being processed
					problemSet.add(currentProb);

					assignmentPointsPossible += currentProb.getPointsPossible();
					assignmentPointsEarned += currentProb.getPointsEarned();

				} else {
					// this must be a new assignment-problem set

					// commit previous set to map, unless its the first assignment processed
					if (!rs.isFirst()) {
						// update assignment with point totals
						assign = new Assignment(assign.getId(), assign.getcId(), assign.getTitle(), assign.getDueDate(),
						        assignmentPointsPossible, assignmentPointsEarned);
						map.put(assign, problemSet);
					}

					assignmentPointsPossible = currentProb.getPointsPossible();
					assignmentPointsEarned = currentProb.getPointsEarned();

					// create the new assignment with placeholder points
					assign = new Assignment(currentAssignId, courseId, rs.getString("a.assignment_title"),
					        rs.getDate("a.due_date"), 0.0, 0.0);
					previousAssignId = currentAssignId;

					// create the new problemSet
					problemSet = new ArrayList<Problem>();
					problemSet.add(currentProb);
				}
			}
			map.put(assign, problemSet);

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

	/**
	 * @deprecated use
	 *             {@link #query(ProcessResultSetCallback, String, Object...)}
	 */
	List<Course> queryCourses() {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - begin"));

		List<Course> courseList = new ArrayList<Course>();

		final String SQL = "SELECT c.course_id, c.course_title " + "FROM enrollment e LEFT JOIN courses c "
		        + "ON e.enr_cid = c.course_id WHERE e.enr_username = \"" + ServerUtils.getUsername() + "\";";
		ResultSet rs = null;
		try {
			rs = executeQuery(SQL);
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

	/**
	 * @deprecated use
	 *             {@link #query(ProcessResultSetCallback, String, Object...)}
	 */
	Course queryCourseFromId(int courseId) {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryCourseFromId - begin"));

		Course course = null;

		final String SQL = "SELECT c.course_id, c.course_title " + "FROM enrollment e LEFT JOIN courses c "
		        + "ON e.enr_cid = c.course_id WHERE e.enr_username = \"" + ServerUtils.getUsername()
		        + "\" AND c.course_id = "
		        + courseId + " LIMIT 1;";
		ResultSet rs;
		
		try {
			rs = executeQuery(SQL);
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

	/**
	 * @deprecated use
	 *             {@link #query(ProcessResultSetCallback, String, Object...)}
	 */
	String[] queryAnswers(int permutationId) {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryAnswers - begin"));

		String[] answers = new String[10];

		final String SQL = "SELECT answer_1, answer_2, answer_3, answer_4, answer_5, answer_6, answer_7, answer_8, answer_9, answer_10 "
		        + "FROM permutations WHERE perm_id = " + permutationId + ";";
		final ResultSet rs = executeQuery(SQL);
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
