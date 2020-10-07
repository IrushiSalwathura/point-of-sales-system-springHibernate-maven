package lk.ijse.dep.business.custom.impl;

import lk.ijse.dep.business.custom.ItemBO;
import lk.ijse.dep.dao.DAOFactory;
import lk.ijse.dep.dao.DAOType;
import lk.ijse.dep.dao.custom.ItemDAO;
import lk.ijse.dep.db.HibernateUtil;
import lk.ijse.dep.entity.Customer;
import lk.ijse.dep.entity.Item;
import lk.ijse.dep.util.ItemTM;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ItemBOImpl implements ItemBO {
    private static ItemDAO itemDAO = DAOFactory.getInstance().getDAO(DAOType.ITEM);

    public String getNewItemCode() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        itemDAO.setSession(session);
        String lastItemCode;
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            lastItemCode = itemDAO.getLastItemCode();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally{
            session.close();
        }
        if(lastItemCode == null){
            return "I001";
        }else{
            int maxId=  Integer.parseInt(lastItemCode.replace("I",""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "I00" + maxId;
            } else if (maxId < 100) {
                id = "I0" + maxId;
            } else {
                id = "I" + maxId;
            }
            return id;
        }
    }

    public List<ItemTM> getAllItems() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        itemDAO.setSession(session);
        List<Item> allItems = null;
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            allItems = itemDAO.findAll();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }

        ArrayList<ItemTM> items = new ArrayList<>();
        for (Item item : allItems) {
            items.add(new ItemTM(item.getCode(),item.getDescription(),item.getUnitPrice().doubleValue(),item.getQtyOnHand()));
        }
        return items;
    }

    public void saveItem(String code, String description, double unitPrice, int qtyOnHand) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        itemDAO.setSession(session);
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            itemDAO.save(new Item(code,description, BigDecimal.valueOf(unitPrice),qtyOnHand));
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }
    }

    public void updateItem(String description, double unitPrice, int qtyOnHand, String code) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        itemDAO.setSession(session);
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            itemDAO.update(new Item(code,description,BigDecimal.valueOf(unitPrice),qtyOnHand));
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }
    }

    public void deleteItem(String itemCode) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        itemDAO.setSession(session);
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            itemDAO.delete(itemCode);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }
    }
}
