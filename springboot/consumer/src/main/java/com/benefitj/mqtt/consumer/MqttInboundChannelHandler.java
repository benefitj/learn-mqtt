package com.benefitj.mqtt.consumer;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

public class MqttInboundChannelHandler implements MessageHandler {
  @Override
  public void handleMessage(Message<?> message) throws MessagingException {

  }
}
