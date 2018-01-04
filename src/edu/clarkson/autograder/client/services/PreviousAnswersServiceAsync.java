package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PreviousAnswersServiceAsync {

	void fetchPreviousAnswers(int permutationId, AsyncCallback<String[]> callback) throws IllegalArgumentException;
}
