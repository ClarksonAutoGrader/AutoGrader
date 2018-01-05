package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.GradebookData;

public interface GradebookDataServiceAsync {
	void fetchGradebookData(int courseId, AsyncCallback<GradebookData> callback) throws IllegalArgumentException;
}
