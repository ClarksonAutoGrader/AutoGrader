package edu.clarkson.autograder.server;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataService;
import edu.clarkson.autograder.client.widgets.AssignmentTreeViewModel.Category;

@SuppressWarnings("serial")
public class AssignmentProblemTreeDataImpl extends RemoteServiceServlet implements AssignmentProblemTreeDataService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public List<Category> fetchTreeData() throws IllegalArgumentException {
		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - begin"));

		List<Category> data = Database.queryAssignmentProblemTreeData();

		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - end"));
		return data;
	}
	
}
