package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.OrderData;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class OrderRepository {
	private final EbeanServer ebeanServer;
	private final DatabaseExecutionContext executionContext;

	@Inject
	public OrderRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
		this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
		this.executionContext = executionContext;
	}

	public CompletionStage<Optional<OrderData>> find(String id) {
		return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(OrderData.class).setId(id).findOne()), executionContext);
	}

	public CompletionStage<List<OrderData>> listBy(String propertyName, String filter) {
		return supplyAsync(() ->
				ebeanServer.find(OrderData.class).where()
						.ilike(propertyName, "%" + filter + "%")
						.findList(), executionContext);
	}

	public CompletionStage<List<OrderData>> listAll() {
		return supplyAsync(() ->
				ebeanServer.find(OrderData.class).findList(), executionContext);
	}

	public CompletionStage<List<OrderData>> listByEmail(String filter) {
		return supplyAsync(() ->
				ebeanServer.find(OrderData.class).where()
						.ilike("customerEmailAddress", "%" + filter + "%")
						.findList(), executionContext);
	}
}
