package edu.clarkson.autograder.client.objects;

public class Student {
	public String username;
	public double[] grades = new double[10];
	
	// not robust, simply for testing now
	public Student(String name, double[] gradeData) {
		this.username = name;
		System.arraycopy(gradeData, 0, grades, 0, gradeData.length);
	}
}
