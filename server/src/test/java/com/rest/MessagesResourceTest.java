package com.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.domain.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MessagesResourceTest {
	private static final String RESOURCE_URL = "/rest/messages/";
	
	@Autowired
	private final ObjectMapper jsonMapper = new ObjectMapper();
	
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testCreateMessage() throws Exception {
		String text = "The chosen one";
		ResponseEntity<Message> response = createMessage(text);
	    assertEquals(HttpStatus.CREATED, response.getStatusCode());
	    assertTrue(response.getBody().getId() > 0);
	    assertEquals(text, response.getBody().getText());
	}
	
	@Test
	public void testUpdateMessage() throws Exception {
		Message message = createMessage("The message from 'testUpdateMessage' method").getBody();
		Message updatedMsg = new Message();
		updatedMsg.setText("The updated text");
		updatedMsg.setId(message.getId());
		// update present
		ResponseEntity<String> response = updateMessage(updatedMsg);
	    assertEquals(HttpStatus.OK, response.getStatusCode());
	    message = getMessage(message.getId()).getBody();
	    assertEquals(updatedMsg.getText(), message.getText());
	    // update not present
		updatedMsg.setId(Long.MAX_VALUE);
	    response = updateMessage(updatedMsg);
	    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testDeleteMessage() throws Exception {
		Message msg = createMessage("The message from 'testDeleteMessage' method").getBody();
		// delete
		ResponseEntity<String> responseDel = deleteMessage(msg.getId());
	    assertEquals(HttpStatus.OK, responseDel.getStatusCode());
	    // check deleted
	    ResponseEntity<Message> responseGet = getMessage(msg.getId());
		assertEquals(HttpStatus.NOT_FOUND, responseGet.getStatusCode());
	}
	
	@Test
	public void testGetMessage() throws Exception {
		// get valid message
		Message msg = createMessage("The message from 'testGetMessage' method").getBody();
		ResponseEntity<Message> response = getMessage(msg.getId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(msg.getId(), response.getBody().getId());
		assertEquals(msg.getText(), response.getBody().getText());
		// get invalid message
		response = getMessage(Long.MAX_VALUE);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());		
	}
	
	@Test
	public void testGetMessages() throws Exception {
		// create messages
		Message msg1 = createMessage("The message 1 from 'testGetMessages' method").getBody();
		Message msg2 = createMessage("The message 2 from 'testGetMessages' method").getBody();
		// get messages
		ResponseEntity<Collection<Message>> response = getMessages();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().size() >= 2);
	    BiPredicate<Message, Message> equals = (x, y) -> x.getId() == y.getId() && Objects.equals(x.getText(), y.getText());
		assertTrue(response.getBody().stream().anyMatch(msg -> equals.test(msg, msg1) || equals.test(msg, msg2)));
	}
	
	// ***********************
	// *** Private methods ***
	// ***********************
	
	private ResponseEntity<Collection<Message>> getMessages() {
		ResponseEntity<Message[]> response = restTemplate.getForEntity("/rest/messages/", Message[].class);
		return ResponseEntity.status(response.getStatusCode()).body(Arrays.asList(response.getBody()));
	}
	
	private ResponseEntity<String> updateMessage(Message msgUpdated) {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    return restTemplate.exchange(RESOURCE_URL + msgUpdated.getId(), HttpMethod.PUT, new HttpEntity<Message>(msgUpdated, headers), String.class);
	}
	
	private ResponseEntity<Message> createMessage(String text) {
		Message msg = new Message();
		msg.setText(text);
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    return restTemplate.exchange(RESOURCE_URL, HttpMethod.POST, new HttpEntity<Message>(msg, headers), Message.class);
	}
	
	private ResponseEntity<Message> getMessage(long id) throws Exception {
		ResponseEntity<String> response = restTemplate.getForEntity(RESOURCE_URL + id, String.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			return ResponseEntity.status(response.getStatusCode()).body(jsonMapper.readValue(response.getBody(), Message.class));
		}
		return ResponseEntity.status(response.getStatusCode()).build();
	}
	
	private ResponseEntity<String> deleteMessage(long id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return restTemplate.exchange(RESOURCE_URL + id, HttpMethod.DELETE, new HttpEntity<Message>(headers), String.class);
	}

}
