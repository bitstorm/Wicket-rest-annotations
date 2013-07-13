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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.rest.annotations.AuthorizeInvocation;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.utils.HttpMethod;
import org.wicketstuff.rest.utils.ReflectionUtils;

/**
 * This class contains the informations of a resource's mapped method (i.e. a
 * method annotated with {@link MethodMapping})
 * 
 * @author andrea del bene
 * 
 */
class UrlMappingInfo {
	/**  */
	private final HttpMethod httpMethod;
	/**  */
	private final List<StringValue> segments = new ArrayList<StringValue>();
	/**  */
	private Roles roles = new Roles();
	/**  */
	private final Method method;
	/**  */
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
	public UrlMappingInfo(String urlPath, HttpMethod httpMethod, Method method) {
		this.httpMethod = httpMethod;
		this.method = method;

		loadSegments(urlPath);
		loadRoles();
		loadNotAnnotatedParameters();
	}

	/**
	 * 
	 */
	private void loadNotAnnotatedParameters() {
		Class<?>[] parameters = method.getParameterTypes();
		List<Class<?>> notAnnotParams = new ArrayList<Class<?>>();

		for (int i = 0; i < parameters.length; i++) {
			Class<?> param = parameters[i];

			if (!ReflectionUtils.isParameterAnnotatedWithAnnotatedParam(i, method))
				notAnnotParams.add(param);
		}

		notAnnotatedParams = notAnnotParams.toArray(parameters);
	}

	/**
	 * 
	 * @param urlPath
	 */
	private void loadSegments(String urlPath) {
		String[] segArray = urlPath.split("/");

		for (int i = 0; i < segArray.length; i++) {
			String segment = segArray[i];
			StringValue segmentValue;

			if (segment.isEmpty())
				continue;

			if (isParameterSegment(segment))
				segmentValue = new VariableSegment(segment);
			else
				segmentValue = StringValue.valueOf(segment);

			segments.add(segmentValue);
		}
	}

	/**
	 * Utility method to check if a segment contains a parameter (i.e.
	 * '/{parameterName}/').
	 * 
	 * @param segment
	 * @return true if the segment contains a parameter, false otherwise.
	 */
	public static boolean isParameterSegment(String segment) {
		return segment.length() >= 4 && segment.startsWith("{") && segment.endsWith("}");
	}

	/**
	 * 
	 */
	private void loadRoles() {
		AuthorizeInvocation authorizeInvocation = method.getAnnotation(AuthorizeInvocation.class);

		if (authorizeInvocation != null) {
			roles = new Roles(authorizeInvocation.value());
		}
	}

	// getters and setters
	public List<StringValue> getSegments() {
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