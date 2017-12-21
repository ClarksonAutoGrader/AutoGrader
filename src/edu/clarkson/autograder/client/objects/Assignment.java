package edu.clarkson.autograder.client.objects;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Assignment implements Serializable {
    private int id;
    private int cId;
    private String title;
	private Date dueDate;

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
	 */
	public Assignment(int id, int cId, String title, Date dueDate) {
        this.id = id;
        this.cId = cId;
        this.title = title;
		this.dueDate = dueDate;
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
}