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

import java.util.LinkedList;

/**
 * Provides structural information about a UIElement's position in the display
 * hierarchy. This also provides a point for overriding of stock behavior or
 * manipulation of an element's apparent locator.
 */
public class UIElement {
	private final UITemplate template;
	private final UIElement parent;
	
	/**
	 * Creates a new reference having the giving parent and based off of the given
	 * UI Element.
	 * 
	 * @param parent
	 * @param template
	 */
	public UIElement(UIElement parent, UITemplate template) {
		this.parent = parent;
		this.template = template;
	}
	
	/**
	 * Gets the parent of this object in the UI Hierarchy.
	 * 
	 * @return Parent, or null if this is the top of the hierarchy
	 */
	public UIElement getParent() {
		return parent;
	}
	
	/**
	 * Gets the UIObject that is referenced by this object
	 * 
	 * @return
	 */
	public UITemplate getTemplate() {
		return template;
	}
	
	/**
	 * Gets the locator describing the referenced object.
	 * 
	 * Implementing classes may override this to produce a more specific
	 * locator representing the object on the page. Common usage would
	 * include an Index parameter for a specific occurrence on the page.
	 * 
	 * @return Locator describing the referenced UI object
	 */
	public String getLocator() {
		return template.getLocator();
	}
	
	/**
	 * Provides a reference to a child of the wrapped object
	 * @param name Name of child type to retrieve
	 * @return
	 */
	public UIElement ui(String name) {
		return new UIElement(this, template.getChild(name));
	}
	
	/**
	 * Returns the chain of locators that identifies the referenced
	 * UIObject.
	 * 
	 * @return
	 */
	public String[] getLocatorPath() {
		LinkedList<String> path = new LinkedList<String>();
		buildLocatorPath(path);
		return path.toArray(new String[path.size()]);
	}
	
	/**
	 * Recurses up the chain of parents, pushing locators onto
	 * the stack until there are no parents left.
	 * 
	 * @param path Stack containing series of locators
	 */
	private void buildLocatorPath(LinkedList<String> path) {
		path.addFirst(getLocator());
		
		if(parent != null) {
			parent.buildLocatorPath(path);
		}
	}
}
