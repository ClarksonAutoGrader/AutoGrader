package edu.clarkson.autograder.client.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class GradebookData implements Serializable {
	private List<String> assignmentNames = new ArrayList<String>();
	
	private List<StudentRowData> studentGrades = new ArrayList<StudentRowData>();
	
	public GradebookData(List<String> assignmentNames, List<StudentRowData> studentGrades) {
		this.assignmentNames.addAll(assignmentNames);
		this.studentGrades.addAll(studentGrades);
	}
	
	/**
	 * Default constructor required for serialization
	 */
	public GradebookData() {
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
