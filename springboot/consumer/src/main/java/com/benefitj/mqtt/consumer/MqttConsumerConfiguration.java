package com.benefitj.mqtt.consumer;


import com.benefitj.mqtt.spring.MqttProperty;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;

/**
 * MQTT消费者配置
 */
@EnableConfigurationProperties(MqttProperty.class)
@Configuration
@IntegrationComponentScan
public class MqttConsumerConfiguration {

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

  /**
   * 处理订阅的MessageHandler
   * 订阅 mqttInboundChannel 通道的信息
   */
  @ConditionalOnBean(MqttInboundChannelHandler.class)
  @Bean
  @ServiceActivator(inputChannel = "mqttInboundChannel")
  public MqttInboundChannelHandler mqttInboundChannelHandler() {
    return new MqttInboundChannelHandler() {
      @Override
      public void handleMessage(Message<?> message) throws MessagingException {
        System.out.println("收到消息 = " + message.getPayload());
      }
    };
  }

  @ConditionalOnBean(name = "mqttInboundChannel")
  @Bean
  public DirectChannel mqttInboundChannel() {
    return new DirectChannel();
  }

  /**
   * 1. 订阅主题，可订阅多个主题
   * 2. 将主题返回的内容发布到指定的 MessageChannel 里
   */
  @Bean
  public MqttPahoMessageDrivenChannelAdapter mqttInbound(MqttPahoClientFactory mqttClientFactory,
                                                         @Qualifier("mqttInboundChannel") MessageChannel mqttInboundChannel,
                                                         MqttProperty property) {
    String clientId = property.getClientId();
    MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, mqttClientFactory);
    if (StringUtils.isNotBlank(property.getDefaultTopic())) {
      adapter.addTopic(property.getDefaultTopic(), property.getDefaultQos());
    }
    adapter.setManualAcks(property.isManualAcks());
    adapter.setConverter(new DefaultPahoMessageConverter());
    adapter.setOutputChannel(mqttInboundChannel);
    return adapter;
  }


}
