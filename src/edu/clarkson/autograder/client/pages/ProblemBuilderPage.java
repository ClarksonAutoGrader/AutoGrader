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
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.DatePicker;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.clarkson.autograder.client.widgets.Content;
import edu.clarkson.autograder.client.widgets.texteditor.RichTextEditor;

public class ProblemBuilderPage extends Content {
	
	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	private SimpleContainer container;
	private FramedPanel panel;
	
	private CssFloatLayoutContainer metadata;
	
	private TextField problemName;
	private TextField points;
	private TextField resets;
	private TextField attempts;
	private DatePicker dueDate;
	
	private RichTextEditor editor;

	public ProblemBuilderPage() {
		
		LOG.publish(new LogRecord(Level.INFO, "ProblemBuilderPage#<init>"));
		
		panel = new FramedPanel();
		container = new SimpleContainer();
		container.setWidth(500);
		
		problemName = new TextField();
		points = new TextField();
		resets = new TextField();
		attempts = new TextField();
		
		metadata = new CssFloatLayoutContainer();
		metadata.add(new FieldLabel(problemName, "Problem Name"), new CssFloatData(-1, new Margins(4, 0, 0, 0)));
		metadata.add(new FieldLabel(attempts, "Attempts"), new CssFloatData(-1, new Margins(4, 7, 0, 0)));
		metadata.add(new FieldLabel(resets, "Resets"), new CssFloatData(-1, new Margins(4, 0, 0, 0)));
		metadata.add(new FieldLabel(points, "Total Points"), new CssFloatData(-1, new Margins(4, 0, 0, 0)));
		
		panel.setHeaderVisible(false);
		panel.setWidget(metadata);
		
		container.add(panel);
		initWidget(panel);
		
	}

	@Override
	public String getPrimaryStyleName() {
		return "problemBuilderPage";
	}
//	
//	public void buttons() {
//
//		final HTML content = new HTML();
//
//		Button showHtmlButton = new Button("Show HTML");
//		showHtmlButton.addClickHandler(e -> {
//				String rawText = getRichText();
//				String escapedText = SafeHtmlUtils.htmlEscape(rawText);
//				
//				content.setHTML(escapedText);
//		});
//		
//		Button updatePreview = new Button("Update Preview");
//		updatePreview.addClickHandler(e -> {
//			preview = new HTMLPanel(getRichText());
//			for(int i = 0; i < count; i++) {
//				preview.addAndReplaceElement(editor.getData("button" + i), "button" + i);
//			}
//			grid.setWidget(0, 1, preview);
//			
//		});
//		
//		Button showElementButton = new Button("Show test element");
//		showElementButton.addClickHandler(e -> {
//			HTMLPanel panel = new HTMLPanel(getRichText());
//			final Element spanElement = panel.getElementById("button1");
//
//			Window.alert(spanElement.getString());
//		});
//		
//		Button insertTestButton = new Button("Insert test button");
//		insertTestButton.addClickHandler(e -> {
//				Button testButton = new Button("Label");
//				final String key = "button" + count;
//				editor.setData(key, testButton);
//				editor.getTextArea().getFormatter().insertHTML("&nbsp;<span id=\"" + key + "\" contenteditable=\"false\">" + testButton.asWidget().toString() + "&nbsp;</span>&nbsp;");
//				count++;
//				
//				testButton.addClickHandler(l -> { Window.alert("Clicked!"); });
//		});
//		
//		Button removeTestButton = new Button("remove test button");
//		removeTestButton.addClickHandler(e -> {
//			HTMLPanel panel = new HTMLPanel(getRichText());
//			final Element spanElement = panel.getElementById("button1");
//			String oldHtml = editor.getValue();
//			String newHtml = oldHtml.replaceAll("&nbsp;" + spanElement.getString() + "&nbsp;", "");
//			editor.setValue(newHtml);
//		});
//		
//		
//		
//		toplevel.add(this);
//		toplevel.add(showHtmlButton);
//		toplevel.add(updatePreview);
//		toplevel.add(showElementButton);
//		toplevel.add(insertTestButton);
//		toplevel.add(removeTestButton);
//		toplevel.add(content);
//
//		RootPanel.get("content").add(toplevel);
//	}

}
