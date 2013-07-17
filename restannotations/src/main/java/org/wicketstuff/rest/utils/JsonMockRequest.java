/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wicketstuff.rest.utils;

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
