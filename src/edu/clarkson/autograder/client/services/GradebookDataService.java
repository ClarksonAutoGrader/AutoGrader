package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.objects.GradebookData;

@RemoteServiceRelativePath("gradebook_data")
public interface GradebookDataService extends RemoteService {
	GradebookData fetchGradebookData(int courseId) throws IllegalArgumentException;
}