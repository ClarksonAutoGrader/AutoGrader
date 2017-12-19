package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.view.client.ListDataProvider;

import edu.clarkson.autograder.client.AssignmentTreeViewModel.Category;

@RemoteServiceRelativePath("treedata")
public interface AssignmentProblemTreeDataService extends RemoteService{
	ListDataProvider<Category> fetchTreeData() throws IllegalArgumentException;
}
