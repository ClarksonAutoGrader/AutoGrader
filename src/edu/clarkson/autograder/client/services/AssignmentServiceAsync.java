package edu.clarkson.autograder.client.services;

import java.util.List;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.clarkson.autograder.client.objects.Assignment;

public interface AssignmentServiceAsync {

	void fetchAssignments(int courseId, AsyncCallback<List<Assignment>> callback) throws IllegalArgumentException;
}
