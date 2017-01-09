package erj4.as;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import org.controlsfx.control.CheckComboBox;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import erj4.as.CustomElements.DataDisplay;
import erj4.as.DataClasses.Custom;
import erj4.as.DataClasses.Data;
import erj4.as.DataClasses.Template;
import erj4.as.DataClasses.Wallet;

public class CustomsIndexController extends IndexController implements Initializable{

	@FXML
	CheckComboBox<Wallet> walletFilterBox;

	@FXML
	ComboBox<Template> templateFilterBox;

	@FXML
	ListView<Custom> itemsList;

	@FXML
	VBox detailsPane;

	Template templateFilter = null;

	FilteredList<Custom> filtered;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize();
		walletFilterBox.getItems().clear();
		walletFilterBox.getItems().addAll(Wallet.getAllWallets().sorted());

		templateFilterBox.setItems(Template.getAllTemplates().sorted());

		filtered = Custom.getAllCustoms().sorted().filtered(new Predicate<Custom>() {
			@Override
			public boolean test(Custom t) {
				if(templateFilterBox.getSelectionModel().getSelectedItem()!=null && t.getTemplate()!=templateFilterBox.getSelectionModel().getSelectedItem()) return false;
				for(Wallet w:walletFilterBox.getCheckModel().getCheckedItems()) if(!w.getCustoms().contains(t)) return false;
				if(!t.getName().contains(searchBox.getText())) return false;
				return true;
			}});
		itemsList.setItems(filtered);
		walletFilterBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Wallet>)x->updateListView());
		templateFilterBox.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Template>)(x,y,z)->updateListView());
	}

	public void updateListView() {
		filtered.setPredicate(new Predicate<Custom>() {
			@Override
			public boolean test(Custom t) {
				if(templateFilterBox.getSelectionModel().getSelectedItem()!=null && t.getTemplate()!=templateFilterBox.getSelectionModel().getSelectedItem()) return false;
				for(Wallet w:walletFilterBox.getCheckModel().getCheckedItems()) if(!w.getCustoms().contains(t)) return false;
				if(!t.getName().contains(searchBox.getText())) return false;
				return true;
			}});
	}

	public void unwrapChildren(ArrayList<Node> nodeBranch) { // Debug method - outputs trees of children of a node in an arraylist
		Node last = nodeBranch.get(nodeBranch.size()-1);
		for(Node n:nodeBranch) System.out.print(n); System.out.println();
		if(last instanceof Parent) for(Node child:((Parent) last).getChildrenUnmodifiable()) {
			@SuppressWarnings("unchecked")
			ArrayList<Node> newArray = (ArrayList<Node>) nodeBranch.clone();
			newArray.add(child);
			unwrapChildren(newArray);
		}
	}

	@FXML
	public void newItem() {
		String fileName = "new_item.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
		Stage stage = new Stage();
		stage.setTitle("Add new item");
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			Main.fatalError(e, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
		}
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(searchBox.getScene().getWindow());
		stage.showAndWait();
	}

	@FXML
	public void itemSelected(MouseEvent e) {
		if(e==null||e.getClickCount()==1){
			detailsPane.getChildren().clear();
			Custom custom = itemsList.getSelectionModel().getSelectedItem();
			if (custom==null) return;
			Separator separator=null;
			for (Data d:custom.getData()){
				Label columnLabel = new Label(d.getColumn().getName());
				columnLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
				separator=new Separator();
				detailsPane.getChildren().addAll(columnLabel, new DataDisplay(d), separator);
			}
			if(separator!=null) detailsPane.getChildren().remove(separator);
		}
		else if(e.getClickCount()==2){
			Custom custom = itemsList.getSelectionModel().getSelectedItem();
			if (custom==null) return;
			String fileName = "new_item.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
			Stage stage = new Stage();
			stage.setTitle("Edit item");
			try {
				stage.setScene(new Scene(loader.load()));
				((NewItemController) loader.getController()).editMode(custom);
			} catch (IOException ex) {
				Main.fatalError(ex, "An error occured while trying to load the resource "+fileName+", so the program must exit immediately");
			}
			stage.setResizable(false);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(searchBox.getScene().getWindow());
			stage.showAndWait();
			itemSelected(null);
		}
	}

	@FXML
	public void clearWalletFilter() {
		walletFilterBox.getCheckModel().clearChecks();
	}

	@FXML
	public void clearTemplateFilter() {
		templateFilterBox.getSelectionModel().clearSelection();
	}

	public void filter(Wallet wallet){
		walletFilterBox.getCheckModel().check(wallet);
	}

	public void filter(Template template){
		templateFilter=template;
	}
}
