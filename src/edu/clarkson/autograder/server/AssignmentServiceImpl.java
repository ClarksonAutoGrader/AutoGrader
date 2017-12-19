package edu.clarkson.autograder.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.services.AssignmentService;

@SuppressWarnings("serial")
public class AssignmentServiceImpl extends RemoteServiceServlet implements AssignmentService{

	private static ConsoleHandler LOG = new ConsoleHandler();
	
	@Override
	public List<Assignment> fetchAssignments(int courseId) throws IllegalArgumentException{
		LOG.publish(new LogRecord(Level.INFO, "fetchAssinments for courseId=" + courseId));

		List<Assignment> assignments = null;
		try {
			//temp method call until db hook in
			assignments = createAssignments(courseId);
		} 
		catch (IllegalArgumentException e){
			//TODO: handle IllegalArgumentException to allow for application to fail gracefully
		
		}
		
		return assignments;
	}
	
	//Temp method until db hook in
	public List<Assignment> createAssignments(int courseId) {
		List<Assignment> assignments = new ArrayList<Assignment>();
		
		int number_of_assignments = 12;
		for (int a_num = 0; a_num < number_of_assignments; a_num++) {
			// open date 1498795200000 is 06/30/2017 04:00 AM GMT
			// One day increment: 86400000
			// close date is between -10 and +2 days from current date
			assignments.add(new Assignment(a_num, courseId, "Assignment Title " + (a_num + 1), "dueDate"));
		}
		return assignments;
	}
}
