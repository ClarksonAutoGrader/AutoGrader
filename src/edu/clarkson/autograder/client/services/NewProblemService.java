package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.objects.UserWork;

@RemoteServiceRelativePath("new_problem")
public interface NewProblemService extends RemoteService {
	ProblemData fetchNewProblem(UserWork userWork);
}
