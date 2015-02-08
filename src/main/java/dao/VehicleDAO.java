package dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import model.Vehicle;
import model.VehicleClass;

import org.primefaces.model.SortOrder;


@Stateless
public class VehicleDAO extends GenericDAO<Vehicle, Integer> {
    
    @Inject
    private VehicleTypeDAO vehicleTypeDAO; 
    
    
    public VehicleDAO() {
	super(Vehicle.class);
    }
    
    
    public List<Vehicle> getAll() {
	TypedQuery<Vehicle> query = 
		em.createNamedQuery("Vehicle.getAll", Vehicle.class);
	return query.getResultList();
    }
    
    public List<Vehicle> getAllNotDisabled() {
	TypedQuery<Vehicle> query = 
		em.createNamedQuery("Vehicle.getAllNotDisabled", Vehicle.class);
	return query.getResultList();
    }
    
    //not used yet. see getByConditions
    public List<Vehicle> getByConditions_Criteria(int first, int pageSize, String sortField, 
	    SortOrder sortOrder, Map<String, String> filters, boolean clientMode) {

	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<Vehicle> cq = cb.createQuery(Vehicle.class);
	
	//SELECT vehicle FROM
	Root<Vehicle> vehicle = cq.from(Vehicle.class);
	//sorting (ORDER BY)
	if (sortField != null) {
	    if (sortOrder == SortOrder.ASCENDING) {
		cq.orderBy(cb.asc(vehicle.get(sortField)));
	    } else if (sortOrder == SortOrder.DESCENDING) {
		cq.orderBy(cb.desc(vehicle.get(sortField)));
	    }
	}
	
	//filter (WHERE)
	Predicate filterCondition = cb.conjunction();
	for (Map.Entry<String, String> filter : filters.entrySet()) {
	    if (filter.getValue().isEmpty()) {
		continue;
	    }

	    //getting key class name
	    String fieldClass;
	    try {
		fieldClass = Vehicle.class.getDeclaredField(
			filter.getKey()).getType().getSimpleName();
	    } catch (NoSuchFieldException | SecurityException e) {
		continue;
	    }
	    
	    //setting condition depending on class name
	    switch (fieldClass) {
	    case "String":
		//for String conditions is always 'LIKE'
		Path<String> pathFilter = vehicle.get(filter.getKey());
		filterCondition = cb.and(filterCondition,
			cb.like(pathFilter, "%" + filter.getValue() + "%"));
		break;
	    case "BigDecimal":
		//for BigDecimal condition is 'le' - less or equal
		Expression<BigDecimal> expr1 = vehicle.get(filter.getKey());
		BigDecimal expr2 = (BigDecimal) getConditionValue(fieldClass, filter.getValue());		
		filterCondition = cb.and(filterCondition, cb.le(expr1, expr2));
		break;
	    default:
		//for other classes condition is always 'EQUAL'
		filterCondition = cb.and(filterCondition,
			cb.equal(vehicle.get(filter.getKey()),
			getConditionValue(fieldClass, filter.getValue())));
		break;
	    }	    
	}
	
	//when in client mode: one filter is always active - show disabled = false
	if (clientMode) {
	    filterCondition = cb.and(filterCondition,
		    cb.equal(vehicle.get("disabled"),
			    getConditionValue("boolean", "false")));
	}
	    
	cq.where(filterCondition);

	TypedQuery<Vehicle> tq = em.createQuery(cq);
	
	//pagination
	if (pageSize >= 0) {
	    tq.setMaxResults(pageSize);
	}
	if (first >= 0) {
	    tq.setFirstResult(first);
	} 
	
	return tq.getResultList();
	
    }
    
    private Object getConditionValue(String fieldClass, String value) {
	switch (fieldClass) {
	    case "VehicleClass":
		return VehicleClass.valueOf(value);
	    case "VehicleType":
		return vehicleTypeDAO.getById(Integer.valueOf(value));
	    case "boolean":
		return Boolean.valueOf(value);
	    case "short":
		try {
		    return Short.valueOf(value);
		} catch (NumberFormatException e) {
		    return Short.MIN_VALUE;
		}
	    case "BigDecimal":
		try {
		    return BigDecimal.valueOf(Double.valueOf(value));
		} catch (NumberFormatException e) {
		    return BigDecimal.ZERO;
		}
	    default:
		return value;
	}
    }
    
    public List<Vehicle> getByConditions(int first, int pageSize, String sortField, 
	    SortOrder sortOrder, Map<String, String> filters,
	    Date startDate, Date endDate, boolean clientMode) {
	
	//query text
	StringBuilder sb = new StringBuilder();
	
	//SELECT FROM
	sb.append("SELECT vehicle ");
	sb.append("FROM Vehicle AS vehicle ");
	
	
	//WHERE
	boolean addAND = false;
	if (filters.size() > 0 || clientMode || startDate != null || endDate != null) { 
	    sb.append("WHERE ");
	}
	for (Map.Entry<String, String> filter : filters.entrySet()) {
	    if (filter.getValue().isEmpty()) {
		continue;
	    }
	    
	    if (addAND) {
		sb.append("AND ");
		addAND = false;
	    }
	    
	    //getting key class name
	    String fieldClass;
	    try {
		fieldClass = Vehicle.class.getDeclaredField(
			filter.getKey()).getType().getSimpleName();
	    } catch (NoSuchFieldException | SecurityException e) {
		continue;
	    }
	    
	    //setting condition depending on class name
	    switch (fieldClass) {
	    case "String":
		//for String condition is always 'LIKE'
		sb.append("vehicle." + filter.getKey() + " LIKE '%" + filter.getValue() + "%' ");
		addAND = true;
		break;
	    case "BigDecimal":
		//for BigDecimal condition is less or equal
		sb.append("vehicle." + filter.getKey() + " <= " + filter.getValue() + " ");
		addAND = true;
		break;
	    case "boolean":
		//for boolean condition is TRUE or FALSE
		sb.append("vehicle." + filter.getKey() + " = " + Boolean.valueOf(filter.getValue()) + " ");
		addAND = true;
		break;
	    default:
		//for other classes condition is always 'EQUAL'
		sb.append("vehicle." + filter.getKey() + " = '" + filter.getValue() + "' ");
		addAND = true;
		break;
	    }	    
	}
	
	//when in client mode: one filter is always active - show disabled = false
	if (clientMode) {
	    if (addAND) {
		sb.append("AND ");
	    }
	    sb.append("vehicle.disabled = FALSE ");
	    addAND = true;
	}
	
	
	//sub-query to applications to get vehicles which are not reserved in needed period
	if (startDate != null || endDate != null) { 
	    if (addAND) {
		sb.append("AND ");
	    }
	    if (startDate != null && endDate == null) {
		endDate = startDate;
	    } else if (startDate == null && endDate != null) {
		startDate = endDate;
	    }
	    sb.append("(SELECT COUNT(*) AS apps ");
	    sb.append("FROM Application AS application ");
	    sb.append("WHERE application.vehicle = vehicle AND ");
	    sb.append("application.applicationStatus IN ('New', 'Accepted', 'Closed') AND ");
	    sb.append("(application.startDate BETWEEN :begin AND :end OR ");
	    sb.append("application.endDate BETWEEN :begin AND :end OR ");
	    sb.append(":begin BETWEEN application.startDate AND application.endDate OR ");
	    sb.append(":end BETWEEN application.startDate AND application.endDate)");
	    sb.append(") = 0 ");
	}
	
	//ORDER BY
	if (sortField != null) {
	    sb.append("ORDER BY vehicle." + sortField);
	    if (sortOrder == SortOrder.ASCENDING) {
		sb.append(" ASC");
	    } else if (sortOrder == SortOrder.DESCENDING) {
		sb.append(" DESC");
	    }
	}
			
	//creating query
	TypedQuery<Vehicle> query = 
		em.createQuery(sb.toString(), Vehicle.class);
	
	//setting parameters
	if (startDate != null || endDate != null) {
	    query.setParameter("begin", startDate).setParameter("end", endDate);
	}
	
	//pagination
	if (pageSize >= 0) {
	    query.setMaxResults(pageSize);
	}
	if (first >= 0) {
	    query.setFirstResult(first);
	}
	
	return query.getResultList();

    }
    
}
