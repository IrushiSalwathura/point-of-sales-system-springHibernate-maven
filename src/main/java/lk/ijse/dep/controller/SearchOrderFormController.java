package lk.ijse.dep.controller;

import lk.ijse.dep.business.BOFactory;
import lk.ijse.dep.business.BOType;
import lk.ijse.dep.business.custom.OrderBO;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep.db.HibernateUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import lk.ijse.dep.util.OrderTM;
import org.hibernate.Session;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * FXML Controller class
 *
 * @author Irushi Salwathura
 */
public class SearchOrderFormController {
    public AnchorPane root;
    public Button btnBack;
    public TextField txtSearch;
    public TableView<OrderTM> tblSearch;
    private ArrayList<OrderTM> searchOrdersArray = new ArrayList<>();

    private final OrderBO orderBO = BOFactory.getInstance().getBO(BOType.ORDER);
    /**
     * Initializes the lk.ijse.dep.controller class.
     */
    public void initialize(){
        tblSearch.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderId"));
        tblSearch.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tblSearch.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tblSearch.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tblSearch.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));
        loadOrderDetails();

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                ObservableList<OrderTM> search = tblSearch.getItems();
                search.clear();
                for (OrderTM order : searchOrdersArray) {
                    if ((order.getOrderId().contains(newValue)||
                            order.getCustomerId().contains(newValue) ||
                            order.getCustomerName().contains(newValue) ||
                            order.getOrderDate().toString().contains(newValue))){
                        search.add(order);
                    }
                }
            }
        });

    }

    public void loadOrderDetails(){
        try {
            tblSearch.getItems().clear();
            List<OrderTM> searchOrder = orderBO.searchOrder();
            ObservableList<OrderTM> orders = FXCollections.observableArrayList(searchOrder);
            tblSearch.setItems(orders);
            for (OrderTM order : searchOrder) {
                searchOrdersArray.add(new OrderTM(order.getOrderId(),order.getOrderDate(),order.getCustomerId(),order.getCustomerName(),order.getTotal()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("Duplicates")
    public void btnBack_OnAction(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
            Scene mainScene = new Scene(root);
            Stage mainStage = (Stage)this.root.getScene().getWindow();
            mainStage.setScene(mainScene);
            mainStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void tblSearch_OnMouseClicked(MouseEvent mouseEvent) throws IOException {
        if (tblSearch.getSelectionModel().getSelectedItem() == null){
            return;
        }
        if (mouseEvent.getClickCount() == 2){
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/PlaceOrderForm.fxml"));
            Parent root = fxmlLoader.load();
            PlaceOrderFormController controller = (PlaceOrderFormController) fxmlLoader.getController();
            controller.initializeWithSearchOrderForm(tblSearch.getSelectionModel().getSelectedItem().getOrderId());
            Scene orderScene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(orderScene);
            stage.centerOnScreen();
            stage.show();
        }
    }

    public void btnReport_OnAction(ActionEvent actionEvent) throws JRException {
        JasperDesign jasperDesign = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            jasperDesign = JRXmlLoader.load(this.getClass().getResourceAsStream("/lk/ijse/dep/report/POSBill.jrxml"));
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("orderID",txtSearch.getText());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, (Connection) session);
            JasperViewer.viewReport(jasperPrint,false);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
