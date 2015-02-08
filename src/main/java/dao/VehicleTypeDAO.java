package dao;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import model.VehicleType;


@Stateless
public class VehicleTypeDAO extends GenericDAO<VehicleType, Integer> implements Serializable {

    private static final long serialVersionUID = 1695106268683077170L;

    public VehicleTypeDAO() {
	super(VehicleType.class);
    }
    
    public List<VehicleType> getAll() {
	TypedQuery<VehicleType> query = em.createNamedQuery("VehicleType.getAll",
		VehicleType.class);
	return query.getResultList();
    }

}
