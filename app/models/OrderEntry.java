package models;


import io.ebean.Model;
import io.ebean.annotation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class OrderEntry extends Model {
	@Id
	public String entryId;
	@ManyToOne(optional = false)
	public OrderData order;
	@NotNull
	public String productId;
	@NotNull
	public String productName;
	@NotNull
	public int quantity;
	@NotNull
	public int totalEntryPrice;

	public OrderEntry(OrderData order, String productId, String productName, int quantity, int totalEntryPrice) {
		this.order = order;
		this.productId = productId;
		this.productName = productName;
		this.quantity = quantity;
		this.totalEntryPrice = totalEntryPrice;
		entryId = order.orderId + ";" + productId;
	}
}
