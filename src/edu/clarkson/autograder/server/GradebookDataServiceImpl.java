package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
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

		Database db = new Database();
		GradebookData data = db.query(processResultSetcallback, "some query");

		LOG.publish(new LogRecord(Level.INFO, "GradebookDataServiceImpl#getGradebookData - end"));
		return data;
	}

	private ProcessResultSetCallback<GradebookData> processResultSetcallback = new ProcessResultSetCallback<GradebookData>() {

		@Override
		public GradebookData process(ResultSet rs) throws SQLException {
			
			GradebookData gradebookData = null;
			
			return gradebookData;
		}
	};
}
