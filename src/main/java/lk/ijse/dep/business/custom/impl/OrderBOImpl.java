package lk.ijse.dep.business.custom.impl;

import lk.ijse.dep.business.custom.OrderBO;
import lk.ijse.dep.dao.custom.*;
import lk.ijse.dep.db.HibernateUtil;
import lk.ijse.dep.entity.CustomEntity;
import lk.ijse.dep.entity.Item;
import lk.ijse.dep.entity.Order;
import lk.ijse.dep.entity.OrderDetail;
import lk.ijse.dep.util.OrderDetailTM;
import lk.ijse.dep.util.OrderTM;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component
public class OrderBOImpl implements OrderBO {
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private OrderDetailDAO orderDetailDAO;
    @Autowired
    private ItemDAO itemDAO;
    @Autowired
    private QueryDAO queryDAO;
    @Autowired
    private CustomerDAO customerDAO;

    public void placeOrder(OrderTM order, List<OrderDetailTM> orderDetails) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        orderDAO.setSession(session);
        orderDetailDAO.setSession(session);
        itemDAO.setSession(session);
        customerDAO.setSession(session);
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            orderDAO.save(new Order(order.getOrderId(), order.getOrderDate(), customerDAO.find(order.getCustomerId())));
            for (OrderDetailTM orderDetail : orderDetails) {
                orderDetailDAO.save(new OrderDetail(order.getOrderId(), orderDetail.getItemCode(), orderDetail.getQty(), BigDecimal.valueOf(orderDetail.getUnitPrice())));
                Object i = itemDAO.find(orderDetail.getItemCode());
                Item item = (Item) i;
                item.setQtyOnHand(item.getQtyOnHand() - orderDetail.getQty());
                itemDAO.update(item);
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }


    public String getNewOrderId() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        orderDAO.setSession(session);
        String lastOrderId;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            lastOrderId = orderDAO.getLastOrderId();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally{
            session.close();
        }
            if (lastOrderId == null){
                return "OD001";
            }else{
                int maxId=  Integer.parseInt(lastOrderId.replace("OD",""));
                maxId = maxId + 1;
                String id = "";
                if (maxId < 10) {
                    id = "OD00" + maxId;
                } else if (maxId < 100) {
                    id = "OD0" + maxId;
                } else {
                    id = "OD" + maxId;
                }
                return id;
            }
    }

    public List<OrderTM> searchOrder() throws Exception{
            Session session = HibernateUtil.getSessionFactory().openSession();
            queryDAO.setSession(session);
            List<CustomEntity> searchOrders = null;
            Transaction tx = null;

            try{
                tx = session.beginTransaction();
                searchOrders =  queryDAO.searchOrder();
                tx.commit();
            }catch(Throwable t){
                tx.rollback();
                throw t;
            }
            List<OrderTM> allOrders = new ArrayList<>();
            for (CustomEntity searchOrder : searchOrders) {
                allOrders.add(new OrderTM(searchOrder.getOrderId(),searchOrder.getOrderDate(),
                        searchOrder.getCustomerName()
                        ,searchOrder.getCustomerId(),searchOrder.getTotal()));
            }
            return allOrders;

    }
}
