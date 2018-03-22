/*
	Autograder is an online homework tool used by Clarkson University.
	
	Copyright 2017-2018 Clarkson University.
	
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
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.services.CourseFromIdService;

@SuppressWarnings("serial")
public class CourseFromIdServiceImpl extends RemoteServiceServlet implements CourseFromIdService {
	
	private static ConsoleHandler LOG = new ConsoleHandler();
	
	@Override
	public Course fetchCourseFromId(int courseId) {
		
		LOG.publish(new LogRecord(Level.INFO, "#fetchCourseFromId - begin"));
		
		Database db = new Database();
		Course course = db.query(processResultSetCallback, Database.selectCourseFromIdSql, ServerUtils.getUsername(),
				courseId);

		if (course == null) {
			throw new RuntimeException("Course was null");
		}
		
		db.closeConnection();
		
		LOG.publish(new LogRecord(Level.INFO, "#fetchCourseFromId - end"));

		return course;
		
	}

	private ProcessResultSetCallback<Course> processResultSetCallback = new ProcessResultSetCallback<Course>() {

		@Override
		public Course process(ResultSet rs) throws SQLException {

			LOG.publish(new LogRecord(Level.INFO, "CourseFromIdServiceImpl#process - begin"));

			Course course = null;
			rs.next();
			course = new Course(rs.getInt("course_id"), rs.getString("course_title"));
			LOG.publish(new LogRecord(Level.INFO, "Course: " + rs.getString("course_title")));

			LOG.publish(new LogRecord(Level.INFO, "CourseFromIdServiceImpl#process - end"));
			return course;
		}

	};

}