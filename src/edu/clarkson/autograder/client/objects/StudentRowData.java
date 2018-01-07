package edu.clarkson.autograder.client.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the name and list of grades for one student
 */
@SuppressWarnings("serial")
public class StudentRowData implements Serializable {
	
	private String name;
	private List<Double> grades = new ArrayList<Double>();
	
	public StudentRowData(String studentName, ArrayList<Double> studentGrades) {
		this.name = studentName;
		this.grades.addAll(studentGrades);
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
