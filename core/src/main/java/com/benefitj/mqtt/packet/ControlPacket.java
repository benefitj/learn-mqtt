package com.benefitj.mqtt.packet;

import com.benefitj.mqtt.MqttUtils;

/**
 * 控制报文
 */
public interface ControlPacket<T> {

  /**
   * 原始报文数据
   */
  byte[] getRaw();

  /**
   * 获取标志位
   */
  byte getFlags();

  /**
   * 获取剩余长度
   */
  int getRemainingLength();

  /**
   * 获取报文数据
   */
  T getPacket();

  /**
   * 获取控制报文类型
   */
  ControlPacketType getType();

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
  static int getRemainingLength(byte[] raw) {
    return getRemainingLength(raw, 0);
  }

  /**
   * 剩余长度解码
   *
   * @param raw   数据
   * @param start 开始的位置
   * @return 返回剩余长度
   */
  static int getRemainingLength(byte[] raw, int start) {
    return MqttUtils.remainingLengthDecode(raw, start);
  }

}
