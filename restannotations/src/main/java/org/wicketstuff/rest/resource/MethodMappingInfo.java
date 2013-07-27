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
import org.wicketstuff.rest.annotations.AuthorizeInvocation;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.resource.urlsegments.GeneralURLSegment;
import org.wicketstuff.rest.resource.urlsegments.ParamSegment;
import org.wicketstuff.rest.utils.http.HttpMethod;

/**
 * This class contains the informations of a resource mapped method (i.e. a
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
	private final List<GeneralURLSegment> segments;
	/**
	 * Optional roles we used to annotate the method (see annotation
	 * AuthorizeInvocation).
	 */
	private final Roles roles;
	/** The resource method we have mapped. */
	private final Method method;

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
		this.segments = loadSegments(urlPath);
		this.roles = loadRoles();
	}

	/**
	 * Loads the segment that compose the URL used to map the method. Segments
	 * are instances of class {@link GeneralURLSegment}.
	 * 
	 * @param urlPath
	 * 			the URL path of the method.
	 * @return 
	 * 			a list containing the segments that compose the URL in input
	 */
	private List<GeneralURLSegment> loadSegments(String urlPath) {
		String[] segArray = urlPath.split("/");
		ArrayList<GeneralURLSegment> segments = new ArrayList<GeneralURLSegment>();
		
		for (int i = 0; i < segArray.length; i++) {
			String segment = segArray[i];
			GeneralURLSegment segmentValue;

			if (segment.isEmpty())
				continue;

			segmentValue = GeneralURLSegment.newSegment(segment);
			segments.add(segmentValue);
		}
		
		return segments;
	}

	/**
	 * Load the optional roles used to annotate the method with
	 * {@link AuthorizeInvocation}
	 * @return 
	 * 			the authorization roles for the method.
	 */
	private Roles loadRoles() {
		AuthorizeInvocation authorizeInvocation = method.getAnnotation(AuthorizeInvocation.class);
		Roles roles = new Roles();
		
		if (authorizeInvocation != null) {
			roles = new Roles(authorizeInvocation.value());
		}
		return roles;
	}
	
	/**
	 * This method is invoked to populate the path parameters found in the URL with the values
	 * obtained from the current request.
	 * 
	 * @param pageParameters
	 * 		the current PageParameters.
	 * @return
	 * 		a Map containing the path parameters with their relative value.
	 */
	public LinkedHashMap<String, String> populatePathParameters(PageParameters pageParameters) {
		LinkedHashMap<String, String> pathParameters = new LinkedHashMap<String, String>();
		int indexedCount = pageParameters.getIndexedCount();

		for (int i = 0; i < indexedCount; i++) {
			String segmentContent = GeneralURLSegment.getActualSegment(pageParameters.get(i)
					.toString());
			GeneralURLSegment segment = segments.get(i);

			segment.populatePathVariables(pathParameters, segmentContent);
		}

		return pathParameters;
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
}