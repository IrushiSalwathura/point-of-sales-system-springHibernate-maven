package lk.ijse.dep.business.custom;

import lk.ijse.dep.business.SuperBO;
import lk.ijse.dep.util.OrderDetailTM;
import lk.ijse.dep.util.OrderTM;

import java.util.List;

public interface OrderBO extends SuperBO {
    void placeOrder(OrderTM order, List<OrderDetailTM> orderDetails) throws Exception;
    String getNewOrderId() throws Exception;
    List<OrderTM> searchOrder() throws Exception;
}
