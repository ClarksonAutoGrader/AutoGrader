package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.PreviousAnswersRow;
import edu.clarkson.autograder.client.services.PreviousAnswersService;

@SuppressWarnings("serial")
public class PreviousAnswersImpl extends RemoteServiceServlet implements PreviousAnswersService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public List<PreviousAnswersRow> fetchPreviousAnswers(int permutationId, int answerNumber) {
		LOG.publish(new LogRecord(Level.INFO, "PreviousAnswersImpl#fetchPreviousAnswers - begin"));

		Database db = new Database();
		List<PreviousAnswersRow> previousAnswers = db.query(processResultSetcallback, Database.previousAnswersSql,
		        answerNumber, ServerUtils.getUsername(), permutationId);

		LOG.publish(new LogRecord(Level.INFO, "PreviousAnswersImpl#fetchPreviousAnswers - end"));
		return previousAnswers;
	}

	private ProcessResultSetCallback<List<PreviousAnswersRow>> processResultSetcallback = new ProcessResultSetCallback<List<PreviousAnswersRow>>() {

		@Override
		public List<PreviousAnswersRow> process(ResultSet rs) throws SQLException {

			List<PreviousAnswersRow> data = new ArrayList<PreviousAnswersRow>();
			for (int count = 1; rs.next(); count++) {
				final PreviousAnswersRow item = new PreviousAnswersRow(count, rs.getString(1), null);
				data.add(item);
			}

			return data;
		}
	};

}