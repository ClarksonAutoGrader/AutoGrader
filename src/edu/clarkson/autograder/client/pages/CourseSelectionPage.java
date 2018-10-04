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

package edu.clarkson.autograder.client.pages;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.Autograder;
import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.services.CoursesService;
import edu.clarkson.autograder.client.services.CoursesServiceAsync;
import edu.clarkson.autograder.client.widgets.Content;
import edu.clarkson.autograder.client.widgets.Listing;

/**
 * Users select a course from among enrolled courses.
 */
public class CourseSelectionPage extends Content {
	
	public static final String TOKEN = "courses";

	public static final SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	private FlexTable courseTable = new FlexTable();

	public CourseSelectionPage() {
		requestCourseListAsync();

		// Create page title
		Label pageTitle = new Label("Enrolled Courses");
		pageTitle.addStyleName("courseSelectionPageTitle");

		// Configure empty course listing courseTable
		courseTable.setCellSpacing(6);

		// Add elements to page
		VerticalPanel layout = new VerticalPanel();
		layout.add(pageTitle);
		layout.add(courseTable);

		initWidget(layout);
	}

	private void requestCourseListAsync() {
		LOG.publish(new LogRecord(Level.INFO, "CourseSelectionPage#requestCourseListAsync - begin"));
		CoursesServiceAsync courseService = GWT.create(CoursesService.class);
		courseService.fetchCourses(new AsyncCallback<List<Course>>() {
			@Override
			public void onFailure(Throwable caught) {
				LOG.publish(new LogRecord(Level.INFO, "CourseSelectionPage#requestCourseListAsync - onFailure"));
				Label errorLabel = new Label("Failed to load enrolled courses.");
				errorLabel.addStyleName("errorLabel");
				courseTable.setWidget(0, 0, errorLabel);
			}

			@Override
			public void onSuccess(List<Course> courseList) {
				LOG.publish(new LogRecord(Level.INFO, "CourseSelectionPage#requestCourseListAsync - onSuccess"));
				courseTable.clear();

				if (courseList.isEmpty()) {
					Label errorLabel = new Label("You are not enrolled in any courses.");
					errorLabel.addStyleName("errorLabel");
					courseTable.setWidget(0, 0, errorLabel);
				} else {

					// populate table of course listings
					for (Course course : courseList) {
						courseTable.setWidget(courseTable.getRowCount(), 0, new Listing(course.getTitle(), Autograder.formatIdToken(course.getId())));
					}
				}
			}
		});
	}

	@Override
	public String getPrimaryStyleName() {
		return "courseSelectionPage";
	}

}
