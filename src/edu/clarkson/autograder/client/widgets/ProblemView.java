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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A widget to display all the visual facets of a problem. Each problem includes
 * a header, body, and footer.<br>
 * <br>
 * <br>
 * Problem header
 * <li>Title</li>
 * <li>Grade: points earned / total points possible (%)</li>
 * <li><i>(not yet supported) Previous answers</i></li> <br>
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

	private TextBox answerField = new TextBox();

	public ProblemView() {

		Header header = new Header();
		Body body = new Body();
		Footer footer = new Footer();

		VerticalPanel toplevel = new VerticalPanel();
		toplevel.add(header);
		toplevel.add(body);
		toplevel.add(footer);

		initWidget(toplevel);
	}

	private class Header extends Composite {

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

			// Formatting for the header FlowPanel
			toplevel.setStyleName("problemFlowPanel");
			toplevel.setWidth("100%");

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

		private HTML toplevel;

		private Body() {
			create();
			initWidget(toplevel);
		}

		/**
		 * Instantiate top-level widget.
		 */
		private void create() {
			// Creates the question body
			// add parser here
			String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ac massa vitae lorem accumsan scelerisque. Nulla viverra dictum odio,"
			        + " et consectetur ante condimentum quis. Curabitur maximus sollicitudin finibus. Duis tincidunt vehicula sem, quis eleifend tellus feugiat "
			        + "quis. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed iaculis massa eu sem hendrerit "
			        + "aliquam. Aenean luctus risus nec ante gravida malesuada. Nulla id lorem nulla. Morbi feugiat congue lectus condimentum consectetur. "
			        + "Vestibulum mattis, massa et efficitur blandit, eros arcu sodales nisi, quis sodales nulla dolor nec nulla. Praesent sagittis tincidunt "
			        + "magna, ac vestibulum felis pharetra ut. Mauris in pretium magna. Sed felis nisi, tempor vel nisi quis, gravida laoreet erat. Duis"
			        + " eleifend neque eros, nec posuere sapien accumsan id. Nullam vel arcu et est scelerisque posuere eget non justo. In et eros ut urna "
			        + "condimentum vestibulum. Duis id lacus ipsum. Donec arcu sem, egestas sit amet accumsan vel, ultrices in enim. Duis arcu nunc, venenatis "
			        + "at suscipit ut, convallis a nibh. Nulla hendrerit venenatis ex. Vestibulum nunc nibh, cursus ac nunc eu, bibendum lacinia magna.<br><br> Cras id "
			        + "eros a felis ullamcorper pulvinar id sed nunc. Duis ullamcorper ac erat et faucibus. Aenean consectetur neque a libero convallis finibus. "
			        + "Praesent commodo luctus quam, vel imperdiet sem blandit nec. Nulla porta urna massa, vel efficitur mauris vehicula in. Donec in nulla ligula. "
			        + "Suspendisse dignissim lacinia malesuada. Vestibulum convallis viverra dignissim. Ut nec lectus sed sapien tempus pretium non nec nisl. Vivamus "
			        + "aliquam blandit diam quis vestibulum. Aliquam iaculis ac nibh eget suscipit. Etiam eu molestie nisl. Maecenas dictum dui et tincidunt"
			        + " pellentesque. Vestibulum sapien odio, porttitor at risus ut, sollicitudin efficitur elit. Nullam at elit metus. Donec sodales vitae neque"
			        + " vel interdum. Mauris vel posuere enim, maximus vestibulum tellus. Curabitur in lectus id est pulvinar auctor. Duis vel faucibus orci. "
			        + "Praesent ut urna a massa tempor dictum. Morbi tristique, lectus sit amet malesuada convallis, lectus metus posuere orci, id semper eros "
			        + "ante ornare elit. Cras elementum rhoncus sem id cursus. Fusce arcu nulla, semper vitae fringilla et, malesuada pulvinar justo. Mauris a malesuada "
			        + "sapien, ut dapibus nisi. Aliquam mattis est sit amet lacus cursus vehicula. Quisque quis nisi pulvinar, luctus nisi non, faucibus velit. "
			        + "Etiam condimentum sollicitudin orci vitae pulvinar. Fusce consectetur mauris id ante interdum, sed porttitor turpis ultrices. Nunc id eros"
			        + " quam. In hac habitasse platea dictumst. Integer augue ligula, gravida eu enim nec, aliquet rhoncus tortor. Duis vel lacus euismod, facilisis "
			        + "ipsum id, dignissim erat. Proin id aliquet eros. Aenean sed risus ex. Nunc eget ultrices nisi, eu luctus nisi. Nulla facilisi. Vivamus "
			        + "eleifend efficitur risus. Nunc finibus iaculis ultricies.<br><br>";
			HTML bodyText = new HTML(text);

			FlowPanel problemBodyContent = new FlowPanel();
			problemBodyContent.add(bodyText);
			problemBodyContent.add(new InlineLabel("Solve the following: 7 + 3 = "));
			problemBodyContent.add(answerField);
			problemBodyContent.addStyleName("problemBodyText");

			toplevel = new HTML(problemBodyContent.getElement().getString());
		}
	}

	private class Footer extends Composite {

		private FlowPanel toplevel;

		private Footer() {
			create();
			initWidget(toplevel);
		}

		/**
		 * Instantiate top-level widget.
		 */
		private void create() {

			toplevel = new FlowPanel();

			toplevel.setStylePrimaryName("problemFooter");
			Button submit = new Button("Submit");
			submit.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					validateAnswer();
				}
			});
			submit.addStyleName("buttons");
			toplevel.add(submit);
			int resets = 3;
			Button newQuestion = new Button("New Question");
			newQuestion.addStyleName("buttons");
			toplevel.add(newQuestion);
			toplevel.add(new InlineLabel("Resets Remaining: " + resets));
		}

		private void validateAnswer() {
			double answer = 0;
			try {
				answer = Double.parseDouble(answerField.getValue());
				if (answer == 10) {
					MyDialog box = new MyDialog("Correct answer", "7 + 3 = 10");
					int left = Window.getClientWidth() / 2;
					int top = Window.getClientHeight() / 2;

					box.setPopupPosition(left, top);
					box.center();
					box.show();
				} else {
					MyDialog box = new MyDialog("Incorrect answer", "Try again");
					int left = Window.getClientWidth() / 2;
					int top = Window.getClientHeight() / 2;

					box.setPopupPosition(left, top);
					box.center();
					box.show();
				}
			} catch (NumberFormatException e) {
				answerField.setText("");
			}
		}
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
