package Exceptions;

@SuppressWarnings("serial")
public class OutOfResetsException extends RuntimeException{
	public OutOfResetsException(String message){
		super(message);
	}
}