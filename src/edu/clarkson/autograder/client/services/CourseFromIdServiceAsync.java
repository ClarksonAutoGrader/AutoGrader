package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.Course;

public interface CourseFromIdServiceAsync {
	void fetchCourseFromId(int courseId, AsyncCallback<Course> callback) throws IllegalArgumentException;
}
