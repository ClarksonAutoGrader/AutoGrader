package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.objects.UserWork;
import edu.clarkson.autograder.client.services.NewProblemService;

@SuppressWarnings("serial")
public class NewProblemServiceImpl extends RemoteServiceServlet implements NewProblemService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public ProblemData fetchNewProblem(UserWork userWork) {

		LOG.publish(new LogRecord(Level.INFO, "NewProblemServiceImpl#fetchNewProblem - begin"));

		Database db = null;
		ProblemData data = null;

		try {

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

			// ensure at least one user work submission has been made before
			// allowing a new problem permutation
			ProcessResultSetCallback<Boolean> processUserWorkExistsCallback = new ProcessResultSetCallback<Boolean>() {
				@Override
				public Boolean process(ResultSet rs) throws SQLException {
					rs.first();
					return rs.getString("soln_id") != null;
				}
			};
			boolean userWorkExists = db.query(processUserWorkExistsCallback, Database.selectUserWorkId,
					ServerUtils.getUsername(), userWork.getProblemId());
			if (!userWorkExists) {
				// force return of empty ProblemData
				db.commitTransaction();
				db.closeConnection();
				return null;
			}

			// check if user has any resets remaining
			ProcessResultSetCallback<Integer> processResetsRemainingCallback = new ProcessResultSetCallback<Integer>() {
				@Override
				public Integer process(ResultSet rs) throws SQLException {
					rs.next();
					return rs.getInt("num_new_questions_remaining");
				}
			};
			int resetsRemaining = db.query(processResetsRemainingCallback, Database.selectResetsRemaining,
					ServerUtils.getUsername(), userWork.getProblemId());
			if (resetsRemaining <= 0) {
				// force return of empty ProblemData
				return null;
			}

			// Delete user work record if exists
			int result = db.update(Database.deleteUserWorkRecord, ServerUtils.getUsername(),
					userWork.getPermutationId());
			LOG.publish(
					new LogRecord(Level.INFO, "NewProblemServiceImpl#fetchNewProblem - records deleted: " + result));

			// decrement number of resets used, default value will be used since
			// user work record has just been deleted
			final int defaultResetsUsed = userWork.getResetsUsed() + 1;

			// query for Problem Data, evaluate answers based on existing user
			// work, update user work points
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
