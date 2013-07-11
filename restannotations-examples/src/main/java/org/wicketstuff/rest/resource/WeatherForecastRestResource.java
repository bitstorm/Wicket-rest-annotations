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

import java.util.Date;

import javax.xml.ws.RequestWrapper;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.cycle.RequestCycle;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.parameters.QueryParam;
import org.wicketstuff.rest.domain.WeatherForecast;
import org.wicketstuff.rest.resource.gson.GsonRestResource;

import com.google.gson.Gson;

public class WeatherForecastRestResource extends GsonRestResource {
	private MetaDataKey<String> callbackName = new MetaDataKey<String>(){};
	
	@MethodMapping("/forecast/{date}/{partday}")
	public WeatherForecast getForecast(long day, int partOfTheDay, @QueryParam("callback") String callback){
		Date dayDate = new Date(day);
		RequestCycle.get().setMetaData(callbackName, callback);
		
		return new WeatherForecast(67.8f, 24.2f, 3, 
				partOfTheDay, dayDate);
	}
	
	@Override
	protected String serializeObjToString(Object result, Gson jsonSerialDeserial) {
		String jsonStr = super.serializeObjToString(result, jsonSerialDeserial);
		String callbackFunction = RequestCycle.get().getMetaData(callbackName);
		
		return callbackFunction + "(" + jsonStr + ");";
	}
}
