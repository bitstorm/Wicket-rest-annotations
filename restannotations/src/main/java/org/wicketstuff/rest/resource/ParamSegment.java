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
import java.util.regex.Matcher;

import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.util.string.StringValue;

/**
 * {@link StringValue} subtype that contains a mounted segment containing a
 * parameter's value (for example '/{id}/').
 * 
 * @author andrea del bene
 * 
 */
public class ParamSegment extends GeneralURLSegment {
	
	final private String paramName;
	
	final private MetaPattern metaPattern;
	
	ParamSegment(String text) {
		super(text);
		
		String segmentContent = trimFirstAndLastCharacter(this.toString());
		
		this.paramName = loadParamName(segmentContent);
		this.metaPattern = loadRegExp(segmentContent);
	}
	
	@Override
	protected int calculateScore(String actualSegment) {
		Matcher matcher = metaPattern.matcher(actualSegment);
		
		return matcher.matches() ? 1 : 0;
	}

	private String loadParamName(String segmentContent) {
		Matcher matcher = MetaPattern.VARIABLE_NAME.matcher(segmentContent);
		
		matcher.find();
		return matcher.group();
	}
	
	private MetaPattern loadRegExp(String segmentContent) {
		Matcher matcher = REGEXP_DECLARATION.matcher(segmentContent);
		String regExp;
		
		if(matcher.find()){
			String group = matcher.group();
			regExp = group.substring(1, group.length());
		}else{
			regExp = MetaPattern.ANYTHING_NON_EMPTY.toString();
		}
		
		return new MetaPattern(regExp);
	}
	
	public static String trimFirstAndLastCharacter(String segValue) {
		return segValue.substring(1, segValue.length() - 1);
	}

	public String getParamName() {
		return paramName;
	}

	public MetaPattern getMetaPattern() {
		return metaPattern;
	}
}