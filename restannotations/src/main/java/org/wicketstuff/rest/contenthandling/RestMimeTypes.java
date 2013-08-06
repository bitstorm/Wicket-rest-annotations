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
package org.wicketstuff.rest.contenthandling;

/**
 * Utility class that contains constant values for MIME types.
 * 
 * @author andrea del bene
 *
 */
public class RestMimeTypes {
	public static final String APPLICATION_RSS_XML = "application/rss+xml;";

	public static final String TEXT_CSS = "text/css";

	public static final String TEXT_CSV = "text/csv";

	public static final String TEXT_PLAIN = "text/plain";

	public static final String TEXT_HTML = "text/html";

	public static final String APPLICATION_XML = "application/xml";

	public static final String APPLICATION_JSON = "application/json";

	public static final String CHARSET_UT8 = "charset=utf-8";

	public static final String JSON_UTF8 = APPLICATION_JSON + ";" + CHARSET_UT8;
	
	public static final String XML_UTF8 = APPLICATION_XML + ";" + CHARSET_UT8;
	
	public static final String HTML_UTF8 = TEXT_HTML + ";" + CHARSET_UT8;
	
	public static final String PLAIN_TEXT_UTF8 = TEXT_PLAIN + ";" + CHARSET_UT8;
	
	public static final String CSV_UTF8 = TEXT_CSV + ";" + CHARSET_UT8;
	
	public static final String CSS_UTF8 = TEXT_CSS + ";" + CHARSET_UT8;
	
	public static final String RSS_UTF8 = APPLICATION_RSS_XML + ";" + CHARSET_UT8;
	
	public static final String IMAGE_GIF = "image/gif";
	
	public static final String IMAGE_JPEG = "image/jpeg";
	
	public static final String IMAGE_PNG = "image/png";
	
	public static final String OCTET_STREAM = "application/octet-stream";
}
