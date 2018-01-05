package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.objects.ProblemData;

@RemoteServiceRelativePath("submit_answers")
public interface SubmitAnswersService extends RemoteService {
	ProblemData submitAnswers(int permutationId, String[] userAnswers);
}
