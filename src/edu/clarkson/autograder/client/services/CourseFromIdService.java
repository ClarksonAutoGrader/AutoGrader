package edu.clarkson.autograder.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.clarkson.autograder.client.objects.Course;

@RemoteServiceRelativePath("course_from_id")
public interface CourseFromIdService extends RemoteService {
	Course fetchCourseFromId(int courseId);
}
