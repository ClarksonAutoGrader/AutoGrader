package edu.clarkson.autograder.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/*
 * TODO
 * ProblemContent needs to parse and display Problem body
 * ProblemContent needs to populate mutable problem fields
 */
public class ProblemContent extends Composite {
	
	private static SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();
	
	private VerticalPanel problemContent = new VerticalPanel();
	private FlowPanel problemHeader = new FlowPanel();
	private FlowPanel problemFooter = new FlowPanel();
	private Label problemBody = new Label();

	private String name;

	public ProblemContent(){	
		
		//Initialize components
		createHeader();
		createBody();
		createFooter();
		
		//Add content to problem container
		problemContent.add(problemHeader);
		problemContent.add(problemBody);
		problemContent.add(problemFooter);
		
		initWidget(problemContent);
	}
	
	private void createHeader(){
		
		//Formatting for the header FlowPanel
		problemHeader.setStyleName("problemFlowPanel");
		problemHeader.setWidth("100%");
		
		
		double pointsReceived = (int) (25*Math.random());
		double pointsTotal = (int) (50*Math.random()+25);
		int problemNumber = 1 + (int) (10 * Math.random());
		String problemHeaderHTML = "<div id=problemHeaderQuestionNumber>Problem " + problemNumber + "</div>";
		String nextString = "<div id=problemHeaderPoints>" + (double) Math.round(pointsReceived) + "/"
		        + ((double) Math.round(pointsTotal)) + " (" + Math.round((pointsReceived / pointsTotal) * 100)
		        + "%)</div>";
		problemHeader.add(new HTML(problemHeaderHTML));
		problemHeader.add(new HTML(nextString));
		
		Button previousAnswers = new Button("Previous Answers", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event){
				MyDialog dialogBox = new MyDialog();
				int left = Window.getClientWidth() / 2;
				int top = Window.getClientHeight() / 2;
				
				dialogBox.setPopupPosition(left, top);
				dialogBox.center();
				dialogBox.show();
			}
		});
		problemHeader.add(previousAnswers);
	}
	
	private void createFooter(){
		problemFooter.setStylePrimaryName("problemFlowPanel");
		problemFooter.setStyleDependentName("footer", true);
		Button submit = new Button("Submit");
		submit.addStyleName("buttons");
		problemFooter.add(submit);
		int resets = 3;
		Button newQuestion = new Button("New Question");
		newQuestion.addStyleName("buttons");
		problemFooter.add(newQuestion);
		problemFooter.add(new InlineLabel("Resets Remaining: " + resets));
		
	}
	
	/**
	 * TODO TEMPORARY constructor
	 */
	public ProblemContent(String name) {
		this();
		this.name = name;
	}

	public String getName(){
		if (name == null)
			return "Problem 1";
		return name;
	}
	
		private void createBody(){
		//Creates the question body
		//TODO: add parser
		String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ac massa vitae lorem accumsan scelerisque. Nulla viverra dictum odio,"
				+ " et consectetur ante condimentum quis. Curabitur maximus sollicitudin finibus. Duis tincidunt vehicula sem, quis eleifend tellus feugiat "
				+ "quis. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed iaculis massa eu sem hendrerit "
				+ "aliquam. Aenean luctus risus nec ante gravida malesuada. Nulla id lorem nulla. Morbi feugiat congue lectus condimentum consectetur. "
				+ "Vestibulum mattis, massa et efficitur blandit, eros arcu sodales nisi, quis sodales nulla dolor nec nulla. Praesent sagittis tincidunt "
				+ "magna, ac vestibulum felis pharetra ut. Mauris in pretium magna. Sed felis nisi, tempor vel nisi quis, gravida laoreet erat. Duis"
				+ " eleifend neque eros, nec posuere sapien accumsan id. Nullam vel arcu et est scelerisque posuere eget non justo. In et eros ut urna "
				+ "condimentum vestibulum. Duis id lacus ipsum. Donec arcu sem, egestas sit amet accumsan vel, ultrices in enim. Duis arcu nunc, venenatis "
				+ "at suscipit ut, convallis a nibh. Nulla hendrerit venenatis ex. Vestibulum nunc nibh, cursus ac nunc eu, bibendum lacinia magna. Cras id "
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
				+ "eleifend efficitur risus. Nunc finibus iaculis ultricies.";
		problemBody.setText(text);
		problemBody.addStyleName("problemBodyText");
	}
	
	//Private DialogBox class to hold previous answers
	private static class MyDialog extends DialogBox {

	      public MyDialog() {
	         // Set the dialog box's caption.
	         setText("Previous Answers");

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

	         Label label = new Label("Previous Answers Below");

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
