package edu.clarkson.autograder.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jasig.cas.client.util.AssertionHolder;

import edu.clarkson.autograder.client.objects.Course;

public class DatabaseQuery {
	
	//Console logging for debugging
	private static ConsoleHandler LOG = new ConsoleHandler();
	
	//Connection for database
	//private static Connection conn;
	
	//Database parameters
	private String url = "jdbc:mysql://autograder.clarkson.edu:3306/autograder_db";
    private String user = "autograder_dev";
    private String password = "292.2K16";
    
    private String getUsername(){
    	//return "woodrj";
    	return AssertionHolder.getAssertion().getPrincipal().getName();
    }
	
	public List<Course> queryCourses(){
		
		//Debug statement
		LOG.publish(new LogRecord(Level.INFO, "#establishConn"));
		
		Connection conn = null;
		List<Course> courseList = new ArrayList<Course>();
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			//create connection to database
			conn = DriverManager.getConnection(url, user, password);
			LOG.publish(new LogRecord(Level.INFO, "#establishConnection: DB Connection Successful"));
	    	LOG.publish(new LogRecord(Level.INFO, "user is: " + (AssertionHolder.getAssertion().getPrincipal().getName())));
			LOG.publish(new LogRecord(Level.INFO, "Conn = " + conn));
		} 
		catch (SQLException | ClassNotFoundException e){
			//Handle exception here
			LOG.publish(new LogRecord(Level.INFO, "#DatabaseQuery Failed: " + e.toString()));
		}
		finally {
			try{
				//Temp course selection
				String sql = "SELECT c.course_id, c.course_title, c.course_num, c.course_descr "
						+ "FROM enrollment e LEFT JOIN courses c "
						+ "ON e.enr_cid = c.course_id WHERE e.enr_username = \"" + getUsername() + "\";";
				
				LOG.publish(new LogRecord(Level.INFO, "#establishConnection: String entered"));
				
				Statement stmt;
				LOG.publish(new LogRecord(Level.INFO, "#establishConnection: stmt"));
				stmt = conn.createStatement();
				LOG.publish(new LogRecord(Level.INFO, "#establishConnection: conn.createStatement()"));
				ResultSet rs = stmt.executeQuery(sql);
				LOG.publish(new LogRecord(Level.INFO, "#establishConnection: RS"));
				
				while(rs.next()){
					courseList.add(new Course(Integer.parseInt(rs.getString("course_id")), rs.getString("course_title"), true));
					LOG.publish(new LogRecord(Level.INFO, "Course: " + rs.getString("course_title")));
				}
				
				LOG.publish(new LogRecord(Level.INFO, "#establishConnection: Statement run"));
				
				return courseList;
			} 
			catch (SQLException ex){	
			}
		}
		return courseList;

	}
}