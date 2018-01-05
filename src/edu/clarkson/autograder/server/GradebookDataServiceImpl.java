package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.GradebookData;
import edu.clarkson.autograder.client.objects.StudentRowData;
import edu.clarkson.autograder.client.services.GradebookDataService;

@SuppressWarnings("serial")
public class GradebookDataServiceImpl extends RemoteServiceServlet implements GradebookDataService {

	private static ConsoleHandler LOG = new ConsoleHandler();
	
	// TODO: remove temporary hardcoded data values
	private GradebookData temp = new GradebookData(
			Arrays.asList(
					"Homework 1", "Homework 2", "Homework 3", "Homework 4", "Homework 5",
					"Homework 6", "Homework 7", "Homework 8", "Homework 9", "Homework 10"),
			Arrays.asList(
					new StudentRowData("woodrj", new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0}),
					new StudentRowData("woodrj", new double[] {11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0}),
					new StudentRowData("woodrj", new double[] {31.0, 32.0, 33.0, 34.0, 35.0, 36.0, 37.0, 38.0, 39.0, 40.0})
			));

	@Override
	public GradebookData fetchGradebookData(int courseId) throws IllegalArgumentException {

		LOG.publish(new LogRecord(Level.INFO, "GradebookDataServiceImpl#getGradebookData - begin"));

//		Database db = new Database();
//		GradebookData data = db.query(processResultSetcallback, "some query");

		LOG.publish(new LogRecord(Level.INFO, "GradebookDataServiceImpl#getGradebookData - end"));
		return temp; // TODO: return data, not temp
	}

	private ProcessResultSetCallback<GradebookData> processResultSetcallback = new ProcessResultSetCallback<GradebookData>() {

		@Override
		public GradebookData process(ResultSet rs) throws SQLException {
			
			GradebookData gradebookData = null;
			
			return gradebookData;
		}
	};
}
