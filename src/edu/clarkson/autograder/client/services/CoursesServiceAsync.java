package edu.clarkson.autograder.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.clarkson.autograder.client.objects.Course;

public interface CoursesServiceAsync {

	void fetchCourses(AsyncCallback<List<Course>> callback);
	
}
