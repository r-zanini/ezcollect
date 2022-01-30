package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.User;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class UserRepository {
	private final EbeanServer ebeanServer;
	private final DatabaseExecutionContext executionContext;

	@Inject
	public UserRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
		this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
		this.executionContext = executionContext;
	}

	public CompletionStage<Optional<User>> find(String id) {
		return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(User.class).setId(id).findOne()), executionContext);
	}
}
