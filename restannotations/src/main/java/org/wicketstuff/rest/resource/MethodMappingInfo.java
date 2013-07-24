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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.rest.annotations.AuthorizeInvocation;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.utils.HttpMethod;
import org.wicketstuff.rest.utils.ReflectionUtils;

/**
 * This class contains the informations of a resource's mapped method (i.e. a
 * method annotated with {@link MethodMapping}). These informations are used at
 * runtime to select the most suited method to serve the current request.
 * 
 * @author andrea del bene
 * 
 */
class MethodMappingInfo {
	/** The HTTP method used to invoke this mapped method. */
	private final HttpMethod httpMethod;
	/** Segments that compose the URL we mapped the method on. */
	private final List<GeneralURLSegment> segments = new ArrayList<GeneralURLSegment>();
	/**
	 * Optional roles we used to annotate the method (see annotation
	 * AuthorizeInvocation).
	 */
	private Roles roles = new Roles();
	/** The resource method we have mapped. */
	private final Method method;
	/**
	 * Store method parameters that are NOT annotated with an AnnotatedParam
	 * annotation (i.e. their value must be extracted from URL). See annotations
	 * in package org.wicketstuff.rest.annotations.parameters .
	 */
	private Class<?>[] notAnnotatedParams;

	/**
	 * Class construnctor.
	 * 
	 * @param urlPath
	 *            the URL used to map a resource's method
	 * @param httpMethod
	 *            the request method that must be used to invoke the mapped
	 *            method (see class {@link HttpMethod}).
	 * @param method
	 *            the resource's method mapped.
	 */
	public MethodMappingInfo(String urlPath, HttpMethod httpMethod, Method method) {
		this.httpMethod = httpMethod;
		this.method = method;

		loadSegments(urlPath);
		loadRoles();
	}

	/**
	 * Loads the method parameters that are NOT annotated with an AnnotatedParam
	 * annotation (i.e. their value must be extracted from URL). See annotations
	 * in package {@link org.wicketstuff.rest.annotations.parameters}.
	 * 
	 * @return
	 */
	private Class<?>[] loadNotAnnotatedParameters() {
		Class<?>[] parameters = method.getParameterTypes();
		List<Class<?>> notAnnotParams = new ArrayList<Class<?>>();

		for (int i = 0; i < parameters.length; i++) {
			Class<?> param = parameters[i];

			if (!ReflectionUtils.isParameterAnnotatedWithAnnotatedParam(i, method))
				notAnnotParams.add(param);
		}

		return notAnnotParams.toArray(parameters);
	}

	/**
	 * Loads the segment that compose the URL used to map the method. Segments
	 * are instances of class {@link GeneralURLSegment}. Segments that contains
	 * a parameter value (for example '/{id}/') are stored with class
	 * {@link ParamSegment}.
	 * 
	 * @param urlPath
	 */
	private void loadSegments(String urlPath) {
		String[] segArray = urlPath.split("/");

		for (int i = 0; i < segArray.length; i++) {
			String segment = segArray[i];
			GeneralURLSegment segmentValue;

			if (segment.isEmpty())
				continue;

			segmentValue = GeneralURLSegment.newSegment(segment);
			this.segments.add(segmentValue);
		}
	}

	/**
	 * Load the optionals roles used to annotate the method with
	 * {@link AuthorizeInvocation}
	 */
	private void loadRoles() {
		AuthorizeInvocation authorizeInvocation = method.getAnnotation(AuthorizeInvocation.class);

		if (authorizeInvocation != null) {
			roles = new Roles(authorizeInvocation.value());
		}
	}

	public LinkedHashMap<String, String> populatePathVariables(PageParameters pageParameters) {
		LinkedHashMap<String, String> pathVariables = new LinkedHashMap<String, String>();
		int indexedCount = pageParameters.getIndexedCount();

		for (int i = 0; i < indexedCount; i++) {
			StringValue segmentContent = pageParameters.get(i);
			GeneralURLSegment segment = segments.get(i);

			segment.populatePathVariables(pathVariables, segmentContent.toString());
		}

		return pathVariables;
	}

	// getters and setters
	public List<GeneralURLSegment> getSegments() {
		return segments;
	}

	public int getSegmentsCount() {
		return segments.size();
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public Method getMethod() {
		return method;
	}

	public Roles getRoles() {
		return roles;
	}

	public Class<?>[] getNotAnnotatedParams() {
		return notAnnotatedParams;
	}
}