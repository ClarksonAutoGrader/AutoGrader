/*
	Autograder is an online homework tool used by Clarkson University.
	
	Copyright 2017-2018 Clarkson University.
	
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

package edu.clarkson.autograder.client.widgets.dialogbox;

public class DialogBoxWidget {

	public interface Constants {

		String okButton = "OK";
		String cancelButton = "Cancel";

	}

	private static DialogBoxView view = null;
	private static DialogBoxPresenter presenter = null;

	public static DialogBoxPresenter confirm(String header, String dialogText, String cancelButtonText,
	        String affirmativeButtonText, ConfirmDialogCallback callback) {
		view = new DialogBoxView();
		presenter = new DialogBoxPresenter(view, header, dialogText, cancelButtonText, affirmativeButtonText, callback);

		presenter.init();

		return presenter;
	}
	
	public static DialogBoxPresenter confirm(String header, String dialogText, String cancelButtonText,
	        String affirmativeButtonText, String thirdButtonText, ThreeOptionDialogCallback callback) {
		view = new DialogBoxView();
		presenter = new DialogBoxPresenter(view, header, dialogText, cancelButtonText, affirmativeButtonText, thirdButtonText, callback);

		presenter.init();

		return presenter;
	}

	public static DialogBoxPresenter confirm(String header, String dialogText, ConfirmDialogCallback callback) {
		return DialogBoxWidget.confirm(header, dialogText, Constants.cancelButton, Constants.okButton, callback);
	}

	public static DialogBoxPresenter alert(String header, String dialogText, String affirmativeButtonText,
	        AlertDialogCallback callback) {
		view = new DialogBoxView();
		presenter = new DialogBoxPresenter(view, header, dialogText, affirmativeButtonText, callback);

		presenter.init();

		return presenter;
	}

	public static DialogBoxPresenter alert(String header, String dialogText, AlertDialogCallback callback) {
		return DialogBoxWidget.alert(header, dialogText, Constants.okButton, callback);
	}

	protected DialogBoxWidget() {
	}
}
