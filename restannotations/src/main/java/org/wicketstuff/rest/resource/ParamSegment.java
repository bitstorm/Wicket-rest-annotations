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

import java.util.List;

import org.apache.wicket.util.string.StringValue;

/**
 * {@link StringValue} subtype that contains a mounted segment containing a
 * parameter's value (for example '/{id}/').
 * 
 * @author andrea del bene
 * 
 */
public class ParamSegment extends GeneralURLSegment {
	private Class<?> paramClass;
	
	ParamSegment(String text, MethodMappingInfo mappingInfo) {
		super(text);
		
		loadParamClass(mappingInfo);
	}

	private void loadParamClass(MethodMappingInfo mappingInfo) {
		Class<?>[] notAnnotatedParams = mappingInfo.getNotAnnotatedParams();
		List<GeneralURLSegment> urlSegments = mappingInfo.getSegments();
		
		int paramSegmentsCount = 0;
		
		for (GeneralURLSegment generalURLSegment : urlSegments) {
			if(generalURLSegment instanceof ParamSegment)
				paramSegmentsCount++;
		}
		
		this.paramClass = notAnnotatedParams[paramSegmentsCount];
	}
	
	@Override
	protected int calculateScore(String actualSegment) {
		if(isSegmentCompatible(actualSegment, paramClass))
			return 2;
		
		return 0;
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
	private boolean isSegmentCompatible(String segment, Class<?> paramClass) {
		try {
			Object convertedObject = AbstractRestResource.toObject(paramClass, segment.toString());
		} catch (Exception e) {
			// segment's value not compatible with paramClass
			return false;
		}

		return true;
	}
	
	@Override
	protected String loadSegmentVarName() {
		String segValue = toString();
		return segValue.substring(1, segValue.length() - 2);
	}
}