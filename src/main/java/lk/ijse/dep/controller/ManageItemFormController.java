package lk.ijse.dep.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep.AppInitializer;
import lk.ijse.dep.business.custom.ItemBO;
import lk.ijse.dep.util.ItemTM;

import java.io.IOException;
import java.util.Optional;

/**
 * FXML Controller class
 *
 * @author Irushi Salwathura
 */
public class ManageItemFormController {
    public AnchorPane root;
    public Button btnBack;
    public Button btnAddNewItem;
    public Button btnSave;
    public Button btnDelete;
    public TextField txtItemCode;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public TableView<ItemTM> tblItems;
    private ItemBO itemBO = AppInitializer.getApplicationContext().getBean(ItemBO.class);

    /**
     * Initializes the lk.ijse.dep.controller class.
     */
    public void initialize() {
        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("quantityOnHand"));
        loadItems();

        tblItems.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM newValue) {
                ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                    btnDelete.setDisable(true);
                    txtDescription.clear();
                    txtUnitPrice.clear();
                    txtQtyOnHand.clear();
                    return;
                }
                btnSave.setText("UPDATE");
                btnDelete.setDisable(false);
                btnSave.setDisable(false);
                txtDescription.setDisable(false);
                txtUnitPrice.setDisable(false);
                txtQtyOnHand.setDisable(false);
                txtItemCode.setText(selectedItem.getItemCode());
                txtDescription.setText(selectedItem.getDescription());
                txtUnitPrice.setText(selectedItem.getUnitPrice() + "");
                txtQtyOnHand.setText(selectedItem.getQuantityOnHand() + "");

            }
        });
    }

    public void btnAddNewItem_OnAction(ActionEvent actionEvent) {
        btnSave.setText("SAVE");
        txtDescription.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
        btnDelete.setDisable(true);
        btnSave.setDisable(false);

        int maxId = 0;
        for (ItemTM items : tblItems.getItems()) {
            int id = Integer.parseInt(items.getItemCode().replace("I", ""));
            if (maxId < id) {
                maxId = id;
            }
        }
        maxId = maxId + 1;
        String id = "";
        if (maxId < 10) {
            id = "I00" + maxId;
        } else if (maxId < 100) {
            id = "I0" + maxId;
        } else {
            id = "I" + maxId;
        }
        txtItemCode.setText(id);

    }

    public void btnSave_OnAction(ActionEvent actionEvent) {

        if (txtDescription.getText().trim().isEmpty() ||
                txtQtyOnHand.getText().trim().isEmpty() ||
                txtUnitPrice.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Description, Qty. on Hand or Unit Price can't be empty").show();
            return;
        }

        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText().trim());
        double unitPrice = Double.parseDouble(txtUnitPrice.getText().trim());

        if (qtyOnHand < 0 || unitPrice <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty. or UnitPrice").show();
            return;
        }

        try {
            if (btnSave.getText().equals("SAVE")) {

                itemBO.saveItem(txtItemCode.getText(), txtDescription.getText(), unitPrice, qtyOnHand);
                btnAddNewItem_OnAction(actionEvent);
            } else {
                ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();
                itemBO.updateItem(txtDescription.getText(), unitPrice, qtyOnHand, selectedItem.getItemCode());
                tblItems.refresh();
                btnAddNewItem_OnAction(actionEvent);
            }
            loadItems();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save the item", ButtonType.OK).show();
            new Alert(Alert.AlertType.ERROR, "Failed to update the Item").show();
            e.printStackTrace();
        }
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        String itemCode = txtItemCode.getText();
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure whether you want to delete this item?",
                    ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.YES) {
                ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();
                itemBO.deleteItem(selectedItem.getItemCode());
                tblItems.getItems().remove(selectedItem);
                tblItems.getSelectionModel().clearSelection();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the item", ButtonType.OK).show();
            e.printStackTrace();
        }
    }

    public void loadItems() {
        try {
            ObservableList<ItemTM> items = tblItems.getItems();
            items.clear();
            items = FXCollections.observableArrayList(itemBO.getAllItems());
            tblItems.setItems(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    public void btnBack_OnAction(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
            Scene mainScene = new Scene(root);
            Stage mainStage = (Stage) this.root.getScene().getWindow();
            mainStage.setScene(mainScene);
            mainStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
