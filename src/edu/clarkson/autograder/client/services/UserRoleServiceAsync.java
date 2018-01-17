package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserRoleServiceAsync {
	void fetchUserRole(AsyncCallback<String> callback) throws IllegalArgumentException;
}
