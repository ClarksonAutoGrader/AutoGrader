/*
	Autograder is an online homework tool used by Clarkson University.
	
	Copyright 2017-2018 Clarkson University.
	
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
import java.util.Date;

@SuppressWarnings("serial")
public class Assignment implements Serializable, Comparable<Assignment> {
	private int id;
	private int cId;
	private String title;
	private Date dueDate;
	private double totalPoints;
	private double earnedPoints;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            unique assignment id
	 * @param cId
	 *            parent course
	 * @param title
	 *            course title
	 * @param dueDate
	 *            assignments after due date cannot be worked on for a grade
	 * @param totalPoints
	 *            cumulative total points across all problems in this assignment
	 * @param earnedPoints
	 *            cumulative earned points across all problems in this
	 *            assignment
	 */
	public Assignment(int id, int cId, String title, Date dueDate, double totalPoints, double earnedPoints) {
		this.id = id;
		this.cId = cId;
		this.title = title;
		this.dueDate = dueDate;
		this.totalPoints = totalPoints;
		this.earnedPoints = earnedPoints;
	}

	/**
	 * Default constructor required for serialization
	 */
	public Assignment() {
	}

	public int getId() {
		return id;
	}

	public int getcId() {
		return cId;
	}

	public String getTitle() {
		return title;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public double getTotalPoints() {
		return totalPoints;
	}

	public double getEarnedPoints() {
		return earnedPoints;
	}

	/**
	 * Sort assignments by due date, future to past
	 */
	@Override
	public int compareTo(Assignment other) {
		Date now = new Date();
		if (getDueDate().compareTo(now) > 0) {
			return getDueDate().compareTo(other.getDueDate());
		} else {
			return other.getDueDate().compareTo(getDueDate());
		}
	}
}