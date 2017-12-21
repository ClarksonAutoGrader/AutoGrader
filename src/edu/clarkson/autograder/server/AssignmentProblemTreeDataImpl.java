package edu.clarkson.autograder.server;

import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Problem;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataService;

@SuppressWarnings("serial")
public class AssignmentProblemTreeDataImpl extends RemoteServiceServlet implements AssignmentProblemTreeDataService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public Map<Assignment, List<Problem>> fetchTreeData(int courseId) throws IllegalArgumentException {
		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - begin"));

		Database db = new Database();
		Map<Assignment, List<Problem>> data = db.queryAssignmentProblemTreeData(courseId);

		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - end"));
		return data;
	}
	
}
