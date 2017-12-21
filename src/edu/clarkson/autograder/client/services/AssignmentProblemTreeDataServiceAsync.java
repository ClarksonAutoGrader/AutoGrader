package edu.clarkson.autograder.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.widgets.AssignmentTreeViewModel.Category;

public interface AssignmentProblemTreeDataServiceAsync {
	
	void fetchTreeData(AsyncCallback<List<Category>> callback) throws IllegalArgumentException;

}
