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
package org.wicketstuff.rest;

import java.io.BufferedReader;
import java.io.StringReader;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.wicketstuff.rest.domain.PersonPojo;
import org.wicketstuff.rest.utils.JsonMockRequest;

import com.google.gson.Gson;

/**
 * Simple test using the WicketTester
 */
public class TestPersonResource
{
	private WicketTester tester;
	final private Gson gson = new Gson();

	@Before
	public void setUp()
	{
		tester = new WicketTester(new WicketApplication());
	}

	@Test
	public void testCreatePerson()
	{
		JsonMockRequest mockRequest =new JsonMockRequest(tester.getRequest(), "POST");
		String jsonObj = gson.toJson(new PersonPojo("James", "Smith", "changeit"));
		
		mockRequest.setReader(new BufferedReader(new StringReader(jsonObj)));
		
		tester.setRequest(mockRequest);
		tester.executeUrl("./weather/persons");
		
		tester.getRequest().setMethod("GET");
		tester.executeUrl("./weather/persons");
		
		System.out.println(tester.getLastResponseAsString());
	}
}
