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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jasig.cas.client.util.AssertionHolder;
import org.apache.commons.dbutils.DbUtils;

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
	private static final String DEFAULT_USERNAME = "murphycd";

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
			rs = query(SQL);
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
			rs = query(SQL);
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

	ProblemData querySelectedProblemData(int problemId) {
		LOG.publish(new LogRecord(Level.INFO, "Database#querySelectedProblemData - begin"));

		ProblemData problemData = null;
		
		/*
		 SELECT 
		    prob.problem_id,
		    prob.problem_aid,
		    prob.problem_title,
		    prob.points_possible,
		    prob.num_check_allowed - COALESCE(uw.num_check_used, 0) AS 'checks_remaining',
    		prob.num_new_questions_allowed - COALESCE(uw.num_new_questions_used, 0) AS 'questions_remaining',
		    b.body_text,
		    COALESCE(uw.points, 0) AS 'uw.points',
		    COALESCE(uw.soln_perm_id, perm.perm_id) AS 'perm.perm_id',
		    perm.num_inputs,
		    perm.num_answers,
		    perm.input_1,
		    perm.input_2,
		    perm.input_3,
		    perm.input_4,
		    perm.input_5,
		    perm.input_6,
		    perm.input_7,
		    perm.input_8,
		    perm.input_9,
		    perm.input_10,
		    perm.answer_1,
		    perm.answer_2,
		    perm.answer_3,
		    perm.answer_4,
		    perm.answer_5,
		    perm.answer_6,
		    perm.answer_7,
		    perm.answer_8,
		    perm.answer_9,
		    perm.answer_10,
		    uw.user_answer_1,
		    uw.user_answer_2,
		    uw.user_answer_3,
		    uw.user_answer_4,
		    uw.user_answer_5,
		    uw.user_answer_6,
		    uw.user_answer_7,
		    uw.user_answer_8,
		    uw.user_answer_9,
		    uw.user_answer_10
		FROM
		    permutations perm,
		    body b,
		    problems prob
		LEFT JOIN
		    user_work uw ON prob.problem_id = uw.soln_prob_id
		WHERE
		    b.body_prob_id = prob.problem_id
		        AND perm.perm_prob_id = prob.problem_id
		        AND IF((uw.soln_username = 'murphycd'
		            OR uw.soln_username IS NULL),
		        TRUE,
		        FALSE) AND prob.problem_id = 1 AND IF((uw.soln_prob_id IS NULL), TRUE, uw.soln_perm_id = perm.perm_id)
		ORDER BY IF((uw.soln_perm_id IS NOT NULL), uw.soln_perm_id, RAND())
		LIMIT 1;
		 */
		final String SQL = "SELECT prob.problem_id, prob.problem_aid, prob.problem_title, prob.points_possible, "
				+ "prob.num_check_allowed - COALESCE(uw.num_check_used, 0) AS 'checks_remaining', "
				+ "prob.num_new_questions_allowed - COALESCE(uw.num_new_questions_used, 0) AS 'questions_remaining', "
		        + "b.body_text, COALESCE(uw.points, 0) AS 'uw.points', COALESCE(uw.soln_perm_id, perm.perm_id) AS 'perm.perm_id', perm.num_inputs, perm.num_answers, perm.input_1, perm.input_2, "
				+ "perm.input_3, perm.input_4, perm.input_5, perm.input_6, perm.input_7, perm.input_8, perm.input_9, perm.input_10, perm.answer_1, perm.answer_2, perm.answer_3, perm.answer_4, "
		        + "perm.answer_5, perm.answer_6, perm.answer_7, perm.answer_8, perm.answer_9, perm.answer_10, uw.user_answer_1, uw.user_answer_2, uw.user_answer_3, uw.user_answer_4, "
				+ "uw.user_answer_5, uw.user_answer_6, uw.user_answer_7, uw.user_answer_8, uw.user_answer_9, uw.user_answer_10 "
				+ "FROM permutations perm, body b, problems prob LEFT JOIN user_work uw ON prob.problem_id = uw.soln_prob_id "
				+ "WHERE b.body_prob_id = prob.problem_id AND perm.perm_prob_id = prob.problem_id AND "
				+ "IF((uw.soln_username = '" + getUsername() + "' OR uw.soln_username IS NULL), TRUE, FALSE) AND prob.problem_id = " + problemId + " "
		        + "AND IF((uw.soln_prob_id IS NULL), TRUE, uw.soln_perm_id = perm.perm_id) "
				+ "ORDER BY IF((uw.soln_perm_id IS NOT NULL), uw.soln_perm_id, RAND()) LIMIT 1;";

		ResultSet rs;

		try {
			rs = query(SQL);

			// get first and only problem
			rs.next();

			/*
			 * Store data from ResultSet into local variables
			 */
			final Problem prob = new Problem(rs.getInt("prob.problem_id"), rs.getInt("prob.problem_aid"),
			        rs.getString("prob.problem_title"), rs.getDouble("prob.points_possible"),
			        rs.getDouble("uw.points"));
			final String[] inputs = {
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
			final String[] correctAnswers = {
					rs.getString("perm.answer_1"),
					rs.getString("perm.answer_2"),
					rs.getString("perm.answer_3"),
					rs.getString("perm.answer_4"),
					rs.getString("perm.answer_5"),
					rs.getString("perm.answer_6"),
					rs.getString("perm.answer_7"),
					rs.getString("perm.answer_8"),
					rs.getString("perm.answer_9"),
					rs.getString("perm.answer_10")
			};
			final String[] userAnswers = {
					rs.getString("uw.user_answer_1"),
					rs.getString("uw.user_answer_2"),
					rs.getString("uw.user_answer_3"),
					rs.getString("uw.user_answer_4"),
					rs.getString("uw.user_answer_5"),
					rs.getString("uw.user_answer_6"),
					rs.getString("uw.user_answer_7"),
					rs.getString("uw.user_answer_8"),
					rs.getString("uw.user_answer_9"),
					rs.getString("uw.user_answer_10")
			};
			final Permutation permutation = new Permutation(rs.getInt("perm.perm_id"), rs.getInt("prob.problem_id"),
			        rs.getInt("perm.num_inputs"), rs.getInt("perm.num_answers"), inputs);
			String bodyText = rs.getString("b.body_text");

			
			/*
			 * Inject inputs into problem body markup
			 */
			for (int i = 1; i <= permutation.getNumInputs(); i++) {
				bodyText = bodyText.replaceAll("!in_" + i + "!", permutation.getInputString(i - 1));
			}

			/*
			 * Replace answer tags with HTML divs of the proper id
			 */
			Matcher answerMatch = Pattern.compile(SelectedProblemDataServiceImpl.RAW_ANSWER_TAG).matcher(bodyText);
			StringBuffer buffer = new StringBuffer();
			// iterate through answer tags
			while (answerMatch.find()) {

				/*
				 * Retrieve value of match groups for this iteration
				 */
				// note that answer number is one-based and array indices shall
				// be zero-based
				final int index = Integer.parseInt(answerMatch.group("number")) - 1;
				final String type = answerMatch.group("type");
				final String content = answerMatch.group("content");

				/*
				 * Evaluate answer (sets value in gradeFlag) Default flag "null"
				 * renders no flag
				 */
				String gradeFlag = "null";
				// check if user answer exists and evaluate, or skip evaluation
				// if user supplied no answer
				if (userAnswers[index] != null) {

					// evaluate correctness based on type:
					// numeric types must be checked against a tolerance, which
					// is stored in content group as a positive decimal
					if (type.equals("numeric")) {
						double tolerance = 0;
						double correctAnswer = 0;
						double userAnswer = 0;
						String errorText = "";

						// parse tolerance
						try {
							tolerance = Double.parseDouble(content);
						} catch (NumberFormatException | NullPointerException exception) {
							errorText = "Numeric tolerance could not be parsed: tolerance=\"" + content
							        + "\", user answer index " + index;
						}

						// parse correctAnswer
						try {
							correctAnswer = Double.parseDouble(correctAnswers[index]);
						} catch (NumberFormatException exception) {
							errorText = "correctAnswers[" + index + "] could not be parsed: " + correctAnswers[index];
						}

						// parse userAnswer
						try {
							userAnswer = Double.parseDouble(userAnswers[index]);
						} catch (NumberFormatException exception) {
							errorText = "userAnswers[" + index + "] could not be parsed: " + userAnswers[index];
						}

						// abort on failure
						if (!errorText.isEmpty()) {
							LOG.publish(new LogRecord(Level.INFO, "Database#querySelectedProblemData - " + errorText));
							throw new NumberFormatException(errorText);
						}

						// check against tolerance
						if (Math.abs(correctAnswer - userAnswer) <= (correctAnswer * tolerance)) {
							gradeFlag = "correct";
						} else {
							gradeFlag = "incorrect";
						}

					} else {
						// case: type is text, boolean, list, etc.
						gradeFlag = userAnswers[index].equals(correctAnswers[index]) ? "correct" : "incorrect";
					}
				}
				
				/*
				 * Transform answer tag into answer div using all information
				 */
				String answerDivRegex = SelectedProblemDataServiceImpl.CREATE_ANSWER_DIV.replace("${flag}", gradeFlag);
				answerMatch.appendReplacement(buffer, answerDivRegex);
			}
			answerMatch.appendTail(buffer);
			String bodyWithReplacements = buffer.toString();

			/*
			 * Finalize ProblemData
			 */
			problemData = new ProblemData(prob, bodyWithReplacements,
			        rs.getInt("questions_remaining"),
			        rs.getInt("checks_remaining"),
			        permutation);
				
		} catch (SQLException exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#querySelectedProblemData - SQLException " + exception));
			throw new RuntimeException(exception);
		} catch (Exception exception) {
			LOG.publish(new LogRecord(Level.INFO, "Database#querySelectedProblemData - unexpected exception " + exception));
			throw new RuntimeException(exception);
		}

		closeConnection(rs);
		LOG.publish(new LogRecord(Level.INFO, "Database#querySelectedProblemData - end"));
		return problemData;
	}

	String[] queryAnswers(int permutationId) {
		LOG.publish(new LogRecord(Level.INFO, "Database#queryAnswers - begin"));

		String[] answers = new String[10];

		final String SQL = "SELECT answer_1, answer_2, answer_3, answer_4, answer_5, answer_6, answer_7, answer_8, answer_9, answer_10 "
		        + "FROM permutations WHERE perm_id = " + permutationId + ";";
		final ResultSet rs = query(SQL);
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
