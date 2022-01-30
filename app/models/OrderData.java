package models;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class OrderData extends Model {
	@Id
	public String orderId;
	public String customerAddress;
	public String customerEmailAddress;
	public String customerName;
	public String customerPhoneNumber;
	public String parcelSize;
	public int preparationTime;
	@OneToMany(mappedBy = "order")
	List<OrderEntry> orderEntries;
	public LocalDateTime fulfilledAt = null;
	public boolean isBeingProcessed;
	public LocalDateTime pickupTime;
	@ManyToOne
	public Store pickupStore;

	public OrderData(String orderId, String customerAddress, String customerEmailAddress, String customerName, String customerPhoneNumber, String parcelSize, int preparationTime) {
		this.orderId = orderId;
		this.customerAddress = customerAddress;
		this.customerEmailAddress = customerEmailAddress;
		this.customerName = customerName;
		this.customerPhoneNumber = customerPhoneNumber;
		this.parcelSize = parcelSize;
		this.preparationTime = preparationTime;
	}

	public OrderData() {
		this.orderId = "null";
	}

	public void update(OrderData newData) {
		this.customerAddress = newData.customerAddress;
		this.customerEmailAddress = newData.customerEmailAddress;
		this.customerName = newData.customerName;
		this.customerPhoneNumber = newData.customerPhoneNumber;
		this.parcelSize = newData.parcelSize;
		this.preparationTime = newData.preparationTime;
		update();
	}

	public boolean fulfill(){
		if (fulfilledAt == null) {
			fulfilledAt = LocalDateTime.now();
			update();
			return true;
		}
		return false;
	}

	public boolean toggleProcessing() {
		if (fulfilledAt == null) {
			isBeingProcessed = !isBeingProcessed;
			update();
			return true;
		}
		return false;
	}

	public void schedulePickup(LocalDateTime pickupTime, Store pickupStore) {
		this.pickupTime = pickupTime;
		this.pickupStore = pickupStore;
		update();
	}

	@Override
	public String toString() {
		return "OrderData{" +
				"orderId='" + orderId + '\'' +
				", customerAddress='" + customerAddress + '\'' +
				", customerEmailAddress='" + customerEmailAddress + '\'' +
				", customerName='" + customerName + '\'' +
				", customerPhoneNumber='" + customerPhoneNumber + '\'' +
				", parcelSize='" + parcelSize + '\'' +
				", preparationTime=" + preparationTime +
				'}';
	}
}
