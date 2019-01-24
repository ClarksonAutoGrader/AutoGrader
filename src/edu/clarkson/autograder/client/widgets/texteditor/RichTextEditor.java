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

import java.util.ArrayList;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RichTextArea;
import com.sencha.gxt.widget.core.client.form.HtmlEditor;

public class RichTextEditor extends Composite {
	
	private static final String TEST_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABTElEQVR42pVTS0oDQRDtE/g5g3qPkOlBJQbciB5CMeJJRt1klY2B6RkxoCs/4AkUT5AIrvxtApkBSfezqhPHidOTkIIHTXXV66pX1UI4TEZYrKphXSocMbwYW+wTs6zSwZJU+kQqk1IC9q6B3SuAz+RPPaUDjnEm+22seZHp2WDC8QMw1MA3Yf9+TELwlOmuR1gtvJxPZjSfkVnw+Oe3JBRbaeUqsWVHk0HbHeDuBbjpAfXLyTuGH+sgE+y3Zxc2Ltx+aiXZPMeCsGo7Ag6o71t6/bVvUEZOqAk7KsflDqn/mQBvg9Jk0kIflhIw3gfTCWSoG2K8JM6Aj1kEtgUrok7nJaClGoloJxHq07kroJypi+QTvpIR/OIIu4WV5vXMk5w9GVsBg8/55GqIFed/qLWxzKXxkrh65rvSz5Q3FocVlrFu8Kj4nAn2z34AD9hVpNwUS2EAAAAASUVORK5CYII=";

	private Grid grid;

	private RichTextArea area;

	private RichTextToolbar toolbar;

	private FlowPanel toplevel = new FlowPanel();
	
	private HtmlEditor editor;
	
	private HTMLPanel preview;
	
	public int count = 0;
	
	public ArrayList<Image> images = new ArrayList<Image>(); 

	public RichTextEditor() {
		
		editor = new HtmlEditor();
		editor.setHeight(500);
		editor.setWidth(700);
		editor.setShowToolBar(false);
		
		toolbar = new RichTextToolbar(editor.getTextArea());
		toolbar.setWidth("100%");
		preview = new HTMLPanel(getRichText());

		grid = new Grid(2, 2);
		grid.setStyleName("richTextEditor");
		Style gridStyle = grid.getElement().getStyle();
		gridStyle.setBorderWidth(1.0, Unit.PX);
		gridStyle.setBorderColor("#BBB");
		gridStyle.setBorderStyle(BorderStyle.SOLID);
		grid.setWidget(0, 0, toolbar);
		grid.setWidget(1, 0, editor);
		grid.setWidget(0, 1, preview);
		grid.setCellSpacing(0);
		grid.setCellPadding(0);

		initWidget(grid);
	}

	private String getRichText() {
		return editor.getValue();
	}
	
	private void updatePreview() {
		
	}

	private native void download(String filename, String text)/*-{
		var pom = document.createElement('a');
		pom.setAttribute('href', 'data:text/plain;charset=utf-8,'
				+ encodeURIComponent(text));
		pom.setAttribute('download', filename);
		document.body.appendChild(pom);
		pom.click();
		document.body.removeChild(pom);
	}-*/;

}
