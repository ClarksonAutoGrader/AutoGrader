/*
	Autograder is an online homework tool used by Clarkson University.
	
	Copyright ©2017-2018 Clarkson University.
	
	This file is part of Autograder.
	
	This program is licensed under the GNU General Purpose License version 3.
	
	Autograder is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	Autograder is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with Autograder. If not, see <http://www.gnu.org/licenses/>.
*/

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
