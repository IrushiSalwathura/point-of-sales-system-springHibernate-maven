package lk.ijse.dep.dao.custom.impl;

import lk.ijse.dep.dao.CrudDAOImpl;
import lk.ijse.dep.dao.custom.OrderDAO;
import lk.ijse.dep.entity.Order;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl extends CrudDAOImpl<Order,String> implements OrderDAO {
    public String getLastOrderId() throws Exception {
        return (String) session.createNativeQuery("SELECT id FROM `Order` ORDER BY id DESC")
                .setMaxResults(1).list().get(0);
    }

}
