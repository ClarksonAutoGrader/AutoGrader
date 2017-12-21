package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Problem implements Serializable {

	private int id;
	private int aId;
	private String title;
	private double totalPoints;
	private double earnedPoints;
	
	
    /**
	 * Constructor
	 * 
	 * @param id
	 *            unique problem id
	 * @param aId
	 *            parent assignment
	 * @param title
	 *            name of problem
	 * @param totalPoints
	 *            total points possible across all questions
	 * @param earnedPoints
	 *            earned points across all questions
	 */
	public Problem(int id, int aId, String title, double totalPoints, double earnedPoints) {
        this.id = id;
		this.aId = aId;
        this.title = title;
		this.totalPoints = totalPoints;
		this.earnedPoints = earnedPoints;
    }

	/**
	 * Default constructor required for serialization
	 */
	public Problem() {
	}

	public int getId() {
		return id;
	}

	public int getaId() {
		return aId;
	}

	public String getTitle() {
		return title;
	}

	public double getTotalPoints() {
		return totalPoints;
	}

	public double getEarnedPoints() {
		return earnedPoints;
	}
}
