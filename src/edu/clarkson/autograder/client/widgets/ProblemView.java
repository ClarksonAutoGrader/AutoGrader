package edu.clarkson.autograder.client.widgets;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.clarkson.autograder.client.Autograder;
import edu.clarkson.autograder.client.AutograderResources;
import edu.clarkson.autograder.client.objects.Permutation;
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

	public static final SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	private static final Image greenCheck = new Image(AutograderResources.INSTANCE.greenCheck());

	private static final Image redCross = new Image(AutograderResources.INSTANCE.redCross());

	private static final Image info = new Image(AutograderResources.INSTANCE.info());

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
					// null if content has not yet been generated for this problem
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

	private interface Answer {
		String getAnswer();
	}

	private class Body extends Composite {

		private static final String STYLE_TOP_LEVEL = "problemBody";

		private static final String TEXT_DEFAULT_MARKUP = "Nothing to see here.";

		private final FlowPanel toplevel;

		private String markup;

		private QuestionWidget[] questions;

		private final class QuestionWidget extends Composite {

			private static final String ANSWER_FIELD = "field";

			private static final String ANSWER_BOOLEAN = "boolean";

			private static final String ANSWER_LIST = "list";

			private String type;

			private String content;

			private final FlowPanel layout;

			private final Widget ANSWER_WIDGET;

			private Image gradeFlag;

			private final class CustomField extends TextBox implements Answer {

				private CustomField() {
					super();
				}

				@Override
				public String getAnswer() {
					return getValue();
				}
			}

			private final class CustomList extends ListBox implements Answer {

				private CustomList(String... items) {
					super();
					addItem("");
					for (String item : items) {
						addItem(item);
					}
				}

				@Override
				public String getAnswer() {
					return getSelectedItemText();
				}
			}

			private QuestionWidget(final String type, final String content, Image gradeFlag) {
				this.type = type;
				this.content = content;
				this.gradeFlag = gradeFlag;

				// Make QuestionWidget div display inline
				layout = new FlowPanel();
				layout.getElement().getStyle().setDisplay(Display.INLINE);

				/*
				 * Possibly add grade flag (usually green check or red cross)
				 */
				if (gradeFlag != null) {
					layout.add(gradeFlag);
				}

				/*
				 * Add answer widget
				 */
				if (type.equals(ANSWER_FIELD)) {
					// simple text field
					ANSWER_WIDGET = new CustomField();

				} else if (type.equals(ANSWER_BOOLEAN)) {
					// true/false drop-down
					ANSWER_WIDGET = new CustomList("True", "False");

				} else if (type.equals(ANSWER_LIST)) {
					// custom (content-specified) drop-down
					final String[] items = content.split(",");
					ANSWER_WIDGET = new CustomList(items);

				} else {
					// not supported
					ANSWER_WIDGET = null;
					reportErrorParsingBody("Error parsing problem body: unsupported answer type: " + type,
					        "Error loading problem body (Error 200)");
				}
				layout.add(ANSWER_WIDGET);

				/*
				 * Add question info button (usually previous answers)
				 */
				layout.add(info);

				initWidget(layout);
			}

			private String getAnswer() {
				return ((Answer) ANSWER_WIDGET).getAnswer();
			}

			private void setGradeFlag(final Image gradeFlag) {
				if (this.gradeFlag != null) {
					layout.remove(0);
				}
				this.gradeFlag = gradeFlag;
				layout.insert(gradeFlag, 0);
			}
		}

		/**
		 * Reference: http://rubular.com/r/SlPJkHnlY8
		 */
		private static final String PROCESS_ANSWER_DIV = "^type:(field|boolean|list)?,content:(.*)$";

		private final RegExp PROCESS_PATTERN = RegExp.compile(PROCESS_ANSWER_DIV);

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

		private void update(final String bodyMarkup, final Permutation permutation) {
			// update body only if it's different
			if (!bodyMarkup.equals(markup)) {
				markup = bodyMarkup;
				if (questions != null) {
					for (int i = 0; i < questions.length; ++i) {
						questions[0] = null;
					}
				}
				renderMarkup(permutation);
			}
		}

		private void renderMarkup(final Permutation permutation) {

			// Attach to DOM
			HTMLPanel panel = new HTMLPanel(markup);
			toplevel.clear();
			toplevel.add(panel);

			questions = new QuestionWidget[permutation.getNumAnswers()];

			// Replace answer divs with widgets by ID
			for(int i = 1; i <= permutation.getNumAnswers(); i++) {
				final String id = "ans_" + i;
				final Element divElement = panel.getElementById(id);
				final String innerText = divElement.getInnerText();

				final MatchResult matcher = PROCESS_PATTERN.exec(innerText);
				final boolean matchFound = matcher != null;
				if (matchFound) {
					
					if (matcher.getGroupCount() != 3) {
						reportErrorParsingBody("Error parsing problem body: answer match group count is != 3",
						        "Error loading problem body (Error 100)");
						return;
					}

					// group 0 is whole, group 1 type
					final String type = matcher.getGroup(1);
					// group 2 content
					final String content = matcher.getGroup(2);

					// create QuestionWidget to house each answer field
					final QuestionWidget widget = new QuestionWidget(type, content, greenCheck);
					panel.addAndReplaceElement(widget, id);
					questions[i - 1] = widget;
				}
			}
		}

		private void reportErrorParsingBody(String logMessage, String userMessage) {
			LOG.publish(new LogRecord(Level.INFO, logMessage));
			toplevel.clear();
			Label errorLabel = new Label(userMessage);
			errorLabel.addStyleName("errorLabel");
			toplevel.add(errorLabel);
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
			if (body.questions == null) {
				Window.alert("Nothing to submit...");
				return;
			}
			String[] answers = new String[body.questions.length];
			for (int i = 0; i < body.questions.length; ++i) {
				answers[i] = body.questions[i].getAnswer();
			}

			// temporary:
			String alert = answers[0];
			for (int i = 1; i < answers.length; ++i) {
				alert += ", " + answers[1];
			}
			Window.alert(alert);
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

	public void update(final ProblemData data) {
		header.update(data.getTitle(), data.getEarnedPoints(), data.getTotalPoints());
		body.update(data.getBodyMarkup(), data.getPermutation());
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
