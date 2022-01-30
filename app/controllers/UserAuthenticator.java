package controllers;

import models.User;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.Optional;


public class UserAuthenticator extends Security.Authenticator {
	private final MessagesApi messagesApi;
	@Inject
	public UserAuthenticator(MessagesApi messagesApi) {
		this.messagesApi = messagesApi;
	}
	@Override
	public Optional<String> getUsername(Http.Request request) {
		User user = SessionManager.getUser(request);
		if (user == null) return Optional.empty();      // user with specified userName does not exist or is inactive
		return Optional.of(user.userName);
	}

	@Override
	public Result onUnauthorized(Http.Request request) {
		User user = SessionManager.getUser(request);
		if (user == null) return redirect(routes.HomeController.loginPage()).flashing("error", "Invalid Credentials. Please log in again.");
		return redirect(routes.HomeController.loginPage());
	}
}
