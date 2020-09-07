package com.benefitj.mqtt.packet;

import com.benefitj.mqtt.MqttUtils;

/**
 * 控制报文包装器
 */
public interface ControlPacketWrapper<T extends ControlPacket> {

  /**
   * 原始报文数据
   */
  byte[] getRaw();

  /**
   * 设置原始报文数据
   *
   * @param raw 数据
   */
  void setRaw(byte[] raw);

  /**
   * 获取报文
   */
  T getPacket();

  /**
   * 设置报文
   *
   * @param packet 报文
   */
  void setPacket(T packet);

  /**
   * 获取控制报文类型
   */
  ControlPacketType getPacketType();

  /**
   * 设置控制报文类型
   *
   * @param packetType 报文类型
   */
  void setPacketType(ControlPacketType packetType);

  /**
   * 获取标志位
   */
  byte getFlags();

  /**
   * 设置标志位
   *
   * @param flags 标志位
   */
  void setFlags(byte flags);

  /**
   * 获取剩余长度
   */
  int getRemainingLength();

//  /**
//   * 控制报文的重复分发标志
//   */
//  boolean isDUP();
//
//  /**
//   * 报文的服务质量等级
//   */
//  byte getQoS();

  /**
   * 剩余长度解码
   *
   * @param raw 数据
   * @return 返回剩余长度
   */
  default int getRemainingLength(byte[] raw) {
    return getRemainingLength(raw, 0);
  }

  /**
   * 剩余长度解码
   *
   * @param raw   数据
   * @param start 开始的位置
   * @return 返回剩余长度
   */
  default int getRemainingLength(byte[] raw, int start) {
    return MqttUtils.remainingLengthDecode(raw, start);
  }

}
