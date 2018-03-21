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

package edu.clarkson.autograder.client.widgets.dialogbox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DialogBoxPresenter {

	public interface Display {

		Label getDialogText();

		Button getAffirmativeButton();

		Button getCancelButton();

		Widget asWidget();

		public void center();

		public void hide();

		public void setHeader(String text);
	}

	private Display display;
	private String header;
	private String dialogText;
	private String cancelButtonText;
	private String affirmativeButtonText;
	private ConfirmDialogCallback confirmCallback;
	private AlertDialogCallback alertCallback;

	protected DialogBoxPresenter() {
	}

	public DialogBoxPresenter(Display display, String header, String dialogText, String cancelButtonText,
	        String affirmativeButtonText, ConfirmDialogCallback callback) {
		this.display = display;
		this.header = header;
		this.dialogText = dialogText;
		this.cancelButtonText = cancelButtonText;
		this.affirmativeButtonText = affirmativeButtonText;
		this.confirmCallback = callback;

		bind();
	}

	public DialogBoxPresenter(Display display, String header, String dialogText, String affirmativeButtonText,
	        AlertDialogCallback callback) {
		this.display = display;
		this.header = header;
		this.dialogText = dialogText;
		this.affirmativeButtonText = affirmativeButtonText;
		this.alertCallback = callback;

		this.display.getCancelButton().setVisible(false);

		bind();
	}

	private void bind() {

		this.display.getDialogText().setText(dialogText);
		this.display.getAffirmativeButton().setText(affirmativeButtonText);
		this.display.getCancelButton().setText(cancelButtonText);
		this.display.setHeader(header);

		addClickHandlers();

	}

	private void addClickHandlers() {
		this.display.getAffirmativeButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doAffirmative();
			}
		});

		this.display.getCancelButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doCancel();
			}
		});
	}

	private void doAffirmative() {
		if (confirmCallback != null) {
			confirmCallback.onAffirmative();
		} else {
			alertCallback.onAffirmative();
		}
		display.hide();
	}

	private void doCancel() {
		confirmCallback.onCancel();
		display.hide();
	}

	public void init() {
		display.center();
	}

	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}
}