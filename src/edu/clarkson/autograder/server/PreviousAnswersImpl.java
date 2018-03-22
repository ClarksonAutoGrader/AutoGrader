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

package edu.clarkson.autograder.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.PreviousAnswersRow;
import edu.clarkson.autograder.client.services.PreviousAnswersService;

@SuppressWarnings("serial")
public class PreviousAnswersImpl extends RemoteServiceServlet implements PreviousAnswersService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public List<PreviousAnswersRow> fetchPreviousAnswers(int permutationId, int answerNumber) {
		LOG.publish(new LogRecord(Level.INFO, "PreviousAnswersImpl#fetchPreviousAnswers - begin"));

		Database db = new Database();
		List<PreviousAnswersRow> previousAnswers = db.query(processResultSetcallback, Database.selectPreviousAnswersSql,
		        answerNumber, ServerUtils.getUsername(), permutationId);
		db.closeConnection();

		LOG.publish(new LogRecord(Level.INFO, "PreviousAnswersImpl#fetchPreviousAnswers - end"));
		return previousAnswers;
	}

	private ProcessResultSetCallback<List<PreviousAnswersRow>> processResultSetcallback = new ProcessResultSetCallback<List<PreviousAnswersRow>>() {

		@Override
		public List<PreviousAnswersRow> process(ResultSet rs) throws SQLException {

			List<PreviousAnswersRow> data = new ArrayList<PreviousAnswersRow>();
			for (int count = 1; rs.next(); count++) {
				final PreviousAnswersRow item = new PreviousAnswersRow(count, rs.getString(1), null);
				data.add(item);
			}

			return data;
		}
	};

}