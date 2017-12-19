package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;

import edu.clarkson.autograder.client.AssignmentTreeViewModel.Category;

public interface AssignmentProblemTreeDataServiceAsync {
	
	void fetchTreeData(AsyncCallback<ListDataProvider<Category>> callback) throws IllegalArgumentException;

}
