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
package org.wicketstuff.rest.resource.gson;

import org.wicketstuff.rest.contenthandling.RestMimeTypes;
import org.wicketstuff.rest.contenthandling.serialdeserial.TextualObjectSerialDeserial;

import com.google.gson.Gson;

/**
 * Textual object serializer/deserializer that works with JSON format and uses
 * <a href="http://code.google.com/p/google-gson/">Gson library</a>.
 * 
 * @author andrea del bene
 * 
 */
public class GsonSerialDeserial extends TextualObjectSerialDeserial {
	private final Gson gson;

	public GsonSerialDeserial() {
		this(new Gson());
	}
	
	public GsonSerialDeserial(Gson gson) {
		this.gson = gson;
	}

	@Override
	public String objectToString(Object targetObject, String mimeType) {
		return gson.toJson(targetObject);
	}

	@Override
	public <T> T stringToObject(String source, Class<T> targetClass, String mimeType) {
		return gson.fromJson(source, targetClass);
	}

	@Override
	public boolean isMimeTypeSupported(String mimeType) {
		if(mimeType != null && RestMimeTypes.JSON_UTF8.startsWith(mimeType))
			return true;
		
		return false;
	}
}