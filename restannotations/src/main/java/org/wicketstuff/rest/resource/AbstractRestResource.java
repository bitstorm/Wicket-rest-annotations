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
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.collections.MultiMap;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.rest.annotations.AuthorizeInvocation;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.parameters.CookieParam;
import org.wicketstuff.rest.annotations.parameters.HeaderParam;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.utils.HttpMethod;
import org.wicketstuff.rest.utils.ReflectionUtils;

/**
 * Base class to build a resource that serves REST requests.
 * 
 * @author andrea del bene
 * 
 */
public abstract class AbstractRestResource<T> implements IResource {
	/** Multimap that stores every mapped method of the class */
	private MultiMap<String, UrlMappingInfo> mappedMethods = new MultiMap<String, UrlMappingInfo>();

	/**
	 * General class that is used to serialize/desiarilze objects to string (for
	 * example to a Json string)
	 */
	private final T objSerialDeserial;

	/** Role-checking strategy. */
	private final IRoleCheckingStrategy roleCheckingStrategy;

	/**
	 * Constructor with no role-checker. If don't use
	 * {@link AuthorizeInvocation} we don't need such a checker.
	 * 
	 * @param roleCheckingStrategy
	 *            Role checking strategy.
	 */
	public AbstractRestResource(T jsonSerialDeserial) {
		this(jsonSerialDeserial, null);
	}

	/**
	 * Main constructor that takes in input the object serializer/deserializer
	 * and the role-checking strategy to use.
	 * 
	 * @param jsonSerialDeserial
	 *            General class that is used to serialize/desiarilze objects to
	 *            string
	 * @param roleCheckingStrategy
	 *            Role-checking strategy.
	 */
	public AbstractRestResource(T jsonSerialDeserial, IRoleCheckingStrategy roleCheckingStrategy) {
		this.objSerialDeserial = jsonSerialDeserial;
		this.roleCheckingStrategy = roleCheckingStrategy;

		configureObjSerialDeserial(jsonSerialDeserial);
		loadAnnotatedMethods();
	}

	/***
	 * Handles a REST request invoking one of the methods annotated with
	 * {@link MethodMapping}. If the annotated method returns a value, this
	 * latter is automatically serialized as a JSON string and written in the
	 * web response.<br/>
	 * If no method is found to serve the current request, a 400 HTTP code is
	 * returned to the client. Similarly, a 401 HTTP code is return if the user
	 * doesn't own one of the roles required to execute an annotated method (See
	 * {@link AuthorizeInvocation}).
	 */
	@Override
	public final void respond(Attributes attributes) {
		PageParameters pageParameters = attributes.getParameters();
		WebResponse response = (WebResponse) attributes.getResponse();
		HttpMethod httpMethod = getHttpMethod((WebRequest) RequestCycle.get().getRequest());
		int indexedParamCount = pageParameters.getIndexedCount();

		// mapped method are stored concatenating the number of the segments of
		// their URL and their HTTP method (see annotation MethodMapping)
		List<UrlMappingInfo> mappedMethodsCandidates = mappedMethods.get(indexedParamCount + "_"
				+ httpMethod.getMethod());

		UrlMappingInfo mappedMethod = null;
		// if we have no segments return the first candidate method (if any)
		if (indexedParamCount == 0 && mappedMethodsCandidates != null) {
			if (mappedMethodsCandidates.size() > 1)
				throwAmbiguousMethodsException(mappedMethodsCandidates);

			mappedMethod = mappedMethodsCandidates.get(0);
		} else {
			mappedMethod = selectMostSuitedMethod(mappedMethodsCandidates, pageParameters);
		}

		if (mappedMethod != null) {
			if (!hasAny(mappedMethod.getRoles())) {
				response.sendError(401, "User is not allowed to invoke method on server.");
				return;
			}

			Object result = invokeMappedMethod(mappedMethod, pageParameters);
			// if the invoked method returns a value, it is written to response
			if (result != null) {
				serializeObjectToResponse(response, result);
			}
		} else {
			response.sendError(400, "No suitable method found for URL '"
					+ RequestCycle.get().getRequest().getClientUrl() + "' and method " + httpMethod);
		}
	}

	/**
	 * Method invoked to serialize an object and write it as response.
	 * 
	 * @param response
	 *            The current response object.
	 * @param result
	 *            The object to write as response.
	 */
	protected void serializeObjectToResponse(WebResponse response, Object result) {
		try {
			response.write(serializeObjToString(result, objSerialDeserial));
		} catch (Exception e) {
			throw new RuntimeException("Error serializing object to response", e);
		}
	}

	/**
	 * Method invoked to select the most suited method to serve the current
	 * request.
	 * 
	 * @param mappedMethods
	 *            List of {@link UrlMappingInfo} containing the informations of
	 *            mapped methods.
	 * @param pageParameters
	 *            The PageParameters of the current request.
	 * @return The "best" method found to serve the request.
	 */
	private UrlMappingInfo selectMostSuitedMethod(List<UrlMappingInfo> mappedMethods,
			PageParameters pageParameters) {
		int highestScore = 0;
		// no method mapped
		MultiMap<Integer, UrlMappingInfo> mappedMethodByScore = new MultiMap<Integer, UrlMappingInfo>();

		if (mappedMethods == null || mappedMethods.size() == 0)
			return null;

		/**
		 * To select the "best" method, a score is assigned to every mapped
		 * method. The score is computed comparing each segment of the current
		 * URL with the corresponding segment of the mounted method. If these
		 * two segment are equal, the score is increased of 2 points. If the
		 * mounted segment contains a parameter's value (for example '/{id}/'),
		 * the value of the URL's segment is checked to see if its value is
		 * compatible with the corresponding method parameter. If so, the score
		 * is increased by one point. In any other case the total score for a
		 * method is set to 0.
		 */
		for (UrlMappingInfo mappedMethod : mappedMethods) {
			List<StringValue> segments = mappedMethod.getSegments();
			Class<?>[] argsClasses = mappedMethod.getNotAnnotatedParams();

			int score = 0;
			int functionParamsIndex = 0;

			for (StringValue segment : segments) {
				int i = segments.indexOf(segment);

				if (segment instanceof VariableSegment
						&& isSegmentCompatible(pageParameters.get(i),
								argsClasses[functionParamsIndex++])) {
					score++;
				} else if (pageParameters.get(i).equals(segment)) {
					score += 2;
				} else {
					score = 0;
					break;
				}
			}

			if (score > highestScore) {
				highestScore = score;
				mappedMethodByScore.addValue(score, mappedMethod);
			}
		}

		if (mappedMethodByScore.get(highestScore) != null
				&& mappedMethodByScore.get(highestScore).size() > 1)
			throwAmbiguousMethodsException(mappedMethodByScore.get(highestScore));

		return mappedMethodByScore.getFirstValue(highestScore);
	}
	
	/**
	 * Throw an exception if two o more methods are suited for the current request.
	 * 
	 * @param list the list of ambiguous methods.
	 */
	private void throwAmbiguousMethodsException(List<UrlMappingInfo> list) {
		WebRequest request = (WebRequest) RequestCycle.get().getRequest();
		String methodsNames = "";
		
		for (UrlMappingInfo urlMappingInfo : list) {
			if(!methodsNames.isEmpty())
				methodsNames += ", ";
			
			methodsNames += urlMappingInfo.getMethod().getName();
		}
		
		throw new WicketRuntimeException("Ambiguous methods mapped for the current request: URL '"
				+ request.getClientUrl() + "', HTTP method " + getHttpMethod(request) + ". "
				+ "Mapped methods: " + methodsNames);
	}

	/**
	 * This method checks if a string value can be converted to a target type.
	 * 
	 * @param segment
	 *            the string value we want to convert.
	 * @param paramClass
	 *            the target type.
	 * @return true if the segment value is compatible, false otherwise
	 */
	private boolean isSegmentCompatible(StringValue segment, Class<?> paramClass) {
		try {
			Object convertedObject = toObject(paramClass, segment.toString());
		} catch (Exception e) {
			// segment's value not compatible with paramClass
			return false;
		}

		return true;
	}

	/**
	 * Method called by the constructor to configure the object
	 * serializer/deserializer.
	 * 
	 * 
	 * @param objSerialDeserial
	 *            the object serializer/deserializer
	 */
	protected void configureObjSerialDeserial(T objSerialDeserial) {
	};

	/**
	 * Method invoked to serialize the value returned by the mapped method
	 * invoked to serve the request.
	 * 
	 * @param result
	 *            the value returned by the method invoked to serve the request.
	 * @param objSerialDeserial
	 *            the object used to serialize/deserialize an object.
	 * @return the object serialized as string.
	 */
	protected abstract String serializeObjToString(Object result, T objSerialDeserial);

	/**
	 * Method invoked to deserialize an object from a string.
	 * 
	 * @param argClass
	 *            the type used to deserialize the object.
	 * @param strValue
	 *            the string value containing our object.
	 * @param objSerialDeserial
	 *            the object serializer/deserializer.
	 * @return the deserialized object
	 */
	protected abstract Object deserializeObjFromString(Class<?> argClass, String strValue,
			T objSerialDeserial);

	/***
	 * Internal method to load class methods annotated with
	 * {@link MethodMapping}
	 */
	private void loadAnnotatedMethods() {
		Method[] methods = getClass().getDeclaredMethods();
		boolean isUsingAuthAnnot = false;

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			MethodMapping methodMapped = method.getAnnotation(MethodMapping.class);
			AuthorizeInvocation authorizeInvocation = method
					.getAnnotation(AuthorizeInvocation.class);
			isUsingAuthAnnot = isUsingAuthAnnot || authorizeInvocation != null;

			if (methodMapped != null) {
				String urlPath = methodMapped.value();
				HttpMethod httpMethod = methodMapped.httpMethod();
				UrlMappingInfo urlMappingInfo = new UrlMappingInfo(urlPath, httpMethod, method);

				mappedMethods.addValue(
						urlMappingInfo.getSegmentsCount() + "_" + httpMethod.getMethod(),
						urlMappingInfo);
			}
		}

		if (isUsingAuthAnnot && roleCheckingStrategy == null)
			throw new WicketRuntimeException(
					"Annotation AuthorizeInvocation is used but no roleCheckingStrategy has been provided to the controller.");
	}

	/**
	 * Utility method to extract the request method
	 * 
	 * @param request
	 *            the current request object
	 * @return the HTTP method used for this request
	 * @see HttpMethod
	 */
	public static HttpMethod getHttpMethod(WebRequest request) {
		HttpServletRequest httpRequest = (HttpServletRequest) request.getContainerRequest();
		return HttpMethod.toHttpMethod((httpRequest.getMethod()));
	}

	/***
	 * This method invokes one of the resource methods annotated with
	 * {@link MethodMapping}.
	 * 
	 * @param mappedMethod
	 *            mapping info of the method
	 * @param pageParameters
	 *            PageParametrs object of the current request
	 * @return the value returned by the invoked method
	 */
	private Object invokeMappedMethod(UrlMappingInfo mappedMethod, PageParameters pageParameters) {

		Method targetMethod = mappedMethod.getMethod();
		Class<?>[] argsClasses = targetMethod.getParameterTypes();
		List parametersValues = new ArrayList();
		Iterator<StringValue> segmentsIterator = mappedMethod.getSegments().iterator();

		for (int i = 0; i < argsClasses.length; i++) {
			Class<?> argClass = argsClasses[i];
			Object paramValue = null;

			if (!ReflectionUtils.isParameterAnnotatedWithAnnotatedParam(i, targetMethod))
				paramValue = extractParameterFromUrl(mappedMethod, pageParameters,
						segmentsIterator, argClass);
			else
				paramValue = extractParameterValue(i, targetMethod, argClass, pageParameters);

			if (paramValue == null)
				throw new WicketRuntimeException("Could not find a value for the " + (i + 1)
						+ "° parameter of controller's function " + targetMethod.getName());
			parametersValues.add(paramValue);
		}

		try {
			return targetMethod.invoke(this, parametersValues.toArray());
		} catch (Exception e) {
			throw new RuntimeException("Error invoking method '" + targetMethod.getName() + "'", e);
		}
	}

	/**
	 * Extract the value for a method parameter.
	 * 
	 * @param i
	 *            the index of the method parameter in the parameters list.
	 * @param targetMethod
	 *            the target method.
	 * @param argClass
	 *            the type of the current parameter.
	 * @param pageParameters
	 *            PageParameters for the current request.
	 * @return the extracted value.
	 */
	private Object extractParameterValue(int i, Method targetMethod, Class<?> argClass,
			PageParameters pageParameters) {
		Object paramValue = null;
		Annotation[][] parametersAnnotations = targetMethod.getParameterAnnotations();

		if (ReflectionUtils.isParameterAnnotatedWith(i, targetMethod, RequestBody.class))
			paramValue = extractObjectFromBody(argClass);
		else if (ReflectionUtils.isParameterAnnotatedWith(i, targetMethod, RequestParam.class))
			paramValue = extractParameterFromQuery(pageParameters, parametersAnnotations[i],
					argClass);
		else if (ReflectionUtils.isParameterAnnotatedWith(i, targetMethod, HeaderParam.class))
			paramValue = extractParameterFromHeader(parametersAnnotations[i], argClass);
		else if (ReflectionUtils.isParameterAnnotatedWith(i, targetMethod, CookieParam.class))
			paramValue = extractParameterFromCookies(parametersAnnotations[i], argClass);

		return paramValue;
	}

	/**
	 * Extract method parameter's value from request header.
	 * 
	 * @param parameterAnnotations
	 *            an array containing the annotations for the current method
	 *            parameter.
	 * @param argClass
	 *            the type of the current method parameter.
	 * @return the extracted value.
	 */
	private Object extractParameterFromHeader(Annotation[] parameterAnnotations, Class<?> argClass) {

		HeaderParam headerParam = ReflectionUtils.findAnnotation(parameterAnnotations,
				HeaderParam.class);
		String value = headerParam.value();
		WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();

		return toObject(argClass, webRequest.getHeader(value));
	}

	/**
	 * Extract method parameter's value from query string parameters.
	 * 
	 * @param pageParameters
	 *            the PageParameters of the current request.
	 * @param parameterAnnotations
	 *            an array containing the annotations for the current method
	 *            parameter.
	 * @param argClass
	 *            the type of the current method parameter.
	 * @return the extracted value.
	 */
	private Object extractParameterFromQuery(PageParameters pageParameters,
			Annotation[] parameterAnnotations, Class<?> argClass) {

		RequestParam queryParam = ReflectionUtils.findAnnotation(parameterAnnotations,
				RequestParam.class);
		String value = queryParam.value();

		return toObject(argClass, pageParameters.get(value).toString());
	}

	/**
	 * Extract method parameter's value from cookies.
	 * 
	 * @param parameterAnnotations
	 *            an array containing the annotations for the current method
	 *            parameter.
	 * @param argClass
	 *            the type of the current method parameter.
	 * @return the extracted value.
	 */
	private Object extractParameterFromCookies(Annotation[] parameterAnnotations, Class<?> argClass) {

		CookieParam cookieParam = ReflectionUtils.findAnnotation(parameterAnnotations,
				CookieParam.class);
		String value = cookieParam.value();
		WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();

		return toObject(argClass, webRequest.getCookie(value).getValue());
	}

	/**
	 * Internal method that tries to extract an instance of the given class from
	 * the request body.
	 * 
	 * @param argClass
	 *            the type we want to extract from request body.
	 * @return the extracted object.
	 */
	private Object extractObjectFromBody(Class<?> argClass) {
		WebRequest servletRequest = (WebRequest) RequestCycle.get().getRequest();
		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest.getContainerRequest();
		try {
			BufferedReader bufReader = httpRequest.getReader();
			StringBuilder builder = new StringBuilder();
			String jsonString;

			while ((jsonString = bufReader.readLine()) != null)
				builder.append(jsonString);

			return deserializeObjFromString(argClass, builder.toString(), objSerialDeserial);
		} catch (IOException e) {
			throw new RuntimeException("Error deserializing object from request", e);
		}
	}

	/***
	 * Extract parameters values from the rest URL.
	 * 
	 * @param mappedMethod
	 *            mapping info of the method.
	 * @param pageParameters
	 *            PageParametrs object of the current request.
	 * @param segmentsIterator
	 *            iterator over the mapped segments.
	 * @param argClass
	 *            type of the parameter we want to extract.
	 * @return the parameter value.
	 */
	private Object extractParameterFromUrl(UrlMappingInfo mappedMethod,
			PageParameters pageParameters, Iterator<StringValue> segmentsIterator, Class<?> argClass) {
		try {
			StringValue segmentValue = null;

			while (segmentsIterator.hasNext()) {
				StringValue currentSegment = segmentsIterator.next();

				if (currentSegment instanceof VariableSegment) {
					segmentValue = currentSegment;
					break;
				}
			}

			if (segmentValue != null) {
				int indexOf = mappedMethod.getSegments().indexOf(segmentValue);
				StringValue actualValue = pageParameters.get(indexOf);

				return toObject(argClass, actualValue.toString());
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving a constructor with a string parameter.", e);
		}
	}

	/**
	 * Utility method to convert string values to the corresponding objects
	 * 
	 * @param clazz
	 *            the primitive class we want to convert
	 * @param value
	 *            the string value we want to convert into the wrapper class
	 * @return the wrapper class for the given primitive type
	 */
	protected Object toObject(Class clazz, String value) throws IllegalArgumentException {
		IConverter converter = Application.get().getConverterLocator().getConverter(clazz);

		if (converter != null)
			return converter.convertToObject(value, Session.get().getLocale());

		throw new IllegalArgumentException("Could not find a suitable constructor for value "
				+ value + " of class " + clazz);

	}

	/**
	 * Utility method to check that the user owns one of the roles provided in
	 * input.
	 * 
	 * @param roles
	 *            checked roles.
	 * @return true if the user owns one of roles in input, false otherwise.
	 */
	protected final boolean hasAny(Roles roles) {
		if (roles.isEmpty()) {
			return true;
		} else {
			return roleCheckingStrategy.hasAnyRole(roles);
		}
	}
}

/**
 * {@link StringValue} subtype that contains a mounted segment containing a
 * parameter's value (for example '/{id}/').
 * 
 * @author andrea del bene
 * 
 */
class VariableSegment extends StringValue {
	
	final private String name;
	
	protected VariableSegment(String text) {
		super(text);
		this.name = loadSegmentVarName();
	}
	 
	protected String loadSegmentVarName() {
		return null;
	}

	static public VariableSegment createVariableSegment(String text){
		return new VariableSegment(text);
	} 
}

class MatrixVarSegment extends VariableSegment{
	private MatrixVarSegment(String text) {
		super(text);
	}
}