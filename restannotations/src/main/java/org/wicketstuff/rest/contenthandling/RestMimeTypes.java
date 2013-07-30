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

public class RestMimeTypes {
	public static final String CHARSET_UT8 = "charset=utf-8";

	public static final String JSON = "application/json;" + CHARSET_UT8;
	
	public static final String XML = "application/xml;" + CHARSET_UT8;
	
	public static final String HTML = "text/html;" + CHARSET_UT8;
	
	public static final String PLAIN_TEXT = "text/plain;" + CHARSET_UT8;
	
	public static final String CSV = "text/csv;" + CHARSET_UT8;
	
	public static final String CSS = "text/css;" + CHARSET_UT8;
	
	public static final String RSS = "application/rss+xml;" + CHARSET_UT8;
	
	public static final String IMAGE_GIF = "image/gif";
	
	public static final String IMAGE_JPEG = "image/jpeg";
	
	public static final String IMAGE_PNG = "image/png";
	
	public static final String OCTET_STREAM = "application/octet-stream";
}
