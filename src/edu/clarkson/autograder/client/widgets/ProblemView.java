package edu.clarkson.autograder.client.widgets;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import edu.clarkson.autograder.client.objects.PreviousAnswersRow;
import edu.clarkson.autograder.client.objects.ProblemData;
import edu.clarkson.autograder.client.objects.UserWork;
import edu.clarkson.autograder.client.services.NewProblemService;
import edu.clarkson.autograder.client.services.NewProblemServiceAsync;
import edu.clarkson.autograder.client.services.PreviousAnswersService;
import edu.clarkson.autograder.client.services.PreviousAnswersServiceAsync;
import edu.clarkson.autograder.client.services.SubmitAnswersService;
import edu.clarkson.autograder.client.services.SubmitAnswersServiceAsync;

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

		private void update() {
			problemTitle.setText(problemData.getTitle());

			if (this.earnedPoints != problemData.getPointsEarned()
			        || this.totalPoints != problemData.getPointsPossible()) {
				problemGrade.setText(Autograder.formatGrade(problemData.getPointsEarned(),
				        problemData.getPointsPossible(), decimalPrecision));
				earnedPoints = problemData.getPointsEarned();
				totalPoints = problemData.getPointsPossible();
			}
		}

	}

	/**
	 * Abstracts question input widget type which may have various methods for
	 * obtaining their current value.
	 */
	private interface AnswerField {

		String getAnswer();

		/**
		 * @param value
		 *            answer to put in field
		 * @return whether operation succeeded
		 */
		boolean setAnswer(String value);

		void setDisabled(boolean disabled);
	}

	private Widget createPreviousAnswersContent(final int answerNumber) {
		FlowPanel contentHandle = new FlowPanel();
		requestPreviousAnswersAsync(contentHandle, answerNumber);
		return contentHandle;
	}

	private class Body extends Composite {

		private static final String STYLE_TOP_LEVEL = "problemBody";

		private static final String TEXT_DEFAULT_MARKUP = "Nothing to see here.";

		private final FlowPanel toplevel;

		private String markup;

		private QuestionWidget[] questions;

		private final class QuestionWidget extends Composite {

			private static final String ANSWER_NUMERIC = "numeric";

			private static final String ANSWER_TEXT = "text";

			private static final String ANSWER_BOOLEAN = "boolean";

			private static final String ANSWER_LIST = "list";

			private final FlowPanel layout;

			private final Widget ANSWER_WIDGET;

			private final class CustomField extends TextBox implements AnswerField {

				private CustomField() {
					super();
				}

				@Override
				public String getAnswer() {
					return getValue();
				}

				@Override
				public boolean setAnswer(String value) {
					boolean successful = true;
					try {
						setValue(value);
					} catch (RuntimeException e) {
						successful = false;
					}
					return successful;
				}

				@Override
				public void setDisabled(boolean disabled) {
					setReadOnly(disabled);
				}
			}

			private final class CustomList extends ListBox implements AnswerField {

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

				@Override
				public boolean setAnswer(String value) {
					boolean found = false;
					for (int index = 0; index < getItemCount(); index++) {
						if (value.equals(getItemText(index))) {
							setSelectedIndex(index);
							found = true;
							break;
						}
					}
					return found;
				}

				@Override
				public void setDisabled(boolean disabled) {
					this.setEnabled(!disabled);
				}
			}

			private QuestionWidget(final int answerId, final String type, final String content,
			        Image gradeFlag) {

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
				if (type.equals(ANSWER_NUMERIC)) {
					// simple text field, only allow numeric input
					// TODO: only allow numeric input
					ANSWER_WIDGET = new CustomField();

				} else if (type.equals(ANSWER_TEXT)) {
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
						Widget popupContent = createPreviousAnswersContent(answerId);

						// ensure only one previous answers pop-up object is created
						if (previousAnswersPopup == null) {
							previousAnswersPopup = new ProblemPopup(TEXT_PREVIOUS_ANSWERS, popupContent);
						} else {
							previousAnswersPopup.update(TEXT_PREVIOUS_ANSWERS, popupContent);
						}

						previousAnswersPopup.showRelativeTo(layout);
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
				return ((AnswerField) ANSWER_WIDGET).getAnswer();
			}

			private boolean setAnswer(String value) {
				return ((AnswerField) ANSWER_WIDGET).setAnswer(value);
			}

			private void setDisabled(boolean disabled) {
				((AnswerField) ANSWER_WIDGET).setDisabled(disabled);
			}
		}

		/**
		 * Reference: http://rubular.com/r/SlPJkHnlY8
		 */
		private static final String PROCESS_ANSWER_DIV = "^flag:(correct|incorrect|null),type:(numeric|text|boolean|list)?,content:(.*)$";

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

		private void update() {
			// update body only if it's different
			if (!problemData.getBodyMarkup().equals(markup)) {
				markup = problemData.getBodyMarkup();

				if (questions == null) {
					// Autograder only supports 10 questions per problem
					questions = new QuestionWidget[10];
				}
				for (int i = 0; i < questions.length; ++i) {
					questions[i] = null;
				}
				renderMarkup();
			}

			// populate question widgets with answers from user work
			final String[] answers = problemData.getUserAnswers();
			if (answers == null) {
				reportErrorParsingBody("Error loading problem body: previous work corrupt (Error 5)",
				        "Error loading problem body: previous work corrupt (Error 5)");
			}
			for (int i = 0; i < body.questions.length; i++) {

				if (body.questions[i] != null && answers[i] != null) {
					boolean success = body.questions[i].setAnswer(answers[i]);
					if (!success) {
						reportErrorParsingBody("Error loading problem body: previous work corrupt (Error 6)",
						        "Error loading problem body: previous work corrupt (Error 6)");
					}
				}
			}

		}

		private void renderMarkup() {

			// Attach to DOM
			HTMLPanel panel = new HTMLPanel(markup);
			toplevel.clear();
			toplevel.add(panel);

			// Replace answer divs with widgets by ID
			for (int ansNum = 1; ansNum <= problemData.getNumAnswers(); ansNum++) {
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
					
					if (matcher.getGroupCount() != 4) {
						reportErrorParsingBody("Error parsing problem body: answer match group count is != 4",
						        "Error loading problem body (Error 100)");
						return;
					}

					// group 0 is whole, group 1 flag
					final String flag = matcher.getGroup(1);
					// group 2 type
					final String type = matcher.getGroup(2);
					// group 3 content
					final String content = matcher.getGroup(3);

					final Image gradeFlag;
					if (flag.equals("correct")) {
						gradeFlag = new Image(AutograderResources.INSTANCE.greenCheck());
					} else if (flag.equals("incorrect")) {
						gradeFlag = new Image(AutograderResources.INSTANCE.redCross());
					} else if (flag.equals("null")) {
						gradeFlag = null;
					} else {
						reportErrorParsingBody(
						        "Error parsing problem body: invalid value for answer group flag, found " + flag,
						        "Error loading problem body (Error 309)");
						return;
					}

					// create QuestionWidget to house each answer field
					final QuestionWidget widget = new QuestionWidget(ansNum, type, content, gradeFlag);
					// lock questionWidgets
					if ((problemData.getAttemptsAllowed() - problemData.getAttemptsUsed()) <= 0
					        || flag.equals("correct")) {
						widget.setDisabled(true);
					}

					// add widget to page
					panel.addAndReplaceElement(widget, id);
					questions[ansNum - 1] = widget;

				} else {
					reportErrorParsingBody(
					        "Error parsing problem body: " + id + " has unknown inner text \"" + innerText + "\"",
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
		
		private Element loadingIcon;

		private Footer() {
			toplevel = new FlowPanel();
			create();
			initWidget(toplevel);
		}

		/**
		 * Instantiate top-level widget.
		 */
		private void create() {

			loadingIcon = new Image(AutograderResources.INSTANCE.loading()).getElement();
			loadingIcon.getStyle().setPaddingLeft(3, Unit.PX);
			loadingIcon.getStyle().setPaddingRight(3, Unit.PX);

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

		private void update() {
			resetsRemaining
			        .setText(TEXT_RESETS_REMAINING + (problemData.getResetsAllowed() - problemData.getResetsUsed()));
			attemptsRemaining.setText(
			        TEXT_ATTEMPTS_REMAINING + (problemData.getAttemptsAllowed() - problemData.getAttemptsUsed()));
		}

		/**
		 * Set the enable state of buttons in the footer
		 */
		private void setEnabled(boolean enabled) {
			submit.setEnabled(enabled);
			newProblem.setEnabled(enabled);
		}
		
		/**
		 * Whether to add loading icon to submit button
		 */
		private void setSubmitButtonLoading(boolean loading) {
			if (loading) {
				submit.getElement().appendChild(loadingIcon);
			} else {
				submit.getElement().removeChild(loadingIcon);
			}
		}

		/**
		 * Whether to add loading icon to New Problem button
		 */
		private void setNewProblemButtonLoading(boolean loading) {
			if (loading) {
				newProblem.getElement().appendChild(loadingIcon);
			} else {
				newProblem.getElement().removeChild(loadingIcon);
			}
		}
	}
	
	private void actionSubmit() {

		// check if body never completed rendering
		if (body.questions == null) {
			Window.alert("Nothing to submit...");
			return;
		}

		// poll answers from QuestionWidgets
		String[] answers = new String[10];
		boolean somethingToSubmit = false;
		for (int i = 0; i < body.questions.length; i++) {

			if (body.questions[i] != null && !body.questions[i].getAnswer().isEmpty()) {
				answers[i] = body.questions[i].getAnswer();
			}

			LOG.publish(new LogRecord(Level.INFO,
			        "ANSWER " + i + " =\"" + answers[i] + "\" isEmpty? "
			                + (answers[i] != null ? answers[i].isEmpty() : "null")));

			// calculate if there is at least one answer to submit
			if (!somethingToSubmit && answers[i] != null && !answers[i].isEmpty()) {
				somethingToSubmit = true;
			}
		}
		// pad answers to length 10 with null
		for (int i = body.questions.length; i < 10; i++) {
			answers[i] = null;
		}

		// do not submit if all answer fields were empty
		if (!somethingToSubmit) {
			Window.alert("Nothing to submit...");
			return;
		}
		
		// prepare footer for longer server response times
		footer.setEnabled(false);
		footer.setSubmitButtonLoading(true);
		
		// Create userWork object
		UserWork userWork = new UserWork(problemData.getUserWorkId(), problemData.getProblemId(),
		        problemData.getPermutationId(), problemData.getResetsUsed(), problemData.getAttemptsUsed(),
		        problemData.getPointsEarned(), answers);

		// push to server
		requestSubmitAnswersAsync(userWork);
	}

	private void actionNewProblem() {

		// prepare footer for longer server response times
		footer.setEnabled(false);
		footer.setNewProblemButtonLoading(true);

		// Create userWork object, null answers
		UserWork userWork = new UserWork(problemData.getUserWorkId(), problemData.getProblemId(),
		        problemData.getPermutationId(), problemData.getResetsUsed(), problemData.getAttemptsUsed(),
		        problemData.getPointsEarned(), null);

		// push to server
		requestNewProblemAsync(userWork);
	}

	public ProblemView(final ProblemData data) {

		problemData = data;

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
		
		header.update();
		body.update();
		footer.update();
	}

	private void requestPreviousAnswersAsync(final FlowPanel layout, int answerNumber) {
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestPreviousAnswersAsync - begin"));

		PreviousAnswersServiceAsync requestPreviousAnswers = GWT.create(PreviousAnswersService.class);
		requestPreviousAnswers.fetchPreviousAnswers(problemData.getPermutationId(), answerNumber,
		        new AsyncCallback<List<PreviousAnswersRow>>() {
			        @Override
			        public void onFailure(Throwable caught) {
				        LOG.publish(new LogRecord(Level.INFO, "ProblemView#requestPreviousAnswersAsync - onFailure"));
				        onError();
			        }

			        @Override
			        public void onSuccess(List<PreviousAnswersRow> previousAnswers) {
				        LOG.publish(new LogRecord(Level.INFO, "ProblemView#requestPreviousAnswersAsync - onSuccess"));

				        if (previousAnswers == null) {
					        onError();
				        }

				        CellTable<PreviousAnswersRow> table = new CellTable<PreviousAnswersRow>();
				        table.addStyleName("previousAnswersContent");

				        // Add a numeric column index previous answers
				        TextColumn<PreviousAnswersRow> indexColumn = new TextColumn<PreviousAnswersRow>() {
					        @Override
					        public String getValue(PreviousAnswersRow object) {
						        return "" + object.getSequenceValue();
					        }
				        };
				        table.addColumn(indexColumn, "#");

				        // Add a text column to show the user's previous
				        // attempts
				        TextColumn<PreviousAnswersRow> userAnswerColumn = new TextColumn<PreviousAnswersRow>() {
					        @Override
					        public String getValue(PreviousAnswersRow object) {
						        return object.getPreviousUserAnswer();
					        }
				        };
				        table.addColumn(userAnswerColumn, "Attempt");

				        // Add a text column to show the correct answer
				        // corresponding to the user's attempt.
				        TextColumn<PreviousAnswersRow> correctAnswerColumn = new TextColumn<PreviousAnswersRow>() {
					        @Override
					        public String getValue(PreviousAnswersRow object) {
						        return object.getPreviousCorrectAnswer();
					        }
				        };
				        // Only display the "Key" column if at least one cell
				        // contains data
				        for (PreviousAnswersRow row : previousAnswers) {
					        if (row.getPreviousCorrectAnswer() != null && !row.getPreviousCorrectAnswer().isEmpty()) {
						        table.addColumn(correctAnswerColumn, "Key");
						        break;
					        }
				        }

				        table.setRowCount(previousAnswers.size(), true);
				        table.setVisibleRange(0, previousAnswers.size());
				        table.setRowData(0, previousAnswers);
				        layout.add(table);
			        }

			        private void onError() {
				        Label errorLabel = new Label("Error loading previous answers.");
				        errorLabel.addStyleName("errorLabel");
				        layout.add(errorLabel);
			        }
		        });
		LOG.publish(new LogRecord(Level.INFO, "CoursePage#requestPreviousAnswersAsync - end"));
	}

	private void requestSubmitAnswersAsync(UserWork userWork) {
		LOG.publish(new LogRecord(Level.INFO, "ProblemView#requestSubmitAnswersAsync - begin"));

		SubmitAnswersServiceAsync submitAnswersService = GWT.create(SubmitAnswersService.class);
		submitAnswersService.submitAnswers(userWork, new AsyncCallback<ProblemData>() {
			@Override
			public void onFailure(Throwable caught) {
				LOG.publish(new LogRecord(Level.INFO, "ProblemView#requestSubmitAnswersAsync - onFailure"));
				onError();
			}

			@Override
			public void onSuccess(ProblemData problemData) {
				LOG.publish(new LogRecord(Level.INFO, "ProblemView#requestSubmitAnswersAsync - onSuccess"));
				if (problemData == null) {
					onError();
					return;
				}
				update(problemData);
				restoreState();
			}

			private void onError() {
				String errorText = "";
				if ((problemData.getAttemptsAllowed() - problemData.getAttemptsUsed()) <= 0) {
					// guess at cause of failure: this check is performed after
					// failure, and even then it's only a guess, in case client
					// had previous spoofed the number of attempts
					errorText = "Unable to submit: No attempts remaining.";
					if ((problemData.getResetsAllowed() - problemData.getResetsUsed()) > 0) {
						errorText += " Press \"New Problem\" to reset your grade and receive a new problem.";
					}
				} else {
					errorText = "Unable to submit problem. Error 10." + problemData.getPermutationId();
				}
				Window.alert(errorText);
				restoreState();
			}

			private void restoreState() {
				footer.setEnabled(true);
				footer.setSubmitButtonLoading(false);
			}
		});
		LOG.publish(new LogRecord(Level.INFO, "ProblemView#requestSubmitAnswersAsync - end"));
	}

	private void requestNewProblemAsync(UserWork userWork) {
		LOG.publish(new LogRecord(Level.INFO, "ProblemView#requestNewProblemAsync - begin"));

		NewProblemServiceAsync newProblemService = GWT.create(NewProblemService.class);
		newProblemService.fetchNewProblem(userWork, new AsyncCallback<ProblemData>() {
			@Override
			public void onFailure(Throwable caught) {
				LOG.publish(new LogRecord(Level.INFO, "ProblemView#requestNewProblemAsync - onFailure"));
				onError();
			}

			@Override
			public void onSuccess(ProblemData problemData) {
				LOG.publish(new LogRecord(Level.INFO, "ProblemView#requestNewProblemAsync - onSuccess"));
				if (problemData == null) {
					onError();
					return;
				}
				update(problemData);
				restoreState();
			}

			private void onError() {
				String errorText = "";
				if ((problemData.getResetsAllowed() - problemData.getResetsUsed()) <= 0) {
					// guess at cause of failure: this check is performed after
					// failure, and even then it's only a guess, in case client
					// had previous spoofed the number of resets
					errorText = "Unable to load new problem: No resets remaining.";
				} else if (problemData.getAttemptsUsed() == 0) {
					errorText = "Please make at least one submission before requesting a new problem.";
				} else {
					errorText = "Unable to load new problem. Error 11." + problemData.getPermutationId();
				}
				Window.alert(errorText);
				restoreState();
			}

			private void restoreState() {
				footer.setEnabled(true);
				footer.setNewProblemButtonLoading(false);
			}
		});
		LOG.publish(new LogRecord(Level.INFO, "ProblemView#requestNewProblemAsync - end"));
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
