package com.benefitj.mqtt.producer;

import com.benefitj.mqtt.spring.MqttProperty;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

/**
 * MQTT消息生产者配置
 */
@EnableConfigurationProperties(MqttProperty.class)
@Configuration
@IntegrationComponentScan
public class MqttProducerConfiguration {

  /**
   * MQTT客户端连接工厂
   */
  @ConditionalOnBean(MqttPahoClientFactory.class)
  @Bean
  public MqttPahoClientFactory mqttClientFactory(MqttProperty property) {
    DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    MqttConnectOptions options = new MqttConnectOptions();
    BeanUtils.copyProperties(property, options);
    // 设置用户名和密码
    options.setUserName(property.getUsername());
    options.setPassword(property.getPassword().toCharArray());
    factory.setConnectionOptions(options);
    return factory;
  }

  @ConditionalOnBean(name = "mqttOutboundChannel")
  @Bean
  public DirectChannel mqttOutboundChannel() {
    return new DirectChannel();
  }

  /**
   * 发布信息的MessageHandler，订阅 mqttOutboundChannel 通道的信息
   */
  @ConditionalOnBean(MqttOutboundChannelHandler.class)
  @Bean
  @ServiceActivator(inputChannel = "mqttOutboundChannel")
  public MqttOutboundChannelHandler mqttOutboundChannelHandler(MqttPahoClientFactory mqttClientFactory,
                                                               MqttProperty property) {
    String clientId = property.getClientId();
    MqttOutboundChannelHandler messageHandler = new MqttOutboundChannelHandler(clientId, mqttClientFactory);
    MqttProperty.MqttMessageHandler mqttOutbound = property.getMqttOutbound();
    messageHandler.setAsync(mqttOutbound.isAsync());
    messageHandler.setDefaultTopic(property.getDefaultTopic());
    messageHandler.setDefaultQos(property.getDefaultQos());
    messageHandler.setDefaultRetained(mqttOutbound.isDefaultRetained());
    messageHandler.setAsyncEvents(mqttOutbound.isAsyncEvents());
    return messageHandler;
  }

}
