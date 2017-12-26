package edu.clarkson.autograder.server;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.Problem;
import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.services.SelectedProblemDataService;

@SuppressWarnings("serial")
public class SelectedProblemDataServiceImpl extends RemoteServiceServlet implements SelectedProblemDataService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public ProblemData fetchProblemData(int problemId) {
		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - begin"));

		// TODO uncomment and use
//		Database db = new Database();
//		ProblemData data = db.querySelectedProblemData(problemId);
		
		// TODO delete this debug line
		ProblemData data = new ProblemData(new Problem(7, 8, "MyTitle", 50, 35.5), "Sample body text.", 4, 5);

		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - end"));
		return data;
	}
	
}
