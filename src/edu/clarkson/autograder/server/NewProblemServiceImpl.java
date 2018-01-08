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

		Database db = new Database();

		// check if user has any resets remaining
		// TODO: implement lookup resets remaining
		ProcessResultSetCallback<Integer> processResetsRemainingCallback = new ProcessResultSetCallback<Integer>() {
			@Override
			public Integer process(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt("num_new_questions_remaining");
			}
		};
//		int resetsRemaining = db.query(processResetsRemainingCallback, Database.selectResetsRemaining);
		int resetsRemaining = 5;
		if (resetsRemaining <= 0) {
			// force return of empty ProblemData
			return null;
		}

		// Delete user work record if exists
		int result = db.update(Database.deleteUserWorkRecord, ServerUtils.getUsername(), userWork.getPermutationId());
		LOG.publish(new LogRecord(Level.INFO, "NewProblemServiceImpl#fetchNewProblem - records deleted: " + result));

		// decrement number of resets used, default value will be used since
		// user work record has just been deleted
		final int defaultResetsUsed = userWork.getResetsUsed() + 1;

		// query for Problem Data, evaluate answers based on existing user work,
		// update user work points
		ProblemData data = ServerUtils.createProblemData(db, userWork.getProblemId(), defaultResetsUsed);

		db.closeConnection();

		LOG.publish(new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - end"));
		return data;
	}
}
