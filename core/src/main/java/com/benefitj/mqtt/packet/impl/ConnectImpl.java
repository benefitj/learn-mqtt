package com.benefitj.mqtt.packet.impl;

import com.benefitj.mqtt.packet.CONNECT;
import com.benefitj.mqtt.packet.ControlPacketType;

public class ConnectImpl implements CONNECT<byte[]> {

  /**
   * 协议名
   */
  private String protocolName;
  /**
   * 协议等级
   */
  private byte protocolLevel;
  /**
   * 连接标志位
   */
  private byte connectFlags = 0;

  /**
   * 获取协议名
   */
  @Override
  public String getProtocolName() {
    return protocolName;
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
  public byte getProtocolLevel() {
    return protocolLevel;
  }

  /**
   * 设置协议等级
   *
   * @param protocolLevel 协议等级
   */
  @Override
  public void setProtocolLevel(byte protocolLevel) {
    this.protocolLevel = protocolLevel;
  }

  /**
   * 获取连接标志
   */
  @Override
  public byte getConnectFlags() {
    return 0;
  }

  /**
   * 设置遗嘱标志
   *
   * @param willFlag 遗嘱标志
   */
  @Override
  public void setWillFlag(boolean willFlag) {

  }

  /**
   * 是否设置遗嘱
   */
  @Override
  public boolean isWillFlag() {
    return false;
  }

  /**
   * 设置 Will QoS
   *
   * @param willQoS 值
   */
  @Override
  public void setWillQoS(byte willQoS) {

  }

  /**
   * 获取 Will QoS，服务质量，
   */
  @Override
  public byte getWillQoS() {
    return 0;
  }

  /**
   * 设置 Will Retain
   *
   * @param willRetain 值
   */
  @Override
  public void setWillRetain(boolean willRetain) {

  }

  /**
   * 获取 Will Retain
   */
  @Override
  public byte getWillRetain() {
    return 0;
  }

  /**
   * 设置用户名
   *
   * @param username 用户名
   */
  @Override
  public void setUsername(byte[] username) {

  }

  /**
   * 获取用户名
   */
  @Override
  public byte[] getUsername() {
    return new byte[0];
  }

  /**
   * 设置密码
   *
   * @param password 密码
   */
  @Override
  public void setPassword(byte[] password) {

  }

  /**
   * 获取密码
   */
  @Override
  public byte[] getPassword() {
    return new byte[0];
  }

  /**
   * 设置保持连接的时长，以秒为单位
   *
   * @param keepAlive 保持连接时长(秒)
   */
  @Override
  public void setKeepAlive(int keepAlive) {

  }

  /**
   * 获取保持连接的时长，以秒为单位
   */
  @Override
  public int getKeepAlive() {
    return 0;
  }

  /**
   * 原始报文数据
   */
  @Override
  public byte[] getRaw() {
    return new byte[0];
  }

  /**
   * 获取标志位
   */
  @Override
  public byte getFlags() {
    return 0;
  }

  /**
   * 获取剩余长度
   */
  @Override
  public int getRemainingLength() {
    return 0;
  }

  /**
   * 获取报文数据
   */
  @Override
  public byte[] getPacket() {
    return null;
  }

  /**
   * 获取控制报文类型
   */
  @Override
  public ControlPacketType getType() {
    return null;
  }
}
