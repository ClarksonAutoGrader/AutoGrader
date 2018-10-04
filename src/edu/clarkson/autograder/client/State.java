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

package edu.clarkson.autograder.client;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.Course;
import edu.clarkson.autograder.client.pages.CoursePage;
import edu.clarkson.autograder.client.pages.CourseSelectionPage;
import edu.clarkson.autograder.client.pages.GradebookPage;
import edu.clarkson.autograder.client.pages.RawProblemAdderPage;
import edu.clarkson.autograder.client.services.UserRoleService;
import edu.clarkson.autograder.client.services.UserRoleServiceAsync;
import edu.clarkson.autograder.client.widgets.dialogbox.DialogBoxWidget;
import edu.clarkson.autograder.client.widgets.dialogbox.ThreeOptionDialogCallback;

public final class State implements ValueChangeHandler<String> {

	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	private static final State instance = new State();

	private static Course course;

	private State() {
	}

	public static State getInstance() {
		return instance;
	}

	public static Course getCourse() {
		return course;
	}

	public static void setCourse(Course course) {
		State.course = course;
	}

	private void loadDefault() {
		ContentContainer.setContent(new CourseSelectionPage());
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// TODO clean history of duplicate tokens and do not process
		String historyToken = event.getValue();

		// Check for: course selection page
		if (historyToken.equals(CourseSelectionPage.TOKEN)) {
			loadDefault();
			return;
		}

		// Check for: specific course page
		int courseId = -1;
		try {
			courseId = Integer.parseInt(historyToken.substring(0, Autograder.ID_TOKEN_WIDTH));
		} catch (NumberFormatException e) {
			History.replaceItem(CourseSelectionPage.TOKEN);
			return;
		}

		// Choose course page based on user role
		requestUserRoleService(courseId);

	}

	private void requestUserRoleService(final int courseId) {
		UserRoleServiceAsync userRoleService = GWT.create(UserRoleService.class);
		userRoleService.fetchUserRole(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				LOG.publish(new LogRecord(Level.INFO, "requestUserRoleService#onFailure"));
			}

			@Override
			public void onSuccess(String role) {
				LOG.publish(new LogRecord(Level.INFO, "requestUserRoleService#onSuccess - role=" + role));
				if (role.equals("student")) {
					ContentContainer.setContent(new CoursePage(courseId));
				} else if (role.equals("instructor") || role.equals("developer")) {
					DialogBoxWidget.threeButton("Welcome, Instructor",
					        "As an instructor, you may continue to course gradebook or view course as student. Please select an option below.",
					        "Continue to Gradebook", "Continue as Student", "Continue to Problem Builder",  new ThreeOptionDialogCallback() {
						        @Override
						        public void onAffirmative() {
							        ContentContainer.setContent(new CoursePage(courseId));
						        }

						        @Override
						        public void onCancel() {
							        ContentContainer.setContent(new GradebookPage(courseId));
						        }

								@Override
								public void onThirdOption() {
									ContentContainer.setContent(new RawProblemAdderPage());
								}
					        });
				}
			}
		});
	}
}