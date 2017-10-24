package edu.clarkson.autograder.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.objects.Assignment;

@RemoteServiceRelativePath("assignment")
public interface AssignmentService extends RemoteService{
	List<Assignment> fetchAssignments(int courseId) throws IllegalArgumentException;
}
