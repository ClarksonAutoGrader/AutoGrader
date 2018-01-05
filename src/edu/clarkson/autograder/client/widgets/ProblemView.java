package edu.clarkson.autograder.client.widgets;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
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

	private static final SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	// core ProblemView elements
	private final VerticalPanel toplevel;
	private final Header header;
	private final Body body;
	private final Footer footer;

	// pop-up elements
	private ProblemPopup previousAnswersPopup;
	private static final String TEXT_PREVIOUS_ANSWERS = "Previous Answers";
	
	private ProblemData problemData;

	private class Header extends Composite {

		private static final String STYLE_TOP_LEVEL = "problemHeader";

		private static final String STYLE_TITLE = "problemTitleInlineLabel";

		private static final String STYLE_GRADE = "problemGradeInlineLabel";

		private final FlowPanel toplevel;

		private InlineLabel problemTitle;

		private InlineLabel problemGrade;

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
		}

	}

	/**
	 * Abstracts question input widget type which may have various methods for
	 * obtaining their current value.
	 */
	private interface Answer {
		String getAnswer();
	}

	private Widget createPreviousAnswersContent(final int permutationId, final int answerNumber) {

		// TODO: call this method at some point in here
		requestPreviousAnswersAsync();

		Label content = new Label("Previous answers listed below" + " temp: perm=" + permutationId + " ans_" + answerNumber);
		content.addStyleName("previousAnswersContent");
		return content;
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

			private QuestionWidget(final int permutationId, final int answerId, final String type, final String content,
			        Image gradeFlag) {
				this.gradeFlag = gradeFlag;

				// Make QuestionWidget div display inline
				layout = new FlowPanel();
				layout.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
				layout.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

				/*
				 * Possibly add grade flag (usually green check or red cross)
				 */
				if (gradeFlag != null) {
					layout.add(gradeFlag);
					gradeFlag.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
					gradeFlag.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
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
				ANSWER_WIDGET.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
				ANSWER_WIDGET.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
				ANSWER_WIDGET.getElement().getStyle().setMarginLeft(3, Unit.PX);
				ANSWER_WIDGET.getElement().getStyle().setMarginRight(0, Unit.PX);
				ANSWER_WIDGET.getElement().getStyle().setPadding(1, Unit.PX);

				/*
				 * Add question info button (usually previous answers)
				 */
				final Button infoButton = new Button();
				infoButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Widget popupContent = createPreviousAnswersContent(permutationId, answerId);

						// ensure only one previous answers pop-up object is created
						if (previousAnswersPopup == null) {
							previousAnswersPopup = new ProblemPopup(TEXT_PREVIOUS_ANSWERS, popupContent);
						} else {
							previousAnswersPopup.update(TEXT_PREVIOUS_ANSWERS, popupContent);
						}

						previousAnswersPopup.center();
						previousAnswersPopup.show();
					}
				});
				// add style and image to button
				final Element infoElement = infoButton.getElement();
				final Image info = new Image(AutograderResources.INSTANCE.info());
				infoElement.appendChild(info.getElement());
				infoElement.getStyle().setPadding(1, Unit.PX);
				infoElement.getStyle().setHeight(20, Unit.PX);
				infoElement.getStyle().setDisplay(Display.INLINE_BLOCK);
				infoElement.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
				layout.add(infoButton);

				initWidget(layout);
			}

			private String getAnswer() {
				return ((Answer) ANSWER_WIDGET).getAnswer();
			}

			private void setGradeFlag(final Image gradeFlag) {
				// Possibly remove current flag
				if (this.gradeFlag != null) {
					layout.remove(0);
				}
				// Possibly set new flag
				if (gradeFlag != null) {
					layout.insert(gradeFlag, 0);
					gradeFlag.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
					gradeFlag.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
				}
				// record change
				this.gradeFlag = gradeFlag;
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

				if (questions == null) {
					// Autograder only supports 10 questions per problem
					questions = new QuestionWidget[10];
				}
				for (int i = 0; i < questions.length; ++i) {
					questions[0] = null;
				}
				renderMarkup(permutation);
			}
		}

		private void renderMarkup(final Permutation permutation) {

			// Attach to DOM
			HTMLPanel panel = new HTMLPanel(markup);
			toplevel.clear();
			toplevel.add(panel);

			// Replace answer divs with widgets by ID
			for (int ansNum = 1; ansNum <= permutation.getNumAnswers(); ansNum++) {
				final String id = "ans_" + ansNum;
				final Element divElement = panel.getElementById(id);
				if (divElement == null) {
					reportErrorParsingBody("Error parsing problem body: cannot find element " + id,
					        "Error loading problem body (Error 76)");
					return;
				}
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
					final QuestionWidget widget = new QuestionWidget(permutation.getId(), ansNum, type, content, null);
					panel.addAndReplaceElement(widget, id);
					questions[ansNum - 1] = widget;
				} else {
					reportErrorParsingBody("Error parsing problem body: no answer match found for " + id,
					        "Error loading problem body (Error 50)");
					return;
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
	}
	
	private void actionSubmit() {
		// TODO implement submit action
		
		// check if body never completed rendering
		if (body.questions == null) {
			Window.alert("Nothing to submit...");
			return;
		}
		String[] answers = new String[problemData.getNumAnswers()];
		for (int i = 0; i < answers.length; ++i) {
			answers[i] = body.questions[i].getAnswer();
		}
		
		// temporary:
		String alert = answers[0];
		for (int i = 1; i < answers.length; ++i) {
			alert += ", " + answers[i];
		}
		Window.alert(alert);

		// temporary: clear gradeFlag for testing purposes
		for (Body.QuestionWidget question : body.questions) {
			question.setGradeFlag(null);
		}
	}
	
	private void actionNewProblem() {
		// TODO implement new problem action

		// temporary: change gradeFlag for testing purposes
		for (Body.QuestionWidget question : body.questions) {
			question.setGradeFlag(new Image(AutograderResources.INSTANCE.redCross()));
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
		
		problemData = data;
		
		header.update(data.getTitle(), data.getEarnedPoints(), data.getTotalPoints());
		body.update(data.getBodyMarkup(), data.getPermutation());
		footer.update(data.getResets(), data.getAttempts());
	}

	private void requestPreviousAnswersAsync() {
		// onSuccess: call #createPreviousAnswersContent
	}

	private class ProblemPopup extends DialogBox {

		private static final String STYLE_TOP_LEVEL = "problemPopup";

		private static final String TEXT_CLOSE_BUTTON = "Close";

		private static final String STYLE_CLOSE_BUTTON = "problemPopupCloseButton";

		private VerticalPanel toplevel;

		private Button closeButton;

		private ProblemPopup(final String popupCaption, final Widget popupContent) {

			setText(popupCaption);

			// pop-up animation
			setAnimationEnabled(true);

			// glass background
			setGlassEnabled(true);


			closeButton = new Button(TEXT_CLOSE_BUTTON);
			closeButton.setStyleName(STYLE_CLOSE_BUTTON);
			closeButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
				}
			});

			toplevel = new VerticalPanel();
			toplevel.setStyleName(STYLE_TOP_LEVEL);

			if (popupContent != null) {
				toplevel.add(popupContent);
			}
			toplevel.add(closeButton);

			setWidget(toplevel);
		}

		private void update(final String popupCaption, final Widget popupContent) {
			setText(popupCaption);
			if (popupContent != null) {
				toplevel.remove(0);
				toplevel.insert(popupContent, 0);
			}
		}
	}
}
