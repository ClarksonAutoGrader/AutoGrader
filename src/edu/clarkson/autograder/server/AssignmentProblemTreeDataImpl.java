package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Problem;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataService;

@SuppressWarnings("serial")
public class AssignmentProblemTreeDataImpl extends RemoteServiceServlet implements AssignmentProblemTreeDataService {

	private static ConsoleHandler LOG = new ConsoleHandler();
	private int courseId;

	@Override
	public SortedMap<Assignment, List<Problem>> fetchTreeData(int courseId) {
		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - begin"));

		String username = ServerUtils.getUsername();
		this.courseId = courseId;
		
		Database db = new Database();
		TreeMap<Assignment, List<Problem>> data = db.query(processResultSetCallback,
				Database.selectAssignmentTreeDataSql, username, username, courseId);
		db.closeConnection();
		
		LOG.publish(new LogRecord(Level.INFO, "AssignmentProblemTreeDataImpl#fetchTreeData - end"));
		
		return data;
	}
	
	private ProcessResultSetCallback<TreeMap<Assignment, List<Problem>>> processResultSetCallback = new ProcessResultSetCallback<TreeMap<Assignment, List<Problem>>>() {

		
		@Override
		public TreeMap<Assignment, List<Problem>> process(ResultSet rs) throws SQLException {

			TreeMap<Assignment, List<Problem>> map = new TreeMap<Assignment, List<Problem>>();

			if (!rs.next()) {
				return map;
			}
			rs.beforeFirst();

			// put resultSet into map
			Assignment assign = null;
			double assignmentPointsPossible = 0;
			double assignmentPointsEarned = 0;
			List<Problem> problemSet = new ArrayList<Problem>();
			int currentAssignId = -1;
			int previousAssignId = -1;
			while (rs.next()) {
				currentAssignId = rs.getInt("a.assignment_id");
				final Problem currentProb = new Problem(rs.getInt("prob.problem_id"), currentAssignId,
						rs.getString("prob.problem_title"), rs.getDouble("prob.points_possible"),
						rs.getDouble("uw.points"), rs.getInt("prob.num_new_questions_allowed"),
						rs.getInt("prob.num_check_allowed"));

				if (currentAssignId == previousAssignId) {
					// add problem to the problem set for the assignment
					// currently being processed
					problemSet.add(currentProb);

					assignmentPointsPossible += currentProb.getPointsPossible();
					assignmentPointsEarned += currentProb.getPointsEarned();

				} else {
					// this must be a new assignment-problem set

					// commit previous set to map, unless its the first
					// assignment processed
					if (!rs.isFirst()) {
						// update assignment with point totals
						assign = new Assignment(assign.getId(), assign.getcId(), assign.getTitle(), assign.getDueDate(),
								assignmentPointsPossible, assignmentPointsEarned);
						map.put(assign, problemSet);
					}

					assignmentPointsPossible = currentProb.getPointsPossible();
					assignmentPointsEarned = currentProb.getPointsEarned();

					// create the new assignment with placeholder points
					assign = new Assignment(currentAssignId, courseId, rs.getString("a.assignment_title"),
							rs.getDate("a.due_date"), 0.0, 0.0);
					previousAssignId = currentAssignId;

					// create the new problemSet
					problemSet = new ArrayList<Problem>();
					problemSet.add(currentProb);
				}
			}
			// update assignment with point totals
			assign = new Assignment(assign.getId(), assign.getcId(), assign.getTitle(), assign.getDueDate(),
			        assignmentPointsPossible, assignmentPointsEarned);
			map.put(assign, problemSet);

			LOG.publish(new LogRecord(Level.INFO, "Database#queryAssignmentProblemTreeData - end"));
			return map;
		}
	};

}
