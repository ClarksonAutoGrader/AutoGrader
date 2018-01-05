package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.objects.ProblemData;

@RemoteServiceRelativePath("problem_data")
public interface SelectedProblemDataService extends RemoteService {
	ProblemData fetchProblemData(int problemId) throws IllegalArgumentException;
}
