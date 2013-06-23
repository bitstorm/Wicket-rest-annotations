package com.mycompany.resource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.StringValueConversionException;

import com.mycompany.annotations.HttpMethod;
import com.mycompany.annotations.MethodMapping;

public class AbstractRestResource implements IResource {
	private Map<String, UrlMappingInfo> mappedMethods = new HashMap<String, UrlMappingInfo>();
	
	public AbstractRestResource() {
		loadAnnotatedMethods();
	}

	@Override
	public void respond(Attributes attributes) {
		PageParameters pageParameters = attributes.getParameters();
		ServletWebResponse response = (ServletWebResponse) attributes.getResponse();
		HttpMethod httpMethod = getHttpMethod((ServletWebRequest) RequestCycle.get().getRequest());
		int indexedParamCount = pageParameters.getIndexedCount();
		
		UrlMappingInfo mappedMethod = mappedMethods.get(indexedParamCount + "_" +
										httpMethod.getMethod());
		
		if(mappedMethod != null){
			Object result = invokeMappedMethod(mappedMethod, pageParameters);
			
			if(result != null){
				JSONObject jsonObject = new JSONObject(result);
				response.setContentType("application/json");
				response.write(jsonObject.toString());
			}
		}
	}
	
	private void loadAnnotatedMethods() {
		Method[] methods = getClass().getDeclaredMethods();		
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			MethodMapping methodMapped = method.getAnnotation(MethodMapping.class);
			
			if(methodMapped != null){
				String urlPath = methodMapped.value();
				HttpMethod httpMethod = methodMapped.httpMethod();
				UrlMappingInfo urlMappingInfo = new UrlMappingInfo(urlPath, httpMethod, method);
				
				mappedMethods.put(urlMappingInfo.getSegmentsCount() + "_" + 
									httpMethod.getMethod(), urlMappingInfo);
			}
		}
	}
	
	public static HttpMethod getHttpMethod(ServletWebRequest request){
		HttpServletRequest httpRequest = request.getContainerRequest();
		return HttpMethod.toHttpMethod((httpRequest.getMethod()));
	}
	
	private Object invokeMappedMethod(UrlMappingInfo mappedMethod,
			PageParameters pageParameters) {
		
		Method targetMethod = mappedMethod.getMethod();
		Class<?>[] argsClasses = targetMethod.getParameterTypes();
		List parametersValues = new ArrayList();
		
		Iterator<StringValue> segmentsIterator = mappedMethod.getSegments().iterator();
		
		for (int i = 0; i < argsClasses.length; i++) {
			Class<?> argClass = argsClasses[i];
			try {
				StringValue segmentValue = null;
				
				while(segmentsIterator.hasNext()){
					segmentValue = segmentsIterator.next();
					if(segmentValue instanceof VariableSegment)
						break;
				}
				
				if(segmentValue != null){
					int indexOf = mappedMethod.getSegments().indexOf(segmentValue);
					StringValue actualValue = pageParameters.get(indexOf);
					
					parametersValues.add(toObject(argClass, actualValue.toString()));
				}
				
			} catch (Exception e) {
				throw new RuntimeException("Error retrieving a constructor with a string parameter.", e);
			} 
		}
		
		try {
			return targetMethod.invoke(this, parametersValues.toArray());
		} catch (Exception e) {
			throw new RuntimeException("Error invoking method '" + targetMethod.getName() + "'", e);
		} 
	}
	
	public static Object toObject( Class clazz, String value ) {
	    if( boolean.class == clazz ) return Boolean.parseBoolean( value );
	    if( byte.class == clazz ) return Byte.parseByte( value );
	    if( short.class == clazz ) return Short.parseShort( value );
	    if( int.class == clazz ) return Integer.parseInt( value );
	    if( long.class == clazz ) return Long.parseLong( value );
	    if( float.class == clazz ) return Float.parseFloat( value );
	    if( double.class == clazz ) return Double.parseDouble( value );
	    return value;
	}
}

class UrlMappingInfo{
	private HttpMethod httpMethod;
	private List<StringValue> segments = new ArrayList<StringValue>();
	private Method method;
	
	public UrlMappingInfo(String urlPath, HttpMethod httpMethod, Method method){
		this.httpMethod = httpMethod;
		this.method = method;
		
		String[] segArray = urlPath.split("/");
		
		for (int i = 0; i < segArray.length; i++) {
			String segment = segArray[i];
			StringValue segmentValue;
			
			if(segment.isEmpty())continue;
			
			if(isVariableSegment(segment))
				segmentValue = new VariableSegment(segment);
			else
				segmentValue = StringValue.valueOf(segment);
			
			segments.add(segmentValue);
		}
	}

	public List<StringValue> getSegments() {
		return segments;
	}

	private boolean isVariableSegment(String segment) {
		return segment.length() >= 4 && segment.startsWith("{") 
				&& segment.endsWith("}");
	}
	
	public int getSegmentsCount(){
		return segments.size();
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public Method getMethod() {
		return method;
	}
}

class VariableSegment extends StringValue{
	protected VariableSegment(String text) {
		super(text);
	}
}