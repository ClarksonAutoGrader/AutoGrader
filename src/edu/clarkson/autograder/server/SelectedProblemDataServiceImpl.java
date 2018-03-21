/*
	Autograder is an online homework tool used by Clarkson University.
	
	Copyright ©2017-2018 Clarkson University.
	
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

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.services.SelectedProblemDataService;

@SuppressWarnings("serial")
public class SelectedProblemDataServiceImpl extends RemoteServiceServlet implements SelectedProblemDataService {

	private static ConsoleHandler LOG = new ConsoleHandler();

	@Override
	public ProblemData fetchProblemData(int problemId) {
		LOG.publish(new LogRecord(Level.INFO, "SelectedProblemDataServiceImpl#fetchProblemData - begin"));

		// assume zero resets used if no user work is present
		final int defaultResetsUsed = 0;
		Database db = null;
		ProblemData data = null;

		try {

			db = new Database();
			db.beginTransaction();

			data = ServerUtils.createProblemData(db, problemId, defaultResetsUsed);

			// Commit transaction
			db.commitTransaction();
		} catch (Throwable exception) {
			db.rollBackTransaction();
			LOG.publish(new LogRecord(Level.INFO, "SubmitAnswersServiceImpl#submitAnswers - Transaction rolled back"));
		} finally {
			db.closeConnection();
		}

		LOG.publish(new LogRecord(Level.INFO, "SelectedProblemDataServiceImpl#fetchProblemData - end"));
		return data;
	}

}
