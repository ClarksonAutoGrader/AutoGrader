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
public class Permutation implements Serializable {

	private int id;
	private int problemId;
	private int numInputs;
	private int numAnswers;
	private String[] inputs;
	
    /**
	 * Constructor
	 * 
	 * @param id
	 *            unique permutation id
	 * @param problemId
	 *            enclosing problem
	 * @param numInputs
	 *            number of valid indices in String[] inputs
	 * @param numAnswers
	 *            number of question answers
	 * @param inputs
	 *            data to be injected into problem body
	 */
	public Permutation(int id, int problemId, int numInputs, int numAnswers, String[] inputs) {
        this.id = id;
		this.problemId = problemId;
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

	public int getProblemId() {
		return problemId;
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
