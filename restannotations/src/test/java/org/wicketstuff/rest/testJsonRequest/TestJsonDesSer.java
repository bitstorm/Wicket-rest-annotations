package org.wicketstuff.rest.testJsonRequest;

public class TestJsonDesSer {
	static public Object getObject(){
		return TestRestResource.createTestPerson();
	}
	
	static public String getJSON(){
		return "{\"name\" : \"Mary\", \"surname\" : \"Smith\", \"email\" : \"m.smith@gmail.com\"}";
	}
}
