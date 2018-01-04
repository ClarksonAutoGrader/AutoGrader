package edu.clarkson.autograder.client.objects;

import java.util.ArrayList;
import java.util.List;

//This class holds the name and list of grades for one student
public class StudentRowData {
	
	private String name;
	private List<Double> grades = new ArrayList<Double>();
	
	public StudentRowData(String studentName, ArrayList<Double> studentGrades) {
		this.name = studentName;
		this.grades.addAll(studentGrades);
	}
	
	public String getName() {
		return this.name;
	}
	
	public ArrayList<Double> getGrades() {
		return this.getGrades();
	}
}
