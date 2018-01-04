package edu.clarkson.autograder.client.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.objects.Student;
import edu.clarkson.autograder.client.objects.StudentRowData;
import edu.clarkson.autograder.client.widgets.Content;

public class GradebookPage extends Content {
	
	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();
	
	public DataGrid<StudentRowData> gradebookDataTable = new DataGrid<StudentRowData>();
	
	// temporary hardcoded data values
	
	private List<StudentRowData> studentList = Arrays.asList(
			new StudentRowData("woodrj", new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))),
			new StudentRowData("clappdj", new ArrayList<Double>(Arrays.asList(7.0, 8.0, 9.0, 10.0, 11.0, 12.0))),
			new StudentRowData("murphycd", new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))),
			new StudentRowData("sheppa", new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))),
			new StudentRowData("belkelk", new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)))
			);
	
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
		
			for (int index = 0; index < studentList.get(0).getNumGrades(); index++)
			{
				final int currentIndex = index;
				gradeColumns.add(new TextColumn<StudentRowData>() {
					@Override
					public String getValue(StudentRowData currentStudent) {
						return Double.toString(currentStudent.getGrade(currentIndex));
					}
				});
			}
			
		for(TextColumn<StudentRowData> assignColumn : gradeColumns) {
			gradebookDataTable.addColumn(assignColumn, "Assignment Name is really long");
		}
		
		// populate gradebookCellTable with temp hardcoded values
		LOG.publish(new LogRecord(Level.INFO, "Number of students: " + studentList.size()));
		
		gradebookDataTable.setVisibleRange(0, studentList.size());
		gradebookDataTable.setRowCount(studentList.size(), true);
		gradebookDataTable.setRowData(0, studentList);
		
		LOG.publish(new LogRecord(Level.INFO, "DataTable populated successfully."));
		
		gradebookDataTable.setStyleName("gradebookDataTable");
		
		for(int i = 0; i < gradeColumns.size(); i++) {
			gradebookDataTable.setColumnWidth(i, "10em");
		}
		
		
		// add widgets to page
		LayoutPanel layout = new LayoutPanel();
		layout.ensureDebugId("layoutPanel");
		layout.add(gradebookDataTable);
		
		initWidget(layout);
	}

	@Override
	public String getPrimaryStyleName() {
		return "gradebookPage";
	}
}
