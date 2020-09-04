package com.benefitj.mqtt;

public enum QoS {

  /**
   *
   */
  QoS_0((byte)0),
  QoS_1((byte)1),
  QoS_2((byte)2);

  private final byte value;

  QoS(byte value) {
    this.value = value;
  }

  public byte getValue() {
    return value;
  }

}
