package lk.ijse.dep.dao;

import lk.ijse.dep.entity.SuperEntity;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class CrudDAOImpl<T extends SuperEntity,ID extends Serializable> implements CrudDAO<T,ID> {
    protected Session session;
    private Class<T> entity;

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    protected CrudDAOImpl(){
        entity = (Class<T>)(((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]);
    }

    @Override
    public List<T> findAll() throws Exception {
        return session.createQuery("FROM " + entity.getName()).list();
    }

    @Override
    public T find(ID pk) throws Exception {
        return (T) session.get(entity,pk);
    }

    @Override
    public void save(T entity) throws Exception {
        session.save(entity);
    }

    @Override
    public void update(T entity) throws Exception {
        session.update(entity);
    }

    @Override
    public void delete(ID pk) throws Exception {
        session.delete(session.load(entity,pk));
    }
}
