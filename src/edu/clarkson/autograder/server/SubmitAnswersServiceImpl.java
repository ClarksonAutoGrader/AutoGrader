package edu.clarkson.autograder.server;

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

		Database db = null;
		ProblemData data = null;

		LOG.publish(new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - begin"));
		try {
			// Establish connection to database and begin transaction
			db = new Database();
			db.beginTransaction();

			// Delete user work record if exists
			int result = -1;
			result = db.update(Database.deleteUserWorkRecord, ServerUtils.getUsername(), userWork.getPermutationId());
			LOG.publish(
					new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - records deleted: " + result));

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
			Object[] params = new Object[31];
			final String str = "";
			final String tick = "'";
			params[0] = str + userWork.getId();
			params[1] = str + userWork.getProblemId();
			params[2] = ServerUtils.getUsername();
			params[3] = str + userWork.getPermutationId();
			params[4] = str + userWork.getResetsUsed();
			params[5] = str + (userWork.getAttemptsUsed() + 1);
			for (int index = 6; index <= 15; index++) {
				String param = userWork.getUserAnswers()[index - 6];
				params[index] = param != null ? tick + param + tick : param;
			}
			for (int index = 16; index <= 30; index++) {
				params[index] = params[index - 15];
			}
			result = db.update(Database.insertSubmittedUserWork, params);
			LOG.publish(
					new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - records inserted: " + result));

			// assume zero resets used if no user work is present
			final int defaultResetsUsed = 0;

			// query for Problem Data, evaluate answers based on existing user
			// work,
			// update user work points
			data = ServerUtils.createProblemData(db, userWork.getProblemId(), defaultResetsUsed);

			// Commit transaction
			db.commitTransaction();
		}
		catch (Throwable exception) {
			db.rollBackTransaction();
			LOG.publish(new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - Transaction rolled back"));
		}
		finally {
			db.closeConnection();
		}

		LOG.publish(new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - end"));
		return data;
	}
}