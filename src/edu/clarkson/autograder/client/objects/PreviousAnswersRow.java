package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PreviousAnswersRow implements Serializable {

	private int sequenceValue;
	private String previousUserAnswer;
	private String previousCorrectAnswer;
	
    /**
	 * Constructor
	 * 
	 * @param sequenceValue
	 *            used for sorting
	 * @param previousUserAnswer
	 * @param previousCorrectAnswer
	 */
	public PreviousAnswersRow(int sequenceValue, String previousUserAnswer, String previousCorrectAnswer) {
		this.sequenceValue = sequenceValue;
		this.previousUserAnswer = previousUserAnswer;
		this.previousCorrectAnswer = previousCorrectAnswer;
    }

	/**
	 * Default constructor required for serialization
	 */
	public PreviousAnswersRow() {
	}

	public int getSequenceValue() {
		return sequenceValue;
	}

	public String getPreviousUserAnswer() {
		return previousUserAnswer;
	}

	public String getPreviousCorrectAnswer() {
		return previousCorrectAnswer;
	}
}
