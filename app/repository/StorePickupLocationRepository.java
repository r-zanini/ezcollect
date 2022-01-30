package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.StorePickupLocation;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class StorePickupLocationRepository {
	private final EbeanServer ebeanServer;
	private final DatabaseExecutionContext executionContext;

	@Inject
	public StorePickupLocationRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
		this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
		this.executionContext = executionContext;
	}

	public CompletionStage<Optional<StorePickupLocation>> find(String id) {
		return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(StorePickupLocation.class).setId(id).findOne()), executionContext);
	}

	public CompletionStage<List<StorePickupLocation>> listAll() {
		return supplyAsync(() ->
				ebeanServer.find(StorePickupLocation.class).findList(), executionContext);
	}

	public CompletionStage<List<StorePickupLocation>> listFromStore(String storeId) {
		return supplyAsync(() ->
				ebeanServer.find(StorePickupLocation.class).where().ieq("store_store_id",storeId).findList(), executionContext);
	}
}
