/* Copyright 2008
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.blastradius.qa.tseng;

import static com.blastradius.qa.tseng.Globals.s;
import static com.blastradius.qa.tseng.TestGlobals.server;
import static org.testng.Assert.*;

import org.mortbay.http.HttpContext;
import org.mortbay.http.handler.ResourceHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CoreTests {
	private static final String DOCROOT = "./src/test/resources/";
	private static final String CONTEXT_ROOT = "/test";

	@BeforeClass(description="Set up a simple context for the test directory", enabled=false)
	public void coreTestContext() {
		HttpContext ctx = server.getContext(CONTEXT_ROOT + "/*");
		ctx.setResourceBase(DOCROOT);
		
		ResourceHandler rh = new ResourceHandler();
		ctx.addHandler(rh);
		
		try {
			ctx.start();
		} catch (Exception e) {
			throw new RuntimeException("Context failed to start", e);
		}
	}
	
	@AfterClass(description="Clean up Core Test context")
	public void cleanupTestContext() {
		HttpContext ctx = server.getContext("/test/*");
		server.removeContext(ctx);
	}
	
	@Test(description="Ensure that context has been created by checking title present in index.html")
	public void verifyCoreTestContext() {
		final String title = "TSeNG Test Home";
		s.open(CONTEXT_ROOT + "/index.html");
		assertEquals(s.getTitle(), title, "Title should be '" + title + "'");
	}
	
	@Test(description="Ensure that the given browser string can support cross-domain navigation")
	public void navigateBetweenDomains() {
		s.open("http://www.google.ca");
		s.type("name=q", "hello");
		s.open("http://www.yahoo.com");
	}
}
