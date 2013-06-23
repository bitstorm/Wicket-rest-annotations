package com.mycompany.annotations;

public enum HttpMethod {
	GET("GET"), 
	POST("POST"), 
	HEAD("HEAD"), 
	OPTIONS("OPTIONS"), 
	PUT("PUT"), 
	PATCH("PATCH"), 
	DELETE("DELETE"), 
	TRACE("TRACE");
	
	private String method;

	private HttpMethod(String method) {
		this.method = method;
	}
	
	public static HttpMethod toHttpMethod(String httpMethod){
		HttpMethod[] values = HttpMethod.values();
		httpMethod = httpMethod.toUpperCase();
		
		for (int i = 0; i < values.length; i++) {
			if(values[i].method.equals(httpMethod))
				return values[i];
		}
		
		throw new RuntimeException("The string value '" + httpMethod + 
				"' does not correspond to any valid HTTP request method");
	}

	public String getMethod() {
		return method;
	}
}
