package com.mycompany;

import junit.framework.Assert;

import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

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
		JSONObject jsonObject = new JSONObject(TestRestResource.createTestPerson());		
		Assert.assertEquals(jsonObject.toString(), tester.getLastResponseAsString());
	}
}
