package edu.clarkson.autograder.client.widgets;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProblemContent extends Composite {

	public ProblemContent(){
		VerticalPanel problemContent = new VerticalPanel();
		//Container for the problem header information (question #, points, previous answers)
		FlowPanel problemHeader = new FlowPanel();
		problemHeader.setStyleName("problemFlowPanel");
		problemHeader.setWidth("100%");
		double pointsReceived = 1;
		double pointsTotal = 4;
		int problemNumber = (int) (100*Math.random());
		String problemHeaderHTML = "<div id=problemHeaderQuestionNumber>Question " + problemNumber + "</div>";
		String nextString = "<div id=problemHeaderPoints>"	+  (pointsReceived / pointsTotal)*100 + "%</div>";
		problemHeader.add(new HTML(problemHeaderHTML));
		problemHeader.add(new HTML(nextString));
		problemHeader.add(new Button("Previous Answers"));
		//Container for the text
		String text = "";
		for(int i=0;i<50;i++) text += "ProblemText ";
		Label problemText = new Label(text);
		//Container for the problem footer information (submit, new question, resets remaining)
		FlowPanel problemFooter = new FlowPanel();
		problemFooter.setStyleName("problemFlowPanel");
		problemFooter.add(new Button("Submit"));
		problemFooter.add(new Button("New Question"));
		int resets = 3;
		String resetsRemaining = "<div id=problemFooterResets> Resets Remaining: " + resets + "</div>";
		problemFooter.add(new HTML(resetsRemaining));
		
		//Add content to problem container
		problemContent.add(problemHeader);
		problemContent.add(problemText);
		problemContent.add(problemFooter);
		
		initWidget(problemContent);
	}
	
	public String getName(){
		return "Problem 1";
	}
}
