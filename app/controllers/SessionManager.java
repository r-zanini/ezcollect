package controllers;

import models.User;
import play.mvc.Http;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.SortedMap;
import java.util.TreeMap;

public class SessionManager {
	private static SortedMap<String, Node> sessionManager = new TreeMap<>();
	private static final int TIMEOUT = 30;  // in minutes
	private static final int ID_SIZE = 16;
	private static class Node {
		private User user;
		private LocalDateTime timestamp;
		private String sessionID;

		Node(User user){
			this.user = user;
			this.timestamp = LocalDateTime.now();
			this.sessionID = generateSessionID();
		}
	}

	private static String generateSessionID(){
		String options = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM";
		StringBuilder builder = new StringBuilder(ID_SIZE);
		for(int i = 0; i<ID_SIZE; i++) {builder.append(options.charAt((int)(options.length() * Math.random())));}
		return builder.toString();
	}
	static SortedMap<String, String> loginUser(User user){
		Node newNode = new Node(user);
		sessionManager.put(user.userName, newNode);
		SortedMap<String, String> answer = new TreeMap<>();
		answer.put("user",user.userName);
		answer.put("sessionID", newNode.sessionID);
		return answer;
	}
	static void logoutUser(Http.Request request) {sessionManager.remove(request.session().get("user").orElse(""));}

	static User getUser(Http.Request request) {
		String userName = request.session().get("user").orElse("");
		String sessionID = request.session().get("sessionID").orElse("");
		if (verifySession(userName, sessionID) == 1) {
			Node node = sessionManager.get(userName);
			User user = node.user;
			user.refresh();
			return user;
		}
		return null;
	}

	static int verifySession(String userName, String sessionID){
		Node oldNode = sessionManager.get(userName);
		if (oldNode == null) return 0;                                                          // there is no login information (userName is null or not on the list of sessions)
		if (oldNode.timestamp.isBefore(LocalDateTime.now().minusMinutes(TIMEOUT))) {
			sessionManager.remove(userName);
			return -2;                                                                           // session has expired, remove it from the list.
		}
		if (!oldNode.sessionID.equals(sessionID)) return -1;                                    // sessionID is different but still valid (currently logged in from a different browser)
		oldNode.timestamp = LocalDateTime.now();
		sessionManager.put(userName, oldNode);
		return 1;                                                                               // session is valid, timestamp was updated (same sessionID is kept)
	}
	//private static void cleanSessions(){for (Node n : sessionManager.values()) {if (n.timestamp.isBefore(LocalDateTime.now().minusMinutes(TIMEOUT))) sessionManager.remove(n.userName);}}
	public static SortedMap<String, String> getActiveSessions(){
		SortedMap<String, String> answer = new TreeMap<>();
		for (Node n : sessionManager.values()) {
			if (n.timestamp.isBefore(LocalDateTime.now().minusMinutes(TIMEOUT))) sessionManager.remove(n.user.userName);
			else answer.put(n.user.userName, n.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " (" + n.sessionID + ")");
		}
		return answer;
	}
}
