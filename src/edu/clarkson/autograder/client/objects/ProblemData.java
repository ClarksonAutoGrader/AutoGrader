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

@SuppressWarnings("serial")
public class ProblemData implements Serializable {

	private Problem problem;

	private String bodyMarkup;

	private Permutation permutation;

	private UserWork userWork;

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            {@link edu.clarkson.autograder.client.objects.Problem} wrapped
	 *            within this data object
	 * @param bodyMarkup
	 *            raw markup used to render the problem body on the clientside
	 * @param permutation
	 *            problem permutation
	 * @param userWork
	 *            contains unique user work ID and user answers
	 */
	public ProblemData(Problem problem, String bodyMarkup, Permutation permutation, UserWork userWork) {
		this.problem = problem;
		this.bodyMarkup = bodyMarkup;
		this.permutation = permutation;
		this.userWork = userWork;
	}

	/**
	 * Default constructor required for serialization
	 */
	public ProblemData() {
	}

	/**
	 * @return true if the state of this object is consistent
	 */
	public boolean isValid() {
		return true;
	}

	/**
	 * Wraps {@link edu.clarkson.autograder.client.objects.Problem#getId()}
	 */
	public int getProblemId() {
		return problem.getId();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.Problem#getAssignmentId()}
	 */
	public int getAssignmentId() {
		return problem.getAssignmentId();
	}

	/**
	 * Wraps {@link edu.clarkson.autograder.client.objects.Problem#getTitle()}
	 */
	public String getTitle() {
		return problem.getTitle();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.Problem#getPointsPossible()}
	 */
	public double getPointsPossible() {
		return problem.getPointsPossible();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.Problem#getResetsAllowed()}
	 */
	public double getResetsAllowed() {
		return problem.getResetsAllowed();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.Problem#getAttemptsAllowed()}
	 */
	public double getAttemptsAllowed() {
		return problem.getAttemptsAllowed();
	}

	/**
	 * @return raw markup used to render the problem body on the clientside
	 */
	public String getBodyMarkup() {
		return bodyMarkup;
	}

	/**
	 * Wraps {@link edu.clarkson.autograder.client.objects.Permutation#getId()}
	 */
	public int getPermutationId() {
		return permutation.getId();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.Permutation#getNumInputs()}
	 */
	public int getNumInputs() {
		return permutation.getNumInputs();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.Permutation#getNumAnswers()}
	 */
	public int getNumAnswers() {
		return permutation.getNumAnswers();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.Permutation#getInputString()}
	 */
	public String getInputString(int index) {
		return permutation.getInputString(index);
	}

	/**
	 * Wraps {@link edu.clarkson.autograder.client.objects.UserWork#getId()}
	 */
	public int getUserWorkId() {
		return userWork.getId();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.UserWork#getAttemptsUsed()}
	 */
	public int getAttemptsUsed() {
		return userWork.getAttemptsUsed();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.UserWork#getResetsUsed()}
	 */
	public int getResetsUsed() {
		return userWork.getResetsUsed();
	}

	/**
	 * Wraps {@link edu.clarkson.autograder.client.objects.UserWork#getPoints()}
	 */
	public double getPointsEarned() {
		return userWork.getPoints();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.UserWork#getUserAnswers()}
	 */
	public String[] getUserAnswers() {
		return userWork.getUserAnswers();
	}
}
