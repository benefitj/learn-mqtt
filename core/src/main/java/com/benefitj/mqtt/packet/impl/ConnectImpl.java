package com.benefitj.mqtt.packet.impl;

import com.benefitj.mqtt.packet.CONNECT;
import com.benefitj.mqtt.packet.ControlPacketType;

/**
 * CONNECT 报文实现, {@link CONNECT}
 *
 * 连接标志位:
 *  Bit    7         6          5         4  3           2            1           0
 * value username password  will Retain  will QoS   will Flag   Clean Session  Reserved
 *
 * @author DINGXIUAN
 */
public class ConnectImpl implements CONNECT {

  /**
   * 客户端唯一标识
   */
  private String clientId;
  /**
   * 协议名
   */
  private String protocolName;
  /**
   * 协议等级
   */
  private int protocolLevel;
  /**
   * 清理回话
   */
  private boolean cleanSession;
  /**
   * 遗嘱标志
   */
  private boolean willFlag;
  /**
   * 遗嘱 QoS
   */
  private int willQoS;
  /**
   * 遗嘱保留
   */
  private boolean willRetain;
  /**
   * 遗嘱主题
   */
  private String willTopic;
  /**
   * 遗嘱消息
   */
  private String willMessage;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private byte[] password;
  /**
   * 保持连接时长
   */
  private int keepAlive = 0;

  public ConnectImpl() {
  }

  /**
   * 获取控制报文的类型
   */
  @Override
  public final ControlPacketType getType() {
    return ControlPacketType.CONNECT;
  }

  /**
   * 获取协议名
   */
  @Override
  public String getProtocolName() {
    return protocolName;
  }

  /**
   * 获取客户端标识
   */
  @Override
  public String getClientId() {
    return this.clientId;
  }

  /**
   * 设置客户端标识
   *
   * @param clientId 客户端标识
   */
  @Override
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /**
   * 设置协议名
   *
   * @param protocolName 协议名
   */
  @Override
  public void setProtocolName(String protocolName) {
    this.protocolName = protocolName;
  }

  /**
   * 获取协议等级
   */
  @Override
  public int getProtocolLevel() {
    return protocolLevel;
  }

  /**
   * 设置协议等级
   *
   * @param protocolLevel 协议等级
   */
  @Override
  public void setProtocolLevel(int protocolLevel) {
    this.protocolLevel = protocolLevel;
  }

  /**
   * 是否清理回话
   */
  @Override
  public boolean isCleanSession() {
    return this.cleanSession;
  }

  /**
   * 设置是否清理回话
   *
   * @param cleanSession 是否清理
   */
  @Override
  public void setCleanSession(boolean cleanSession) {
    this.cleanSession = cleanSession;
  }

  /**
   * 是否设置遗嘱
   */
  @Override
  public boolean isWillFlag() {
    return this.willFlag;
  }

  /**
   * 设置遗嘱标志
   *
   * @param willFlag 遗嘱标志
   */
  @Override
  public void setWillFlag(boolean willFlag) {
    this.willFlag = willFlag;
  }

  /**
   * 获取 Will QoS，服务质量，
   */
  @Override
  public int getWillQoS() {
    return this.willQoS;
  }

  /**
   * 设置 Will QoS
   *
   * @param willQoS 值
   */
  @Override
  public void setWillQoS(int willQoS) {
    this.willQoS = willQoS;
  }

  /**
   * 获取 Will Retain
   */
  @Override
  public boolean isWillRetain() {
    return this.willRetain;
  }

  /**
   * 设置 Will Retain
   *
   * @param willRetain 值
   */
  @Override
  public void setWillRetain(boolean willRetain) {
    this.willRetain = willRetain;
  }

  /**
   * 获取遗嘱主题
   */
  @Override
  public String getWillTopic() {
    return this.willTopic;
  }

  /**
   * 设置遗嘱主题
   *
   * @param willTopic 遗嘱主题
   */
  @Override
  public void setWillTopic(String willTopic) {
    this.willTopic = willTopic;
  }

  /**
   * 获取遗嘱消息
   */
  @Override
  public String getWillMessage() {
    return this.willMessage;
  }

  /**
   * 设置遗嘱消息
   *
   * @param willMessage 遗嘱消息
   */
  @Override
  public void setWillMessage(String willMessage) {
    this.willMessage = willMessage;
  }

  /**
   * 获取用户名
   */
  @Override
  public String getUsername() {
    return this.username;
  }

  /**
   * 设置用户名
   *
   * @param username 用户名
   */
  @Override
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * 获取密码
   */
  @Override
  public byte[] getPassword() {
    return this.password;
  }

  /**
   * 设置密码
   *
   * @param password 密码
   */
  @Override
  public void setPassword(byte[] password) {
    this.password = password;
  }

  /**
   * 获取保持连接的时长，以秒为单位
   */
  @Override
  public int getKeepAlive() {
    return this.keepAlive;
  }

  /**
   * 设置保持连接的时长，以秒为单位
   *
   * @param keepAlive 保持连接时长(秒)
   */
  @Override
  public void setKeepAlive(int keepAlive) {
    this.keepAlive = keepAlive;
  }


  public static final class Builder {
    private String clientId;
    private String protocolName;
    private byte protocolLevel;
    private boolean willFlag;
    private int willQoS;
    private boolean willRetain;
    private String willTopic;
    private String willMessage;
    private String username;
    private byte[] password;
    private int keepAlive;

    public Builder() {
    }

    public Builder setClientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public Builder setProtocolName(String protocolName) {
      this.protocolName = protocolName;
      return this;
    }

    public Builder setProtocolLevel(byte protocolLevel) {
      this.protocolLevel = protocolLevel;
      return this;
    }

    public Builder setWillFlag(boolean willFlag) {
      this.willFlag = willFlag;
      return this;
    }

    public Builder setWillQoS(int willQoS) {
      this.willQoS = willQoS;
      return this;
    }

    public Builder setWillRetain(boolean willRetain) {
      this.willRetain = willRetain;
      return this;
    }

    public Builder setWillTopic(String willTopic) {
      this.willTopic = willTopic;
      return this;
    }

    public Builder setWillMessage(String willMessage) {
      this.willMessage = willMessage;
      return this;
    }

    public Builder setUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder setPassword(byte[] password) {
      this.password = password;
      return this;
    }

    public Builder setKeepAlive(int keepAlive) {
      this.keepAlive = keepAlive;
      return this;
    }

    public ConnectImpl build() {
      ConnectImpl connect = new ConnectImpl();
      connect.setClientId(this.clientId);
      connect.setProtocolName(this.protocolName);
      connect.setProtocolLevel(this.protocolLevel);
      connect.setWillFlag(this.willFlag);
      connect.setWillQoS(this.willQoS);
      connect.setWillRetain(this.willRetain);
      connect.setWillTopic(this.willTopic);
      connect.setWillMessage(this.willMessage);
      connect.setUsername(this.username);
      connect.setPassword(this.password);
      connect.setKeepAlive(this.keepAlive);
      return connect;
    }
  }
}
