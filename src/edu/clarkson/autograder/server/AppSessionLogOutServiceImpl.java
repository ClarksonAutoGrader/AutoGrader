package edu.clarkson.autograder.server;

import javax.servlet.http.HttpServletRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.services.AppSessionLogOutService;

@SuppressWarnings("serial")
public class AppSessionLogOutServiceImpl extends RemoteServiceServlet implements AppSessionLogOutService {
	public boolean appSessionLogOut() {
		boolean success = false;
		HttpServletRequest req = this.getThreadLocalRequest();
		req.getSession().invalidate();
		
		if(!req.isRequestedSessionIdValid()) {
			success = true;
		}
		
		return success;
		
	}
	
}
