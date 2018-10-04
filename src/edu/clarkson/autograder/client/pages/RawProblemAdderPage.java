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

package edu.clarkson.autograder.client.pages;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

import edu.clarkson.autograder.client.widgets.Content;
import edu.clarkson.autograder.client.widgets.ProblemView;
import edu.clarkson.autograder.client.widgets.texteditor.RichTextEditor;

public class RawProblemAdderPage extends Content {
	
	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();
	
	private HorizontalPanel buttonBar;

	private ProblemView problemPreview;
	
	private FlexTable topLevel;
	
	private Button submitDbButton;
	private Button showPreviewButton;
	
	private ProblemView preview;
	
//	private TextField problemName;
//	private TextField points;
//	private TextField resets;
//	private TextField attempts;
//	private DatePicker dueDate;
	
	private RichTextEditor editor;

	public RawProblemAdderPage() {
		
		LOG.publish(new LogRecord(Level.INFO, "RawProblemAdderPage#<init>"));
		
		editor = new RichTextEditor();
		
		topLevel = new FlexTable();
		
		buttonBar = new HorizontalPanel();

		
		// buttons
		submitDbButton = new Button("Submit to Database");
		submitDbButton.addClickHandler(e -> {
			// method with SQL stuff/rpc/etc
				
		});
		
		showPreviewButton = new Button("Show Problem Preview");
		showPreviewButton.addClickHandler(e -> {
			topLevel.setWidget(0, 1, new HTML("<div>Consider a classroom that has !in_1! light fixtures each containing !in_2! lamps rated at 60W each.&nbsp; The lamps are on for !in_3! hours a day, 365 days a year. The classroom is only used for an average of !in_4! hours a day. The price of electricity is $!in_5!/kWh.&nbsp; To reduce electricity usage, the classroom lights can be triggered on with a motion sensor which costs $!in_6! to purchase and $!in_7!&nbsp;to install.</div><div><br></div><div><ol><li>Find the energy savings in kWh after installing motion sensors. !ans_1_numeric{0.02}!&nbsp;kWh/year</li><li>What is the cost savings in dollars per year after installing motion sensors? $!ans_2_numeric{0.02}!/year</li><li>Find the simple payback period for costs of implementing the sensors. !ans_3_numeric{0.02}!&nbsp;years</li></ol></div>"));
		});
		
		buttonBar.add(submitDbButton);
		buttonBar.add(showPreviewButton);
		
		topLevel.setWidget(0,0, editor);
		//topLevel.setWidget(0,1, problemPreview);
		topLevel.setWidget(1,0, buttonBar);
		
		initWidget(topLevel);
		
	}


	
	@Override
	public String getPrimaryStyleName() {
		return "RawProblemAdderPage";
	}
	
	public String getToken() {
		return "temp2";
	}
	
}
