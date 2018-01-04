package edu.clarkson.autograder.server;

import org.jasig.cas.client.util.AssertionHolder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.services.UsernameService;

public class UsernameServiceImpl extends RemoteServiceServlet implements UsernameService {

	@Override
	public String getCurrentUsername() {
		return AssertionHolder.getAssertion().getPrincipal().getName();
	}

}
