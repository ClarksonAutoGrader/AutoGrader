/*
	Autograder is an online homework tool used by Clarkson University.
	
	Copyright ©2017-2018 Clarkson University.
	
	This file is part of Autograder.
	
	This program is licensed under the GNU General Purpose License version 3.
	
	Autograder is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	Autograder is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with Autograder. If not, see <http://www.gnu.org/licenses/>.
*/

package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

	@Override
	public GradebookData fetchGradebookData(int courseId) throws IllegalArgumentException {

		LOG.publish(new LogRecord(Level.INFO, "GradebookDataServiceImpl#fetchGradebookData - begin"));

		Database db = new Database();
		GradebookData data = db.query(processResultSetCallback, Database.selectGradebookDataSql, courseId);
		db.closeConnection();

		LOG.publish(new LogRecord(Level.INFO, "GradebookDataServiceImpl#fetchGradebookData - end"));
		return data;
	}

	private ProcessResultSetCallback<GradebookData> processResultSetCallback = new ProcessResultSetCallback<GradebookData>() {

		@Override
		public GradebookData process(ResultSet rs) throws SQLException {
			
			LOG.publish(new LogRecord(Level.INFO, "GradebookDataServiceImpl#process - begin"));
			
			List<StudentRowData> studentRowDataList = new ArrayList<StudentRowData>();
			
			String currentUsername = "initial value";
			String lastUsername = "initial value";
			double currentUserPoints = -1;
			
			List<Double> currentStudentPoints = new ArrayList<Double>();
			
			// iterate on rows
			while (rs.next()) {
				
				currentUsername = rs.getString("e.enr_username");
				final String userRole = rs.getString("u.user_role");
				if (!userRole.equals("student")) {
					currentUsername += " (" + userRole + ")";
				}

				currentUserPoints = rs.getDouble("uw.points");
				
				// test if a new user
				if (currentUsername.equals(lastUsername)) {
					currentStudentPoints.add(currentUserPoints);
				} else {
					
					if(!rs.isFirst()) {
						studentRowDataList.add(new StudentRowData(lastUsername, currentStudentPoints));
					}
					
					lastUsername = currentUsername;
					
					currentStudentPoints = new ArrayList<Double>();
					currentStudentPoints.add(rs.getDouble("uw.points"));
				}
			}
			studentRowDataList.add(new StudentRowData(lastUsername, currentStudentPoints));
			
			// add assignment names to list
			ArrayList<String> assignmentNames = new ArrayList<String>();
			rs.first();
			String firstUser = rs.getString("enr_username");
			assignmentNames.add(rs.getString("assignment_title") + " (" + rs.getDouble("prob.points_possible") + " points)");
			while(rs.next() && rs.getString("enr_username").equals(firstUser)) {
				assignmentNames.add(rs.getString("assignment_title") + " (" + rs.getDouble("prob.points_possible") + " points)");
			}
			
			GradebookData gradebookData = new GradebookData(assignmentNames, studentRowDataList);
			
			return gradebookData;
		}
	};
}
