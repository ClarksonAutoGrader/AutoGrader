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
	private VerticalPanel container;

	public DialogBoxView() {
		// init items
		dialogText = new Label();
		dialogText.getElement().getStyle().setMargin(0.5, Unit.EM);

		affirmativeButton = new Button();
		affirmativeButton.getElement().getStyle().setMargin(0.5, Unit.EM);
		cancelButton = new Button();
		cancelButton.getElement().getStyle().setMargin(0.5, Unit.EM);

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
	public void setHeader(String text) {
		this.setText(text);
	}
	
}