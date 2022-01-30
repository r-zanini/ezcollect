package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.libs.ws.*;
import play.mvc.Security;
import repository.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/**
 * Manage a database of computers
 */
public class HomeController extends Controller {
	private final HttpExecutionContext httpExecutionContext;
	private final FormFactory formFactory;
	private final MessagesApi messagesApi;
	private final WSClient ws;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final StoreRepository storeRepository;
	private final StorePickupLocationRepository storePickupLocationRepository;
	private final OrderEntryRepository orderEntryRepository;

	@Inject
	public HomeController(HttpExecutionContext httpExecutionContext,
	                      FormFactory formFactory,
	                      MessagesApi messagesApi,
	                      WSClient ws,
	                      OrderRepository orderRepository,
	                      UserRepository userRepository,
	                      StoreRepository storeRepository,
	                      StorePickupLocationRepository storePickupLocationRepository,
	                      OrderEntryRepository orderEntryRepository) {
		this.httpExecutionContext = httpExecutionContext;
		this.formFactory = formFactory;
		this.messagesApi = messagesApi;
		this.ws = ws;
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.storeRepository = storeRepository;
		this.storePickupLocationRepository = storePickupLocationRepository;
		this.orderEntryRepository = orderEntryRepository;
	}

	/**
	 * Handle default path requests, redirect to computers list
	 */
//    public CompletionStage<Result> index(Http.Request request) {
//        return listStoreOrders("", request);
//    }
	public Result index(Http.Request request) {
		return welcome(request);
	}

	private void reloadOrders(String email) throws ExecutionException, InterruptedException {
		WSRequest request;
		if (email.equals("")) {
			request = ws.url("https://sapstore.conuhacks.io/orders");
		} else {
			request = ws.url("https://sapstore.conuhacks.io/orders/byEmail?email=" + email);
		}
		CompletionStage<JsonNode> jsonNodeCompletionStage = request.get().thenApplyAsync(WSResponse::asJson);
		JsonNode jsonNode = jsonNodeCompletionStage.toCompletableFuture().get();
		for (JsonNode j : jsonNode) {
			String orderId = j.get("orderId").asText();
			String customerAddress = j.get("customerAddress").asText();
			String customerEmailAddress = j.get("customerEmailAddress").asText();
			String customerName = j.get("customerName").asText();
			String customerPhoneNumber = j.get("customerPhoneNumber").asText();
			String parcelSize = j.get("parcelSize").asText();
			int preparationTime = j.get("preparationTime").asInt();
			OrderData order = orderRepository.find(orderId).toCompletableFuture().get().orElse(null);
			OrderData newOrder = new OrderData(orderId, customerAddress, customerEmailAddress, customerName, customerPhoneNumber, parcelSize, preparationTime);
			if (order == null) {
				newOrder.save();
			} else {
				order.update(newOrder);
			}
		}
	}

	private void reloadStores() throws ExecutionException, InterruptedException {
		WSRequest request;
		request = ws.url("https://sapstore.conuhacks.io/stores");
		CompletionStage<JsonNode> jsonNodeCompletionStage = request.get().thenApplyAsync(WSResponse::asJson);
		JsonNode jsonNode = jsonNodeCompletionStage.toCompletableFuture().get();
		for (JsonNode j : jsonNode) {
			String storeId = j.get("storeId").asText();
			String storeAddress = j.get("address").asText();
			String storeName = j.get("name").asText();
			//String storeOpeningHours = j.get("openingHours").asText();
			String storeOpeningHours = "";
			String storePhoneNumber = j.get("phoneNumber").asText();
			int employeeCount = 0;
			for (JsonNode e : j.get("employees")) {
				employeeCount++;
			}
			Store store = storeRepository.find(storeId).toCompletableFuture().get().orElse(null);
			Store newStore = new Store(storeId, storeAddress, storeName, storeOpeningHours, storePhoneNumber, employeeCount);
			if (store == null) {
				newStore.save();
				for (JsonNode l : j.get("pickupLocations")) {
					StorePickupLocation location = new StorePickupLocation(newStore, l.get("id").asText(), l.get("name").asText(), l.get("parcelSize").asText());
					location.save();
				}
			} else {
				store.update(newStore);
				LinkedList<StorePickupLocation> newObjects = new LinkedList<>();
				List<StorePickupLocation> oldObjects = storePickupLocationRepository.listFromStore(storeId).toCompletableFuture().get();
				for (JsonNode l : j.get("pickupLocations")) {
					StorePickupLocation newLocation = new StorePickupLocation(store, l.get("id").asText(), l.get("name").asText(), l.get("parcelSize").asText());
					StorePickupLocation location = storePickupLocationRepository.find(newLocation.locationId).toCompletableFuture().get().orElse(null);
					if (location == null) {
						newLocation.save();
						newObjects.add(newLocation);
					} else {
						location.update(newLocation);
						newObjects.add(location);
					}
					for (StorePickupLocation toKeep : newObjects) {
						oldObjects.remove(toKeep);
					}
					for (StorePickupLocation toDelete : oldObjects) {
						toDelete.delete();
					}
				}
			}
		}
	}

	private void initialize() {
		Store store1;
		Store store2;
		User e1;
		User e2;
		User c1;
		User c2;
		User c3;
		try {
			reloadStores();
			reloadOrders("");
			store1 = storeRepository.find("6").toCompletableFuture().get().orElse(null);
			store2 = storeRepository.find("10").toCompletableFuture().get().orElse(null);
			e1 = userRepository.find("6/49-Colleen Beahan").toCompletableFuture().get().orElse(null);
			User employee1 = new User("6/49-Colleen Beahan", "pwd", store1);
			if (e1 == null) employee1.save();
			e2 = userRepository.find("10/102-Marianne Hyatt").toCompletableFuture().get().orElse(null);
			User employee2 = new User("10/102-Marianne Hyatt", "pwd", store2);
			if (e2 == null) employee2.save();
			c1 = userRepository.find("test").toCompletableFuture().get().orElse(null);
			User client1 = new User("test", "pwd");
			if (c1 == null) client1.save();
			c2 = userRepository.find("aidan.gutkowski.772559@test.com").toCompletableFuture().get().orElse(null);
			User client2 = new User("aidan.gutkowski.772559@test.com", "pwd");
			if (c2 == null) client2.save();
			c3 = userRepository.find("alda.jacobs.423513@test.com").toCompletableFuture().get().orElse(null);
			User client3 = new User("alda.jacobs.423513@test.com", "pwd");
			if (c3 == null) client3.save();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public CompletionStage<Result> findOrder(String id, Http.Request request) {
		return orderRepository.find(id).thenApplyAsync(orderOptional -> {
			// This is the HTTP rendering thread context
			OrderData o = orderOptional.orElse(new OrderData());
			return ok(o.orderId);
		}, httpExecutionContext.current());
	}

	public CompletionStage<Result> listCustomerOrders(String email, Http.Request request) {
		StringBuilder stringBuilder = new StringBuilder();
		return orderRepository.listByEmail(email).thenApplyAsync(list -> {
			// This is the HTTP rendering thread context
			for (OrderData o : list) {
				stringBuilder.append(o).append("\n");
			}
			return ok(stringBuilder.toString());
		}, httpExecutionContext.current());
	}

	public CompletionStage<Result> listStoreOrders(String storeId, Http.Request request) {
		StringBuilder stringBuilder = new StringBuilder();
		return orderRepository.listAll().thenApplyAsync(list -> {
			// This is the HTTP rendering thread context
			for (OrderData o : list) {
				stringBuilder.append(o).append("\n");
			}
			return ok(stringBuilder.toString());
		}, httpExecutionContext.current());
	}

	public CompletionStage<Result> viewOrder(String id, Http.Request request) {
		return orderRepository.find(id).thenApplyAsync(orderOptional -> {
			// This is the HTTP rendering thread context
			OrderData o = orderOptional.orElse(new OrderData());
			return ok(o.orderId);//TODO link to actual page
		}, httpExecutionContext.current());
	}

	public CompletionStage<Result> listStores(Http.Request request) {
		StringBuilder stringBuilder = new StringBuilder();
		return storeRepository.listAll().thenApplyAsync(list -> {
			// This is the HTTP rendering thread context
			for (Store s : list) {
				stringBuilder.append(s).append("\n");
			}
			return ok(stringBuilder.toString());
		}, httpExecutionContext.current());
	}

	//welcome page (not logged in) (main.scala, to change)
	public Result welcome(Http.Request request) {
		User user = SessionManager.getUser(request);
		return ok(views.html.welcome.render(user, request, messagesApi.preferred(request)));
	}

	//login page
	public Result loginPage(Http.Request request) throws ExecutionException, InterruptedException {
		initialize();
		Form<User> userForm = formFactory.form(User.class);
		return ok(views.html.login.render(userForm, request, messagesApi.preferred(request)));
	}

	public Result loginAction(Http.Request request) {
		Form<User> userForm = formFactory.form(User.class).bindFromRequest(request);
		User toCheck = userForm.get();
		User user;
		try {
			if (userForm.hasErrors()) throw new Exception();
			user = userRepository.find(toCheck.userName).toCompletableFuture().get().orElse(null);
			if (user == null || !user.password.equals(toCheck.password)) throw new Exception();
		} catch (Exception e) {
			//return badRequest(views.html.login.render(userForm, request, messagesApi.preferred(request)));
			return redirect(routes.HomeController.loginPage()).flashing("error", "Invalid Login Credentials");
		}
		return redirect(routes.HomeController.index()).withSession(SessionManager.loginUser(user));
	}

	public Result logoutAction(Http.Request request) {
		SessionManager.logoutUser(request);
		return redirect(routes.HomeController.index()).flashing("success", "You have been logged out.");
	}

	//dashboard page
	@Security.Authenticated(UserAuthenticator.class)
	public Result dashboardPage(Http.Request request) throws ExecutionException, InterruptedException {
		User user = SessionManager.getUser(request);
		List<OrderData> orders = orderRepository.listByEmail(user.userName).toCompletableFuture().get();
		return ok(views.html.dashboard.render(user, orders, request, messagesApi.preferred(request)));
	}

	//view order
	@Security.Authenticated(UserAuthenticator.class)
	public Result orderPage(String orderId, Http.Request request) throws ExecutionException, InterruptedException {
		User user = SessionManager.getUser(request);
		OrderData order = orderRepository.find(orderId).toCompletableFuture().get().orElse(null);
		if (order == null) return redirect(routes.HomeController.dashboardPage()).flashing("error", "Invalid order.");
		if ((user.store == null && user.userName.equals(order.customerEmailAddress)) ||
				(user.store != null && user.store == order.pickupStore)){
			List<OrderEntry> entries = orderEntryRepository.listByOrder(orderId).toCompletableFuture().get();
			return ok(views.html.order.render(user, order, entries, request, messagesApi.preferred(request)));
		}
		return redirect(routes.HomeController.dashboardPage()).flashing("error", "You are not authorized to see that order.");
	}

	@Security.Authenticated(UserAuthenticator.class)
	public Result orderToggleProcessing(String orderId, Http.Request request) throws ExecutionException, InterruptedException {
		User user = SessionManager.getUser(request);
		OrderData order = orderRepository.find(orderId).toCompletableFuture().get().orElse(null);
		if (order == null) return redirect(routes.HomeController.dashboardPage()).flashing("error", "Invalid order.");
		if ((user.store == null && user.userName.equals(order.customerEmailAddress)) ||
				(user.store != null && user.store == order.pickupStore)){
			if (order.toggleProcessing()) {
				return redirect(routes.HomeController.orderPage(orderId)).flashing("success", "The order is " + (order.isBeingProcessed?"now":"not") + " being processed.");
			} else {
				return redirect(routes.HomeController.orderPage(orderId)).flashing("error","The order has already been completed.");
			}
		}
		return redirect(routes.HomeController.dashboardPage()).flashing("error", "You are not authorized to see that order.");
	}

	@Security.Authenticated(UserAuthenticator.class)
	public Result orderFulfill(String orderId, Http.Request request) throws ExecutionException, InterruptedException {
		User user = SessionManager.getUser(request);
		OrderData order = orderRepository.find(orderId).toCompletableFuture().get().orElse(null);
		if (order == null) return redirect(routes.HomeController.dashboardPage()).flashing("error", "Invalid order.");
		if ((user.store == null && user.userName.equals(order.customerEmailAddress)) ||
				(user.store != null && user.store == order.pickupStore)){
			if (order.fulfill()) {
				return redirect(routes.HomeController.orderPage(orderId)).flashing("success", "The order is now completed.");
			} else {
				return redirect(routes.HomeController.orderPage(orderId)).flashing("error","The order had already been completed.");
			}
		}
		return redirect(routes.HomeController.dashboardPage()).flashing("error", "You are not authorized to see that order.");
	}

	//schedule pickup
	@Security.Authenticated(UserAuthenticator.class)
	public Result scheduleOrderPage(String orderId, Http.Request request) throws ExecutionException, InterruptedException {
		User user = SessionManager.getUser(request);
		OrderData order = orderRepository.find(orderId).toCompletableFuture().get().orElse(null);
		if (order == null) return redirect(routes.HomeController.dashboardPage()).flashing("error", "Invalid order.");
		if (!user.userName.equals(order.customerEmailAddress) || (order.pickupTime!= null && !order.pickupTime.toLocalDate().equals(LocalDate.now()))) {
			return redirect(routes.HomeController.orderPage(orderId)).flashing("error", "You are not authorized to schedule that order.");
		}
		List<Store> stores = storeRepository.listAll().toCompletableFuture().get();
		return ok(views.html.pickTime.render(user,stores,request, messagesApi.preferred(request)));
	}

}
