package edu.clarkson.autograder.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.services.UsernameService;

@SuppressWarnings("serial")
public class UsernameServiceImpl extends RemoteServiceServlet implements UsernameService {

	@Override
	public String getCurrentUsername() {
		return Database.getUsername();
	}

}
