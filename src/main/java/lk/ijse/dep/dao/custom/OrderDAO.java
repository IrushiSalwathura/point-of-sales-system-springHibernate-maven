package lk.ijse.dep.dao.custom;

import lk.ijse.dep.dao.CrudDAO;
import lk.ijse.dep.entity.Order;

public interface OrderDAO extends CrudDAO<Order,String> {
    String getLastOrderId() throws Exception;
}
