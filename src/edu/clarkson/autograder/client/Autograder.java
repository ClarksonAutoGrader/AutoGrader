/*
	Autograder is an online homework tool used by Clarkson University.
	
	Copyright ©2017-2018 Clarkson University.
	
	This file is part of Autograder.
	
	This program is licensed under the GNU General Purpose License version 3.
	
	Autograder is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	Autograder is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with Autograder. If not, see <http://www.gnu.org/licenses/>.
*/

package edu.clarkson.autograder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;

import edu.clarkson.autograder.client.services.AppSessionLogOutService;
import edu.clarkson.autograder.client.services.AppSessionLogOutServiceAsync;
import edu.clarkson.autograder.client.services.UsernameService;
import edu.clarkson.autograder.client.services.UsernameServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Autograder implements EntryPoint {

	public static final Image loadingImage = new Image(AutograderResources.INSTANCE.loading());

	public static final int ID_TOKEN_WIDTH = 6;
	
	private InlineLabel usernameLabel = new InlineLabel("");

	/**
	 * Attempts to provide identical functionality as:<br>
	 * <br>
	 * <code>String.format("%0" + ID_TOKEN_WIDTH + "d", id);</code>
	 */
	public static String formatIdToken(int id) {
		int unpadded_length = ("" + id).length();
		String padding = "";
		for (int i = 0; i < ID_TOKEN_WIDTH - unpadded_length; ++i)
			padding += "0";
		return padding + id;
	}
	
	/**
	 * 
	 * @param unformatted
	 *            - raw double-precision number
	 * @param decimalPlaces
	 *            - number of decimal places to keep
	 * @return double formatted to the number of decimal places specified
	 */
	public static double numberPrecision(double unformatted, int decimalPlaces) {
		double precision = Math.pow(10, decimalPlaces);
		return (int) (unformatted * precision + 0.5) / precision;
	}
	
	/**
	 * Returns a formatted string showing the earned / total points and a
	 * percentage. If decimalPrecision is 2, the format will be as follows:<br>
	 * <code>00.00/00.00 (00.00%)</code><br>
	 * Problems out of zero total points are marked as 100% complete.
	 * 
	 * @param earnedPoints
	 * @param totalPoints
	 * @param decimalPrecision
	 * @return formatted grade string
	 */
	public static String formatGrade(double earnedPoints, double totalPoints, int decimalPrecision) {
		StringBuilder builder = new StringBuilder();
		builder.setLength(0);
		double earnedFormatted = Autograder.numberPrecision(earnedPoints, decimalPrecision);
		builder.append(earnedFormatted);
		builder.append("/");
		double totalFormatted = Autograder.numberPrecision(totalPoints, decimalPrecision);
		builder.append(totalFormatted);
		builder.append(" points (");
		// problems out of zero total points are always 100% complete
		double percentageFormatted = Autograder.numberPrecision(
				(totalPoints != 0.0 ? earnedPoints / totalPoints * 100 : 100.0), decimalPrecision);
		builder.append(percentageFormatted);
		builder.append("%)");
		return builder.toString();
	}

    /**
     * This is the entry point method.
     */
	public void onModuleLoad() {
		
		requestUserAsync();
		
		// Logout button
		RootPanel.get("info").add(new Button("Log Out", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				requestLogOut();
				}	
    		}));
		
		// Home button
		RootPanel.get("info").add(new Button("Course Selection", new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Window.Location.replace("#courses");
			}	
		}));
		RootPanel.get("info").add(usernameLabel);

        History.addValueChangeHandler(State.getInstance());
        if (History.getToken().isEmpty()) {
            History.newItem("courses");
        }
		// trigger State#onValueChange
        History.fireCurrentHistoryState();
    }

    private void requestUserAsync() {
    	UsernameServiceAsync userService = GWT.create(UsernameService.class);
    	AsyncCallback<String> callback = new AsyncCallback<String>() {
    		public void onFailure(Throwable caught) {
				usernameLabel.setText("Authentication Error (1)");
				ContentContainer.clearContent();
    		}
    		
    		public void onSuccess(String username) {
				if (username.equals("null")) {
					ContentContainer.clearContent();
					usernameLabel.setText("Authentication Error (2)");
				} else {
					usernameLabel.setText(username);
				}
    		}
    	};
    	userService.getCurrentUsername(callback);
    }
    
    private void requestLogOut() {
    	AppSessionLogOutServiceAsync logOutService = GWT.create(AppSessionLogOutService.class);
    	AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
    		public void onFailure(Throwable caught) {
    			Window.alert("Logout failed. Please try again.");
    		}
    		
    		public void onSuccess(Boolean success) {
    			Window.Location.assign("https://cas.clarkson.edu/cas/logout");
    		}
    	};
    	
    	logOutService.appSessionLogOut(callback);
    }
    	
}
