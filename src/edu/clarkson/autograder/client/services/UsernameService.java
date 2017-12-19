package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userName")

//Gets current CAS session username
public interface UsernameService extends RemoteService {
	String getCurrentUsername();
}
