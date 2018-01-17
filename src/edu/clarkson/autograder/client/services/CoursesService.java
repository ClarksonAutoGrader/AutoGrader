package edu.clarkson.autograder.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.objects.Course;

@RemoteServiceRelativePath("courses")
public interface CoursesService extends RemoteService{
	
	List<Course> fetchCourses();

}
