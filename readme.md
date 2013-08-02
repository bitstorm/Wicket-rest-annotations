#Wicket rest annotaions

REST-based API are becoming more and more popular around the web and the number of services based on this architecture is constantly increasing.

Wicket is well-known for its capability of transparently handling the state of web applications on server side, but it can be also easily adopted to create RESTful services.

This project provides a special resource class and a set of annotations to implement REST API/services in much the same way as we do it with Spring MVC or with the standard JAX-RS.

Quick overview
---------

The project provides class `AbstractRestResource` as generic abstract class to implement a Wicket resource that handles the request and the response using a particular data format (XML, JSON, etc...). 
Subclassing `AbstractRestResource` we can create custom resources and map their pubblic methods to a given subpath with annotation `@MethodMapping`. The following snippet is taken from resource `PersonsRestResource` inside example module:

````java
	@MethodMapping("/persons")
	public List<PersonPojo> getAllPersons() {
		//method mapped at subpath "/persons" and HTTP method GET
	}
	
	@MethodMapping(value = "/persons/{personIndex}", httpMethod = HttpMethod.DELETE)
	public void deletePerson(int personIndex) {
		//method mapped at subpath "/persons/{personIndex}" and HTTP method DELETE. 
		//Segment {personIndex} will contain an integer value as index.
	}

	@MethodMapping(value = "/persons", httpMethod = HttpMethod.POST)
	public void createPerson(@RequestBody PersonPojo personPojo) {
		//creates a new instance of PersonPojo reading it from request body
	}
````

`@MethodMapping` requires to specify the subpath we want to map the method to. In addition we can specify also the HTTP method that must be used to invoke the method via REST (GET, POST, DELETE, etc...). This value can be specified with enum class `HttpMethod` and is GET by default. For more details on `@MethodMapping` see the section below.
To promote the principle of *convetion over configuration*, we don't need to use any annotation to map method parameters to path parameters if they are declared in the same order. If we don't want to use this default behavior we can use annotation `PathParam`. See the section below to know how to use it. If the mapped method returns a value, this last is automatically serialized to the supported data format and written to response object. 
Annotation `@RequestBody` is used to extract the value of a method parameter from the request body.

**Note:** to convert strings to Java type, `AbstractRestResource` uses the standard Wicket mechanism based on the application converter locator:
````java
	IConverter converter = Application.get().getConverterLocator().getConverter(clazz);

	return converter.convertToObject(value, Session.get().getLocale()); 
````

To serialize/deserialize objects to response/from request, `AbstractRestResource` uses an implementation of interface `IObjectSerialDeserial` which defines the following methods: 

````java
public interface IObjectSerialDeserial {
	
	public void objectToResponse(Object targetObject, WebResponse response, String mimeType) throws Exception;

	public <T> T requestToObject(WebRequest request, Class<T> argClass, String mimeType) throws Exception;
}
````

The interfaces defines just the two perations needed to write an object to the response body and to read an object from request body.
As JSON is de-facto standard format for REST API, the project comes also with a ready-to-use resource (`GsonRestResource`) and a serial/deserial (`GsonSerialDeserial`) that work with JSON format (both inside json project module). These classes use [Gson library](http://code.google.com/p/google-gson/) as Json library.


Advanced mapping and annotations
---------

Authorization
---------

Hook methods
---------


**to be continued...**
