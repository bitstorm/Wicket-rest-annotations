package com.mycompany;

import com.mycompany.annotations.HttpMethod;
import com.mycompany.annotations.MethodMapping;
import com.mycompany.resource.AbstractRestResource;

public class TestRestResource extends AbstractRestResource {
	@MethodMapping("{id}")
	public void testMethodInt(int id){
		System.out.println("method with id:" + id);
	}
	
	@MethodMapping("")
	public void testMethod(){
		System.out.println("method with no param:");
	}
	
	@MethodMapping(value = "",  httpMethod = HttpMethod.POST)
	public Person testMethodPost(){
		System.out.println("method with no param but POST request");
		Person person = createTestPerson();
		return person;
	}

	public static Person createTestPerson() {
		return new Person("Mary", "Smith", "m.smith@gmail.com");
	}
}