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

import static org.testng.Assert.*;

import java.util.Arrays;

import org.testng.annotations.Test;

/**
 * Programmatically creates and reads UI Object Templates. 
 * 
 * @author thomas.johnson@blastradius.com
 */
public class UIMapUsage {
	
	@Test(description="Create and retrieve UITemplates",
			groups="uitemplates")
	public void createAndRetrieveTemplates() {
		UITemplate template = new UITemplate("//div[@name='test']");
		UIMap.registerTemplate("testDiv", template);
		
		UITemplate returned = UIMap.getTemplate("testDiv");
		assertSame(returned, template,
				"UIMap.get should return same object that was added");
		
		UIElement div = UIMap.ui("testDiv");
		UITemplate wrapped = div.getTemplate();
		assertSame(wrapped, template,
				"Element's referenced template should be same object unless changed");
		
		assertEquals(div.getLocator(), "//div[@name='test']",
				"Element and template should have same locator unless altered");
	}
	
	@Test(description="Verifies that Nested templates are stored correctly",
			groups="uitemplates",
			dependsOnMethods="createAndRetrieveTemplates")
	public void createHierarchicalTemplates() {
		UITemplate parent = UIMap.getTemplate("testDiv");
		UITemplate c1 = new UITemplate("//span[@class='c1']");
		UITemplate c11 = new UITemplate("//a[@id='c1link']");
		UITemplate c2 = new UITemplate("//span[@class='c2']");
		
		parent.addChild("child1", c1);
		c1.addChild("link", c11);
		parent.addChild("child2", c2);
		
		assertSame(parent.getChild("child1"), c1,
				"UIElementTempalte.ui(\"...\") should return the value originally stored in it");
		assertSame(parent.getChild("child2"), c2,
				"UIElementTempate.ui(\"...\") should return the value originally stored in it");
		
		assertNull(parent.getChild("does not exist"),
				"Attempting to retrieve a nonexistent UI Template should return null");
	}
	
	@Test(description="Ensure that chaining of ui() calls returns the right objects",
			groups="uielements",
			dependsOnGroups="uitemplates",
			dependsOnMethods="createHierarchicalTemplates")
	public void simpleChaining() {
		UIElement div = UIMap.ui("testDiv");
		assertEquals(div.getLocator(), div.getTemplate().getLocator(),
				"Locator Strings should be identical between the UIElement and the Referenced template");
		
		UIElement c1 = UIMap.ui("testDiv").ui("child1");
		assertEquals(c1.getLocator(), c1.getTemplate().getLocator(),
				"Locator Strings should be identical between the UIElement and the Referenced template");
		
		UIElement c11 = UIMap.ui("testDiv").ui("child1").ui("link");
		assertEquals(c11.getLocator(), c11.getTemplate().getLocator(),
				"Locator Strings should be identical between the UIElement and the Referenced template");
	}
	
	@Test(description="Build effective locator from a deep child node",
			groups="uielements",
			dependsOnMethods="createHierarchicalTemplates")
	public void buildEffectiveLocator() {
		String[] actualPath = UIMap.ui("testDiv").ui("child1").ui("link").getLocatorPath();
		String[] expectedPath = new String[] { "//div[@name='test']", "//span[@class='c1']", "//a[@id='c1link']" };
		
		assertEquals(actualPath.length, expectedPath.length,
				"Compound Locator for UIElement 3 levels deep should contain 3 components");
		assertEquals(Arrays.deepToString(actualPath), Arrays.deepToString(expectedPath),
				"Compound Locator should contain all locators of itself and its parents in least-specific to most-specific order");
	}
	
	@Test(description="Removes an object from a template, rendering it inaccessible to future calls",
			dependsOnGroups="uitemplates")
	public void removeTemplate() {
		UITemplate tempTemplate = new UITemplate("//a[@id='temp']");
		UIMap.getTemplate("testDiv").addChild("temp", tempTemplate);
		
		
	}
}
