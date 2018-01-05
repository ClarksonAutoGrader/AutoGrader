package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UsernameServiceAsync {
	
	void getCurrentUsername(AsyncCallback<String> callback) throws IllegalArgumentException;

}
