package edu.clarkson.autograder.client.objects;

import edu.clarkson.autograder.client.Data;
import edu.clarkson.autograder.client.widgets.Listable;

public class Course implements Listable {
    private int id;
    private String title;
    private String description;
    private boolean visible;

    /**
     * No argument constructor
     */
    public Course() {
    }

    /**
     * Convenience constructor (temporary)
     * 
     * @param id
     * @param title
     * @param description
     * @param visible
     */
    public Course(int id, String title, String description, boolean visible) {
        this.setId(id);
        this.title = title;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String getToken() {
        return Data.formatIdToken(id);
    }
}