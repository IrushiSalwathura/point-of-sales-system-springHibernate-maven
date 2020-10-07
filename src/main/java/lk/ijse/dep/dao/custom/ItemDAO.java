package lk.ijse.dep.dao.custom;

import lk.ijse.dep.dao.CrudDAO;
import lk.ijse.dep.entity.Item;

public interface ItemDAO extends CrudDAO<Item,String> {
    String getLastItemCode() throws Exception;
}
