package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jasig.cas.client.util.AssertionHolder;

import edu.clarkson.autograder.client.objects.Permutation;
import edu.clarkson.autograder.client.objects.Problem;
import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.objects.UserWork;

public class ServerUtils {

	private static ConsoleHandler LOG = new ConsoleHandler();

	// private static final String DEFAULT_USERNAME = "null";
	private static final String DEFAULT_USERNAME = "murphycd";

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

	static ProblemData createProblemData(Database db, int problemId, int defaultResetsUsed) {

		String username = getUsername();
		
		ProblemData data = db.query(processResultSetForProblemData, Database.selectProblemDataSql, defaultResetsUsed, username, username, problemId);

		if (data.getUserWorkId() != 0) {
			db.update(Database.updateUserWorkPointsEarned, data.getPointsEarned(), username,
			        data.getPermutationId());
		}

		return data;
	}

	private static ProcessResultSetCallback<ProblemData> processResultSetForProblemData = new ProcessResultSetCallback<ProblemData>() {

		/**
		 * Reference: http://rubular.com/r/tobA6PJnZA
		 */
		private static final String RAW_ANSWER_TAG = "!ans_(?<number>[1-9]|10)_(?<type>numeric|text|boolean|list)(?:\\{\\s*(?<content>[^,}]+(?:\\s*,\\s*[^,}]+\\s*)*)\\s*\\})?!";

		/**
		 * Uses groups from RAW_ANSWER_TAG (number, type, and content) to create
		 * a div element
		 */
		private static final String CREATE_ANSWER_DIV = "<div id=\"ans_${number}\">flag:${flag},type:${type},content:${content}</div>";

		@Override
		public ProblemData process(ResultSet rs) throws SQLException {
			LOG.publish(new LogRecord(Level.INFO, "createProblemData->process - begin"));

			ProblemData problemData = null;

			// get first and only problem
			rs.next();

			/*
			 * Store data from ResultSet into local variables
			 */
			final String[] inputs = { rs.getString("perm.input_1"), rs.getString("perm.input_2"),
			        rs.getString("perm.input_3"), rs.getString("perm.input_4"), rs.getString("perm.input_5"),
			        rs.getString("perm.input_6"), rs.getString("perm.input_7"), rs.getString("perm.input_8"),
			        rs.getString("perm.input_9"), rs.getString("perm.input_10") };
			final String[] correctAnswers = { rs.getString("perm.answer_1"), rs.getString("perm.answer_2"),
			        rs.getString("perm.answer_3"), rs.getString("perm.answer_4"), rs.getString("perm.answer_5"),
			        rs.getString("perm.answer_6"), rs.getString("perm.answer_7"), rs.getString("perm.answer_8"),
			        rs.getString("perm.answer_9"), rs.getString("perm.answer_10") };
			final String[] userAnswers = { rs.getString("uw.user_answer_1"), rs.getString("uw.user_answer_2"),
			        rs.getString("uw.user_answer_3"), rs.getString("uw.user_answer_4"),
			        rs.getString("uw.user_answer_5"), rs.getString("uw.user_answer_6"),
			        rs.getString("uw.user_answer_7"), rs.getString("uw.user_answer_8"),
			        rs.getString("uw.user_answer_9"), rs.getString("uw.user_answer_10") };
			final Permutation permutation = new Permutation(rs.getInt("perm.perm_id"), rs.getInt("perm.perm_prob_id"),
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
			Matcher answerMatch = Pattern.compile(RAW_ANSWER_TAG).matcher(bodyText);
			StringBuffer buffer = new StringBuffer();
			// iterate through answer tags, evaluating as we go
			String[] gradeResult = new String[10];
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
							errorText = "correctAnswers[" + index + "] could not be parsed: \"" + correctAnswers[index]
							        + "\"";
						}

						// parse userAnswer
						try {
							userAnswer = Double.parseDouble(userAnswers[index]);
						} catch (NumberFormatException exception) {
							errorText = "userAnswers[" + index + "] could not be parsed: \"" + userAnswers[index]
							        + "\"";
						}

						// abort on failure
						if (!errorText.isEmpty()) {
							LOG.publish(new LogRecord(Level.INFO, "createProblemData->process - " + errorText));
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
				// store grade result
				gradeResult[index] = gradeFlag;

				/*
				 * Transform answer tag into answer div using all information
				 */
				String answerDivRegex = CREATE_ANSWER_DIV.replace("${flag}", gradeFlag);
				answerMatch.appendReplacement(buffer, answerDivRegex);
			}
			answerMatch.appendTail(buffer);
			String bodyWithReplacements = buffer.toString();

			/*
			 * Create problem object
			 */
			double pointsPossible = rs.getDouble("prob.points_possible");
			double pointsPerQuestion = pointsPossible / permutation.getNumAnswers();

			double pointsEarned = 0;
			for (int index = 0; index < gradeResult.length; index++) {
				if (gradeResult[index] != null && gradeResult[index].equals("correct")) {
					pointsEarned += pointsPerQuestion;
				}
			}
			final Problem prob = new Problem(rs.getInt("prob.problem_id"), rs.getInt("prob.problem_aid"),
			        rs.getString("prob.problem_title"), pointsPossible, pointsEarned,
			        rs.getInt("prob.num_check_allowed"), rs.getInt("prob.num_new_questions_allowed"));

			final UserWork userWork = new UserWork(rs.getInt("uw.soln_id"), rs.getInt("uw.soln_prob_id"),
			        rs.getInt("uw.soln_perm_id"), rs.getInt("uw.num_new_questions_used"),
			        rs.getInt("uw.num_check_used"), pointsEarned, userAnswers);

			/*
			 * Finalize ProblemData
			 */
			problemData = new ProblemData(prob, bodyWithReplacements, permutation, userWork);
			if (!problemData.isValid()) {
				throw new RuntimeException("ProblemData in internally inconsistent");
			}

			LOG.publish(new LogRecord(Level.INFO, "createProblemData->process - end"));
			return problemData;
		}
	};
}
