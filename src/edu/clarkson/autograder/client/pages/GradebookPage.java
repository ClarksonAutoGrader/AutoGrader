package edu.clarkson.autograder.client.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.objects.GradebookData;
import edu.clarkson.autograder.client.objects.StudentRowData;
import edu.clarkson.autograder.client.services.GradebookDataService;
import edu.clarkson.autograder.client.widgets.Content;

public class GradebookPage extends Content {
	
	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();
	
	private DataGrid<StudentRowData> gradebookDataTable = new DataGrid<StudentRowData>();
	
	// temporary hardcoded data values
	private GradebookData studentData = new GradebookData(
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
	
	public GradebookPage(int courseid) {
		LOG.publish(new LogRecord(Level.INFO, "Attempt to create gradebook page"));
		// page title
		Label pageTitle = new Label("Gradebook: (Hardcoded course number)");
		pageTitle.addStyleName("gradebookPageTitle");
		
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
		
			for (int index = 0; index < studentData.getAssignmentNames().size(); index++)
			{
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
		
		// add widgets to page
		Label courseName = new Label("Course Name");
		courseName.setStyleName("gradebookPageTitle");
		FlowPanel toplevel = new FlowPanel();
		
		LayoutPanel layout = new LayoutPanel();
		layout.addStyleName("gradebookWrapper");
		layout.add(gradebookDataTable);
		
		toplevel.add(layout);
		toplevel.add(courseName);
		
		
		initWidget(toplevel);
	}
	
	private void populateGradebookAsync() {
		GradebookDataServiceAsync GradebookDataSvc = GWT.create(GradebookDataService.class);
		
	}

	@Override
	public String getPrimaryStyleName() {
		return "gradebookPage";
	}
}
