package edu.clarkson.autograder.client.objects;

import java.sql.Date;

import edu.clarkson.autograder.client.Data;
import edu.clarkson.autograder.client.widgets.Listable;

public class Assignment implements Listable {
    private int id;
    private int cId;
    private boolean visible;
    private String title;
    private Date openTime;
    private Date closeTime;

    /**
     * No argument constructor
     */
    public Assignment() {
    }

    /**
     * Convenience constructor (temporary)
     * 
     * @param id
     * @param cId
     * @param visible
     * @param title
     * @param openTime
     * @param closeTime
     */
    public Assignment(int id, int cId, boolean visible, String title, Date openTime, Date closeTime) {
        super();
        this.id = id;
        this.cId = cId;
        this.visible = visible;
        this.title = title;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder(60);
        builder.append("Due: ");
        // builder.append(closeTime.getDay()).append(", ");
        builder.append(closeTime.getMonth()).append("/");
        builder.append(closeTime.getDate()).append("/");
        builder.append(closeTime.getYear() % 100);

        return builder.toString();
    }

    @Override
    public String getToken() {
        return Data.formatIdToken(cId) + Data.formatIdToken(id);
    }
}