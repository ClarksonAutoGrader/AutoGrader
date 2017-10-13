package edu.clarkson.autograder.server;

import org.jasig.cas.client.util.AssertionHolder;

public class User {

	public static final String USER_ID = AssertionHolder.getAssertion().getPrincipal().getName();
	//call AssertionHolder.clear() to delete threadlocal
	
}
