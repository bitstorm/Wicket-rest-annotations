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
package org.wicketstuff.rest.contenthandling;

/**
 * General interface to implement object serializers/deserializers.
 * 
 * @author andrea del bene
 * 
 */
public interface IObjectSerialDeserial {
	/**
	 * Converts the object in input to the corresponding string value.
	 * 
	 * @param targetObject
	 *            the object instance to serialize to string.
	 * @param format
	 *            the text format to use.
	 * @return the textual representation of the object in input.
	 */
	public String objectToString(Object targetObject, RestMimeTypes format);

	/**
	 * Extract an instance of targetClass form the string in input.
	 * 
	 * @param source
	 * 			the source string to convert to object. 
	 * @param targetClass
	 * 			the type of the object we want to extract.
	 * @param format 
	 * 			the text format to use.
	 * @return the object extracted from string value.
	 */
	public <T> T stringToObject(String source, Class<T> targetClass, RestMimeTypes format);
}
