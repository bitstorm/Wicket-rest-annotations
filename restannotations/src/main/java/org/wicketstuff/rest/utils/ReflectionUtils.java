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
package org.wicketstuff.rest.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.wicketstuff.rest.annotations.parameters.JsonBody;
import org.wicketstuff.rest.annotations.parameters.QueryParam;

public class ReflectionUtils {
    /**
     * Check if a parameter is annotated with {@link JsonBody}
     * 
     * @param i
     *            function parameter index
     * @param parametersAnnotations
     *            bidimensional array containing the annotations for function
     *            parameters
     * @return true if the function parameter is annotated with JsonBody, false
     *         otherwise
     * @see JsonBody
     */
    static public boolean isParameterAnnotatedWith(int i, Method method,
	    Class<? extends Annotation> targetAnnotation) {
	Annotation[][] parametersAnnotations = method.getParameterAnnotations();

	if (parametersAnnotations.length == 0)
	    return false;

	Annotation[] parameterAnnotations = parametersAnnotations[i];

	for (int j = 0; j < parameterAnnotations.length; j++) {
	    Annotation annotation = parameterAnnotations[j];
	    if (targetAnnotation.isInstance(annotation))
		return true;
	}
	return false;
    }

    static public boolean isParameterNotAnnotated(int i, Method method) {
	Annotation[][] parametersAnnotations = method.getParameterAnnotations();

	if (parametersAnnotations.length == 0)
	    return true;

	Annotation[] parameterAnnotations = parametersAnnotations[i];

	if (parameterAnnotations.length == 0)
	    return true;

	return false;
    }
    
    static public <T extends Annotation> T findAnnotation(Annotation[] parameterAnnotations, 
	    					Class<T> targetAnnotation){

	for (int i = 0; i < parameterAnnotations.length; i++) {
	    Annotation annotation = parameterAnnotations[i];

	    if (targetAnnotation.isInstance(annotation))
		return targetAnnotation.cast(annotation);
	}
	
	return null;
    }
}
