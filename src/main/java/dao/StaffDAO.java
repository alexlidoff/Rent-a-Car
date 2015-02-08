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

import model.Staff;
import model.User;

import org.primefaces.model.SortOrder;


@Stateless
public class StaffDAO extends GenericDAO<Staff, Integer> {
    
    public StaffDAO() {
	super(Staff.class);
    }
    
    public List<Staff> getAll() {
	TypedQuery<Staff> query = em.createNamedQuery("Staff.getAll",
		Staff.class);
	return query.getResultList();
    }
    
    public Staff getStaffByEmail(String email) {
	TypedQuery<Staff> query = em.createNamedQuery("Staff.getByEmail",
		Staff.class);
	try {
	    return query.setParameter("email", email).getSingleResult();
	} catch (Exception e) {
	    return null;
	}
    }
    
    public Staff getStaffByUser(User user) {
	TypedQuery<Staff> query = em.createNamedQuery("Staff.getByUser",
		Staff.class);
	try {
	    return query.setParameter("user", user).getSingleResult();
	} catch (Exception e) {
	    return null;
	}
    }
    
    public List<Staff> getByConditions(int first, int pageSize, String sortField, 
	    SortOrder sortOrder, Map<String, String> filters) {
	
	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<Staff> cq = cb.createQuery(Staff.class);
	
	//SELECT staff FROM
	Root<Staff> staff = cq.from(Staff.class);
	
	//sorting (ORDER BY)
	if (sortField != null) {
	    if (sortOrder == SortOrder.ASCENDING) {
		cq.orderBy(cb.asc(staff.get(sortField)));
	    } else if (sortOrder == SortOrder.DESCENDING) {
		cq.orderBy(cb.desc(staff.get(sortField)));
	    }
	}
	
	//filter (WHERE)
	Predicate filterCondition = cb.conjunction();
	for (Map.Entry<String, String> filter : filters.entrySet()) {
	    if (filter.getValue().isEmpty()) {
		continue;
	    }

	    //for String conditions is always 'LIKE'
	    Path<String> pathFilter = staff.get(filter.getKey());
	    filterCondition = cb.and(filterCondition,
		cb.like(pathFilter, "%" + filter.getValue() + "%"));
	}
	
	cq.where(filterCondition);

	TypedQuery<Staff> tq = em.createQuery(cq);
	
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
