package edu.clarkson.autograder.client.objects;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProblemData implements Serializable {

	private Problem problem;

	private String bodyMarkup;
	private int resets;
	private int attempts;

	/**
	 * TODO REMOVE THIS TEMPORARY CONSTRUCTOR, UNCOMMENT DEFAULT CONSTRUCTOR
	 */
	public ProblemData() {

		problem = new Problem(1, 1, "Test Problem", 20, 11.1257);

		bodyMarkup = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ac massa vitae lorem accumsan scelerisque. Nulla viverra dictum odio,"
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
		        + "Suspendisse dignissim lacinia malesuada. Vestibulum convallis viverra dignissim. Ut nec lectus sed sapien tempus pretium non nec nisl.<br><br>";

		resets = 3;
		attempts = 10;
	}

	/**
	 * Default constructor required for serialization
	 */
//	public ProblemData() {
//	}

	public int getpId() {
		return problem.getId();
	}

	public int getaId() {
		return problem.getaId();
	}

	public String getTitle() {
		return problem.getTitle();
	}

	public double getTotalPoints() {
		return problem.getTotalPoints();
	}

	public double getEarnedPoints() {
		return problem.getEarnedPoints();
	}

	public String getBodyMarkup() {
		return bodyMarkup;
	}

	public int getResets() {
		return resets;
	}

	public int getAttempts() {
		return attempts;
	}

}
