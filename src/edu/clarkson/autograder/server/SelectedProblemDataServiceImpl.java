package edu.clarkson.autograder.server;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.services.SelectedProblemDataService;

@SuppressWarnings("serial")
public class SelectedProblemDataServiceImpl extends RemoteServiceServlet implements SelectedProblemDataService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public ProblemData fetchProblemData(int problemId) {
		LOG.publish(new LogRecord(Level.INFO, "SelectedProblemDataServiceImpl#fetchProblemData - begin"));

		Database db = new Database();
		ProblemData data = ServerUtils.createProblemData(db, problemId);
		db.closeConnection();

		LOG.publish(new LogRecord(Level.INFO, "SelectedProblemDataServiceImpl#fetchProblemData - end"));
		return data;
	}

}
