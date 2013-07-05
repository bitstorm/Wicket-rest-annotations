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

import junit.framework.Assert;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.wicketstuff.rest.exception.MethodInvocationAuthException;
import org.wicketstuff.rest.testJsonRequest.JsonMockRequest;
import org.wicketstuff.rest.testJsonRequest.TestJsonDesSer;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage
{
	private WicketTester tester;
	private Roles roles = new Roles();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setUp()
	{
		tester = new WicketTester(new WicketApplication(roles));
	}

	@Test
	public void homepageRendersSuccessfully()
	{
		roles.clear();
		
		//start and render the test page
		tester.getRequest().setMethod("GET");
		tester.executeUrl("./api");
		
		tester.getRequest().setMethod("GET");
		tester.executeUrl("./api/1");
		
		tester.getRequest().setMethod("POST");
		tester.executeUrl("./api/monoseg");
		
		tester.getRequest().setMethod("POST");
		tester.executeUrl("./api");
		
		//test JSON result
		Assert.assertEquals(TestJsonDesSer.getJSON(), tester.getLastResponseAsString());
		
		JsonMockRequest jsonMockRequest = new JsonMockRequest(tester.getRequest(), "POST");
		jsonMockRequest.setReader(new BufferedReader(new StringReader(TestJsonDesSer.getJSON())));
		
		tester.setRequest(jsonMockRequest);
		
		tester.executeUrl("./api/19");
	}
	
	@Test
	public void rolesAuthorizationMethod(){
		roles.add("ROLE_ADMIN");
		tester.getRequest().setMethod("GET");
		tester.executeUrl("./api/admin");
		
		roles.clear();
		tester.getRequest().setMethod("GET");
		exception.expect(MethodInvocationAuthException.class);
		
		tester.executeUrl("./api/admin");
	}
}
