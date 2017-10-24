package edu.clarkson.autograder.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.services.AssignmentService;

public class AssignmentServiceImpl extends RemoteServiceServlet implements AssignmentService{

	private static List<Assignment> assignments = new ArrayList<Assignment>();
	int numCourses = 5;
	
	@Override
	public List<Assignment> fetchAssignments(int courseId) throws IllegalArgumentException{
		
		try {
			//temp method call until db hook in
			createAssignments(courseId);
		} 
		catch (IllegalArgumentException e){
			//TODO: handle IllegalArgumentException to allow for application to fail gracefully
		
		}
		
		return assignments;
	}
	
	//Temp method until db hook in
	public void createAssignments(int courseId){
		
		int number_of_assignments = 12;
		int num_current_assignments = 0;
		for (int a_num = 0; a_num < number_of_assignments; a_num++) {
			// open date 1498795200000 is 06/30/2017 04:00 AM GMT
			// One day increment: 86400000
			// close date is between -10 and +2 days from current date
			assignments.add(new Assignment(a_num, courseId, true, "Assignment Title " + (a_num + 1),
			        new Date(1498795200000L),
			        new Date(120000 + (86400000L * (courseId - 2)) + (new Date()).getTime()
			                - (86400000L * (number_of_assignments - num_current_assignments - a_num)))));
		}
	}
}
