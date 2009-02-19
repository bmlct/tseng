/* Copyright 2008 Blast Radius
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
package com.blastradius.qa.tseng.ui;

import java.io.File;

import static org.testng.Assert.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class UIMapFromFile {
	@Test(description = "Load a simple UI Map from a file, demonstrating reading, nesting, and object reuse")
	@Parameters("ui-map.simple-file")
	public void readSimpleFile(@Optional("./src/test/resources/ui-map-basic.xml") String fileName) {
		File f = new File(fileName);
		assert f.exists() : "File " + fileName + " does not exist";
		assert f.isFile() : "Filename " + fileName + " does not refer to a file";
		assert f.canRead() : "Cannot read file " + fileName;
		
		UIMapParser.parseFile(f);
	}
	
	@Test(description = "Ensure that top-level elements have been read from file",
			dependsOnMethods = "readSimpleFile")
	public void topLevelElements() {
		UIObject reused = UIMap.ui("simple-reused");
		assert reused != null : "The 'simple-reused' UI Object was not stored in the UI Map";
		assertEquals(reused.getLocator(), "reused-locator",
				"Initial definition of reusable object has incorrect locator");
		
		UIObject simpleRoot = UIMap.ui("simple-root");
		assert simpleRoot != null : "The 'simple-root' UI Object was not stored in the UI Map";
		assertEquals(simpleRoot.getLocator(), "simple-root-locator",
			"Initial definition of reusable object has incorrect locator");
	}
	
	@Test(description = "Child elements must not be added to root",
			dependsOnMethods = "readSimpleFile")
	public void noChildrenInTopLevel() {
		UIObject o;
		o = UIMap.get("re1");
		assert o == null : "Object re1 was stored at top level, but should be a child of simple-reused";
		
		o = UIMap.get("child1");
		assert o == null : "Object child1 was stored at top level, but should be a child of simple-root";
		
		o = UIMap.get("child2_1");
		assert o == null : "Object child2_1 was stored at top level, but should be a child of simple-root/child2";
	}
	
	@Test(description = "First and Second Level children must be in the correct places",
			dependsOnMethods = "readSimpleFile")
	public void nesting() {
		UIObject o = UIMap.ui("simple-reused", "re1");
		assert o != null : "re1 was not nested under the simple-reused object";
		assertEquals(o.getLocator(), "re1-locator", "A UI Object was stored in simple-reused/re1 but it has the wrong locator");
		
		o = UIMap.ui("simple-root", "child2", "child2_1");
		assert o != null : "child2_1 was not nested under the simple-reused object";
		assertEquals(o.getLocator(), "child2_1-locator",
				"A UI Object was stored in simple-root/child2/child2_1 but it has the wrong locator");
	}
	
	@Test(description = "First and Second Level children must have appropriate parents",
			dependsOnMethods = "nesting")
	public void nestedParents() {
		UIObject o = UIMap.ui("simple-reused", "re1");
		assertSame(o.getParent(), UIMap.ui("simple-reused"),
				"Children of the simple-reused element should have it as their parent");
		
		
		o = UIMap.ui("simple-root", "child2", "child2_1");
		assertSame(o.getParent(), UIMap.ui("simple-root", "child2"),
			"Children of the simple-root/child2 element should have it as their parent");
	}
	
	@Test(description = "Ensure that objects have been reused",
			dependsOnMethods = "nesting")
	public void objectReuse() {
		UIObject reused = UIMap.ui("simple-root", "child2", "child2_1", "child2_1_1");
		assert reused != null : "Reused UI Object was not found in the expected place";
		assertEquals(reused.getLocator(), "child2_1_1-locator",
				"Reused UI Object's locator was not correctly overwritten");
		
		UIObject child = reused.ui("re1");
		assert child != null : "Child of reused object was not correctly added or cloned";
		assertEquals(child.getLocator(), "re1-locator",
				"Child of Reused Object should have its original locator");
		
		assertSame(reused.getParent(), UIMap.ui("simple-root", "child2", "child2_1"),
				"Parent of reused object was improperly assigned");
	}
}
