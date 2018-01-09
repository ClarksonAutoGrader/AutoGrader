package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("app_session_log_out")
public interface AppSessionLogOutService extends RemoteService {
	boolean appSessionLogOut();
}
