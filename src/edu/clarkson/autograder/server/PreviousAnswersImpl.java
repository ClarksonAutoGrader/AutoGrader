package edu.clarkson.autograder.server;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.services.PreviousAnswersService;

@SuppressWarnings("serial")
public class PreviousAnswersImpl extends RemoteServiceServlet implements PreviousAnswersService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public String[] fetchPreviousAnswers(int permutationId) {
		LOG.publish(new LogRecord(Level.INFO, "PreviousAnswersImpl#fetchPreviousAnswers - begin"));

		Database db = new Database();
		String[] previousAnswers = new String[10];
		
		LOG.publish(new LogRecord(Level.INFO, "PreviousAnswersImpl#fetchPreviousAnswers - end"));
		return previousAnswers;
	}

}
