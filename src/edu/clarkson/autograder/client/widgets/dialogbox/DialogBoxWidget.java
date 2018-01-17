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
