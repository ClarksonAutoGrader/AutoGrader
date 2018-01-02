package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProblemData implements Serializable {

	private Problem problem;

	private String bodyMarkup;
	private int resets;
	private int attempts;

	private Permutation permutation;

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
	public ProblemData(Problem problem, String bodyMarkup, int resets, int attempts, Permutation permutation) {
		this.problem = problem;
		this.bodyMarkup = bodyMarkup;
		this.resets = resets;
		this.attempts = attempts;
		this.permutation = permutation;
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

	/**
	 * Wraps {@link edu.clarkson.autograder.client.objects.Permutation#getId()}
	 */
	public int getPermId() {
		return permutation.getId();
	}

	/**
	 * Wraps
	 * {@link edu.clarkson.autograder.client.objects.Permutation#getProblemId()}
	 */
	public int getProblemId() {
		return permutation.getpId();
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
	 * @return Permutation wrapped by this ProblemData
	 */
	public final Permutation getPermutation() {
		return permutation;
	}
}
