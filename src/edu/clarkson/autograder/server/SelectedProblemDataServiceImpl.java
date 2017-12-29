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
	 * Finds answer markup text (example: <code>!ans_##_type!</code>) and parses
	 * into groups containing number, type, and content<br>
	 * <br>
	 * <code>!ans_([1-9]|10)_?(field|boolean|list)\s*\{?(\s*\w+(?:\s*,\s*\w+)*)?\}?!</code><br>
	 * <br>
	 * Format: String "!ans_" followed by a number 1 through 10, another
	 * underscore, and finally the answer type Types include "field" (default),
	 * "boolean" (True/False drop-down), or "list" (drop-down with custom
	 * content). Type "list" must be followed by curly braces containing a
	 * comma-separated list of desired drop-down choices.<br>
	 * <br>
	 * {@link http://rubular.com/r/uU1ThMAy3x} <br>
	 * Examples:
	 * 
	 * <li>!ans_1_field! Exactly one or two digits required Type is a text
	 * field</li>
	 * 
	 * <li>!ans_1_boolean! True/False input drop-down</li>
	 * 
	 * <li>!ans_1_list{Odd, Even} Drop-down with specified content (comma
	 * delimited, whitespace around comma does not matter)</li>
	 */
	static final String RAW_ANSWER_TAG = "!ans_(?<number>[1-9]|10)_(?<type>field|boolean|list)\\s*\\{?(?<content>\\s*\\w+(?:\\s*,\\s*\\w+)*)?\\}?!";

	/**
	 * Uses groups from RAW_ANSWER_TAG (number, type, and content) to create a
	 * div element
	 */
	static final String CREATE_ANSWER_DIV = "<div id=\"ans_${number}\">type:${type},content:${content}</div>";

	@Override
	public ProblemData fetchProblemData(int problemId) {
		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - begin"));
		
		Database db = new Database();
		ProblemData data = db.querySelectedProblemData(problemId);

		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - end"));
		return data;
	}
	
}
