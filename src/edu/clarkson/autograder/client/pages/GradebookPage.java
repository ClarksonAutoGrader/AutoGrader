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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.objects.GradebookData;
import edu.clarkson.autograder.client.objects.StudentRowData;
import edu.clarkson.autograder.client.services.CourseFromIdService;
import edu.clarkson.autograder.client.services.CourseFromIdServiceAsync;
import edu.clarkson.autograder.client.services.GradebookDataService;
import edu.clarkson.autograder.client.services.GradebookDataServiceAsync;
import edu.clarkson.autograder.client.widgets.Content;

public class GradebookPage extends Content {
	
	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();
	
	private int courseId;
	
	private Label courseName;
	
	private FlowPanel toplevel;
	
	private DataGrid<StudentRowData> gradebookDataTable = new DataGrid<StudentRowData>();
	
	public GradebookPage(int courseId) {
		this.courseId = courseId;
		LOG.publish(new LogRecord(Level.INFO, "Attempt to create gradebook page"));
		
		// page title
		courseName = new Label();
		courseName.setStyleName("gradebookPageTitle");
		
		// gradebook data grid
		LayoutPanel layout = new LayoutPanel();
		layout.addStyleName("gradebookWrapper");
		layout.add(gradebookDataTable);

		toplevel = new FlowPanel();
		toplevel.add(layout);
		toplevel.add(courseName);

		requestCourseFromIdAsync();
		requestGradebookDataAsync();
		
		initWidget(toplevel);
	}
	
	private void requestCourseFromIdAsync() {
		LOG.publish(new LogRecord(Level.INFO, "Gradebook#requestCourseFromIdAsync - begin"));

		CourseFromIdServiceAsync courseFromIdService = GWT.create(CourseFromIdService.class);
		courseFromIdService.fetchCourseFromId(courseId, new AsyncCallback<Course>() {
			@Override
			public void onFailure(Throwable caught) {
				final String failureToLoadCourse = "Failed to load course.";
				LOG.publish(new LogRecord(Level.INFO, "Gradebook#requestCourseFromIdAsync - onFailure"));
				toplevel.clear();
				Label errorLabel = new Label(failureToLoadCourse);
				errorLabel.setStyleName("errorLabel");
				toplevel.add(errorLabel);
			}

			@Override
			public void onSuccess(Course course) {
				LOG.publish(new LogRecord(Level.INFO, "Gradebook#requestCourseFromIdAsync - onSuccess"));
				courseName.setText(course.getTitle());
			}
		});
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestCourseFromIdAsync - end"));
	}
	
	private void requestGradebookDataAsync() {
		GradebookDataServiceAsync gradebookDataService = GWT.create(GradebookDataService.class);
		gradebookDataService.fetchGradebookData(courseId, new AsyncCallback<GradebookData>() {
			@Override
			public void onFailure(Throwable caught) {
				final String failureToLoadCourse = "Failed to load gradebook.";
				LOG.publish(new LogRecord(Level.INFO, "Gradebook#requestCourseFromIdAsync - onFailure"));
				toplevel.clear();
				Label errorLabel = new Label(failureToLoadCourse);
				errorLabel.setStyleName("errorLabel");
				toplevel.add(errorLabel);
			}

			@Override
			public void onSuccess(GradebookData data) {
				updateGradebook(data);
			}
		});
		
	}
	
	private void updateGradebook(GradebookData studentData) {
		// create name column
				TextColumn<StudentRowData> names = new TextColumn<StudentRowData>() {
					@Override
					public String getValue(StudentRowData currentStudent) {
						return currentStudent.getName();
					}
				};
				gradebookDataTable.addColumn(names, "Student Username");
				
				// create assignment grade columns
				List<TextColumn<StudentRowData>> gradeColumns = new ArrayList<TextColumn<StudentRowData>>();
					for (int index = 0; index < studentData.getAssignmentNames().size(); index++) {
						final int currentIndex = index;
						gradeColumns.add(new TextColumn<StudentRowData>() {
							@Override
							public String getValue(StudentRowData currentStudent) {
								return Double.toString(currentStudent.getGrade(currentIndex));
							}
						});
					}
					
				int currentIndex = 0;
				for(TextColumn<StudentRowData> assignColumn : gradeColumns) {
					gradebookDataTable.addColumn(assignColumn, studentData.getAssignmentNames().get(currentIndex++));
				}
				
				// populate gradebookDataTable with temp hardcoded values
				LOG.publish(new LogRecord(Level.INFO, "Number of students: " + studentData.getClassSize()));
				
				gradebookDataTable.setVisibleRange(0, studentData.getClassSize());
				gradebookDataTable.setRowCount(studentData.getClassSize(), true);
				gradebookDataTable.setRowData(0, studentData.getStudentGrades());
				
				LOG.publish(new LogRecord(Level.INFO, "DataTable populated successfully."));
				
				for(int i = 0; i < gradeColumns.size(); i++) {
					gradebookDataTable.setColumnWidth(i, "10em");
				}	
	}

	@Override
	public String getPrimaryStyleName() {
		return "gradebookPage";
	}
}
