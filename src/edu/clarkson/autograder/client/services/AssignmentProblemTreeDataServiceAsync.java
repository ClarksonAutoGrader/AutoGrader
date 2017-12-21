package edu.clarkson.autograder.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Problem;

public interface AssignmentProblemTreeDataServiceAsync {
	
	void fetchTreeData(int courseId, AsyncCallback<Map<Assignment, List<Problem>>> callback) throws IllegalArgumentException;

}