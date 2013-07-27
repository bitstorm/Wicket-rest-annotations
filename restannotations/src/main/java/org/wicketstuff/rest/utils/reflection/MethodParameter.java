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
package org.wicketstuff.rest.utils.reflection;

import java.lang.reflect.Method;

// TODO: Auto-generated Javadoc
/**
 * The class contains the informations of a method parameter, like its type or
 * its index in the array of method's parameters.
 * 
 * @author andrea del bene
 */
public class MethodParameter {

	/** The parameter class. */
	final private Class<?> parameterClass;

	/** The owner method. */
	final private Method ownerMethod;

	/** The param index. */
	final private int paramIndex;

	/**
	 * Instantiates a new method parameter.
	 * 
	 * @param type
	 *            the type of the parameter.
	 * @param ownerMethod
	 *            the owner method for the parameter.
	 * @param paramIndex
	 *            the index of the parameter in the array of method's
	 *            parameters.
	 */
	public MethodParameter(Class<?> type, Method ownerMethod, int paramIndex) {
		this.parameterClass = type;
		this.ownerMethod = ownerMethod;
		this.paramIndex = paramIndex;
	}

	/**
	 * Gets the type of the method parameter.
	 * 
	 * @return the parameter class
	 */
	public Class<?> getParameterClass() {
		return parameterClass;
	}

	/**
	 * Gets the owner method.
	 * 
	 * @return the owner method
	 */
	public Method getOwnerMethod() {
		return ownerMethod;
	}

	/**
	 * Gets the index of the parameter in the array of method's parameters.
	 * 
	 * @return the parameter index
	 */
	public int getParamIndex() {
		return paramIndex;
	}

}
