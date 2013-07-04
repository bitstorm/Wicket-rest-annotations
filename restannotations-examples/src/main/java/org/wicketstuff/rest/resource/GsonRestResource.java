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

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.rest.annotations.HttpMethod;
import org.wicketstuff.rest.annotations.JsonBody;
import org.wicketstuff.rest.annotations.MethodMapping;

import com.google.gson.Gson;


/**
 * Base class to build a resource that serves REST requests with JSON as exchange format.
 * 
 * @author andrea del bene
 *
 */
public class GsonRestResource extends AbstractJsonRestResource<Gson>{

	public GsonRestResource(Gson jsonSerialDeserial, IRoleCheckingStrategy roleCheckingStrategy) {
		super(jsonSerialDeserial, roleCheckingStrategy);
	}

	@Override
	protected String serializeToJson(Object result, Gson jsonSerialDeserial) {
		return jsonSerialDeserial.toJson(result);
	}

	@Override
	protected Object deserializeFromJson(Class<?> argClass, String json,
			Gson jsonSerialDeserial) {
		return jsonSerialDeserial.fromJson(json, argClass);
	}
	
}