package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.objects.UserWork;

@RemoteServiceRelativePath("submit_answers")
public interface SubmitAnswersService extends RemoteService {
	ProblemData submitAnswers(UserWork userWork);
}
