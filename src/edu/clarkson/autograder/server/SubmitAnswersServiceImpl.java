package edu.clarkson.autograder.server;

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
		LOG.publish(new LogRecord(Level.INFO, "#submitAnswers - begin"));

		Database db = new Database();
		String[] correctAnswers = db.queryAnswers(permutationId);

		ProblemData problemData = new ProblemData();

		LOG.publish(new LogRecord(Level.INFO, "#submitAnswers - end"));
		return problemData;
	}
}
