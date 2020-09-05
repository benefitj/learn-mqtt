package com.benefitj.mqtt;

import com.benefitj.core.HexUtils;
import com.benefitj.core.local.LocalCacheFactory;
import com.benefitj.core.local.LocalMapCache;

import java.util.Arrays;

/**
 * MQTT工具
 */
public class MqttUtils {
  public static void main(String[] args) {

//    System.err.println(HexUtils.bytesToHex("MQTT".getBytes(StandardCharsets.UTF_8)));
//    System.err.println(HexUtils.bytesToInt(new byte[]{0x00, 0x04}));


    // 0x04
    // 0b0000 0100

    System.err.println(HexUtils.mask((byte) 0b11011001, 1, 6)); // 1
    System.err.println(HexUtils.mask((byte) 0b11011001, 2, 7)); // 3
    System.err.println(HexUtils.mask((byte) 0b11011001, 3, 7)); // 6
    System.err.println(HexUtils.mask((byte) 0b11011001, 3, 6)); // 5
    System.err.println(HexUtils.mask((byte) 0b11011001, 3, 5)); // 3
    System.err.println(HexUtils.mask((byte) 0b11011001, 4, 3)); // 9
    System.err.println(HexUtils.mask((byte) 0b11011001, 4, 7)); // 13
    System.err.println(HexUtils.mask((byte) 0b11011001, 7, 6)); // 89
    System.err.println(HexUtils.mask((byte) 0b11011001, 7, 5)); // 89
    System.err.println(HexUtils.mask((byte) 0b11011001, 7, 7)); // 108
    System.err.println(HexUtils.mask((byte) 0b11011001, 8, 0)); // 217


    byte[] bytes = HexUtils.intToBytes(16384);
//    byte[] bytes = HexUtils.intToBytes(2097151);
//    byte[] bytes = HexUtils.intToBytes(2097152);
//    byte[] bytes = HexUtils.intToBytes(268435455);
    System.err.println(HexUtils.bytesToHex(bytes));
    System.err.println(HexUtils.bytesToHex(bytes, " | "));


  }

  // 0(0x00)                              127(0x7F)
  // 128(0x00)                            16383(0xFF 0x7F)
  // 16384(0x80 0x80 0x01)                2097151(0xFF 0xFF 0x7F)
  // 2097152(0x80 0x80 0x80 0x01)         268435455(0xFF 0xFF 0xFF 0x7F)

  /**
   * 协议支持的最大长度, 128 * 128 * 128 * 128 - 1
   */
  private static final int MAX_LENGTH = 268435455;

  /**
   * 缓存剩余长度的字节
   */
  private static final LocalMapCache<Integer, byte[]> BUFF_CACHE = LocalCacheFactory.newBytesWeakHashMapCache();


  /**
   * 获取缓存字节
   */
  private static byte[] getBuff(int size) {
    return getBuff(size, true);
  }

  /**
   * 获取软引用的缓存字节
   */
  private static byte[] getBuff(int size, boolean local) {
    byte[] buff = local ? BUFF_CACHE.computeIfAbsent(size) : new byte[size];
    if (local) {
      Arrays.fill(buff, (byte) 0x00);
    }
    return buff;
  }

  /**
   * 对剩余长度编码，最多4个字节
   *
   * @param length 长度
   * @return 返回剩余长度的字节
   */
  public static byte[] remainingLengthEncode(int length) {
    return remainingLengthEncode(length, false);
  }

  /**
   * 对剩余长度编码，最多4个字节
   *
   * @param length 长度
   * @param local  是否为本地缓存的字节数组
   * @return 返回剩余长度的字节
   */
  public static byte[] remainingLengthEncode(int length, boolean local) {
    if (length > MAX_LENGTH) {
      throw new IllegalArgumentException("Required max length " + MAX_LENGTH + ", current length: " + length);
    }
    // 每个字节的高位用于标识是否还有长度，低7位
    byte[] buff = getBuff(4);
    int index = 0;
    int x = length;
    do {
      buff[index] = (byte) (x % 128);
      x = x / 128;
      // if there are more data to encode, set the top bit of this byte
      if (x > 0)
        buff[index] = (byte) (buff[index] | 128);
      index++;
    } while (x > 0 && index < 4);
    if (index != buff.length || !local) {
      byte[] remainLength = getBuff(index, local);
      System.arraycopy(buff, 0, remainLength, 0, remainLength.length);
      return remainLength;
    }
    return buff;
  }

  /**
   * 解码的剩余长度
   *
   * @param remainLength 剩余长度字节
   * @return 返回解码后的剩余长度
   */
  public static int remainingLengthDecode(byte[] remainLength) {
    return remainingLengthDecode(remainLength, 0);
  }

  /**
   * 解码的剩余长度
   *
   * @param remainLength 剩余长度字节
   * @param start        开始的位置
   * @return 返回解码后的剩余长度
   */
  public static int remainingLengthDecode(byte[] remainLength, int start) {
    int multiplier = 1;
    int value = 0;
    byte encodedByte;
    for (int i = 0; i < 4; i++) {
      encodedByte = remainLength[i + start];
      value += (encodedByte & 127) * multiplier;
      if ((encodedByte & 128) == 0) {
        break;
      }
      multiplier *= 128;
      if (multiplier > MAX_LENGTH) {
        throw new IllegalArgumentException("Malformed Remaining Length");
      }
    }
    return value;
  }

}
