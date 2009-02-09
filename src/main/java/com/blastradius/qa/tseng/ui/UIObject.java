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

import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Describes an element of the User Interface in terms of
 * its location on a page and its position in a tree
 * of other UI elements.
 */
public class UIObject {
	private final UIObject parent;
	private final String locator;
	
	private final Hashtable<String, UIObject> children = new Hashtable<String, UIObject>();
	// private final Hashtable<String, UIAction> actions = new Hashtable<String, UIAction>(); // For future use
	// private final Hashtable<String, String> attributes = new Hashtable<String, String>(); // For future use
	
	public UIObject(UIObject parent, String locator) {
		this.parent = parent;
		this.locator = locator;
	}
	
	/**
	 * Gets the Selenium locator describing this object.
	 * 
	 * Implementing classes may override this to produce a more specific
	 * locator representing the object on the page. Example usage would
	 * include adding an "index" parameter to the implementing class,
	 * used to replace an instance of %d in the locator.
	 * 
	 * @return Selenium Locator for this object
	 */
	public String getLocator() {
		return locator;
	}
	
	/**
	 * Gets the parent of this object in the UI Tree. May return null
	 * if this is a root element.
	 * 
	 * @return Parent object, or null if there are no parents.
	 */
	public UIObject getParent() {
		return parent;
	}
	
	/**
	 * Retrieves named child. Synonym for {@link #getChild(String)},
	 * except shorter to allow for easy chaining and expression of
	 * hierarchical constructs.
	 * 
	 * @param name Name of child to retrieve
	 * @return The named child, or null if no such child exists
	 * @see #getChild(String)
	 */
	public UIObject ui(String name) {
		return getChild(name);
	}
	
	/**
	 * Retrieves named child.
	 * @param name Name of child to retrieve
	 * @return The named child, or null if no such child exists
	 */
	public UIObject getChild(String name) {
		return children.get(name);
	}
	
	/**
	 * Adds a child with a given name to this UI Object. The child's
	 * parent must already be set to this object, otherwise strange
	 * behavior may result.
	 * 
	 * @param name Name to associate with child. May not be null.
	 * @param child Child object to add. May not be null.
	 * 
	 * @return Child previously associated with provided name
	 */
	public UIObject putChild(String name, UIObject child) {
		return children.put(name, child);
	}
	
	/**
	 * Gets this object's list of children
	 * @return Child map
	 */
	public Hashtable<String, UIObject> getChildren() {
		return (Hashtable<String, UIObject>) children;
	}
	
	/**
	 * Perform a deep clone of this object and all of its children,
	 * re-parenting it to a different object and locator. Locators
	 * of children are preserved.
	 * 
	 * @param parent Parent to assign to the new clone
	 * @param locator New locator for object
	 * @return Deep clone of object and its children
	 */
	public UIObject clone(UIObject parent, String locator) {
		UIObject clone = new UIObject(parent, locator);
		
		/* Recursively clone all of the children, assigning them to
		 * newly-cloned parents.
		 */
		for(String childName : getChildren().keySet()) {
			UIObject originalChild = getChild(childName);
			UIObject newChild = originalChild.clone(clone, originalChild.getLocator());
			
			clone.putChild(childName, newChild);
		}
		
		return clone;
	}
	
	/**
	 * Returns the chain of locators that identifies the referenced
	 * UIObject.
	 * 
	 * @return Array of locators describing path to this object
	 */
	public String[] getLocatorPath() {
		LinkedList<String> path = new LinkedList<String>();
		buildLocatorPath(path);
		return path.toArray(new String[path.size()]);
	}
	
	/**
	 * Recurses up the chain of parents, prepending locators onto
	 * the list until the top of the tree is reached.
	 * 
	 * @param path Linked list to hold locators in order
	 */
	private void buildLocatorPath(LinkedList<String> path) {
		path.addFirst(getLocator());
		
		if(parent != null) {
			parent.buildLocatorPath(path);
		}
	}
}
