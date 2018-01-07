package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(31.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 40.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(41.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 50.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(51.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 60.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(61.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 70.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(71.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 80.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(81.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 90.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(91.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 100.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(101.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 110.0))),
					new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(111.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 120.0)))
			));

	@Override
	public GradebookData fetchGradebookData(int courseId) throws IllegalArgumentException {

		LOG.publish(new LogRecord(Level.INFO, "GradebookDataServiceImpl#getGradebookData - begin"));

		Database db = new Database();
		GradebookData data = db.query(processResultSetcallback, Database.gradebookDataSql, courseId);

		LOG.publish(new LogRecord(Level.INFO, "GradebookDataServiceImpl#getGradebookData - end"));
		return data;
	}

	private ProcessResultSetCallback<GradebookData> processResultSetcallback = new ProcessResultSetCallback<GradebookData>() {

		@Override
		public GradebookData process(ResultSet rs) throws SQLException {
			
			List<String> assignmentNames = new ArrayList<String>();
			List<StudentRowData> studentGrades = new ArrayList<StudentRowData>();
			
			String currentUser = "";
			String lastUser = "initial value";
			
			// iterate on rows
			while (rs.next()) {
				if (!currentUser.equals(lastUser)) {
					currentUser = rs.getString("enr.username");
					//studentGrades.add(new StudentRowData("currentUser", ))
				}
				
				
				rs.getDouble("uw.points");
			}
			
			GradebookData gradebookData = new GradebookData(assignmentNames, studentGrades);
			
			return gradebookData;
		}
	};
}
