package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProblemData implements Serializable {

	private Problem problem;

	private String bodyMarkup;
	private int resets;
	private int attempts;

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            {@link edu.clarkson.autograder.client.objects.Problem} wrapped
	 *            within this data object
	 * @param bodyMarkup
	 *            raw markup used to render the problem body on the clientside
	 * @param resets
	 *            number of resets (new questions) available to the user
	 * @param attempts
	 *            number of attempts (submissions) available to the user
	 */
	public ProblemData(Problem problem, String bodyMarkup, int resets, int attempts) {
		this.problem = problem;
		this.bodyMarkup = bodyMarkup;
		this.resets = resets;
		this.attempts = attempts;
	}

	/**
	 * Default constructor required for serialization
	 */
	public ProblemData() {
	}

	/**
	 * Wraps {@link edu.clarkson.autograder.client.objects.Problem#getId()}
	 */
	public int getpId() {
		return problem.getId();
	}

	/**
	 * Wraps {@link edu.clarkson.autograder.client.objects.Problem#getaId()()}
	 */
	public int getaId() {
		return problem.getaId();
	}

	/**
	 * Wraps {@link edu.clarkson.autograder.client.objects.Problem#getTitle()()}
	 */
	public String getTitle() {
		return problem.getTitle();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.Problem#getTotalPoints()()}
	 */
	public double getTotalPoints() {
		return problem.getTotalPoints();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.Problem#getEarnedPoints()()}
	 */
	public double getEarnedPoints() {
		return problem.getEarnedPoints();
	}

	/**
	 * @return raw markup used to render the problem body on the clientside
	 */
	public String getBodyMarkup() {
		return bodyMarkup;
	}

	/**
	 * @return number of resets (new questions) available to the user
	 */
	public int getResets() {
		return resets;
	}

	/**
	 * @return number of attempts (submissions) available to the user
	 */
	public int getAttempts() {
		return attempts;
	}

}
