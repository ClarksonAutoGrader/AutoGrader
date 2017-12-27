package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Permutation implements Serializable {

	private int id;
	private int pId;
	private int numInputs;
	private int numAnswers;
	private String[] inputs;
	
    /**
	 * Constructor
	 * 
	 * @param id
	 *            unique permutation id
	 * @param pId
	 *            enclosing problem
	 * @param numInputs
	 *            number of valid indices in String[] inputs
	 * @param numAnswers
	 *            number of question answers
	 * @param inputs
	 *            data to be injected into problem body
	 */
	public Permutation(int id, int pId, int numInputs, int numAnswers, String[] inputs) {
        this.id = id;
		this.pId = pId;
		this.numInputs = numInputs;
		this.numAnswers = numAnswers;
		this.inputs = inputs;
    }

	/**
	 * Default constructor required for serialization
	 */
	public Permutation() {
	}

	public int getId() {
		return id;
	}

	public int getpId() {
		return pId;
	}

	public int getNumInputs() {
		return numInputs;
	}

	public int getNumAnswers() {
		return numAnswers;
	}

	public String getInputString(int index) {
		return inputs[index];
	}
}
