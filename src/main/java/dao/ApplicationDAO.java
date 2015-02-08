package dao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import model.Application;
import model.ApplicationStatus;
import model.Client;
import model.Vehicle;

import org.primefaces.model.SortOrder;


@Stateless
public class ApplicationDAO extends GenericDAO<Application, Integer> {

    public ApplicationDAO() {
        super(Application.class);
    }

    public List<Application> getAll() {
        TypedQuery<Application> query =
                em.createNamedQuery("Application.getAll", Application.class);
        return query.getResultList();
    }

    public List<Application> getClientApplications(Client client) {
        TypedQuery<Application> query =
                em.createNamedQuery("Application.getByClient", Application.class);
        return query.setParameter("client", client).getResultList();
    }

    public List<Application> getApplicationPeriodsByVehicle(Vehicle vehicle) {

        //query text
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT apl FROM Application AS apl ");
        sb.append("WHERE apl.vehicle = :vehicle ");
        sb.append("AND apl.endDate >= CURRENT_DATE ");
        sb.append("AND apl.applicationStatus IN ('New', 'Accepted', 'Closed') ");
        sb.append("ORDER BY apl.startDate ASC");

        TypedQuery<Application> query =
                em.createQuery(sb.toString(), Application.class);
        return query.setParameter("vehicle", vehicle).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getIndicatorsByOptions(int param) {

        String option = null;
        switch (param) {
            case 1:
                option = "vehicleClass";
                break;
            case 2:
                option = "vehicleType";
                break;
            case 3:
                option = "gearbox";
                break;
            case 4:
                option = "drive";
                break;
            case 5:
                option = "ac";
                break;
            case 6:
                option = "cruiseControl";
                break;
            case 7:
                option = "cabriolet";
                break;
            default:
                option = "drive";
                break;
        }

        //query text
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) AS quantity, ");
        sb.append("SUM(apl.amount) AS amount, ");
        sb.append("veh." + option);
        sb.append(" FROM Application AS apl, Vehicle AS veh ");
        sb.append("WHERE apl.vehicle = veh ");
        sb.append("GROUP BY veh." + option);

        Query query = em.createQuery(sb.toString());

        return query.getResultList();
    }

    public Long getApplicationsQuantityInPeriod(Vehicle vehicle, Date begin, Date end) {

        //query text
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) AS apps ");
        sb.append("FROM Application AS application ");
        sb.append("WHERE application.vehicle = :vehicle AND ");
        sb.append("application.applicationStatus IN ('New', 'Accepted', 'Closed') AND ");
        sb.append("(application.startDate BETWEEN :begin AND :end OR ");
        sb.append("application.endDate BETWEEN :begin AND :end OR ");
        sb.append(":begin BETWEEN application.startDate AND application.endDate OR ");
        sb.append(":end BETWEEN application.startDate AND application.endDate)");

        TypedQuery<Long> query =
                em.createQuery(sb.toString(), Long.class);
        query.setParameter("vehicle", vehicle).
                setParameter("begin", begin).setParameter("end", end);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return 0l;
        }

    }

    public List<Application> getByConditions(int first, int pageSize, String sortField,
                                             SortOrder sortOrder, Map<String, String> filters) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Application> cq = cb.createQuery(Application.class);

        //SELECT application FROM
        Root<Application> application = cq.from(Application.class);

        //sorting (ORDER BY)
        if (sortField != null) {
            //if sortFiled is a property of property
            Path<Object> pathOrder = null;
            if (sortField.equals("client.lastName")) {
                pathOrder = application.get("client").get("lastName");
            } else if (sortField.equals("client.firstName")) {
                pathOrder = application.get("client").get("firstName");
            } else if (sortField.equals("vehicle.name")) {
                pathOrder = application.get("vehicle").get("name");
            } else {
                //sortFiled is just an application property
                pathOrder = application.get(sortField);
            }
            if (sortOrder == SortOrder.ASCENDING) {
                cq.orderBy(cb.asc(pathOrder));
            } else if (sortOrder == SortOrder.DESCENDING) {
                cq.orderBy(cb.desc(pathOrder));
            }
        } else {
            //default sorting is by status and id descending
            List<Order> defaultSorting = new ArrayList<>();
            defaultSorting.add(cb.desc(application.get("applicationStatus")));
            defaultSorting.add(cb.desc(application.get("id")));
            cq.orderBy(defaultSorting);
        }

        //filter (WHERE)
        Predicate filterCondition = cb.conjunction();
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            if (filter.getValue().isEmpty()) {
                continue;
            }

            //getting key class name
            String key = filter.getKey();
            String fieldClass;
            Path<String> pathFilter = null;
            //if filterKey is a property of property
            if (key.equals("client.lastName")) {
                fieldClass = "String";
                pathFilter = application.get("client").get("lastName");
            } else if (key.equals("client.firstName")) {
                fieldClass = "String";
                pathFilter = application.get("client").get("firstName");
            } else if (key.equals("vehicle.name")) {
                fieldClass = "String";
                pathFilter = application.get("vehicle").get("name");
            } else {
                //filterKey is just an application property
                try {
                    fieldClass = Application.class.getDeclaredField(
                            filter.getKey()).getType().getSimpleName();
                } catch (NoSuchFieldException | SecurityException e) {
                    continue;
                }
            }

            //setting condition depending on class name
            switch (fieldClass) {
                case "String":
                    //for String conditions is always 'LIKE' % val %
                    filterCondition = cb.and(filterCondition,
                            cb.like(pathFilter, "%" + filter.getValue() + "%"));
                    break;
                case "BigDecimal":
                    //for BigDecimal condition is 'le' - less than or equal
                    Expression<BigDecimal> expr1 = application.get(filter.getKey());
                    BigDecimal expr2 = (BigDecimal) getConditionValue(fieldClass, filter.getValue());
                    filterCondition = cb.and(filterCondition, cb.le(expr1, expr2));
                    break;
                default:
                    //for other classes condition is always 'EQUAL'
                    filterCondition = cb.and(filterCondition,
                            cb.equal(application.get(filter.getKey()),
                                    getConditionValue(fieldClass, filter.getValue())));
                    break;
            }
        }
        cq.where(filterCondition);

        TypedQuery<Application> tq = em.createQuery(cq);

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
            case "ApplicationStatus":
                return ApplicationStatus.valueOf(value);
            case "boolean":
                return Boolean.valueOf(value);
            case "Integer":
                return Integer.valueOf(value);
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
            case "Date":
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    return sdf.parse(value);
                } catch (ParseException e) {
                    return new Date();
                }
            default:
                return value;
        }
    }

}
