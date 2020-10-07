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
import lk.ijse.dep.business.custom.CustomerBO;
import lk.ijse.dep.util.CustomerTM;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * FXML Controller class
 *
 * @author Irushi Salwathura
 */
public class ManageCustomerFormController {
    public AnchorPane root;
    public TextField txtCustomerID;
    public TextField txtCustomerName;
    public TextField txtAddress;
    public TableView<CustomerTM> tblCustomers;
    public Button btnSave;
    public Button btnDelete;
    public Button btnBack;
    public Button btnAddNewCustomer;
    private CustomerBO customerBO = AppInitializer.getApplicationContext().getBean(CustomerBO.class);
    /**
     * Initializes the lk.ijse.dep.controller class.
     */
    public void initialize() {
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        loadCustomers();

        tblCustomers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerTM>() {
            @Override
            public void changed(ObservableValue<? extends CustomerTM> observable, CustomerTM oldValue, CustomerTM newValue) {
                CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                    btnDelete.setDisable(true);
                    txtCustomerID.clear();
                    txtCustomerName.clear();
                    txtAddress.clear();
                    return;
                }
                btnSave.setText("UPDATE");
                btnSave.setDisable(false);
                btnDelete.setDisable(false);
                txtCustomerName.setDisable(false);
                txtAddress.setDisable(false);
                txtCustomerID.setText(selectedItem.getCustomerId());
                txtCustomerName.setText(selectedItem.getCustomerName());
                txtAddress.setText(selectedItem.getAddress());
            }
        });
    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        String id = txtCustomerID.getText();
        String name = txtCustomerName.getText();
        String address = txtAddress.getText();
        try {
            if (btnSave.getText().equals("SAVE")) {
                customerBO.saveCustomer(txtCustomerID.getText(), txtCustomerName.getText(), txtAddress.getText());
                btnAddNewCustomer_OnAction(actionEvent);
            } else {
                CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();
                customerBO.updateCustomer(txtCustomerName.getText(), txtAddress.getText(), selectedItem.getCustomerId());
                tblCustomers.refresh();
                btnAddNewCustomer_OnAction(actionEvent);
            }
            loadCustomers();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to update the customer", ButtonType.OK).show();
            e.printStackTrace();
        }
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        String id = txtCustomerID.getText();
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure whether you want to delete this customer?",
                    ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.YES) {
                CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();
                customerBO.deleteCustomer(selectedItem.getCustomerId());
                tblCustomers.getItems().remove(selectedItem);
                tblCustomers.getSelectionModel().clearSelection();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the customer", ButtonType.OK).show();
            e.printStackTrace();
        }
    }

    public void btnAddNewCustomer_OnAction(ActionEvent actionEvent) {
        btnSave.setText("SAVE");
        txtCustomerName.clear();
        txtAddress.clear();
        btnSave.setDisable(false);
        btnDelete.setDisable(true);

        try {
            txtCustomerID.setText(customerBO.getNewCustomerId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCustomers() {
        try {
            tblCustomers.getItems().clear();
            List<CustomerTM> allCustomers = customerBO.getAllCustomers();
            ObservableList<CustomerTM> customers = FXCollections.observableArrayList(allCustomers);
            tblCustomers.setItems(customers);
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
