package org.wicketstuff.rest.testJsonRequest;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;

public class JsonMockRequest extends MockHttpServletRequest {
	BufferedReader reader;
	public JsonMockRequest(MockHttpServletRequest mockHttpServletRequest, String method) {
		super(Application.get(), mockHttpServletRequest.getSession(), mockHttpServletRequest.getServletContext());
		setMethod(method);
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}
}
