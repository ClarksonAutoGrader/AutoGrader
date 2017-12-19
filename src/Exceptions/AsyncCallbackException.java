package Exceptions;

@SuppressWarnings("serial")
public class AsyncCallbackException extends RuntimeException{
	public AsyncCallbackException(String message){
		super(message);
	}
}