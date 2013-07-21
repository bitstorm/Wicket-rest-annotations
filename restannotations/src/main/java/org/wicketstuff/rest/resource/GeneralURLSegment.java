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

import static org.apache.wicket.util.parse.metapattern.MetaPattern.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.util.encoding.UrlDecoder;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.parse.metapattern.Group;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.util.parse.metapattern.parsers.VariableAssignmentParser;
import org.apache.wicket.util.string.StringValue;

public class GeneralURLSegment extends StringValue {

	final private String segmentName;

	public static final MetaPattern SEGMENT_PARAMETER = new MetaPattern(LEFT_CURLY, VARIABLE_NAME,
			RIGHT_CURLY);
	public static final MetaPattern STAR_SEGMENT = new MetaPattern(LEFT_CURLY, STAR,
			RIGHT_CURLY);

	GeneralURLSegment(String text) {
		super(text);
		this.segmentName = loadSegmentVarName();
	}

	protected String loadSegmentVarName() {
		return this.toString();
	}

	static public GeneralURLSegment createSegment(String segment, MethodMappingInfo mappingInfo) {
		if (SEGMENT_PARAMETER.matcher(segment).matches())
			return new ParamSegment(segment, mappingInfo);

		if (STAR_SEGMENT.matcher(segment).matches())
			return new StarSegment(segment);
		
		if (SEGMENT_PARAMETER.matcher(segment).find())
			return new MultivariableSegment(segment);
		
		
		return new GeneralURLSegment(segment);
	}

	static public boolean areSegmentCharactersValid(String segment){
		String decodedSegment = UrlEncoder.PATH_INSTANCE.encode(segment, "UTF-8");
		
		return segment.equals(decodedSegment);
	}
	
	protected int calculateScore(String actualSegment) {
		if (actualSegment.equals(getSegmentName()))
			return 3;

		return 0;
	}

	/**
	 * Get the segment value without optional matrix parameters. For example
	 * given the following value as segment 'segment;parm=value', the function
	 * returns 'segment'.
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

		if (segmentParts.length < 2)
			return matrixParameters;

		for (int i = 1; i < segmentParts.length; i++) {
			String parameterDeclar = segmentParts[i];
			VariableAssignmentParser parser = new VariableAssignmentParser(parameterDeclar);

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