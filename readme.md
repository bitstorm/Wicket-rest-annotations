#Wicket rest annotaions
---------

REST-based API are becoming more and more popular around the web and the number of services based on this architecture is constantly increasing.

Wicket is well-known for its capability of transparently handling the state of web applications on server side, but it can be also easily adopted to create RESTful services.

This project provides a special resource class and a set of annotations to implement REST API/services in much the same way as we do it with Spring MVC or with the standard JAX-RS.

#How to use this module
---------

Quick introduction for impatients.
---------

The project provides a generic abstract class to implement a Wicket resource that handles the request and the response using a particular data format (XML, JSON, etc...). As JSON is de-facto standard data format for REST API, the project comes also with a ready-to-use resource that produces and consumes JSON data.   


````java
angular.module('sample-app', ['restangular'])
  .config(function(RestangularProvider) {
    RestangularProvider.setBaseUrl("/api/v1");
  });
  
angular.module('sample-app').controller('MainCtrl', function($scope, Restangular) {
  $scope.projects = Restangular.all('projects').getList();
});
````


```to be continued...
