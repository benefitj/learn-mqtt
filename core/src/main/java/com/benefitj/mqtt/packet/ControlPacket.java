package com.benefitj.mqtt.packet;

/**
 * 控制报文
 */
public interface ControlPacket {

  /**
   * 获取控制报文的类型
   */
  ControlPacketType getType();

}
