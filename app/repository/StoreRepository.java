package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.OrderData;
import models.Store;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class StoreRepository {
	private final EbeanServer ebeanServer;
	private final DatabaseExecutionContext executionContext;

	@Inject
	public StoreRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
		this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
		this.executionContext = executionContext;
	}

	public CompletionStage<Optional<Store>> find(String id) {
		return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(Store.class).setId(id).findOne()), executionContext);
	}

	public CompletionStage<List<Store>> listAll() {
		return supplyAsync(() ->
				ebeanServer.find(Store.class).findList(), executionContext);
	}
}
