package edu.clarkson.autograder.client.services;

import java.util.List;
import java.util.SortedMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Problem;

@RemoteServiceRelativePath("tree_data")
public interface AssignmentProblemTreeDataService extends RemoteService {
	SortedMap<Assignment, List<Problem>> fetchTreeData(int courseId) throws IllegalArgumentException;
}
