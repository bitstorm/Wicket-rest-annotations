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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.wicket.request.Url;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.junit.Assert;
import org.junit.Test;
import org.wicketstuff.rest.resource.GeneralURLSegment;
import org.wicketstuff.rest.resource.MultivariableSegment;
import org.wicketstuff.rest.resource.ParamSegment;

public class TestSegmentClasses extends Assert {

	@Test
	public void testStandardUrlSegmentPattern() {
		MetaPattern pattern = new MetaPattern(GeneralURLSegment.SEGMENT_PARAMETER);

		Matcher matcher = pattern.matcher("");
		assertTrue(!matcher.matches());

		matcher = pattern.matcher("seg&ment");
		assertTrue(!matcher.matches());

		matcher = pattern.matcher("segment:");
		assertTrue(!matcher.matches());

		matcher = pattern.matcher("{*}");
		assertTrue(!matcher.matches());

		matcher = pattern.matcher("{segment}");
		assertTrue(matcher.matches());

		matcher = pattern.matcher("a segment {segment1} another segment {segment2}");
		assertTrue(matcher.find());
	
		matcher = pattern.matcher("{117}");
		assertTrue(!matcher.matches());
	}

	@Test
	public void testSegmentManipulation() {
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
		assertFalse(GeneralURLSegment.areSegmentCharactersValid("/"));
		assertFalse(GeneralURLSegment.areSegmentCharactersValid("{sa}"));
		assertFalse(GeneralURLSegment.areSegmentCharactersValid("segm()"));
		
		assertTrue(GeneralURLSegment.areSegmentCharactersValid("segment177"));
	}
	
	@Test
	public void testSegmentCreation() {
		String segment = "segment{segment1}anothersegment{xyzw}";
		GeneralURLSegment segmentObj = GeneralURLSegment.createSegment(segment, null);
		
		assertTrue(segmentObj instanceof MultivariableSegment);
		
		MultivariableSegment multivariableSegment = (MultivariableSegment) segmentObj;
		List<String> params = multivariableSegment.getSegmentParams();
		
		assertEquals(2, params.size());
		assertEquals("segment1", params.get(0));
		assertEquals("xyzw", params.get(1));
		
		List<String> staticSubsegments = multivariableSegment.getStaticSubsegments();
		
		assertEquals(2, staticSubsegments.size());
		assertEquals("segment", staticSubsegments.get(0));
		assertEquals("anothersegment", staticSubsegments.get(1));
		
	}
}
