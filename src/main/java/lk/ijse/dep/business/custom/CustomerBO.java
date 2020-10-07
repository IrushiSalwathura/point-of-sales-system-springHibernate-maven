package lk.ijse.dep.business.custom;

import lk.ijse.dep.business.SuperBO;
import lk.ijse.dep.util.CustomerTM;

import java.util.List;

public interface CustomerBO extends SuperBO {
    String getNewCustomerId() throws Exception;
    List<CustomerTM> getAllCustomers() throws Exception;
    void saveCustomer(String id, String name, String address) throws Exception;
    void updateCustomer(String name, String address, String id) throws Exception;
    void deleteCustomer(String customerId) throws Exception;
}
