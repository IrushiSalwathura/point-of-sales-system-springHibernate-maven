package lk.ijse.dep.business.custom.impl;

import lk.ijse.dep.business.custom.CustomerBO;
import lk.ijse.dep.dao.DAOFactory;
import lk.ijse.dep.dao.DAOType;
import lk.ijse.dep.dao.custom.CustomerDAO;
import lk.ijse.dep.db.HibernateUtil;
import lk.ijse.dep.entity.Customer;
import lk.ijse.dep.util.CustomerTM;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class CustomerBOImpl implements CustomerBO {
    private static CustomerDAO customerDAO = DAOFactory.getInstance().getDAO(DAOType.CUSTOMER);
    public String getNewCustomerId() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        customerDAO.setSession(session);
        String lastCustomerId;
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            lastCustomerId = customerDAO.getLastCustomerId();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally{
            session.close();
        }
        if (lastCustomerId == null) {
                return "C001";
            } else {
                int maxId = Integer.parseInt(lastCustomerId.replace("C", ""));
                maxId = maxId + 1;
                String id = "";
                if (maxId < 10) {
                    id = "C00" + maxId;
                } else if (maxId < 100) {
                    id = "C0" + maxId;
                } else {
                    id = "C" + maxId;
                }
                return id;
            }
    }

    public List<CustomerTM> getAllCustomers() throws Exception{
        Session session = HibernateUtil.getSessionFactory().openSession();
        customerDAO.setSession(session);
        List<Customer> allCustomers = null;
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            allCustomers = customerDAO.findAll();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }
        ArrayList<CustomerTM> customers = new ArrayList<>();
            for (Customer customer : allCustomers) {
                customers.add(new CustomerTM(customer.getId(),customer.getName(),customer.getAddress()));
            }
            return customers;

    }

    public void saveCustomer(String id, String name, String address) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        customerDAO.setSession(session);
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            customerDAO.save(new Customer(id,name,address));
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }

    }

    public void updateCustomer(String name, String address, String id) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        customerDAO.setSession(session);
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            customerDAO.update(new Customer(id,name,address));
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }
    }

    public void deleteCustomer(String customerId) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        customerDAO.setSession(session);
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            customerDAO.delete(customerId);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }
    }
}
