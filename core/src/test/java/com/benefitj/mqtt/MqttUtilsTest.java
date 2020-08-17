package com.benefitj.mqtt;


import com.benefitj.core.HexTools;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MqttUtilsTest {

  @BeforeEach
  public void setUp() {
  }

  @AfterEach
  public void tearDown() {
  }

  /**
   * 测试剩余长度的编解码
   */
  @Test
  public void remainLengthCodec() {
//    int length = 128; // 128 (0x80, 0x01)
//    int length = 16383; // 16 383 (0xFF, 0x7F)
//    int length = 2097152; // 2 097 152 (0x80, 0x80, 0x80, 0x01)
    int length = 268435455; // 268 435 455 (0xFF, 0xFF, 0xFF, 0x7F)

    byte[] remainLength = MqttUtils.remainingLengthEncode(length);
    System.err.println("remainLengthEncode: " + HexTools.byteToHex(remainLength));
    System.err.println("remainLengthDecode: " + MqttUtils.remainingLengthDecode(remainLength));
  }

}