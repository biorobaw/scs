package edu.usf.experiment.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.usf.experiment.Globals;

public class ElementWrapper {

	private Element e;

	public ElementWrapper(Element e) {
		this.e = e;
	}

	public ElementWrapper getChild(String name) {
		NodeList elems = e.getElementsByTagName(name);
		for (int i = 0; i < elems.getLength(); i++)
			// Only return direct sibling
			if (elems.item(i).getParentNode() == e)
				return new ElementWrapper((Element) elems.item(i));

		return null;
	}

	public List<ElementWrapper> getChildren(String name) {
		List<ElementWrapper> res = new LinkedList<ElementWrapper>();

		NodeList elems = e.getElementsByTagName(name);
		for (int i = 0; i < elems.getLength(); i++) {
			if (elems.item(i).getParentNode() == e) {
				res.add(new ElementWrapper((Element) elems.item(i)));
			}
		}

		return res;
	}

	public List<ElementWrapper> getChildren() {
		List<ElementWrapper> res = new LinkedList<ElementWrapper>();
		NodeList elems = e.getElementsByTagName("*");
		for (int i = 0; i < elems.getLength(); i++) {
			if (elems.item(i).getParentNode() == e) {
				res.add(new ElementWrapper((Element) elems.item(i)));
			}
		}
		return res;
	}

	public String getChildText(String name) {
		Globals g = Globals.getInstance();
		NodeList elems = e.getElementsByTagName(name);
		for (int i = 0; i < elems.getLength(); i++)
			// Only return direct sibling
			if (elems.item(i).getParentNode() == e) {
				Pattern regex = Pattern.compile("\\$\\(.*\\)");
				StringBuffer resultString = new StringBuffer();
				Matcher regexMatcher = regex.matcher(elems.item(i).getTextContent());
				while (regexMatcher.find()) {
					String var = regexMatcher.group();
					var = var.substring(2, var.length() - 1);
					regexMatcher.appendReplacement(resultString, (String) g.get(var));
				}
				regexMatcher.appendTail(resultString);

				return resultString.toString();
			}

		return null;
	}

	public int getChildInt(String name) {
		return Integer.parseInt(getChildText(name));
	}

	public float getChildFloat(String name) {
		return Float.parseFloat(getChildText(name));
	}

	public String getText() {
		Globals g = Globals.getInstance();
		Pattern regex = Pattern.compile("\\$\\(.*\\)");
		StringBuffer resultString = new StringBuffer();
		Matcher regexMatcher = regex.matcher(e.getTextContent());
		while (regexMatcher.find()) {
			String var = regexMatcher.group();
			var = var.substring(2, var.length() - 1);
			regexMatcher.appendReplacement(resultString, (String) g.get(var));
		}
		regexMatcher.appendTail(resultString);

		return resultString.toString();
	}

	public boolean getChildBoolean(String name) {
		return Boolean.parseBoolean(getChildText(name));
	}

	public long getChildLong(String name) {
		return Long.parseLong(getChildText(name));
	}

	public List<Float> getChildFloatList(String name) {
		String listString = getChildText(name);
		List<Float> list = new LinkedList<Float>();
		StringTokenizer tok = new StringTokenizer(listString, ",");
		while (tok.hasMoreTokens())
			list.add(Float.parseFloat(tok.nextToken()));
		return list;
	}

	public List<Integer> getChildIntList(String name) {
		String listString = getChildText(name);
		List<Integer> list = new LinkedList<Integer>();
		StringTokenizer tok = new StringTokenizer(listString, ",");
		while (tok.hasMoreTokens())
			list.add(Integer.parseInt(tok.nextToken()));
		return list;
	}
	
	public List<String> getChildStringList(String name) {
		String listString = getChildText(name);
		List<String> list = new LinkedList<String>();
		StringTokenizer tok = new StringTokenizer(listString, ",");
		while (tok.hasMoreTokens()) list.add(tok.nextToken().trim());
		return list;
	}

	public Map<String, List<String>> getCalibrationList(ElementWrapper calibrationRoot) {
		Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
		for (ElementWrapper param : calibrationRoot.getChild("model").getChildren()) {
			result.put(param.getName(), new LinkedList<String>());
			for (ElementWrapper value : param.getChildren("value")) {
				result.get(param.getName()).add(value.getText());
			}
		}
		return result;
	}

	private String getName() {
		return e.getNodeName();
	}

	public void changeModelParam(String param, String value, ElementWrapper root) {
		root.getChild("model").getChild("params").getChild(param).setText(value);
	}

	public void setText(String text) {
		e.setTextContent(text);
	}

	public void merge(ElementWrapper o, ElementWrapper n) {
		for (ElementWrapper ge : n.getChildren()) {
			changeModelParam(ge.getName(), ge.getText(), o);
		}

	}
}
