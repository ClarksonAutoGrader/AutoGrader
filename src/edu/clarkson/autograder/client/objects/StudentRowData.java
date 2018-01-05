package edu.clarkson.autograder.client.objects;

import java.util.ArrayList;
import java.util.List;

//This class holds the name and list of grades for one student
public class StudentRowData {
	
	private String name;
	//private List<Double> grades = new ArrayList<Double>();
	private double[] grades;
	
	public StudentRowData(String studentName, double[] studentGrades) {
		this.name = studentName;
		this.grades = studentGrades;
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
