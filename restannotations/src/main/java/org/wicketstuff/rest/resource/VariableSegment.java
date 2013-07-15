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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.util.parse.metapattern.Group;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.util.parse.metapattern.OptionalMetaPattern;
import org.apache.wicket.util.string.StringValue;

/**
 * {@link StringValue} subtype that contains a mounted segment containing a
 * parameter's value (for example '/{id}/').
 * 
 * @author andrea del bene
 * 
 */
public class VariableSegment extends GeneralURLSegment {

	public static final MetaPattern VAR_SEGMENT_PATTERN = initVarSegmentPattern();

	protected VariableSegment(String text) {
		super(text);
	}

	protected static MetaPattern initVarSegmentPattern() {
		List<MetaPattern> patterns = new ArrayList<MetaPattern>();
		MetaPattern segmentName = new MetaPattern(STANDARD_URL_SEGMENT);
		MetaPattern parameter = new MetaPattern(MetaPattern.VARIABLE_NAME, MetaPattern.EQUALS,
				MetaPattern.STRING);
				
		MetaPattern matrixParameter = new MetaPattern(MetaPattern.SEMICOLON, parameter);
		Group matrixParamGroup = new Group(matrixParameter);
		MetaPattern multiGroup = new MetaPattern(matrixParamGroup.toString() + "*");
		
		return new MetaPattern(segmentName, multiGroup);
	}

	@Override
	protected String loadSegmentVarName() {
		return null;
	}

	static public VariableSegment createVariableSegment(String text) {
		return new VariableSegment(text);
	}
}