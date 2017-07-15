package edu.clarkson.autograder.client.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.clarkson.autograder.client.Data;
import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.widgets.Content;
import edu.clarkson.autograder.client.widgets.Listing;

/**
 * Generate a page listing all assignments in the specified course.
 */
public class CoursePage extends Content {

    private Course course;
    private List<Assignment> assignments = null;

    private FlexTable assignmentsTable = new FlexTable();
    // TODO make persistance selection of listing
    private Listing activeAssignmentListing;

    /**
     * Load CoursePage with specified assignment selection.
     */
    public CoursePage(Course course, int initialSelectedAssignmentId) {

        this.course = course;
        loadAssignments();
        int selectedAssignmentId = initialSelectedAssignmentId;
        if (initialSelectedAssignmentId == -1) {
            selectedAssignmentId = selectDefaultAssignmentId();
        }

        // create assignment listings and set selected listing
        List<Listing> currentListings = new ArrayList<>();
        List<Listing> pastListings = new ArrayList<>();
        Date date = new Date();
        for (Assignment assignment : assignments) {
            Listing listing = new Listing(assignment);
            // set selected assignment
            if (assignment.getId() == selectedAssignmentId) {
                activeAssignmentListing = listing;
                activeAssignmentListing.setSelected(true);
            }

            // add listing to either "current" or "past" list
            if (date.before(assignment.getCloseTime())) {
                currentListings.add(listing);
            } else {
                pastListings.add(listing);
            }
        }

        // Create page header
        Label pageTitle = new Label(course.getTitle());
        pageTitle.addStyleName("pageTitle");
        Label subTitle = new Label(course.getDescription());
        subTitle.addStyleName("pageSubTitle");
        VerticalPanel pageHeader = new VerticalPanel();
        pageHeader.add(pageTitle);
        pageHeader.add(subTitle);
        // TODO center page header
        pageHeader.addStyleName("coursePageHeader");

        // Create assignments table
        // TODO: collapse assignment table to the left (using nifty chevrons)
        assignmentsTable.setCellSpacing(6);
        // current assignments
        assignmentsTable.setHTML(0, 0, "Current Assignments:   (" + currentListings.size() + ")");
        assignmentsTable.getCellFormatter().setStyleName(assignmentsTable.getRowCount(), 0, "assignmentsTableHeader");
        for (Listing listing : currentListings) {
            assignmentsTable.setWidget(assignmentsTable.getRowCount(), 0, listing);
        }
        // past assignments
        assignmentsTable.setHTML(assignmentsTable.getRowCount(), 0,
                "Past Assignments:   (" + pastListings.size() + ")");
        assignmentsTable.getCellFormatter().setStyleName(assignmentsTable.getRowCount(), 0, "assignmentsTableHeader");
        for (Listing listing : pastListings) {
            assignmentsTable.setWidget(assignmentsTable.getRowCount(), 0, listing);
        }

        assignmentsTable.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Cell c = assignmentsTable.getCellForEvent(event);
                if (c != null) {
                    Widget widget = assignmentsTable.getWidget(c.getRowIndex(), 0);
                    if (widget instanceof Listing) {
                        setSelectedAssignment((Listing) widget);
                    }
                }
            }
        });

        assignmentsTable.addStyleName("assignmentsTable");

        // Question layout pane
        // TODO put assignment view template in question layout pane
        // TODO populate assignment view template with question data from DB
        String contentString = "<p>";
        for (int i = 0; i < 200; ++i) {
            contentString += "line " + (i + 1) + " Assignment ID=" + selectedAssignmentId + " Content<br>";
            if (i == 10)
                for (int j = 0; j < 50; ++j)
                    contentString += "Assignment Content ";
        }
        contentString += "</p>";
        HTML assignmentContent = new HTML(contentString);
        ScrollPanel scroller = new ScrollPanel(assignmentContent);
        scroller.setSize("400px", "100px");

        HorizontalPanel pageContentPane = new HorizontalPanel();
        pageContentPane.add(assignmentsTable);
        pageContentPane.add(assignmentContent);
        assignmentContent.addStyleName("assignmentContent");
        pageContentPane.addStyleName("assignmentPageContentPane");

        VerticalPanel pageTopLayout = new VerticalPanel();
        pageTopLayout.add(pageHeader);
        pageTopLayout.add(pageContentPane);

        // Add page to app
        initWidget(pageTopLayout);
    }

    public void setSelectedAssignment(Listing selection) {
        // ignore selecting same listing twice in a row
        if (selection == activeAssignmentListing) {

            // remove previous selection
            activeAssignmentListing.setSelected(false);

            // set current selection
            activeAssignmentListing = selection;
            activeAssignmentListing.setSelected(true);

            // TODO update assignment content
        }
    }

    @Override
    public String getPrimaryStyleName() {
        return "coursePage";
    }

    /**
     * Load a reverse-chronologically sorted copy of course assignments
     */
    private void loadAssignments() {
        assignments = Data.getAssignmentsFor(course.getId());

        // sort assignments by date, future to past
        Collections.sort(assignments, new Comparator<Assignment>() {
            @Override
            public int compare(Assignment a1, Assignment a2) {
                return a2.getCloseTime().compareTo(a1.getCloseTime());
            }
        });
    }

    private int selectDefaultAssignmentId() {
        // TODO: choose default assignment as closest future close time
        return assignments.get(0).getId();
    }
}
