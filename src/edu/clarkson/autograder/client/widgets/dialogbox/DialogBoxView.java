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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DialogBoxView extends DialogBox implements DialogBoxPresenter.Display {

	private Label dialogText;
	private Button affirmativeButton;
	private Button cancelButton;
	private Button thirdOptionButton;
	private VerticalPanel container;

	public DialogBoxView() {
		// init items
		dialogText = new Label();
		dialogText.getElement().getStyle().setMargin(0.5, Unit.EM);

		affirmativeButton = new Button();
		affirmativeButton.getElement().getStyle().setMargin(0.5, Unit.EM);
		cancelButton = new Button();
		cancelButton.getElement().getStyle().setMargin(0.5, Unit.EM);
		thirdOptionButton = new Button();
		thirdOptionButton.getElement().getStyle().setMargin(0.5,  Unit.EM);

		container = new VerticalPanel();

		setGlassEnabled(true);
		setAnimationEnabled(true);
		setModal(false);

		init();
	}

	private void init() {
		// add items
		container.add(dialogText);

		HorizontalPanel hp = new HorizontalPanel();
		hp.add(affirmativeButton);
		hp.add(cancelButton);
		hp.add(thirdOptionButton);

		container.add(hp);
		this.add(container);
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public Label getDialogText() {
		return dialogText;
	}

	@Override
	public Button getAffirmativeButton() {
		return affirmativeButton;
	}

	@Override
	public Button getCancelButton() {
		return cancelButton;
	}
	
	@Override
	public Button getThirdButton() {
		return thirdOptionButton;
	}

	@Override
	public void setHeader(String text) {
		this.setText(text);
	}

	
}