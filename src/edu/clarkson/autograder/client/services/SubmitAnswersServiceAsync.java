package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.objects.UserWork;

public interface SubmitAnswersServiceAsync {
	void submitAnswers(UserWork userWork, AsyncCallback<ProblemData> callback)
	        throws IllegalArgumentException;
}
