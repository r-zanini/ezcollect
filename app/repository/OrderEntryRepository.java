package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.OrderData;
import models.OrderEntry;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class OrderEntryRepository {
	private final EbeanServer ebeanServer;
	private final DatabaseExecutionContext executionContext;

	@Inject
	public OrderEntryRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
		this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
		this.executionContext = executionContext;
	}

	public CompletionStage<Optional<OrderEntry>> find(String id) {
		return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(OrderEntry.class).setId(id).findOne()), executionContext);
	}

	public CompletionStage<List<OrderEntry>> listByOrder(String orderId) {
		return supplyAsync(() ->
				ebeanServer.find(OrderEntry.class).where().eq("order_order_id", orderId)
						.findList(), executionContext);
	}

	public CompletionStage<List<OrderEntry>> listBy(String propertyName, String filter) {
		return supplyAsync(() ->
				ebeanServer.find(OrderEntry.class).where()
						.ilike(propertyName, "%" + filter + "%")
						.findList(), executionContext);
	}

	public CompletionStage<List<OrderEntry>> listAll() {
		return supplyAsync(() ->
				ebeanServer.find(OrderEntry.class).findList(), executionContext);
	}

	public CompletionStage<List<OrderEntry>> listByEmail(String filter) {
		return supplyAsync(() ->
				ebeanServer.find(OrderEntry.class).where()
						.ilike("customerEmailAddress", "%" + filter + "%")
						.findList(), executionContext);
	}
}
