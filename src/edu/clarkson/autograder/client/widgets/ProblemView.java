package edu.clarkson.autograder.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.clarkson.autograder.client.Autograder;
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

	private final VerticalPanel toplevel;
	private final Header header;
	private final Body body;
	private final Footer footer;

	private class Header extends Composite {

		private static final String STYLE_TOP_LEVEL = "problemHeader";

		private static final String STYLE_TITLE = "problemTitleInlineLabel";

		private static final String STYLE_GRADE = "problemGradeInlineLabel";

		private static final String STYLE_PREVIOUS_ANSWERS = "previousAnswersButton";

		private static final String TEXT_PREVIOUS_ANSWERS = "Previous Answers";

		private final FlowPanel toplevel;

		private InlineLabel problemTitle;

		private InlineLabel problemGrade;

		private Button previousAnswers;

		private ProblemPopup previousAnswersPopup;

		private Widget previousAnswersPopupContent;

		private double earnedPoints = -1;

		private double totalPoints = -1;

		private static final int decimalPrecision = 2;

		private Header() {
			toplevel = new FlowPanel();
			create();
			initWidget(toplevel);
		}

		/**
		 * Instantiate top-level widget.
		 */
		private void create() {

			toplevel.setStyleName(STYLE_TOP_LEVEL);

			problemTitle = new InlineLabel();
			problemTitle.addStyleName(STYLE_TITLE);

			problemGrade = new InlineLabel();
			problemGrade.addStyleName(STYLE_GRADE);

			toplevel.add(problemTitle);
			toplevel.add(problemGrade);

			previousAnswers = new Button(TEXT_PREVIOUS_ANSWERS);
			previousAnswers.addStyleName(STYLE_PREVIOUS_ANSWERS);
			previousAnswers.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// null if content has not yet been generated for this
					// problem
					if (previousAnswersPopupContent == null) {
						previousAnswersPopupContent = createPreviousAnswersContent();
					}

					// ensure only one previous answers pop-up object is created
					if (previousAnswersPopup == null) {
						previousAnswersPopup = new ProblemPopup(Header.TEXT_PREVIOUS_ANSWERS,
						        previousAnswersPopupContent);
					}

					previousAnswersPopup.center();
					previousAnswersPopup.show();

				}
			});

			toplevel.add(previousAnswers);
		}

		private void update(final String title, final double earnedPoints, final double totalPoints) {
			problemTitle.setText(title);

			if (this.earnedPoints != earnedPoints || this.totalPoints != totalPoints) {
				// 00.00/00.00 (00.00%)
				StringBuilder builder = new StringBuilder();
				builder.setLength(0);
				double earnedFormatted = Autograder.numberPrecision(earnedPoints, decimalPrecision);
				builder.append(earnedFormatted);
				builder.append("/");
				double totalFormatted = Autograder.numberPrecision(totalPoints, decimalPrecision);
				builder.append(totalFormatted);
				builder.append(" (");
				// problems out of zero total points are always 100% complete
				double percentageFormatted = Autograder
				        .numberPrecision((totalPoints != 0.0 ? earnedPoints / totalPoints * 100 : 100.0),
				                decimalPrecision);
				builder.append(percentageFormatted);
				builder.append("%)");
				problemGrade.setText(builder.toString());
			}

			// invalidate previous answers to force redraw (content may have changed)
			previousAnswersPopupContent = null;
		}

		private Widget createPreviousAnswersContent() {
			Label content = new Label("Previous answers listed below");
			content.addStyleName("previousAnswersContent");
			return content;
		}
	}

	private class Body extends Composite {

		private static final String STYLE_TOP_LEVEL = "problemBody";

		private static final String TEXT_DEFAULT_MARKUP = "Nothing to see here.";

		private final FlowPanel toplevel;

		private String markup;

		private Body() {
			toplevel = new FlowPanel();
			create();
			initWidget(toplevel);
		}

		/**
		 * Instantiate top-level widget.
		 */
		private void create() {
			toplevel.setStylePrimaryName(STYLE_TOP_LEVEL);
			toplevel.add(new HTML(TEXT_DEFAULT_MARKUP));
		}

		private void update(final String bodyMarkup) {
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

		private static final String STYLE_SUBMIT = "problemSubmitButton";

		private static final String TEXT_NEW_PROBLEM = "New Problem (resets attempts)";

		private static final String TEXT_RESETS_REMAINING = "Resets Remaining: ";

		private static final String TEXT_ATTEMPTS_REMAINING = "Attempts Remaining: ";

		private static final String TEXT_SUBMIT = "Submit";

		private final FlowPanel toplevel;

		private Button newProblem;
		private InlineLabel resetsRemaining;

		private InlineLabel attemptsRemaining;
		private Button submit;

		private Footer() {
			toplevel = new FlowPanel();
			create();
			initWidget(toplevel);
		}

		/**
		 * Instantiate top-level widget.
		 */
		private void create() {

			toplevel.setStylePrimaryName(STYLE_TOP_LEVEL);

			newProblem = new Button(TEXT_NEW_PROBLEM);
			newProblem.addStyleName(STYLE_NEW_PROBLEM);
			newProblem.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					actionNewProblem();
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
					actionSubmit();
				}
			});

			// add from the left
			toplevel.add(newProblem);
			toplevel.add(resetsRemaining);

			// add from the right
			toplevel.add(submit);
			toplevel.add(attemptsRemaining);
		}

		private void update(final int resets, final int attempts) {
			resetsRemaining.setText(TEXT_RESETS_REMAINING + resets);
			attemptsRemaining.setText(TEXT_ATTEMPTS_REMAINING + attempts);
		}

		private void actionSubmit() {
			// TODO implement submit action
		}

		private void actionNewProblem() {
			// TODO implement new problem action
		}
	}

	public ProblemView(final ProblemData data) {

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

	private void update(final ProblemData data) {
		header.update(data.getTitle(), data.getEarnedPoints(), data.getTotalPoints());
		body.update(data.getBodyMarkup());
		footer.update(data.getResets(), data.getAttempts());
	}

	private void requestPreviousAnswersAsync() {
		// onSuccess: call header.createPreviousAnswersContent
	}

	private class ProblemPopup extends DialogBox {

		private static final String STYLE_TOP_LEVEL = "problemPopup";

		private static final String TEXT_CLOSE_BUTTON = "Close";

		private static final String STYLE_CLOSE_BUTTON = "problemPopupCloseButton";

		private VerticalPanel toplevel;

		private Widget content;

		private Button closeButton;

		public ProblemPopup(String popupCaption, Widget popupContent) {

			setText(popupCaption);

			// pop-up animation
			setAnimationEnabled(true);

			// glass background
			setGlassEnabled(true);

			content = popupContent;

			closeButton = new Button(TEXT_CLOSE_BUTTON);
			closeButton.setStyleName(STYLE_CLOSE_BUTTON);
			closeButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
				}
			});

			toplevel = new VerticalPanel();
			toplevel.setStyleName(STYLE_TOP_LEVEL);

			toplevel.add(content);
			toplevel.add(closeButton);

			setWidget(toplevel);
		}
	}
}
