package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

import edu.clarkson.autograder.client.Autograder;

@SuppressWarnings("serial")
public class Course implements Serializable {
    private int id;
    private String title;
    private boolean visible;

    /**
     * No argument constructor
     */
    public Course() {
    }

    /**
	 * Constructor
	 * 
	 * @param id
	 * @param title
	 * @param visible
	 */
	public Course(int id, String title, boolean visible) {
        this.setId(id);
        this.title = title;
        this.visible = visible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getToken() {
		return Autograder.formatIdToken(id);
    }
}