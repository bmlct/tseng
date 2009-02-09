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
package com.blastradius.qa.tseng.ui;

import static org.testng.Assert.*;

import java.util.Arrays;

import org.testng.annotations.Test;

/**
 * Programmatically creates and reads UI Object Templates. 
 */
public class UIMapUsage {
	
	@Test(description="Create and retrieve UIObjects",
			groups="creation")
	public void createAndRetrieveTemplates() {
		UIObject root = new UIObject(null, "//div[@name='root']");
		UIMap.put("root", root);
		
		UIObject returned = UIMap.get("root");
		UIObject returnedByUI = UIMap.ui("root");
		
		assertSame(returned, root,
				"UIMap.get should return same object that was added");
		assertSame(returnedByUI, root,
				"UIMap.ui should return same object that was added");
		
		assertEquals(root.getLocator(), "//div[@name='root']",
				"Element and template should have same locator unless altered");
	}
	
	@Test(description="Verifies that Nested objects are stored correctly",
			groups="creation",
			dependsOnMethods="createAndRetrieveTemplates")
	public void createHierarchicalTemplates() {
		UIObject root = UIMap.get("root");
		UIObject c1 = new UIObject(root, "//span[@class='c1']");
		UIObject c1_1 = new UIObject(c1, "//a[@id='c1link']");
		UIObject c2 = new UIObject(root, "//span[@class='c2']");
		
		root.putChild("child1", c1);
		c1.putChild("link", c1_1);
		root.putChild("child2", c2);
		
		assertSame(root.getChild("child1"), c1,
				"UIElementTempalte.ui(\"...\") should return the value originally stored in it");
		assertSame(root.getChild("child2"), c2,
				"UIElementTempate.ui(\"...\") should return the value originally stored in it");
		
		assertNull(root.getChild("does not exist"),
				"Attempting to retrieve a nonexistent UI Template should return null");
	}
	
	@Test(description="Verifies that nonexistent children return nothing",
			groups="creation",
			dependsOnMethods="createAndRetrieveTemplates")
	public void nonexistentChildrenAreNull() {
		UIObject root = UIMap.get("root");
		
		assertNull(root.getChild("does not exist"),
				"Attempting to retrieve a nonexistent UI Template should return null");
	}
	
	@Test(description="Build effective locator from a deep child node",
			groups="operations",
			dependsOnGroups="creation")
	public void buildEffectiveLocator() {
		String[] actualPath = UIMap.ui("root").ui("child1").ui("link").getLocatorPath();
		String[] expectedPath = new String[] { "//div[@name='root']", "//span[@class='c1']", "//a[@id='c1link']" };
		
		assertEquals(actualPath.length, expectedPath.length,
				"Compound Locator for UIElement 3 levels deep should contain 3 components");
		assertEquals(Arrays.deepToString(actualPath), Arrays.deepToString(expectedPath),
				"Compound Locator should contain all locators of itself and its parents in least-specific to most-specific order");
	}
	
	@Test(description="Clone a hierarchy as a root node",
			groups="operations",
			dependsOnGroups="creation")
	public void cloneTreeAsRootNode() {
		UIObject root = UIMap.ui("root");
		UIObject clone = root.clone(null, "//div[@name='clone']");
		UIMap.put("clone", clone);
		
		UIObject link = UIMap.ui("root").ui("child1").ui("link");
		UIObject linkClone = UIMap.ui("clone").ui("child1").ui("link");
		
		assertNotSame(linkClone, link,
				"Deep-cloned objects should not be '=='");
		
		String[] linkPath = link.getLocatorPath();
		String[] linkClonePath = linkClone.getLocatorPath();
		
		assertEquals(linkPath[0], "//div[@name='root']",
				"Locator of original root must remain unchanged after clone");
		
		assertEquals(linkClonePath[0], "//div[@name='clone']",
				"Locator of cloned root must be as originally specified");
		
		assertEquals(linkClonePath.length, linkPath.length,
				"Path lengths of original and cloned objects should be the same");
		
		for(int i = 1; i < linkPath.length; i++) {
			assertEquals(linkClonePath[i], linkPath[i],
					"Locators of cloned children should remain unchanged");
		}
	}
	
	@Test(description="Clone a hierarchy as a child node",
			groups="operations",
			dependsOnGroups="creation")
	public void cloneTreeAsChildNode() {
		UIObject root = UIMap.ui("root");
		UIObject cloneParent = new UIObject(null, "//div[@name='cloneParent']");
		cloneParent.putChild("clone", root.clone(cloneParent, "//div[@name='clone']"));
		
		UIMap.put("cloneParent", cloneParent);
		
		UIObject link = UIMap.ui("root").ui("child1").ui("link");
		UIObject linkClone = UIMap.ui("cloneParent").ui("clone").ui("child1").ui("link");
		
		assertNotSame(linkClone, link,
				"Deep-cloned objects should not be '=='");
		
		String[] linkPath = link.getLocatorPath();
		String[] linkClonePath = linkClone.getLocatorPath();
		
		assertEquals(linkPath[0], "//div[@name='root']",
				"Locator of original root must remain unchanged after clone");
		
		assertEquals(linkClonePath[1], "//div[@name='clone']",
				"Locator of cloned root must be as originally specified");
		
		assertEquals(linkClonePath.length, linkPath.length + 1,
				"Path length of clone should be sum of original and depth of new parent");
		
		for(int i = 1; i < linkPath.length; i++) {
			assertEquals(linkClonePath[i + 1], linkPath[i],
					"Locators of cloned children should remain unchanged");
		}
	}
}
