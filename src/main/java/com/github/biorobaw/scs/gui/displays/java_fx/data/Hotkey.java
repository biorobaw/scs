package com.github.biorobaw.scs.gui.displays.java_fx.data;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Hotkey {
	
	public static List<Hotkey> hotkeys = new ArrayList<>();
	
	public StringProperty key = new SimpleStringProperty();
	public StringProperty description = new SimpleStringProperty();
	public IntegerProperty id = new SimpleIntegerProperty();
	
	static int next_id = 0;
	
	static public void addHotkey(String keycode, String description) {
		new Hotkey(keycode, description);
	}
	
	
	public Hotkey() {
		hotkeys.add(this);
		id.set(next_id++);
	}
	
	public Hotkey(String code, String description) {
		hotkeys.add(this);
		key.setValue(code);
		this.description.setValue(description);
		id.set(next_id++);
	}
	
	public void setKey(String value) {
		key.set(value);
	}
	
	public String getKey() {
		return key.getValue();
	}
	
	public StringProperty keyProperty() {
		return key;
	}
	
	public void setDescription(String value) {
		description.set(value);
	}
	
	public String getDescription() {
		return description.getValue();
	}
	
	public StringProperty descriptionProperty() {
		return description;
	}
	
	public void setId(Integer value) {
		id.set(value);
	}
	
	public Integer getId() {
		return id.getValue();
	}
	
	public IntegerProperty descriptionId() {
		return id;
	}
	
}
