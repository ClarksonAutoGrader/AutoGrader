package edu.clarkson.autograder.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.widgets.AssignmentTreeViewModel.Category;

@RemoteServiceRelativePath("tree_data")
public interface AssignmentProblemTreeDataService extends RemoteService {
	List<Category> fetchTreeData() throws IllegalArgumentException;
}
