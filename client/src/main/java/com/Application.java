package com;

import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.domain.Message;

public class Application {
    private final Logger LOGGER = LoggerFactory.getLogger(Application.class);	
	private static String RESOURCE_URL = "http://localhost:8080/rest/messages";
	private Client client;
	
	public Application() {
		client = ClientBuilder.newClient();
	}
	
	public Message createMessage(String text) {
		LOGGER.info("*** Create message '{}'", text);
		Message msg = new Message();
		msg.setText(text);
		Response response = client.target(RESOURCE_URL).request().post(Entity.json(msg));
		logResponse(response);
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			msg = response.readEntity(Message.class);
			LOGGER.info("Id: {}, Text: '{}'", msg.getId(), msg.getText());
			return msg;
		}
		return null;
	}

	public void updateMessage(long id, String text) {
		LOGGER.info("*** Update message id: {} to text '{}'", id, text);
		Message msg = new Message();
		msg.setId(id);
		msg.setText(text);
		Response response = client.target(RESOURCE_URL + "/" + msg.getId()).request().put(Entity.json(msg));
		logResponse(response);
		if (response.getStatus() == Status.OK.getStatusCode()) {
			LOGGER.info("Message updated!");
		}
	}
	
	public void deleteMessage(long id) {
		LOGGER.info("*** Delete message id: {}", id);
		Response response = client.target(RESOURCE_URL + "/" + id).request().delete();
		logResponse(response);
		if (response.getStatus() == Status.OK.getStatusCode()) {
			LOGGER.info("Message deleted!");
		}
	}
	
	public void getMessage(long id, MediaType mediaType) {
		LOGGER.info("*** Get message id: {}", id);
		Response response = client.target(RESOURCE_URL + "/" + id).request(mediaType).get();
		logResponse(response);
		if (response.getStatus() == Status.OK.getStatusCode()) {
			Message msg = response.readEntity(Message.class);
			LOGGER.info("Id: {}, Text: '{}'", msg.getId(), msg.getText());
		}
	}
	
	public void getMessages(MediaType mediaType) {
		LOGGER.info("*** Get all messages");
		Response response = client.target(RESOURCE_URL).request(mediaType).get();
		logResponse(response);
		if (response.getStatus() == Status.OK.getStatusCode()) {
			Collection<Message> list = Arrays.asList(response.readEntity(Message[].class));
			list.stream().forEach(m -> LOGGER.info("Id: {}, Text: '{}'", m.getId(), m.getText()));
		}
	}
	
	private void logResponse(Response response) {
		LOGGER.info("Response Status: {}", response.getStatus());
		LOGGER.info("Response Info: {}", response.getStatusInfo());	
		LOGGER.info("Response Headers: {}", response.getHeaders().toString());
	}
	
	public static void main(String[] args) throws Exception {
		Application app = new Application();
		app.createMessage("Hello World 1");
		app.createMessage("Hello World 2");
		app.createMessage("Hello World 3");
		app.getMessage(2, MediaType.APPLICATION_JSON_TYPE);
		app.deleteMessage(2);
		app.updateMessage(1, "New text of message id 1");
		app.getMessage(1, MediaType.APPLICATION_XML_TYPE);
		app.getMessages(MediaType.APPLICATION_XML_TYPE);
	}

}
