package com.benefitj.mqtt.producer;

import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

public class MqttOutboundChannelHandler extends MqttPahoMessageHandler {

  public MqttOutboundChannelHandler(String url, String clientId, MqttPahoClientFactory clientFactory) {
    super(url, clientId, clientFactory);
  }

  public MqttOutboundChannelHandler(String clientId, MqttPahoClientFactory clientFactory) {
    super(clientId, clientFactory);
  }

  public MqttOutboundChannelHandler(String url, String clientId) {
    super(url, clientId);
  }

}
