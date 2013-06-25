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

import com.mycompany.annotations.HttpMethod;
import com.mycompany.annotations.JsonBody;
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
	
	@MethodMapping(value = "{id}",  httpMethod = HttpMethod.POST)
	public Person testMethodPostComplex(int id, @JsonBody Person person){
		System.out.println("params : " + id + " " + person);
		
		return person;
	}

	public static Person createTestPerson() {
		return new Person("Mary", "Smith", "m.smith@gmail.com");
	}
}