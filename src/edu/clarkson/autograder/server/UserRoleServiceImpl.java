package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.services.UserRoleService;

@SuppressWarnings("serial")
public class UserRoleServiceImpl extends RemoteServiceServlet implements UserRoleService {
	
	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public String fetchUserRole() {
		LOG.publish(new LogRecord(Level.INFO, "UserRoleServiceImpl#fetchUserRole - begin"));
		Database db = new Database();
		String data = db.query(processResultSetcallback, Database.userRoleSql, ServerUtils.getUsername());
		db.closeConnection();
		LOG.publish(new LogRecord(Level.INFO, "UserRoleServiceImpl#fetchUserRole - end"));
		return data;
	}	
	
	private ProcessResultSetCallback<String> processResultSetcallback = new ProcessResultSetCallback<String>() {

		@Override
		public String process(ResultSet rs) throws SQLException {
			rs.next();
			return rs.getString("user_role");
		}
	};
	
}
