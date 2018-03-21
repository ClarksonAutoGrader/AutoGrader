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

@SuppressWarnings("serial")
public class Problem implements Serializable {

	private int id;
	private int assignmentId;
	private String title;
	private double pointsPossible;
	private double pointsEarned;
	private int numResetsAllowed;
	private int numAttemptsAllowed;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            unique problem id
	 * @param assignmentId
	 *            parent assignment
	 * @param title
	 *            name of problem
	 * @param pointsPossible
	 *            total points possible across all questions
	 * @param earnedPoints
	 *            earned points across all questions
	 * @param numResetsAllowed
	 *            total allowable resets (new question permutations) the user
	 *            may request for this problem
	 * @param numAttemptsAllowed
	 *            total allowable attempts (submissions) the user may request
	 *            for this problem
	 */
	public Problem(int id, int assignmentId, String title, double pointsPossible, double pointsEarned,
	        int numResetsAllowed,
	        int numAttemptsAllowed) {
		this.id = id;
		this.assignmentId = assignmentId;
		this.title = title;
		this.pointsPossible = pointsPossible;
		this.pointsEarned = pointsEarned;
		this.numResetsAllowed = numResetsAllowed;
		this.numAttemptsAllowed = numAttemptsAllowed;
	}

	/**
	 * Default constructor required for serialization
	 */
	public Problem() {
	}

	public int getId() {
		return id;
	}

	public int getAssignmentId() {
		return assignmentId;
	}

	public String getTitle() {
		return title;
	}

	public double getPointsPossible() {
		return pointsPossible;
	}

	public double getPointsEarned() {
		return pointsEarned;
	}

	public int getResetsAllowed() {
		return numResetsAllowed;
	}

	public int getAttemptsAllowed() {
		return numAttemptsAllowed;
	}
}
