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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.util.parse.metapattern.Group;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.util.parse.metapattern.parsers.VariableAssignmentParser;
import org.apache.wicket.util.string.StringValue;

public class GeneralURLSegment extends StringValue {

	final private String segmentName;

	public static final String STANDARD_URL_SEGMENT = "([A-Za-z0-9_]+|\\*)";

	public static final MetaPattern VAR_SEGMENT_PATTERN = initVarSegmentPattern();

	GeneralURLSegment(String text) {
		super(text);
		this.segmentName = loadSegmentVarName();
	}

	protected String loadSegmentVarName() {
		return this.toString();
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

	static public GeneralURLSegment createSegment(String segment, MethodMappingInfo mappingInfo) {
		if(isParameterSegment(segment))
			return new ParamSegment(segment, mappingInfo);
		
		if(segment.equals("*"))
			return new StarSegment(segment);
			
		return new GeneralURLSegment(segment);
	}
	
	protected int calculateScore(String actualSegment){
		if(actualSegment.equals(getSegmentName()))
			return 3;
		
		return 0;
	}
	
	/**
	 * Get the segment value without optional matrix parameters. For example given the 
	 * following value as segment 'segment;parm=value', the function returns 'segment'.
	 * 
	 * @param fullSegment
	 * @return
	 */
	static public String getActualSegment(String fullSegment) {
		String[] segmentParts = fullSegment.split(MetaPattern.SEMICOLON.toString());
		return segmentParts[0];
	}

	static public Map<String, String> getSegmentMatrixParameters(String fullSegment) {
		String[] segmentParts = fullSegment.split(MetaPattern.SEMICOLON.toString());
		HashMap<String, String> matrixParameters = new HashMap<String, String>();

		if(segmentParts.length < 2)
			return matrixParameters;
		
		for (int i = 1; i < segmentParts.length; i++) {
			String parameterDeclar = segmentParts[i];
			VariableAssignmentParser parser = new VariableAssignmentParser(
					parameterDeclar);
			
			parser.matcher().find();
			matrixParameters.put(parser.getKey(), parser.getValue());
		}

		return matrixParameters;
	}
	
	/**
	 * Utility method to check if a segment contains a parameter (i.e.
	 * '/{parameterName}/').
	 * 
	 * @param segment
	 * @return true if the segment contains a parameter, false otherwise.
	 */
	public static boolean isParameterSegment(String segment) {
		return segment.length() >= 4 && segment.startsWith("{") && segment.endsWith("}");
	}

	public String getSegmentName() {
		return segmentName;
	}
}