package com.github.biorobaw.scs.gui.displays.java_fx.modals;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.github.biorobaw.scs.gui.displays.java_fx.data.Hotkey;

public class ModalHelp {
	
	
	Stage dialog = new Stage();
	Stage parent;
	
	

	
	public ModalHelp(Stage parent) { 
		this.parent = parent;
		dialog.initOwner(parent);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setTitle("Help");
//		dialog.initStyle(StageStyle.UNDECORATED);
		
		
		// Create a table display hotkeys:
		TableView<Hotkey> table = new TableView<>();
		table.setItems(FXCollections.observableList(Hotkey.hotkeys));
		table.setEditable(false);
		
		
		// Define columns
		TableColumn<Hotkey, Integer> column_id = new TableColumn<>("#");
		TableColumn<Hotkey, String> column_key = new TableColumn<>("Key");
		TableColumn<Hotkey, String> column_description = new TableColumn<>("Description");
		column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
		column_key.setCellValueFactory(new PropertyValueFactory<>("key"));
		column_description.setCellValueFactory(new PropertyValueFactory<>("description"));

		table.getColumns().setAll(column_key, column_description);
		
		BorderPane layout = new BorderPane();
		layout.setCenter(table);
		var scene = new Scene(layout);
		dialog.setScene(scene);
		
		scene.setOnKeyReleased(e -> {
			if(e.getCode() == KeyCode.ESCAPE)
				dialog.close();
		});
		

				
		
	}
	
	public void showAndWait() {
		dialog.setX(parent.getX() + parent.getWidth()/4);
		dialog.setY(parent.getY() + parent.getHeight()/4);
		dialog.setWidth(parent.getWidth()/2);
		dialog.setHeight(parent.getHeight()/2);
		
		dialog.showAndWait();
	}
	
	

	
	
	
	
	

}
