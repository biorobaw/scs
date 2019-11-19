package com.github.biorobaw.scs.utils.files;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * Class for loading xml files
 * @author bucef
 *
 */
public class XML  {

	private Element e;
	private HashMap<String, XML> templates = new HashMap<>();
	private HashMap<String, String> variables = new HashMap<>();

	// constructor that wraps around an element
	public XML(Element e, HashMap<String,XML> templates, HashMap<String,String> variables) {
		this.e = e;
		this.templates = templates;
		this.variables = variables;
	}
	
	public  XML(String filename) {
		try {
			e = new SAXBuilder()
					.build(new File(filename))
					.getRootElement();
			templates.putAll(getChildrenMap("template"));
			if(hasChild("variables"))
				variables = getChild("variables").getAttributes();
			variables.put("SCS", "com.github.biorobaw.scs");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	public String getName() {
		return e.getName();
	}
	
	public String getText() {
		return parseString(e.getText());
	}
	
	public void setText(String text) {
		e.setText(text);
	}
	
	public String getAttribute(String attr) {
		var a = e.getAttributeValue(attr);
		return a == null ? null : parseString(a);
	}
	
	public HashMap<String,String> getAttributes(){
		var res  = new HashMap<String,String>();
		for(var a : e.getAttributes()) res.put(a.getName(), parseString(a.getValue()));
		return res;
	}
	
	public XML setAttribute(String attr, String val) {
		e.setAttribute(attr, val);
		return this;
	}
	
	public boolean hasAttribute(String attr) {
		return e.getAttribute(attr) != null;
	}

	public XML getChild(String name) {
		var c = e.getChild(name);
		return c == null ? null : new XML(c,templates,variables);
	}
	
	public boolean hasChild(String name) {
		return getChild(name)!=null;
	}

	public List<XML> getChildren(String name) {
		List<XML> res = new LinkedList<XML>();
		for(var c : e.getChildren(name)) res.add(new XML(c,templates,variables));
		return res;
	}

	public List<XML> getChildren() {
		List<XML> res = new LinkedList<XML>();
		for(var c : e.getChildren()) res.add(new XML(c,templates,variables));
		return res;
	}
	
	public String getId() {
		return getAttribute("id");
	}
	
	// -------------- Single Attributes ------------------------
	
	public boolean getBooleanAttribute(String name) {
		return Boolean.parseBoolean(getAttribute(name));
	}
	
	public int getIntAttribute(String name) {
		return Integer.parseInt(getAttribute(name));
	}

	public long getLongAttribute(String name) {
		return Long.parseLong(getAttribute(name));
	}

	public float getFloatAttribute(String name) {
		return Float.parseFloat(getAttribute(name));
	}
	
	public double getDoubleAttribute(String name) {
		return Double.parseDouble(getAttribute(name));
	}

	
	// ----------------- LISTS --------------------------------
	
	public <T> List<T> getListAttribute(String name, TypeParser<T> c){
		List<T> list = new LinkedList<>();
		for(var token : getAttribute(name).split(","))
			list.add(c.parse(token.trim()));
		return list;
	}
	
	public List<String> getStringListAttribute(String name) {
		return getListAttribute(name, x -> x);
	}
	public List<Boolean> getBooleanListAttribute(String name){
		return getListAttribute(name, Boolean::parseBoolean);
	}
	
	public List<Integer> getIntListAttribute(String name) {
		return getListAttribute(name, Integer::parseInt);
	}
	
	public List<Long> getLongListAttribute(String name) {
		return getListAttribute(name, Long::parseLong);
	}

	public List<Float> getFloatListAttribute(String name) {
		return getListAttribute(name, Float::parseFloat);
	}
	
	public List<Double> getDoubleListAttribute(String name) {
		return getListAttribute(name, Double::parseDouble);
	}

	
	// ------------------ ARRAYS ------------------------------
	
	public boolean[] getBooleanArrayAttribute(String name) {
		var list = getBooleanListAttribute(name);
		var array = new boolean[list.size()];
		int i = 0;
		for (var f : list)
			array[i++] = f;
		return array;
	}
	
	public int[] getIntArrayAttribute(String name) {
		var list = getIntListAttribute(name);
		var array = new int[list.size()];
		int i = 0;
		for (var f : list)
			array[i++] = f;
		return array;
	}
	
	public long[] getLongArrayAttribute(String name) {
		var list = getLongListAttribute(name);
		var array = new long[list.size()];
		int i = 0;
		for (var f : list)
			array[i++] = f;
		return array;
	}
	
	public float[] getFloatArrayAttribute(String name) {
		var list = getFloatListAttribute(name);
		var array = new float[list.size()];
		int i = 0;
		for (var f : list)
			array[i++] = f;
		return array;
	}
	
	public double[] getDoubleArrayAttribute(String name) {
		var list = getDoubleListAttribute(name);
		var array = new double[list.size()];
		int i = 0;
		for (var f : list)
			array[i++] = f;
		return array;
	}
	
	

	/**
	 * This function merges an 2 xml nodes.
	 * The attributes of the xml node provided as a parameter
	 * are added (possibly replacing) the attributes of the calling node.
	 * Also the children of the parameter node are added to the calling node.
	 * The result is a new xml node independent of the original nodes.
	 * @param xml	the node to be added
	 * @return	A new node containing the attributes and children of the original nodes.
	 */
	public XML merge(XML xml) {
		var res = e.clone();
		for(var a : xml.e.getAttributes())
			res.setAttribute(a.getName(), a.getValue());
		for(var c : xml.e.getChildren())
			res.addContent(c.clone());
		res.setName(xml.getName());
		return new XML(res, xml.templates, xml.variables);
	}	

	/**
	 * Returns a map of xml files containing all children of type "tag".
	 * The attribute "id" of each child is used as the key
	 * @param tag type of children to be included in he map
	 * @return	A hash map of child nodes
	 */
	public HashMap<String,XML> getChildrenMap(String tag){
		var map = new HashMap<String,XML>();
		for(var c : getChildren(tag)) map.put(c.getId(), c);
		return map;
	}

	/**
	 * Loads an object of class T.
	 * It is assumed class T has a constructor from an XML node: 'public T(XML xml);'
	 * @return The loaded object.
	 */
	@SuppressWarnings("unchecked")
	public <T> T loadObject() {
		var xml = merge_template();
		try {
			return (T)Class
					.forName(xml.getAttribute("class"))
					.getConstructor(XML.class)
					.newInstance(xml);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
			return null; // required by compiler
		}
	}
	
	/**
	 * Loads an object of class T.
	 * It is assumed class T has a constructor from an XML node: 'public T(XML xml);'
	 * @return The loaded object.
	 */
	@SuppressWarnings("unchecked")
	public <T> T loadObject(Class<T> my_class) {
		var xml = merge_template();
		try {
			return (T)Class
					.forName(my_class.getCanonicalName())
					.getConstructor(XML.class)
					.newInstance(xml);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
			return null; // required by compiler
		}
	}
	
	
	
	public XML merge_template() {
		if(hasAttribute("template")) {
			var t_xml = templates.get(getAttribute("template")).merge_template();
			return t_xml.merge(this);
		} else return this;
	}
	
	public HashMap<String, String> getVariables() {
		return variables;
	}
	
	/**
	 * Loads a list of objects of the same class T.
	 * It is assumed class T has a constructor from an XML node: 'public T(XML xml);'
	 * and that all children of the calling node define an instance of T. 
	 * @return
	 */
	public <T> LinkedList<T> loadObjectList(){
		var res = new LinkedList<T>();
		for(var c : getChildren()) res.add(c.loadObject());
		return res;
	}
	
	/**
	 * Auxiliary function that parses a string, replacing instances of $(VAR_NAME)
	 * by the value of the global variable VAR_NAME stored in the class Globals
	 * @param s
	 * @return
	 */
	private String parseString(String s) {
		Pattern regex = Pattern.compile("\\$\\(.*\\)");
		StringBuffer resultString = new StringBuffer();
		Matcher regexMatcher = regex.matcher(s);
		while (regexMatcher.find()) {
			String var = regexMatcher.group();
			var = var.substring(2, var.length() - 1);
			regexMatcher.appendReplacement(resultString, variables.get(var));
		}
		regexMatcher.appendTail(resultString);
		return resultString.toString();
	}
	
	/**
	 * Auxiliary interface that parses an object of type T from a string. 
	 * @author bucef
	 *
	 * @param <T>
	 */
	interface  TypeParser<T> {
		public T parse(String s);
	}
	
}

