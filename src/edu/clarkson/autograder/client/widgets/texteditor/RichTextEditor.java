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

package edu.clarkson.autograder.client.widgets.texteditor;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RichTextArea;

public class RichTextEditor extends Composite {

	public static final SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();
	
	private Grid grid;

	private RichTextArea area;

	private RichTextToolbar toolbar;

	public RichTextEditor() {
		
		area = new RichTextArea();
		area.setSize("100%", "14em");

		toolbar = new RichTextToolbar(area);
		toolbar.setWidth("100%");
		
		grid = new Grid(2, 1);
		grid.setStyleName("richTextEditor");
		Style gridStyle = grid.getElement().getStyle();
		gridStyle.setBorderWidth(1.0, Unit.PX);
		gridStyle.setBorderStyle(BorderStyle.SOLID);
		gridStyle.setBorderColor("#BBB");
		grid.setWidget(0, 0, toolbar);
		grid.setWidget(1, 0, area);
		
		initWidget(grid);
	}

	public String getRichText() {
		return area.getHTML();
	}
	
	// TODO: remove (temporary)
	public static com.google.gwt.user.client.ui.Widget createTemporaryExampleWidget() {
		com.google.gwt.user.client.ui.FlowPanel toplevel = new com.google.gwt.user.client.ui.FlowPanel();
		final RichTextEditor editor = new RichTextEditor();
		final com.google.gwt.user.client.ui.HTML content = new com.google.gwt.user.client.ui.HTML();
		com.google.gwt.user.client.ui.Button getContentButton = new com.google.gwt.user.client.ui.Button(
		        "temp (get area HTML)");
		getContentButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				String text = editor.getRichText();
				content.setHTML(text);
				LOG.publish(new LogRecord(Level.INFO, text));
			}
		});
		toplevel.add(editor);
		toplevel.add(content);
		toplevel.add(getContentButton);
		return toplevel;
	}

}
