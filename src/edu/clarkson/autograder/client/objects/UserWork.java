package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserWork implements Serializable {

	private int id;
	private int problemId;
	private int permutationId;
	private int resetsUsed;
	private int attemptsUsed;
	private double points;
	private String[] userAnswers;
	
    /**
	 * Constructor
	 * 
	 * @param id
	 *            unique user work solution ID
	 * @param problemId
	 *            corresponding problem ID
	 * @param permutationId
	 *            corresponding permutation ID
	 * @param resetsUsed
	 *            number of resets (new questions) available to the user so far
	 *            this problem
	 * @param attemptsUsed
	 *            number of attempts (submissions) used to the user so far this
	 *            permutation
	 * @param points
	 *            earned from user answers
	 * @param userAnswers
	 *            list of at user answers end-padded to length 10 with null
	 */
	public UserWork(int id, int problemId, int permutationId, int resetsUsed, int attemptsUsed, double points,
	        String[] userAnswers) {
        this.id = id;
		this.problemId = problemId;
		this.permutationId = permutationId;
		this.resetsUsed = resetsUsed;
		this.attemptsUsed = attemptsUsed;
		this.points = points;
		this.userAnswers = userAnswers;
    }

	/**
	 * Default constructor required for serialization
	 */
	public UserWork() {
	}

	/**
	 * @return user work solution ID corresponding to {@link #getUserAnswers()}
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return problem ID corresponding to this userWork
	 */
	public int getProblemId() {
		return problemId;
	}

	/**
	 * @return permutation ID corresponding to this userWork
	 */
	public int getPermutationId() {
		return permutationId;
	}

	/**
	 * @return number of resets (new questions) available to the user so far
	 *         this problem
	 */
	public int getResetsUsed() {
		return resetsUsed;
	}

	/**
	 * @return number of attempts (submissions) used to the user so far this
	 *         permutation
	 */
	public int getAttemptsUsed() {
		return attemptsUsed;
	}

	/**
	 * @return points earned for this set of user answers
	 */
	public double getPoints() {
		return points;
	}

	/**
	 * @return user answers (end-padded to length 10 with null)
	 */
	public String[] getUserAnswers() {
		return userAnswers;
	}
}
