package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.services.SubmitAnswersService;

@SuppressWarnings("serial")
public class SubmitAnswersServiceImpl extends RemoteServiceServlet implements SubmitAnswersService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public ProblemData submitAnswers(int permutationId, String[] userAnswers) {
		LOG.publish(new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - begin"));

		Database db = new Database();
		ProblemData data = db.query(processResultSetcallback, "some query", permutationId);

		LOG.publish(new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - end"));
		return data;
	}

	private ProcessResultSetCallback<ProblemData> processResultSetcallback = new ProcessResultSetCallback<ProblemData>() {

		@Override
		public ProblemData process(ResultSet rs) throws SQLException {
			return null;
		}
	};
}
