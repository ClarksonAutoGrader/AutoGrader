package edu.clarkson.autograder.server;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.GradebookData;
import edu.clarkson.autograder.client.services.GradebookDataService;

@SuppressWarnings("serial")
public class GradebookDataServiceImpl extends RemoteServiceServlet implements GradebookDataService {

	private static ConsoleHandler LOG = new ConsoleHandler();
	
	@Override
	public GradebookData getGradebookData(int courseId) throws IllegalArgumentException {
		
		LOG.publish(new LogRecord(Level.INFO, "GradebookDataServiceImpl#getGradebookData - begin"));
		
		DatabaseQuery db = new DatabaseQuery();
		//GradebookData data = db.queryGradebookData(courseId);

		LOG.publish(new LogRecord(Level.INFO, "GradebookDataServiceImpl#getGradebookData - end"));
		return null; //TODO return data
	}
}
