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
package org.wicketstuff.rest.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.domain.PersonPojo;
import org.wicketstuff.rest.domain.WeatherForecast;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import org.wicketstuff.rest.utils.HttpMethod;

public class PersonsRestResource extends GsonRestResource {
	private final List<PersonPojo> persons = new ArrayList<PersonPojo>();
	
	@MethodMapping("/forecast/{date}/{partday}")
	public WeatherForecast getForecast(long day, int partOfTheDay){
		Date dayDate = new Date(day);
		
		return new WeatherForecast(67.8f, 24.2f, 3, 
				partOfTheDay, dayDate);
	}

	@MethodMapping("/persons")
	public List<PersonPojo> getAllPersons() {
		return persons;
	}
	
	@MethodMapping(value = "/persons/{personIndex}", httpMethod = HttpMethod.DELETE)
	public void deletePerson(int personIndex) {
		persons.remove(personIndex);
	}
	
	@MethodMapping(value = "/persons", httpMethod = HttpMethod.POST)
	public void createPerson(@RequestBody PersonPojo personPojo) {
		persons.add(personPojo);
	}
}
