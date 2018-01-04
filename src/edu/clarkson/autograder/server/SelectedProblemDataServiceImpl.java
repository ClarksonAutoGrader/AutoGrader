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

	/**
	 * Reference: http://rubular.com/r/tobA6PJnZA
	 */
	static final String RAW_ANSWER_TAG = "!ans_(?<number>[1-9]|10)_(?<type>numeric|text|boolean|list)(?:\\{\\s*(?<content>[^,}]+(?:\\s*,\\s*[^,}]+\\s*)*)\\s*\\})?!";

	/**
	 * Uses groups from RAW_ANSWER_TAG (number, type, and content) to create a
	 * div element
	 */
	static final String CREATE_ANSWER_DIV = "<div id=\"ans_${number}\">flag:${flag},type:${type},content:${content}</div>";

	@Override
	public ProblemData fetchProblemData(int problemId) {
		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - begin"));
		
		Database db = new Database();
		ProblemData data = db.querySelectedProblemData(problemId);

		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - end"));
		return data;
	}
	
}
