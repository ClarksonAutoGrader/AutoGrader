package edu.clarkson.autograder.client.objects;

import java.util.ArrayList;
import java.util.List;

public class GradebookData {
	private List<String> assignmentNames = new ArrayList<String>();
	
	private List<StudentRowData> studentGrades = new ArrayList<StudentRowData>();
	
	public GradebookData(List<String> assignmentNames, List<StudentRowData> studentGrades) {
		this.assignmentNames.addAll(assignmentNames);
		this.studentGrades.addAll(studentGrades);
	}
	
	public List<String> getAssignmentNames() {
		return assignmentNames;
	}
	
	public List<StudentRowData> getStudentGrades() {
		return studentGrades;
	}
	
	public int getClassSize() {
		return studentGrades.size();
	}
}
