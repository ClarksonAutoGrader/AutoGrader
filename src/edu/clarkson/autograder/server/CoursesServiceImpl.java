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

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.services.CoursesService;

@SuppressWarnings("serial")
public class CoursesServiceImpl extends RemoteServiceServlet implements CoursesService {
	
	private static ConsoleHandler LOG = new ConsoleHandler();
	
	@Override
	public List<Course> fetchCourses() {
		
		LOG.publish(new LogRecord(Level.INFO, "#fetchCourses - begin"));
		
		Database db = new Database();
		List<Course> courses = db.query(processResultSetCallback, Database.selectCoursesSql, ServerUtils.getUsername());
		db.closeConnection();
		LOG.publish(new LogRecord(Level.INFO, "#fetchCourses - end"));

		return courses;
		
	}

	private ProcessResultSetCallback<List<Course>> processResultSetCallback = new ProcessResultSetCallback<List<Course>>() {

		@Override
		public List<Course> process(ResultSet rs) throws SQLException {

			LOG.publish(new LogRecord(Level.INFO, "CoursesServiceImpl#process - begin"));

			List<Course> courseList = new ArrayList<Course>();

			while (rs.next()) {
				courseList
						.add(new Course(Integer.parseInt(rs.getString("course_id")), rs.getString("course_title")));
				LOG.publish(new LogRecord(Level.INFO, "Course: " + rs.getString("course_title")));
			}

			LOG.publish(new LogRecord(Level.INFO, "Database#queryCourses - end"));
			return courseList;
		}
	};

}
