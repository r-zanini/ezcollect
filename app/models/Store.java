package models;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Store extends Model {
	@Id
	public String storeId;
	public String storeAddress;
	public String storeName;
	public String storeOpeningHours;
	public String storePhoneNumber;
	public int availableEmployees;

	public Store(String storeId, String storeAddress, String storeName, String storeOpeningHours, String storePhoneNumber, int availableEmployees) {
		this.storeId = storeId;
		this.storeAddress = storeAddress;
		this.storeName = storeName;
		this.storeOpeningHours = storeOpeningHours;
		this.storePhoneNumber = storePhoneNumber;
		this.availableEmployees = availableEmployees;
	}

	public void update(Store newStore) {
		this.storeId = newStore.storeId;
		this.storeAddress = newStore.storeAddress;
		this.storeName = newStore.storeName;
		this.storeOpeningHours = newStore.storeOpeningHours;
		this.storePhoneNumber = newStore.storePhoneNumber;
		this.availableEmployees = newStore.availableEmployees;
		update();
	}

	@Override
	public String toString() {
		return "Store{" +
				"storeId='" + storeId + '\'' +
				", storeAddress='" + storeAddress + '\'' +
				", storeName='" + storeName + '\'' +
				", storeOpeningHours='" + storeOpeningHours + '\'' +
				", storePhoneNumber='" + storePhoneNumber + '\'' +
				", availableEmployees=" + availableEmployees +
				'}';
	}
}
