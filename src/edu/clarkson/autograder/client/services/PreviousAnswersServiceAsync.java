package edu.clarkson.autograder.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.PreviousAnswersRow;

public interface PreviousAnswersServiceAsync {
	void fetchPreviousAnswers(int permutationId, int answerNumber, AsyncCallback<List<PreviousAnswersRow>> callback)
	        throws IllegalArgumentException;
}
