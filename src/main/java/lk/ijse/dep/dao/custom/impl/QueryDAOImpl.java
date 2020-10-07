package lk.ijse.dep.dao.custom.impl;

import lk.ijse.dep.dao.custom.QueryDAO;
import lk.ijse.dep.entity.CustomEntity;
import org.hibernate.Session;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class QueryDAOImpl implements QueryDAO {
    private Session session;

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    public List<CustomEntity> searchOrder() throws Exception {
        return (List<CustomEntity>) session.createNativeQuery("SELECT O.id, O.date, C.id, C.name, SUM(OD.qty*od.unitPrice)\n" +
                "FROM `Order` O\n" +
                "INNER JOIN Customer C ON O.customerId = C.id\n" +
                "INNER JOIN orderdetail OD ON O.id = OD.orderId\n" +
                "GROUP BY O.id").uniqueResult();
    }
}
