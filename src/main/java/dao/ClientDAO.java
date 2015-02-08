package dao;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import model.Client;
import model.User;

import org.primefaces.model.SortOrder;


@Stateless
public class ClientDAO extends GenericDAO<Client, Integer> {

    public ClientDAO() {
        super(Client.class);
    }

    public List<Client> getAll() {
        TypedQuery<Client> query = em.createNamedQuery("Client.getAll",
                Client.class);
        return query.getResultList();
    }

    public Client getClientByEmail(String email) {
        TypedQuery<Client> query = em.createNamedQuery("Client.getByEmail",
                Client.class);
        try {
            return query.setParameter("email", email).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Client getClientByITIN(String ITIN) {
        TypedQuery<Client> query = em.createNamedQuery("Client.getByITIN",
                Client.class);
        try {
            return query.setParameter("ITIN", ITIN).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Client getClientByUser(User user) {
        TypedQuery<Client> query = em.createNamedQuery("Client.getByUser",
                Client.class);
        try {
            return query.setParameter("user", user).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Client> getByConditions(int first, int pageSize, String sortField,
                                        SortOrder sortOrder, Map<String, String> filters) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);

        //SELECT client FROM
        Root<Client> client = cq.from(Client.class);

        //sorting (ORDER BY)
        if (sortField != null) {
            if (sortOrder == SortOrder.ASCENDING) {
                cq.orderBy(cb.asc(client.get(sortField)));
            } else if (sortOrder == SortOrder.DESCENDING) {
                cq.orderBy(cb.desc(client.get(sortField)));
            }
        }

        //filter (WHERE)
        Predicate filterCondition = cb.conjunction();
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            if (filter.getValue().isEmpty()) {
                continue;
            }

            //for String conditions is always 'LIKE'
            Path<String> pathFilter = client.get(filter.getKey());
            filterCondition = cb.and(filterCondition,
                    cb.like(pathFilter, "%" + filter.getValue() + "%"));
        }

        cq.where(filterCondition);

        TypedQuery<Client> tq = em.createQuery(cq);

        //pagination
        if (pageSize >= 0) {
            tq.setMaxResults(pageSize);
        }
        if (first >= 0) {
            tq.setFirstResult(first);
        }

        return tq.getResultList();

    }

}
