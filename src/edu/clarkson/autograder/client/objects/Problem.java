package edu.clarkson.autograder.client.objects;

import Exceptions.OutOfResetsException;

/**
 * Problem number
 * Problem body
 * Problem total points
 * Points per question
 * 
 * DOES NOT INCLUDE:
 * Problem permutation data
 * 
 */
public class Problem {

	//Problem number
	private int problemNumber;
	//Total points possible
	private int totalPoints;
	//The problem content containing all numbers and data needed
	private String problemContent;
	//The number of resets remaining for the students to use
	private int resetsRemaining;
	
	public Problem(int problemNumber, int totalPoints, String problemContent, int resets){
		this.problemNumber = problemNumber;
		this.totalPoints = totalPoints;
		this.problemContent = problemContent;
		resetsRemaining = resets;
	}
	
	//Gets all data about a Problem object
	public int getProblemNumber(){ return problemNumber; }
	public int getTotalPoints() { return totalPoints; }
	public String getProblemContent(){ return problemContent; }
	public int getResetsRemaining() { return resetsRemaining; }
	
	//Decrease the value of resetsRemaining by 1
	//If the user doesn't have any resets remaining, throws OutOfResetsException
	public void incrementResets(){ 
		if(resetsRemaining == 0){
			throw new OutOfResetsException("User out of resets");
		}
		else{
			resetsRemaining--;
		}
	}
		
}
