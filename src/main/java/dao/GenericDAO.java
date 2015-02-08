package dao;

import javax.persistence.EntityManager;


public abstract class GenericDAO<T extends GetIdAble<PK>, PK> {

    private Class<T> type;
    protected EntityManager em;

    public GenericDAO(Class<T> type) {
        this.type = type;
        em = EMF.createEntityManager();
    }

    public PK save(T newInstance) {
        em.persist(newInstance);
        return (PK) newInstance.getId();
    }

    public T getById(PK id) {
        T instance = em.find(type, id);
        return instance;
    }

    public void update(T instance) {
        em.merge(instance);
    }

    public void delete(T instance) {
        if (!em.contains(instance)) {
            instance = em.merge(instance);
        }
        em.remove(instance);
    }

}
