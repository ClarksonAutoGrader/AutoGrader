package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.ProblemData;

public interface SubmitAnswersServiceAsync {
	void submitAnswers(int permutationId, String[] userAnswers, AsyncCallback<ProblemData> callback) throws IllegalArgumentException;
}
