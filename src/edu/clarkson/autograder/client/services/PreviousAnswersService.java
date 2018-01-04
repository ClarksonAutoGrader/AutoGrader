package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("previous_answers")
public interface PreviousAnswersService extends RemoteService {
	String[] fetchPreviousAnswers(int permutationId) throws IllegalArgumentException;
}
