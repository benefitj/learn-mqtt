package com.benefitj.mqtt.packet.impl;

import com.benefitj.mqtt.packet.ControlPacket;
import com.benefitj.mqtt.packet.ControlPacketType;
import com.benefitj.mqtt.packet.ControlPacketWrapper;

/**
 * 控制报文包装器实现
 *
 * @param <T>
 */
public class ControlPacketWrapperImpl<T extends ControlPacket> implements ControlPacketWrapper<T> {

  /**
   * 原始包文
   */
  private byte[] raw;
  /**
   * 报文实体
   */
  private T packet;
  /**
   * 控制报文类型
   */
  private ControlPacketType packetType;
  /**
   * 标志位
   */
  private byte flags;

  public ControlPacketWrapperImpl(byte[] raw, T packet) {
    this.raw = raw;
    this.packet = packet;
  }

  /**
   * 原始报文数据
   */
  @Override
  public byte[] getRaw() {
    return this.raw;
  }

  /**
   * 设置原始报文数据
   *
   * @param raw 数据
   */
  @Override
  public void setRaw(byte[] raw) {
    this.raw = raw;
  }

  /**
   * 获取报文
   */
  @Override
  public T getPacket() {
    return this.packet;
  }

  /**
   * 设置报文
   *
   * @param packet 报文
   */
  @Override
  public void setPacket(T packet) {
    this.packet = packet;
  }

  /**
   * 获取控制报文类型
   */
  @Override
  public ControlPacketType getPacketType() {
    return this.packetType;
  }

  /**
   * 设置控制报文类型
   *
   * @param packetType 报文类型
   */
  @Override
  public void setPacketType(ControlPacketType packetType) {
    this.packetType = packetType;
  }

  /**
   * 获取标志位
   */
  @Override
  public byte getFlags() {
    return this.flags;
  }

  /**
   * 设置标志位
   *
   * @param flags 标志位
   */
  @Override
  public void setFlags(byte flags) {
    this.flags = flags;
  }

  /**
   * 获取剩余长度
   */
  @Override
  public int getRemainingLength() {
    byte[] raw = getRaw();
    return getRemainingLength(raw);
  }
}
