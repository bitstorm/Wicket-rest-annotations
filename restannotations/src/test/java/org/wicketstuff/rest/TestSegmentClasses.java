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
package org.wicketstuff.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.junit.Assert;
import org.junit.Test;
import org.wicketstuff.rest.resource.GeneralURLSegment;
import org.wicketstuff.rest.resource.MultiParamSegment;
import org.wicketstuff.rest.resource.ParamSegment;

public class TestSegmentClasses extends Assert {

	@Test
	public void testStandardUrlSegmentPattern() {
		MetaPattern pattern = new MetaPattern(GeneralURLSegment.SEGMENT_PARAMETER);

		Matcher matcher = pattern.matcher("");
		assertFalse(matcher.matches());

		matcher = pattern.matcher("seg&ment");
		assertFalse(matcher.matches());

		matcher = pattern.matcher("segment:");
		assertFalse(matcher.matches());

		matcher = pattern.matcher("{*}");
		assertFalse(matcher.matches());

		matcher = pattern.matcher("{segment}");
		assertTrue(matcher.matches());

		matcher = pattern.matcher("{segment0} a segment {segment1} another segment {segment2}");
		assertTrue(matcher.find());
		
		matcher.reset();
		assertFalse(matcher.matches());
		
		matcher = pattern.matcher("{117}");
		assertFalse(matcher.matches());
		
		pattern = new MetaPattern(GeneralURLSegment.REGEXP_BODY);
		matcher = pattern.matcher("[0-9]*:abba");
		assertTrue(matcher.matches());
		
		matcher = pattern.matcher("^\\(?\\d{3}\\)?[ -]?\\d{3}[ -]?\\d{4}$anotherseg");
		assertTrue(matcher.matches());
	}

	@Test
	public void testMatrixParameters() {
		String segment = "segment";
		String segmentMatrixParam = segment + ";param=value";

		String segmentValue = GeneralURLSegment.getActualSegment(segment);
		assertEquals(segment, segmentValue);

		Map<String, String> matrixParams = GeneralURLSegment.getSegmentMatrixParameters(segment);
		assertTrue(matrixParams.size() == 0);

		segmentValue = GeneralURLSegment.getActualSegment(segmentMatrixParam);
		assertEquals(segment, segmentValue);

		matrixParams = GeneralURLSegment.getSegmentMatrixParameters(segmentMatrixParam);

		assertEquals(1, matrixParams.size());

		assertNotNull(matrixParams.get("param"));

		String segmentMatrixParamsQuotes = segment + ";param=value;param1='hello world'";
		matrixParams = GeneralURLSegment.getSegmentMatrixParameters(segmentMatrixParamsQuotes);

		assertEquals(2, matrixParams.size());
		assertEquals("value", matrixParams.get("param"));
		assertEquals("'hello world'", matrixParams.get("param1"));
	}
	
	@Test
	public void testSegmentCharactersValid() {
		assertFalse(GeneralURLSegment.isValidSegment("/"));
		assertFalse(GeneralURLSegment.isValidSegment("{sa}"));
		assertFalse(GeneralURLSegment.isValidSegment("segm()"));
		
		assertTrue(GeneralURLSegment.isValidSegment("segment177"));
	}

	@Test
	public void testParamSegment() throws Exception {
		String segmentWithRegEx = "{id:[0-9]*:abba}";
		GeneralURLSegment segment = GeneralURLSegment.newSegment(segmentWithRegEx);
		
		assertTrue(segment instanceof ParamSegment);
		
		ParamSegment paramSegment = (ParamSegment) segment;
		
		assertEquals(paramSegment.getParamName(), "id");
		assertEquals(paramSegment.getMetaPattern().toString(), "[0-9]*:abba");
		
		MetaPattern metaPattern = paramSegment.getMetaPattern();
		
		assertTrue(metaPattern.matcher("1:abba").matches());
		assertTrue(metaPattern.matcher("1234521:abba").matches());
		assertTrue(metaPattern.matcher(":abba").matches());
		
		String segmentMultiParam = "{segment0}asegment{segment1:^\\(?\\d{3}\\)?[ -]?\\d{3}[ -]?\\d{4}$}anotherseg";
		segment = GeneralURLSegment.newSegment(segmentMultiParam);
		
		assertTrue(segment instanceof MultiParamSegment);

		MultiParamSegment multiParamSegment = (MultiParamSegment) segment;		
		List<GeneralURLSegment> subSegments = multiParamSegment.getSubSegments();
		
		assertEquals(4, subSegments.size());
		metaPattern = subSegments.get(2).getMetaPattern();
		assertEquals(metaPattern.toString(), "^\\(?\\d{3}\\)?[ -]?\\d{3}[ -]?\\d{4}$");
		
		System.out.println(segment.getMetaPattern().toString());
		
		segmentMultiParam = "filename-{symbolicName:[a-z]+}-{version:\\d\\.\\d\\.\\d}{extension:\\.[a-z]+}";
		segment = GeneralURLSegment.newSegment(segmentMultiParam);
		metaPattern = segment.getMetaPattern();
		
		String fileName = "filename-gsaon-1.2.3.zip";
		Matcher matcher = metaPattern.matcher(fileName);
		
		assertTrue(matcher.matches());
		
		matcher = metaPattern.matcher("gsaon-1.2.3.zip");
		
		assertFalse(matcher.matches());
		
		HashMap<String, String> map;
		
		segment.populatePathVariables(map = new HashMap<String, String>(), fileName);
		
		assertEquals("gsaon", map.get("symbolicName"));
		assertEquals("1.2.3", map.get("version"));
		assertEquals(".zip", map.get("extension"));
	}
}
