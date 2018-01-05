package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

/**
 * This class holds the name and list of grades for one student
 */
@SuppressWarnings("serial")
public class StudentRowData implements Serializable {
	
	private String name;
	//private List<Double> grades = new ArrayList<Double>();
	private double[] grades;
	
	public StudentRowData(String studentName, double[] studentGrades) {
		this.name = studentName;
		this.grades = studentGrades;
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
		return grades[index];
	}
	
	public int getNumGrades() {
		return grades.length;
	}
}
