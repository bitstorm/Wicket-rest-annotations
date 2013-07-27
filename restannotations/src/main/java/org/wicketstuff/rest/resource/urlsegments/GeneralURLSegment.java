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
package org.wicketstuff.rest.resource.urlsegments;

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

/**
 * Base class to contain the informations of the segments that compose the URL
 * used to map a method. It's used to use simple segments with no path
 * parameters.
 * 
 * @author andrea del bene
 * 
 */
public class GeneralURLSegment extends StringValue {
	/** MetaPattern to identify the content of a regular expression. */
	public static final MetaPattern REGEXP_BODY = new MetaPattern("([^\\}\\{]*|(\\{[\\d]+\\}))*");
	/** MetaPattern to identify the declaration of a regular expression. */
	public static final MetaPattern REGEXP_DECLARATION = new MetaPattern(COLON, REGEXP_BODY);
	/**
	 * MetaPattern to identify a path parameter inside a segment (i.e.
	 * "{paramName:regexp}")
	 */
	public static final MetaPattern SEGMENT_PARAMETER = new MetaPattern(LEFT_CURLY, VARIABLE_NAME,
			new OptionalMetaPattern(REGEXP_DECLARATION), RIGHT_CURLY);

	/** The MetaPattern corresponding to the current segment. */
	final private MetaPattern metaPattern;

	GeneralURLSegment(String text) {
		super(text);

		metaPattern = new MetaPattern(Pattern.quote(text));
	}

	/**
	 * Factory method to create new instances of GeneralURLSegment.
	 * 
	 * @param The
	 *            content of the new segment.
	 * @return the new instance of GeneralURLSegment.
	 */
	static public GeneralURLSegment newSegment(String segment) {
		if (SEGMENT_PARAMETER.matcher(segment).matches())
			return new ParamSegment(segment);

		if (SEGMENT_PARAMETER.matcher(segment).find())
			return new MultiParamSegment(segment);

		return new GeneralURLSegment(segment);
	}

	/**
	 * 
	 * @param segment
	 * @return
	 */
	static public boolean isValidSegment(String segment) {
		String decodedSegment = UrlEncoder.PATH_INSTANCE.encode(segment, "UTF-8");

		return segment.equals(decodedSegment);
	}

	/**
	 * This method checks if a given string is compatible with the current
	 * segment.
	 * 
	 * @param segment
	 * @return an integer positive value if the string in input is compatible
	 *         with the current segment, 0 otherwise. Segments of type
	 *         GeneralURLSegment have the priority over the other subtypes of
	 *         segment. That's why positive matches has a score of 2 if the
	 *         method is invoked on a GeneralURLSegment, while it returns 1 for
	 *         the other types of segment.
	 */
	public int calculateScore(String segment) {
		if (segment.equals(this.toString()))
			return 2;

		return 0;
	}

	/**
	 * Get the segment value without optional matrix parameters. For example
	 * given the following value 'segment;parm=value', the function returns
	 * 'segment'.
	 * 
	 * @param fullSegment
	 * @return the value of the segment without matrix parameters.
	 */
	static public String getActualSegment(String fullSegment) {
		String[] segmentParts = fullSegment.split(MetaPattern.SEMICOLON.toString());
		return segmentParts[0];
	}

	/**
	 * Extract matrix parameters from the segment in input.
	 * 
	 * @param fullSegment
	 * 			the segment in input.
	 * @return a map containing matrix parameters.
	 */
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
	 * 
	 */
	public void populatePathVariables(Map<String, String> variables, String segment) {
		// I don'have path variables, I do nothing
	}

	/**
	 * Getter method for segment MetaPattern.
	 **/
	public MetaPattern getMetaPattern() {
		return metaPattern;
	}
}