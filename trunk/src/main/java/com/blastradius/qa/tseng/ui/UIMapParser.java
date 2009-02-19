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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class UIMapParser {
	/**
	 * Boilerplate XML reader
	 * @param f
	 * @return
	 */
	private static Document readXML(File f) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(f);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Failed to instantiate XML Parser", e);
		} catch (SAXException e) {
			throw new RuntimeException("Input File " + f.getPath() + " is malformed: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException("Failure while reading " + f.getPath() + ": " + e.getMessage(), e);
		}
	}
	
	/**
	 * Populates the UI map with the elements specified in the given file
	 * @param f
	 */
	public static void parseFile(File f) {
		Document d = readXML(f);
		Element doc = d.getDocumentElement();
		
		NodeList topElements = doc.getChildNodes();
		for(int i = 0; i < topElements.getLength(); i++) {
			Node n = topElements.item(i);
			if(n.getNodeType() == Node.ELEMENT_NODE) {
				parseUIObject(null, (Element) n);
			}
		}
	}
	
	/**
	 * Attempts to parse a UI Object and its children from an XML Element,
	 * and add it to the given parent. If the parent is null, the element
	 * is added to the global UI map.
	 * 
	 * The type of the object is determined by the element's tag name. If
	 * it is a "ui" tag, then a new generic UI Object will be created.
	 * For any other type, the logic will attempt to clone an existing
	 * item from the UI Map having the same name. If no match is found,
	 * a runtime exception will be raised.
	 *  
	 * @param parent
	 * @param el
	 * @return
	 */
	public static UIObject parseUIObject(UIObject parent, Element el) {
		UIObject ui;
		
		String type = el.getNodeName();
		
		String locator;
		Attr locatorAttribute = el.getAttributeNode("locator");
		
		String name;
		Attr nameAttribute = el.getAttributeNode("name");
		
		if(locatorAttribute == null) {
			throw new RuntimeException("Locator was not specified for " + type);
		} else {
			locator = locatorAttribute.getTextContent();
		}
		
		if(nameAttribute == null) {
			throw new RuntimeException("Name was not specified for " + type);
		} else {
			name = nameAttribute.getTextContent();
		}
		
		
		if(type.equals("ui")) {
			ui = new UIObject(parent, locator);
		} else if(UIMap.get(type) != null) {
			ui = UIMap.get(type).clone(parent, locator);
		} else {
			throw new RuntimeException("No type named '" + type + "' has been defined");
		}
		
		if(parent == null) {
			UIMap.put(name, ui);
		} else {
			parent.putChild(name, ui);
		}
		
		// Add any defined children to the object
		
		// Identify <elements> nodes and attempt to parse their contents
		NodeList children = el.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if(n.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) n;
				if(child.getNodeName().equals("components")) {
					// Attempt to parse contents of <components> node as UI Objects
					NodeList grandChildren = child.getChildNodes();
					for(int j = 0; j < grandChildren.getLength(); j++) {
						Node grandChild = grandChildren.item(j);
						if(grandChild.getNodeType() == Node.ELEMENT_NODE) {
							parseUIObject(ui, (Element) grandChild);
						}
					}
				}
			}
		}
		
		// Repeat for Actions
		// Repeat for Attributes
		
		return ui;
	}
}
