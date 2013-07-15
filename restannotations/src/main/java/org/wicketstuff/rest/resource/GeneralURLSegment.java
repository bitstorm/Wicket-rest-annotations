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

import org.apache.wicket.util.string.StringValue;

public class GeneralURLSegment extends StringValue {

	final private String name;

	public static final String STANDARD_URL_SEGMENT = "[A-Za-z0-9_]+";

	protected GeneralURLSegment(String text) {
		super(text);
		this.name = loadSegmentVarName();
	}

	protected String loadSegmentVarName() {
		return this.toString();
	}

	static public VariableSegment createVariableSegment(String text) {
		return new VariableSegment(text);
	}

	public String getName() {
		return name;
	}
}