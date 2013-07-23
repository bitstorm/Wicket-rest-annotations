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

import static org.apache.wicket.util.parse.metapattern.MetaPattern.ANYTHING_NON_EMPTY;
import static org.apache.wicket.util.parse.metapattern.MetaPattern.COLON;
import static org.apache.wicket.util.parse.metapattern.MetaPattern.LEFT_CURLY;
import static org.apache.wicket.util.parse.metapattern.MetaPattern.RIGHT_CURLY;
import static org.apache.wicket.util.parse.metapattern.MetaPattern.VARIABLE_NAME;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.util.parse.metapattern.OptionalMetaPattern;
import org.apache.wicket.util.parse.metapattern.parsers.VariableAssignmentParser;
import org.apache.wicket.util.string.StringValue;

public class GeneralURLSegment extends StringValue {
	public static final MetaPattern REGEXP_BODY = new MetaPattern("([^\\}\\{]*|(\\{[\\d]+\\}))*");
	public static final MetaPattern REGEXP_DECLARATION = new MetaPattern(COLON, REGEXP_BODY);
	public static final MetaPattern SEGMENT_PARAMETER = new MetaPattern(LEFT_CURLY, VARIABLE_NAME,
			new OptionalMetaPattern(REGEXP_DECLARATION), RIGHT_CURLY);
	
	final private MetaPattern metaPattern;

	GeneralURLSegment(String text) {
		super(text);
		
		metaPattern = new MetaPattern(Pattern.quote(text));
	}

	static public GeneralURLSegment newSegment(String segment) {
		if (SEGMENT_PARAMETER.matcher(segment).matches())
			return new ParamSegment(segment);
		
		if (SEGMENT_PARAMETER.matcher(segment).find())
			return new MultiParamSegment(segment);
				
		return new GeneralURLSegment(segment);
	}

	static public boolean isValidSegment(String segment){
		String decodedSegment = UrlEncoder.PATH_INSTANCE.encode(segment, "UTF-8");
		
		return segment.equals(decodedSegment);
	}
	
	protected int calculateScore(String segment) {
		if (segment.equals(this.toString()))
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

	public MetaPattern getMetaPattern() {
		return metaPattern;
	}
}