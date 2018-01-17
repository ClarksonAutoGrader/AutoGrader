package edu.clarkson.autograder.client.objects;

import java.io.Serializable;
import java.util.List;

/**
 * This class holds the name and list of grades for one student
 */
@SuppressWarnings("serial")
public class StudentRowData implements Serializable {
	
	private String name;
	private List<Double> grades;
	
	public StudentRowData(String studentName, List<Double> studentGrades) {
		this.name = studentName;
		this.grades = new java.util.ArrayList<Double>(studentGrades);
	}
	
	/**
	 * Default constructor required for serialization
	 */
	public StudentRowData() {
	}
	
	public String getName() {
		return name;
	}
	
	public double getGrade(int index) {
		return grades.get(index);
	}
	
	public int getNumGrades() {
		return grades.size();
	}
}
