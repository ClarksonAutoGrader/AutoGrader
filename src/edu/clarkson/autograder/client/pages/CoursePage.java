/*
	Autograder is an online homework tool used by Clarkson University.
	
	Copyright 2017-2018 Clarkson University.
	
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

package edu.clarkson.autograder.client.pages;

import java.util.List;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.objects.Problem;
import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataService;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataServiceAsync;
import edu.clarkson.autograder.client.services.CourseFromIdService;
import edu.clarkson.autograder.client.services.CourseFromIdServiceAsync;
import edu.clarkson.autograder.client.services.SelectedProblemDataService;
import edu.clarkson.autograder.client.services.SelectedProblemDataServiceAsync;
import edu.clarkson.autograder.client.widgets.AssignmentTreeViewModel;
import edu.clarkson.autograder.client.widgets.Content;
import edu.clarkson.autograder.client.widgets.ProblemView;

/**
 * Generate a page listing all assignments in the specified course.
 */
public class CoursePage extends Content {

	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	private int courseId;

	private VerticalPanel topLevel;

	private Label pageTitle;

	private HorizontalPanel sideBarAndContent;

	private ProblemView problemView;

	private Label errorLabel;

	/**
	 * Attempt to create CoursePage with specified course ID. The course ID
	 * possibly does not exist, or the user cannot access it.
	 */
	public CoursePage(int courseId) {
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#<init> - courseId=" + courseId));
		this.courseId = courseId;

		// Page title
		pageTitle = new Label();
		pageTitle.addStyleName("coursePageHeader");
		requestCourseFromIdAsync();

		// Assemble the course page elements
		sideBarAndContent = new HorizontalPanel();
		sideBarAndContent.addStyleName("sideBarAndContent");
		errorLabel = new Label();
		errorLabel.addStyleName("errorLabel");
		topLevel = new VerticalPanel();
		topLevel.add(pageTitle);
		topLevel.add(sideBarAndContent);

		// request data to finish loading sideBarAndContent
		requestAssignmentProblemTreeDataAsync();

		// Add page to app
		initWidget(topLevel);
	}

	@Override
	public String getPrimaryStyleName() {
		return "coursePage";
	}

	private void requestCourseFromIdAsync() {
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestCourseFromIdAsync - begin"));

		CourseFromIdServiceAsync courseFromIdService = GWT.create(CourseFromIdService.class);
		courseFromIdService.fetchCourseFromId(courseId, new AsyncCallback<Course>() {
			@Override
			public void onFailure(Throwable caught) {
				final String failureToLoadCourse = "Failed to load course.";
				LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestCourseFromIdAsync - onFailure"));
				topLevel.clear();
				errorLabel.setText(failureToLoadCourse);
				topLevel.add(errorLabel);
			}

			@Override
			public void onSuccess(Course course) {
				LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestCourseFromIdAsync - onSuccess"));
				pageTitle.setText(course.getTitle());
			}
		});
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestCourseFromIdAsync - end"));
	}

	private void requestAssignmentProblemTreeDataAsync() {
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestAssignmentProblemTreeDataAsync - begin"));

		AssignmentProblemTreeDataServiceAsync treeDataService = GWT.create(AssignmentProblemTreeDataService.class);
		treeDataService.fetchTreeData(courseId, new AsyncCallback<SortedMap<Assignment, List<Problem>>>() {
			@Override
			public void onFailure(Throwable caught) {
				final String failureToLoadAssignments = "Failed to load course assignments.";
				LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestAssignmentProblemTreeDataAsync - onFailure"));
				sideBarAndContent.remove(errorLabel);
				errorLabel.setText(failureToLoadAssignments);
				sideBarAndContent.add(errorLabel);
			}

			@Override
			public void onSuccess(SortedMap<Assignment, List<Problem>> treeData) {
				LOG.publish(new LogRecord(Level.INFO,
				        "CoursePage#requestAssignmentProblemTreeDataAsync - onSuccess"));
				if (treeData.isEmpty()) {
					sideBarAndContent.remove(errorLabel);
					errorLabel.setText("The instructor has not added any assignments to the course.");
					sideBarAndContent.add(errorLabel);
				} else {
					errorLabel.setText("");
					sideBarAndContent.remove(errorLabel);
					loadSideBar(treeData);
				}

			}
		});
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestAssignmentProblemTreeDataAsync - end"));
	}

	private void loadSideBar(SortedMap<Assignment, List<Problem>> treeData) {

		// Create a side bar for assignment selection.
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#loadSideBar"));
		CellTree sideBar = new CellTree(new AssignmentTreeViewModel(treeData, new ProblemSelectionCallback()), null);
		sideBar.setAnimationEnabled(true);
		sideBar.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		sideBar.addStyleName("assignmentSideBar");
		sideBar.getRootTreeNode().setChildOpen(0, true);

		sideBarAndContent.add(sideBar);
	}

	public final class ProblemSelectionCallback {

		private ProblemSelectionCallback() {
		}

		public void requestSelectedProblemDataAsync(int problemId) {

			LOG.publish(new LogRecord(Level.INFO, "CoursePage.ProblemSelectionCallback#requestSelectedProblemDataAsync - begin"));

			SelectedProblemDataServiceAsync problemDataService = GWT.create(SelectedProblemDataService.class);
			problemDataService.fetchProblemData(problemId, new AsyncCallback<ProblemData>() {
				@Override
				public void onFailure(Throwable caught) {
					LOG.publish(new LogRecord(Level.INFO,
					        "CoursePage.ProblemSelectionCallback#requestSelectedProblemDataAsync - onFailure"));
					sideBarAndContent.remove(errorLabel);
					if (problemView != null) {
						sideBarAndContent.remove(problemView);
						problemView = null;
					}
					errorLabel.setText("Failed to load the selected problem.");
					sideBarAndContent.add(errorLabel);
				}

				@Override
				public void onSuccess(ProblemData data) {
					LOG.publish(new LogRecord(Level.INFO,
					        "CoursePage.ProblemSelectionCallback#requestSelectedProblemDataAsync - onSuccess"));
					errorLabel.setText("");
					sideBarAndContent.remove(errorLabel);
					loadProblemView(data);

				}
			});
			LOG.publish(new LogRecord(Level.INFO,
			        "CoursePage.ProblemSelectionCallback#requestSelectedProblemDataAsync - end"));
		}

	}

	private void loadProblemView(ProblemData data) {
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#loadProblemView - begin"));
		if (problemView == null) {
			problemView = new ProblemView(data);
			problemView.addStyleName("problemView");
			sideBarAndContent.add(problemView);
		} else {
			problemView.update(data);
		}
	}
}
