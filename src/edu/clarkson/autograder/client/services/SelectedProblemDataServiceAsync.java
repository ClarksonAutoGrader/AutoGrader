package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.ProblemData;

public interface SelectedProblemDataServiceAsync {
	
	void fetchProblemData(int problemId, AsyncCallback<ProblemData> callback) throws IllegalArgumentException;

}
