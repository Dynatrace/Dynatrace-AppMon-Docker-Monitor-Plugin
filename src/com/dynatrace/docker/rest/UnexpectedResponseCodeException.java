package com.dynatrace.docker.rest;

import java.io.IOException;
import java.text.MessageFormat;

import com.dynatrace.docker.rest.HTTPClient.ResponseCode;

public final class UnexpectedResponseCodeException extends IOException {

	private static final long serialVersionUID = 1L;
	
	static final String ERR_MSG_PATTERN =
			"Unexpected HTTP response code {0} received (expected: {1})"
			.intern();
	
	public UnexpectedResponseCodeException(
			final ResponseCode expected,
			final int actual
	) {
		super(formatMessage(expected, actual));
	}
	
	private static String formatMessage(final ResponseCode expected, final int actual) {
		final ResponseCode actualCode = ResponseCode.fromCode(actual);
		if (actualCode == null) {
			return MessageFormat.format(ERR_MSG_PATTERN, actual, expected);			
		}
		return MessageFormat.format(ERR_MSG_PATTERN, actualCode, expected);
	}

}
