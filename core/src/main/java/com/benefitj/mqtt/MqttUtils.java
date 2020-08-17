package com.benefitj.mqtt;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * MQTT工具
 */
public class MqttUtils {
  /**
   * 协议支持的最大长度
   */
  private static final int MAX_LENGTH = 128 * 128 * 128 * 128;

  /**
   * 缓存剩余长度的字节
   */
  private static final ThreadLocal<SoftReference<byte[]>> REMAIN_LENGTH_BUFF = ThreadLocal.withInitial(() -> new SoftReference<>(new byte[4]));
  private static final ThreadLocal<Map<Integer, byte[]>> REMAIN_LENGTH_BUFF_MAP = ThreadLocal.withInitial(WeakHashMap::new);
  private static final Function<Integer, byte[]> REMAIN_LENGTH_BUFF_CREATOR = byte[]::new;

  /**
   * 获取软引用的缓存字节
   */
  private static byte[] getBuff() {
    byte[] buff = REMAIN_LENGTH_BUFF.get().get();
    if (buff == null) {
      REMAIN_LENGTH_BUFF.remove();
    }
    return buff;
  }

  /**
   * 获取缓存字节
   */
  private static byte[] getBuff(int size) {
    return getBuff(size, true);
  }

  /**
   * 获取缓存字节
   */
  private static byte[] getBuff(int size, boolean local) {
    if (local) {
      return REMAIN_LENGTH_BUFF_MAP.get().computeIfAbsent(size, REMAIN_LENGTH_BUFF_CREATOR);
    }
    return new byte[size];
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
    // 每个字节的高位用于标识是否还有长度，低7位
    byte[] buff = getBuff();
    Arrays.fill(buff, (byte) 0x00);
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
    byte[] remainLength = getBuff(index, local);
    System.arraycopy(buff, 0, remainLength, 0, remainLength.length);
    return remainLength;
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
    for (int i = 0; i < 4; i++) {
      byte encodedByte = remainLength[i + start];
      value += (encodedByte & 127) * multiplier;
      multiplier *= 128;
      if ((encodedByte & 128) == 0) {
        break;
      }
      if (multiplier > MAX_LENGTH) {
        throw new IllegalArgumentException("Malformed Remaining Length");
      }
    }
    return value;
  }

}
