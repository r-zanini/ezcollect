package models;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class StorePickupLocation extends Model {
	@Id
	public String locationId;
	@ManyToOne(optional = false)
	public Store store;
	public String id;
	public String name;
	public String parcelSize;

	public StorePickupLocation(Store store, String id, String name, String parcelSize) {
		this.store = store;
		this.id = id;
		this.name = name;
		this.parcelSize = parcelSize;
		locationId = store.storeId + ";" + id;
	}

	public void update(StorePickupLocation newLocation) {
		this.name = name;
		this.parcelSize = parcelSize;
		update();
	}
}
