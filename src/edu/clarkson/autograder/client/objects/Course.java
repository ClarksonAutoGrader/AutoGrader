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

package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

import edu.clarkson.autograder.client.Autograder;

@SuppressWarnings("serial")
public class Course implements Serializable {
	private int id;
	private String title;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            unique course id
	 * @param title
	 *            course title (any String)
	 */
	public Course(int id, String title) {
		this.id = id;
		this.title = title;
	}

	/**
	 * Default constructor required for serialization
	 */
	public Course() {
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getToken() {
		return Autograder.formatIdToken(id);
	}
}