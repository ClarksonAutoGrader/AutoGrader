package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.GradebookData;

public interface GradebookDataServiceAsync {

	void getGradebookData(int courseid, AsyncCallback<GradebookData> callback);

}
