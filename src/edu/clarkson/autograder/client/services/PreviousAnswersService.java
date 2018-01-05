package edu.clarkson.autograder.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.objects.PreviousAnswersRow;

@RemoteServiceRelativePath("previous_answers")
public interface PreviousAnswersService extends RemoteService {
	List<PreviousAnswersRow> fetchPreviousAnswers(int permutationId, int answerNumber) throws IllegalArgumentException;
}
