package edu.clarkson.autograder.client.objects;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Assignment implements Serializable, Comparable<Assignment> {
	private int id;
	private int cId;
	private String title;
	private Date dueDate;
	private double totalPoints;
	private double earnedPoints;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            unique assignment id
	 * @param cId
	 *            parent course
	 * @param title
	 *            course title
	 * @param dueDate
	 *            assignments after due date cannot be worked on for a grade
	 * @param totalPoints
	 *            cumulative total points across all problems in this assignment
	 * @param earnedPoints
	 *            cumulative earned points across all problems in this
	 *            assignment
	 */
	public Assignment(int id, int cId, String title, Date dueDate, double totalPoints, double earnedPoints) {
		this.id = id;
		this.cId = cId;
		this.title = title;
		this.dueDate = dueDate;
		this.totalPoints = totalPoints;
		this.earnedPoints = earnedPoints;
	}

	/**
	 * Default constructor required for serialization
	 */
	public Assignment() {
	}

	public int getId() {
		return id;
	}

	public int getcId() {
		return cId;
	}

	public String getTitle() {
		return title;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public double getTotalPoints() {
		return totalPoints;
	}

	public double getEarnedPoints() {
		return earnedPoints;
	}

	/**
	 * Sort assignments by due date, future to past
	 */
	@Override
	public int compareTo(Assignment other) {
		Date now = new Date();
		if (getDueDate().compareTo(now) > 0) {
			return getDueDate().compareTo(other.getDueDate());
		} else {
			return other.getDueDate().compareTo(getDueDate());
		}
	}
}