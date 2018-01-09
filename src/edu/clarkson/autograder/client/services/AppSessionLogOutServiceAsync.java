package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AppSessionLogOutServiceAsync {

	void appSessionLogOut(AsyncCallback<Boolean> callback) throws IllegalArgumentException;

}
