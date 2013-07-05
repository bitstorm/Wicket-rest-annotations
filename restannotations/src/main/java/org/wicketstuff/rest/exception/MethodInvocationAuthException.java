package org.wicketstuff.rest.exception;

import org.apache.wicket.authorization.AuthorizationException;

public class MethodInvocationAuthException extends AuthorizationException {

	public MethodInvocationAuthException() {
		super();
	}

	public MethodInvocationAuthException(String message, Throwable cause) {
		super(message, cause);
	}

	public MethodInvocationAuthException(String message) {
		super(message);
	}

	public MethodInvocationAuthException(Throwable cause) {
		super(cause);
	}
}
