package lk.ijse.dep.dao;

import org.hibernate.Session;

import java.io.Serializable;

public interface SuperDAO extends Serializable {
    void setSession(Session session);
}
