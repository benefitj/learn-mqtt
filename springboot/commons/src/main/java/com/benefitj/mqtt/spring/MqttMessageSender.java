package com.benefitj.mqtt.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@ConditionalOnMissingBean(MqttMessageSender.class)
@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttMessageSender {

  /**
   * 发送
   *
   * @param payload 载荷
   */
  void send(String payload);

  /**
   * 发送
   *
   * @param payload 载荷
   */
  void send(byte[] payload);

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 载荷
   */
  void send(@Header(MqttHeaders.TOPIC) String topic, String payload);

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 载荷
   */
  void send(@Header(MqttHeaders.TOPIC) String topic, byte[] payload);

  /**
   * 发送
   *
   * @param topic   主题
   * @param qos     服务质量
   * @param payload 载荷
   */
  void send(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);

  /**
   * 发送
   *
   * @param topic   主题
   * @param qos     服务质量
   * @param payload 载荷
   */
  void send(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, byte[] payload);

}
