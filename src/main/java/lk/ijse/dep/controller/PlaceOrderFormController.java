package lk.ijse.dep.controller;

import lk.ijse.dep.AppInitializer;
import lk.ijse.dep.business.custom.CustomerBO;
import lk.ijse.dep.business.custom.ItemBO;
import lk.ijse.dep.business.custom.OrderBO;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
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
import javafx.util.Duration;
import lk.ijse.dep.util.CustomerTM;
import lk.ijse.dep.util.ItemTM;
import lk.ijse.dep.util.OrderDetailTM;
import lk.ijse.dep.util.OrderTM;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
/**
 * FXML Controller class
 *
 * @author Irushi Salwathura
 */
public class PlaceOrderFormController {
    public AnchorPane root;
    public Button btnBack;
    public Label lblOrdeID;
    public Label lblDate;
    public ComboBox<CustomerTM> cmbCustomerID;
    public ComboBox<ItemTM> cmbItemCode;
    public TextField txtCustomerName;
    public TextField txtDescription;
    public TextField txtOtyOnHand;
    public TextField txtUnitPrice;
    public TextField txtQuantity;
    public Button btnSave;
    public Button btnDelete;
    public TableView<OrderDetailTM> tblOrders;
    public TextField txtNetTotal;
    public Button btnPlaceOrder;
    private final boolean readOnly = false;

    private OrderBO orderBO = AppInitializer.getApplicationContext().getBean(OrderBO.class);
    private CustomerBO customerBO = AppInitializer.getApplicationContext().getBean(CustomerBO.class);
    private ItemBO itemBO = AppInitializer.getApplicationContext().getBean(ItemBO.class);

    /**
     * Initializes the lk.ijse.dep.controller class.
     */
    public void initialize() {
        FadeTransition ft = new FadeTransition(Duration.millis(1000));
        ft.setNode(root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        lblDate.setText(dateFormat.format(date));
        generateOrderID();

        tblOrders.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblOrders.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrders.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrders.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrders.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));


        tblOrders.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrderDetailTM>() {
            @Override
            public void changed(ObservableValue<? extends OrderDetailTM> observable, OrderDetailTM oldValue, OrderDetailTM selectedOrderDetail) {
                OrderDetailTM selectedItem = tblOrders.getSelectionModel().getSelectedItem();

                if (selectedOrderDetail == null) {
                    return;
                }
                String selectedItemCode = selectedOrderDetail.getItemCode();
                ObservableList<ItemTM> items = cmbItemCode.getItems();
                for (ItemTM item : items) {
                    if (item.getItemCode().equals(selectedItemCode)) {
                        cmbItemCode.getSelectionModel().select(item);
                        txtOtyOnHand.setText(item.getQuantityOnHand() + "");
                        txtQuantity.setText(selectedOrderDetail.getQty() + "");
                        if (!readOnly) {
                            btnSave.setText("UPDATE");
                        }
                        if (readOnly) {
                            txtQuantity.setDisable(true);
                            btnSave.setDisable(true);
                        }
                        cmbItemCode.setDisable(true);
                        Platform.runLater(() -> {
                            txtQuantity.requestFocus();
                        });
                        break;
                    }
                }
//
            }
        });
        loadCustomerIDs();
        cmbCustomerID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerTM>() {
            @Override
            public void changed(ObservableValue<? extends CustomerTM> observable, CustomerTM oldValue, CustomerTM newValue) {
                if (newValue == null) {
                    txtCustomerName.clear();
                    return;
                }
                txtCustomerName.setText(newValue.getCustomerName());
            }
        });
        loadItemCodes();
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM newValue) {
                if (newValue == null) {
                    txtDescription.clear();
                    txtUnitPrice.clear();
                    txtOtyOnHand.clear();
                    btnSave.setDisable(true);
                    return;
                }
                txtDescription.setText(newValue.getDescription());
                txtOtyOnHand.setText(newValue.getQuantityOnHand() + "");
                txtUnitPrice.setText(newValue.getUnitPrice() + "");
            }
        });

    }

    @SuppressWarnings("Duplicates")
    public void btnSave_OnAction(ActionEvent actionEvent) {
        String orderID = lblOrdeID.getText();
        String orderDate = lblDate.getText();
        //String customerID = cmbCustomerID.getSelectionModel().getSelectedItem().toString();
        String itemCode = cmbItemCode.getSelectionModel().getSelectedItem().toString();
        String description = txtDescription.getText();
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtQuantity.getText());
        double total = unitPrice * qty;
        int qtyOnHand = Integer.parseInt(txtOtyOnHand.getText()) - qty;

        if (btnSave.getText().equals("SAVE")) {
            ObservableList<OrderDetailTM> orders = tblOrders.getItems();
            orders.add(new OrderDetailTM(itemCode, description, qty, unitPrice, total));
            txtOtyOnHand.setText(Integer.toString(qtyOnHand));
        } else if (btnSave.getText().equals("UPDATE")) {
            ObservableList<OrderDetailTM> orders = tblOrders.getItems();
            int selectedIndex = tblOrders.getSelectionModel().getSelectedIndex();
            orders.get(selectedIndex).setItemCode(cmbItemCode.getSelectionModel().getSelectedItem().toString());
            orders.get(selectedIndex).setDescription(txtDescription.getText());
            orders.get(selectedIndex).setQty(Integer.parseInt(txtQuantity.getText()));
            orders.get(selectedIndex).setUnitPrice(Double.parseDouble(txtUnitPrice.getText()));
            orders.get(selectedIndex).setTotal(Integer.parseInt(txtQuantity.getText()) * Double.parseDouble(txtUnitPrice.getText()));
            tblOrders.getSelectionModel().clearSelection();
            tblOrders.refresh();
        }
        calculateTotal();
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        String itemCode = cmbItemCode.getSelectionModel().getSelectedItem().toString();
        String description = txtDescription.getText();
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtQuantity.getText());
        double total = unitPrice * qty;
        ObservableList<OrderDetailTM> orders = tblOrders.getItems();
        orders.remove(new OrderDetailTM(itemCode, description, qty, unitPrice, total));
    }

    @SuppressWarnings("Duplicates")
    public void btnPlaceOrder_OnAction(ActionEvent actionEvent) {

        if (cmbCustomerID.getSelectionModel().getSelectedIndex() == -1) {
            new Alert(Alert.AlertType.ERROR, "You need to select a customer", ButtonType.OK).show();
            cmbCustomerID.requestFocus();
            return;
        }

        if (tblOrders.getItems().size() == 0) {
            new Alert(Alert.AlertType.ERROR, "The order is empty", ButtonType.OK).show();
            cmbItemCode.requestFocus();
            return;
        }

        try {
            orderBO.placeOrder(new OrderTM(lblOrdeID.getText(), java.sql.Date.valueOf(LocalDate.now()), cmbCustomerID.getValue().getCustomerId(), cmbCustomerID.getValue().getCustomerName(), BigDecimal.ZERO), tblOrders.getItems());
            new Alert(Alert.AlertType.INFORMATION, "The order has placed SUCCESSFULLY", ButtonType.OK).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "The order has placed UNSUCCESSFULLY!", ButtonType.OK).show();
            e.printStackTrace();
        }

        tblOrders.getItems().clear();
        txtQuantity.clear();
        cmbCustomerID.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        calculateTotal();
        generateOrderID();
    }

    public void generateOrderID() {
        try {
            lblOrdeID.setText(orderBO.getNewOrderId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculateTotal() {
        ObservableList<OrderDetailTM> orderDetails = tblOrders.getItems();
        double netTotal = 0;
        for (OrderDetailTM orderDetail : orderDetails) {
            netTotal += orderDetail.getTotal();
        }
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(2);
        numberInstance.setMinimumFractionDigits(2);
        numberInstance.setGroupingUsed(false);
        String formattedText = numberInstance.format(netTotal);
        txtNetTotal.setText("Total: " + formattedText);
    }

    @SuppressWarnings("Duplicates")
    public void loadCustomerIDs() {
        try {
            cmbCustomerID.getItems().clear();
            cmbCustomerID.setItems(FXCollections.observableArrayList(customerBO.getAllCustomers()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadItemCodes() {
        try {
            cmbItemCode.getItems().clear();
            cmbItemCode.setItems(FXCollections.observableArrayList(itemBO.getAllItems()));
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

    void initializeWithSearchOrderForm(String orderId) {
//        lblOrdeID.setText(orderId);
//        readOnly = true;
//        try {
//            PreparedStatement pstm = DBConnection.getInstance().geConnection().prepareStatement
//                    ("SELECT orderID FROM Orders WHERE orderID = ?");
//            pstm.setObject(1,orderId);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        for (Order order : ordersDB) {
//            if (order.getId().equals(orderId)){
//                lblDate.setText(order.getDate() + "");
//
//                // To select the customer
//                String customerId = order.getCustomerId();
//                for (CustomerTM customer : cmbCustomerId.getItems()) {
//                    if (customer.getId().equals(customerId)){
//                        cmbCustomerId.getSelectionModel().select(customer);
//                        break;
//                    }
//                }
//
//                for (OrderDetail orderDetail : order.getOrderDetails()) {
//                    String description = null;
//                    for (ItemTM item : cmbItemCode.getItems()) {
//                        if (item.getCode().equals(orderDetail.getCode())){
//                            description = item.getDescription();
//                            break;
//                        }
//                    }
//                    OrderDetailTM orderDetailTM = new OrderDetailTM(
//                            orderDetail.getCode(),
//                            description,
//                            orderDetail.getQty(),
//                            orderDetail.getUnitPrice(),
//                            orderDetail.getQty() * orderDetail.getUnitPrice(),
//                            null
//                    );
//                    tblOrderDetails.getItems().add(orderDetailTM);
//                    calculateTotal();
//                }
//
//                cmbCustomerId.setDisable(true);
//                cmbItemCode.setDisable(true);
//                btnSave.setDisable(true);
//                btnPlaceOrder.setVisible(false);
//                break;
//            }
//        }
    }
}
