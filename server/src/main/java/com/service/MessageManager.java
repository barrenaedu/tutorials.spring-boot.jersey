package com.service;

import java.util.Collection;

import com.domain.Message;

public interface MessageManager {

	public Message createMessage(Message msg);
	
	public boolean updateMessage(Message msg);
	
	public boolean deleteMessage(long id);

	public Message getMessage(long id);
	
	public Collection<Message> getMessages();
}
