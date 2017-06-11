package com.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.domain.Message;

@Component
public class MessageManagerImpl implements MessageManager {
	private Map<Long, Message> messages = new HashMap<>();

	@Override
	public Message createMessage(Message msg) {
		Message newMsg = new Message();
		newMsg.setId(messages.size() + 1);
		newMsg.setText(msg.getText());
		messages.put(newMsg.getId(), newMsg);
		return newMsg;
	}

	@Override
	public boolean updateMessage(Message msg) {
		return messages.replace(msg.getId(), msg) != null;
	}
	
	@Override
	public boolean deleteMessage(long id) {
		return messages.remove(id) != null;		
	}

	@Override
	public Message getMessage(long id) {
		return messages.get(id);
	}

	@Override
	public Collection<Message> getMessages() {
		return messages.values();
	}

}
