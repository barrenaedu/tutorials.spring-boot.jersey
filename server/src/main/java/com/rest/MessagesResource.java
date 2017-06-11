package com.rest;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.domain.Message;
import com.service.MessageManager;


@Path("/messages")
@Component
public class MessagesResource {
	private final MessageManager messageManager;

	@Autowired
	public MessagesResource(MessageManager messageManager) {
		this.messageManager = messageManager;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMessage(Message msg) {
		Message newMsg = messageManager.createMessage(msg);
		return Response.status(Status.CREATED).entity(newMsg).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMessage(@PathParam("id") long id, Message msg) {
		if (msg.getId() != id) {
			throw new IllegalArgumentException("Object id cannot be different than the parameter id");
		}
		if (messageManager.updateMessage(msg)) {
			return Response.status(Status.OK).build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteMessage(@PathParam("id") long id) {
		if (messageManager.deleteMessage(id)) {
			return Response.status(Status.OK).build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessage(@PathParam("id") long id) {
		Message msg = messageManager.getMessage(id);
		if (msg != null) {
			return Response.status(Status.OK).entity(msg).build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessages() {
		Collection<Message> msgs = messageManager.getMessages();
		return Response.status(Status.OK).entity(msgs.toArray(new Message[msgs.size()])).build();
	}

}
