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
