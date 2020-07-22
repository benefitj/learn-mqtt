package com.benefitj.mqtt.spring;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;

@Singleton
@Component
@ConfigurationProperties(prefix = "com.hsrg.mqtt")
public class MqttProperty {

  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * 访问路径，如：tcp://127.0.0.1:6666
   */
  private String[] serverURIs;
  /**
   * 生产者的客户端ID
   */
  private String producerClientId;
  /**
   * 消费者的客户端ID
   */
  private String clientId;
  /**
   * 默认主题
   */
  private String defaultTopic;
  /**
   * 默认服务质量
   */
  private Integer defaultQos = 1;
  /**
   * keep-alive的间隔
   */
  private Integer keepAliveInterval = MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT;
  private String willDestination = null;
  /**
   * 是否每次请求后都清空session
   */
  private Boolean cleanSession = MqttConnectOptions.CLEAN_SESSION_DEFAULT;
  /**
   * 连接超时时长
   */
  private Integer connectionTimeout = MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT;
  /**
   * 最大重连延迟时长
   */
  private Integer maxReconnectDelay = 128000;

  /**
   *
   */
  private MqttMessageHandler mqttOutbound;
  /**
   * 是否手动确认
   */
  private boolean manualAcks;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String[] getServerURIs() {
    return serverURIs;
  }

  public void setServerURIs(String[] serverURIs) {
    this.serverURIs = serverURIs;
  }

  public String getProducerClientId() {
    return producerClientId;
  }

  public void setProducerClientId(String producerClientId) {
    this.producerClientId = producerClientId;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getDefaultTopic() {
    return defaultTopic;
  }

  public void setDefaultTopic(String defaultTopic) {
    this.defaultTopic = defaultTopic;
  }

  public Integer getDefaultQos() {
    return defaultQos;
  }

  public void setDefaultQos(Integer defaultQos) {
    this.defaultQos = defaultQos;
  }

  public Integer getKeepAliveInterval() {
    return keepAliveInterval;
  }

  public void setKeepAliveInterval(Integer keepAliveInterval) {
    this.keepAliveInterval = keepAliveInterval;
  }

  public String getWillDestination() {
    return willDestination;
  }

  public void setWillDestination(String willDestination) {
    this.willDestination = willDestination;
  }

  public Boolean getCleanSession() {
    return cleanSession;
  }

  public void setCleanSession(Boolean cleanSession) {
    this.cleanSession = cleanSession;
  }

  public Integer getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(Integer connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public Integer getMaxReconnectDelay() {
    return maxReconnectDelay;
  }

  public void setMaxReconnectDelay(Integer maxReconnectDelay) {
    this.maxReconnectDelay = maxReconnectDelay;
  }

  public MqttMessageHandler getMqttOutbound() {
    return mqttOutbound;
  }

  public void setMqttOutbound(MqttMessageHandler mqttOutbound) {
    this.mqttOutbound = mqttOutbound;
  }

  public boolean isManualAcks() {
    return manualAcks;
  }

  public void setManualAcks(boolean manualAcks) {
    this.manualAcks = manualAcks;
  }

  public static class MqttMessageHandler {
    private boolean defaultRetained;
    private boolean async;
    private boolean asyncEvents;

    public boolean isDefaultRetained() {
      return defaultRetained;
    }

    public void setDefaultRetained(boolean defaultRetained) {
      this.defaultRetained = defaultRetained;
    }

    public boolean isAsync() {
      return async;
    }

    public void setAsync(boolean async) {
      this.async = async;
    }

    public boolean isAsyncEvents() {
      return asyncEvents;
    }

    public void setAsyncEvents(boolean asyncEvents) {
      this.asyncEvents = asyncEvents;
    }
  }
}
