package edu.clarkson.autograder.server;

interface ProcessResultSetCallback<T> {
	T process(java.sql.ResultSet rs) throws java.sql.SQLException;
}
