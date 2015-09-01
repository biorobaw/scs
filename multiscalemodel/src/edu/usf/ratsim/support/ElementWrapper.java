package edu.usf.ratsim.support;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class ElementWrapper {
	
	private Element e;

	public ElementWrapper(Element e){
		this.e = e;
	}
	
	public ElementWrapper getChild(String name){
		return new ElementWrapper((Element) e.getElementsByTagName(name).item(0));
	}
	
	public List<ElementWrapper> getChildren(String name){
		List<ElementWrapper> res = new LinkedList<ElementWrapper>(); 
		
		NodeList elems = e.getElementsByTagName(name);
		for (int i = 0; i < elems.getLength(); i++) {
			if (elems.item(i).getParentNode() == e){
				res.add(new ElementWrapper((Element) elems.item(i)));
			}
		}
		
		return res;
	}

	public String getChildText(String name){
		return e.getElementsByTagName(name).item(0).getTextContent();
	}
	
	public int getChildInt(String name){
		return Integer.parseInt(getChildText(name));
	}
	
	public float getChildFloat(String name){
		return Float.parseFloat(getChildText(name));
	}

	public String getText() {
		return e.getTextContent();
	}

	public boolean getChildBoolean(String name) {
		return Boolean.parseBoolean(getChildText(name));
	}
}
