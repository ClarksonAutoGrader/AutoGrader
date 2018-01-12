package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.objects.UserWork;
import edu.clarkson.autograder.client.services.SubmitAnswersService;

@SuppressWarnings("serial")
public class SubmitAnswersServiceImpl extends RemoteServiceServlet implements SubmitAnswersService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public ProblemData submitAnswers(UserWork userWork) {

		LOG.publish(new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - begin"));

		Database db = null;
		ProblemData data = null;

		try {
			// Establish connection to database and begin transaction
			db = new Database();
			db.beginTransaction();

			// check if assignment is open
			boolean assignmentOpen = ServerUtils.problemIsOpen(db, userWork.getProblemId());
			if (!assignmentOpen) {
				// force return of empty ProblemData
				db.commitTransaction();
				db.closeConnection();
				return null;
			}

			// check if user has any resets remaining
			ProcessResultSetCallback<Integer> processAttemptsRemainingCallback = new ProcessResultSetCallback<Integer>() {
				@Override
				public Integer process(ResultSet rs) throws SQLException {
					rs.next();
					return rs.getInt("num_check_remaining");
				}
			};
			int resetsRemaining = db.query(processAttemptsRemainingCallback, Database.selectAttemptsRemaining,
			        ServerUtils.getUsername(), userWork.getProblemId());
			if (resetsRemaining <= 0) {
				// force return of empty ProblemData
				db.commitTransaction();
				db.closeConnection();
				return null;
			}

			// Delete user work record if exists
			int userWorkResult = -1;
			int prevAnsResult = -1;
			userWorkResult = db.update(Database.deleteUserWorkRecord, ServerUtils.getUsername(),
					userWork.getPermutationId());
			LOG.publish(
					new LogRecord(Level.INFO,
							"SubmitAnswersServiceImpl#submitAnswers - records deleted: " + userWorkResult));

			/*
			 * Insert new user work record and trigger insertion of previous
			 * answers record: [0]<-soln_id, [1]<-soln_prob_id,
			 * [2]<-soln_username, [3]<-soln_perm_id,
			 * [4]<-num_new_questions_used, [5]<-num_check_used,
			 * [6]<-user_answer_1, [7]<-user_answer_2, [8]<-user_answer_3,
			 * [9]<-user_answer_4, [10]<-user_answer_5, [11]<-user_answer_6,
			 * [12]<-user_answer_7, [13]<-user_answer_8, [14]<-user_answer_9,
			 * [15]<-user_answer_10, [16]<-[1], [17]<-[2], [18]<-[3], [19]<-[4],
			 * [20]<-[5], [21]<-[6] (user_answer_1), [22]<-[7], [23]<-[8],
			 * [24]<-[9], [25]<-[10], [26]<-[11], [27]<-[12], [28]<-[13],
			 * [29]<-[14], [30]<-[15]
			 * 
			 */
			Object[] userWorkParams = new Object[31];
			final String str = "";
			final String tick = "'";
			userWorkParams[0] = str + userWork.getId();
			userWorkParams[1] = str + userWork.getProblemId();
			userWorkParams[2] = ServerUtils.getUsername();
			userWorkParams[3] = str + userWork.getPermutationId();
			userWorkParams[4] = str + userWork.getResetsUsed();
			userWorkParams[5] = str + (userWork.getAttemptsUsed() + 1);
			for (int index = 6; index <= 15; index++) {
				String param = userWork.getUserAnswers()[index - 6];
				userWorkParams[index] = param != null ? tick + param + tick : param;
			}
			for (int index = 16; index <= 30; index++) {
				userWorkParams[index] = userWorkParams[index - 15];
			}

			// inserting into user work
			userWorkResult = -1;
			userWorkResult = db.update(Database.insertSubmittedUserWork, userWorkParams);
			LOG.publish(
					new LogRecord(Level.INFO,
							"SubmitAnswersServiceImpl#submitAnswers - records inserted into user_work: "
									+ userWorkResult));

			Object[] prevAnsParams = new Object[13];
			prevAnsParams[0] = str + userWork.getProblemId();
			prevAnsParams[1] = ServerUtils.getUsername();
			prevAnsParams[2] = str + userWork.getPermutationId();
			for (int index = 3; index < prevAnsParams.length; index++) {
				String param = userWork.getUserAnswers()[index - 3];
				prevAnsParams[index] = param != null ? tick + param + tick : param;
			}
			// inserting into previous answers
			prevAnsResult = -1;
			prevAnsResult = db.update(Database.insertIntoPreviousAnswers, prevAnsParams);
			LOG.publish(new LogRecord(Level.INFO,
					"SubmitAnswersServiceImpl#submitAnswers - records inserted into previous_answers: "
							+ prevAnsResult));

			// assume zero resets used if no user work is present
			final int defaultResetsUsed = 0;

			// query for Problem Data, evaluate answers based on existing user
			// work, update user work points
			data = ServerUtils.createProblemData(db, userWork.getProblemId(), defaultResetsUsed);

			// Commit transaction
			db.commitTransaction();
		} catch (Throwable exception) {
			db.rollBackTransaction();
			LOG.publish(new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - Transaction rolled back"));
		} finally {
			db.closeConnection();
		}

		LOG.publish(new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - end"));
		return data;
	}
}
