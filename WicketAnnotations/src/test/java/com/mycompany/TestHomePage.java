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
package com.mycompany;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.Assert;

import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import static com.mycompany.TestRestResource.createTestPerson;

import com.google.gson.Gson;
import com.mycompany.testJsonRequest.JsonMockRequest;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage
{
	private WicketTester tester;

	@Before
	public void setUp()
	{
		tester = new WicketTester(new WicketApplication());
	}

	@Test
	public void homepageRendersSuccessfully()
	{
		//start and render the test page
		tester.getRequest().setMethod("GET");
		tester.executeUrl("./api");
		
		tester.getRequest().setMethod("GET");
		tester.executeUrl("./api/1");
		
		tester.getRequest().setMethod("POST");
		tester.executeUrl("./api");
		
		//test JSON result
		Gson gson = new Gson();
		Assert.assertEquals(gson.toJson(createTestPerson()), tester.getLastResponseAsString());
		
		JsonMockRequest jsonMockRequest = new JsonMockRequest(tester.getRequest(), "POST");
		jsonMockRequest.setReader(new BufferedReader(new StringReader(
				gson.toJson(createTestPerson()))));
		
		tester.setRequest(jsonMockRequest);
		
		tester.executeUrl("./api/19");
		
	}
}
