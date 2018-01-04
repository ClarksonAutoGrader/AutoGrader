package edu.clarkson.autograder.client.objects;

import java.util.ArrayList;
import java.util.List;

//This class holds the name and list of grades for one student
public class StudentRowData {
	
	private String name;
	private int numGrades;
	private List<Double> grades = new ArrayList<Double>();
	
	public StudentRowData(String studentName, List<Double> studentGrades) {
		this.name = studentName;
		this.grades.addAll(studentGrades);
		this.numGrades = grades.size();
	}
	
	public String getName() {
		return name;
	}
	
	public double getGrade(int index) {
		return grades.get(index);
	}
	
	public int getNumGrades() {
		return numGrades;
	}
}
