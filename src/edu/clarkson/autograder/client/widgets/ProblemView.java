package edu.clarkson.autograder.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.clarkson.autograder.client.objects.ProblemData;

/**
 * A widget to display all the visual facets of a problem. Each problem includes
 * a header, body, and footer.<br>
 * <br>
 * <br>
 * Problem header
 * <li>Title</li>
 * <li>Grade: points earned / total points possible (%)</li>
 * <li>Previous answers ({@link com.google.gwt.user.client.ui.Button} triggers
 * pop-up)</li> <br>
 * Problem body:
 * <li>{@link ProblemView.Body} (custom widget)</li> <br>
 * Problem footer:
 * <ul>
 * <li>Left justified:</li>
 * <ul>
 * <li>New Problem (reset attempts)
 * ({@link com.google.gwt.user.client.ui.Button})</li>
 * <li>Resets remaining: <i>resets</i>
 * ({@link com.google.gwt.user.client.ui.InlineLabel})</li>
 * </ul>
 * <li>Right justified:</li>
 * <ul>
 * <li>Attempts remaining: <i>attempts</i>
 * ({@link com.google.gwt.user.client.ui.InlineLabel})</li>
 * <li>Submit ({@link com.google.gwt.user.client.ui.Button})</li>
 * </ul>
 * </ul>
 */
public class ProblemView extends Composite {

	private VerticalPanel toplevel;
	private Header header;
	private Body body;
	private Footer footer;

	private class Header extends Composite {

		private static final String STYLE_TOP_LEVEL = "problemHeader";

		private FlowPanel toplevel;

		private Header() {
			create();
			initWidget(toplevel);
		}

		/**
		 * Instantiate top-level widget.
		 */
		private void create() {

			toplevel = new FlowPanel();
			toplevel.setStyleName(STYLE_TOP_LEVEL);

			double pointsReceived = (int) (25 * Math.random());
			double pointsTotal = (int) (50 * Math.random() + 25);
			int problemNumber = 1 + (int) (10 * Math.random());
			String problemHeaderHTML = "<div id=problemHeaderQuestionNumber>Problem " + problemNumber + "</div>";
			String nextString = "<div id=problemHeaderPoints>" + (double) Math.round(pointsReceived) + "/"
			        + ((double) Math.round(pointsTotal)) + " (" + Math.round((pointsReceived / pointsTotal) * 100)
			        + "%)</div>";
			toplevel.add(new HTML(problemHeaderHTML));
			toplevel.add(new HTML(nextString));

			Button previousAnswers = new Button("Previous Answers", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					MyDialog dialogBox = new MyDialog("Previous Answers", "Previous answers listed below");
					int left = Window.getClientWidth() / 2;
					int top = Window.getClientHeight() / 2;

					dialogBox.setPopupPosition(left, top);
					dialogBox.center();
					dialogBox.show();
				}
			});
			toplevel.add(previousAnswers);
		}
	}

	private class Body extends Composite {

		private static final String STYLE_TOP_LEVEL = "problemBody";

		private static final String TEXT_DEFAULT_MARKUP = "Nothing to see here.";

		private FlowPanel toplevel;

		private String markup;

		private Body() {
			create();
			initWidget(toplevel);
		}

		/**
		 * Instantiate top-level widget.
		 */
		private void create() {

			toplevel = new FlowPanel();
			toplevel.setStylePrimaryName(STYLE_TOP_LEVEL);

			toplevel.add(new HTML(TEXT_DEFAULT_MARKUP));
		}

		private void update(String bodyMarkup) {
			// update body only if it's different
			if (!bodyMarkup.equals(markup)) {
				markup = bodyMarkup;
				renderMarkup();
			}
		}

		private void renderMarkup() {
			toplevel.clear();
			toplevel.add(new HTML(markup));
		}
	}

	private class Footer extends Composite {

		private static final String STYLE_TOP_LEVEL = "problemFooter";

		private static final String STYLE_NEW_PROBLEM = "newProblemButton";

		private static final String STYLE_RESETS_REMAINING = "resetsRemainingInlineLabel";

		private static final String STYLE_ATTEMPTS_REMAINING = "attemptsRemainingInlineLabel";

		private static final String STYLE_SUBMIT = "submitButton";

		private static final String TEXT_NEW_PROBLEM = "New Problem (resets attempts)";

		private static final String TEXT_RESETS_REMAINING = "Resets Remaining: ";

		private static final String TEXT_ATTEMPTS_REMAINING = "Attempts Remaining: ";

		private static final String TEXT_SUBMIT = "Submit";

		private FlowPanel toplevel;

		private Button newProblem;
		private InlineLabel resetsRemaining;

		private InlineLabel attemptsRemaining;
		private Button submit;

		private Footer() {
			create();
			initWidget(toplevel);
		}

		/**
		 * Instantiate top-level widget.
		 */
		private void create() {

			toplevel = new FlowPanel();
			toplevel.setStylePrimaryName(STYLE_TOP_LEVEL);

			newProblem = new Button(TEXT_NEW_PROBLEM);
			newProblem.addStyleName(STYLE_NEW_PROBLEM);
			newProblem.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					requestNewProblemActionAsync();
				}
			});

			resetsRemaining = new InlineLabel();
			resetsRemaining.addStyleName(STYLE_RESETS_REMAINING);

			attemptsRemaining = new InlineLabel();
			attemptsRemaining.addStyleName(STYLE_ATTEMPTS_REMAINING);

			submit = new Button(TEXT_SUBMIT);
			submit.addStyleName(STYLE_SUBMIT);
			submit.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					requestSubmitActionAsync();
				}
			});

			// add from the left
			toplevel.add(newProblem);
			toplevel.add(resetsRemaining);

			// add from the right
			toplevel.add(submit);
			toplevel.add(attemptsRemaining);
		}

		private void update(int resets, int attempts) {
			resetsRemaining.setText(TEXT_RESETS_REMAINING + resets);
			attemptsRemaining.setText(TEXT_ATTEMPTS_REMAINING + attempts);
		}
	}

	public ProblemView(ProblemData data) {

		header = new Header();
		body = new Body();
		footer = new Footer();

		update(data);

		toplevel = new VerticalPanel();
		toplevel.add(header);
		toplevel.add(body);
		toplevel.add(footer);

		initWidget(toplevel);
	}

	private void update(ProblemData data) {
		body.update(data.getBodyMarkup());
		footer.update(data.getResets(), data.getAttempts());
	}

	private void requestSubmitActionAsync() {

	}

	private void requestNewProblemActionAsync() {

	}

	// Private DialogBox class to hold previous answers
	private static class MyDialog extends DialogBox {

		public MyDialog(String popupCaption, String popupContent) {
			// Set the dialog box's caption.
			setText(popupCaption);

			// Enable animation.
			setAnimationEnabled(true);

			// Enable glass background.
			setGlassEnabled(true);

			// DialogBox is a SimplePanel, so you have to set its widget
			// property to whatever you want its contents to be.
			Button ok = new Button("OK");
			ok.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					MyDialog.this.hide();
				}
			});

			Label label = new Label(popupContent);

			VerticalPanel panel = new VerticalPanel();
			panel.setHeight("40vh");
			panel.setWidth("40vw");
			panel.setSpacing(10);
			panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			panel.add(label);
			panel.add(ok);

			setWidget(panel);
		}
	}
}
