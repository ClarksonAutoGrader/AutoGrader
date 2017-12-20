package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

import edu.clarkson.autograder.client.Autograder;

@SuppressWarnings("serial")
public class Course implements Serializable {
	private int id;
	private String title;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            unique course id
	 * @param title
	 *            course title (any String)
	 */
	public Course(int id, String title) {
		this.id = id;
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getToken() {
		return Autograder.formatIdToken(id);
	}
}