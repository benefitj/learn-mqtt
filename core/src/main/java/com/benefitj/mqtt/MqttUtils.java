package com.benefitj.mqtt;

import com.benefitj.core.HexUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.local.LocalCacheFactory;
import com.benefitj.core.local.LocalMapCache;
import com.benefitj.mqtt.buf.ByteBuf;
import com.benefitj.mqtt.packet.CONNECT;
import com.benefitj.mqtt.packet.ControlPacketType;
import com.benefitj.mqtt.packet.impl.ConnectImpl;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * MQTT工具
 * <p>
 * 报文格式：
 * Fixed Header   Variable Header    Payload
 * 固定报头     +   可变报头      +   有效载荷
 * <p>
 * 固定报头： 1个字节(报文类型 + 标志位) +  1 ~ 4 个字节的剩余长度
 * 可变报头： 参考控制报文
 * 有效载荷： ......
 */
public class MqttUtils {
  public static void main(String[] args) {

//    System.err.println(HexUtils.bytesToHex("MQTT".getBytes(StandardCharsets.UTF_8)));
//    System.err.println(HexUtils.bytesToInt(new byte[]{0x00, 0x04}));


    // 0x04
    // 0b0000 0100

//    System.err.println(HexUtils.mask((byte) 0b11011001, 1, 6)); // 1
//    System.err.println(HexUtils.mask((byte) 0b11011001, 2, 7)); // 3
//    System.err.println(HexUtils.mask((byte) 0b11011001, 3, 7)); // 6
//    System.err.println(HexUtils.mask((byte) 0b11011001, 3, 6)); // 5
//    System.err.println(HexUtils.mask((byte) 0b11011001, 3, 5)); // 3
//    System.err.println(HexUtils.mask((byte) 0b11011001, 4, 3)); // 9
//    System.err.println(HexUtils.mask((byte) 0b11011001, 4, 7)); // 13
//    System.err.println(HexUtils.mask((byte) 0b11011001, 7, 6)); // 89
//    System.err.println(HexUtils.mask((byte) 0b11011001, 7, 5)); // 89
//    System.err.println(HexUtils.mask((byte) 0b11011001, 7, 7)); // 108
//    System.err.println(HexUtils.mask((byte) 0b11011001, 8, 0)); // 217


////    byte[] bytes = HexUtils.intToBytes(16384);
////    byte[] bytes = HexUtils.intToBytes(2097151);
////    byte[] bytes = HexUtils.intToBytes(2097152);
////    byte[] bytes = HexUtils.intToBytes(268435455);
//    byte[] bytes = HexUtils.intToBytes(278435455);
//    System.err.println(HexUtils.bytesToHex(bytes));
//    System.err.println(HexUtils.bytesToHex(bytes, " | "));


    ConnectImpl connect = new ConnectImpl();
    // 设置 client ID
    connect.setClientId(IdUtils.nextId("mqtt", null, 12));
    // 协议名
    connect.setProtocolName("MQTT");
    // 协议等级
    connect.setProtocolLevel(4);
    // 遗嘱标志
    connect.setWillFlag(true);
    // 遗嘱保留
    connect.setWillRetain(true);
    // 遗嘱 QoS
    connect.setWillQoS(0);
    // 遗嘱主题
    connect.setWillTopic("test");
    // 遗嘱消息
    connect.setWillMessage("呵呵, game over!");
    // 用户名
    connect.setUsername("admin");
    // 密码
    connect.setPassword("123456".getBytes());
    // 保持30秒
    connect.setKeepAlive(30);

    byte[] encode = encode(connect);
    System.err.println("encode: " + HexUtils.bytesToHex(encode));

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
      if (x > 0) {
        buff[index] = (byte) (buff[index] | 128);
      }
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

  public static byte[] encode(CONNECT connect) {
    if (StringUtils.isEmpty(connect.getClientId())) {
      throw new IllegalArgumentException("Required client id");
    }

    // 检查 will topic 和 will message
    if (connect.isWillFlag() && (StringUtils.isBlank(connect.getWillTopic())
        || StringUtils.isBlank(connect.getWillMessage()))) {
      throw new IllegalStateException("连接标志为1，必须设置will topic和will message");
    }

    final ByteBuf buf = new ByteBuf(1024);
    // 固定报头
    ControlPacketType packetType = connect.getType();
    //byte type = (byte) (packetType.getValue() << 4);
    ////type |= type & connect.geFlags(); // flags为0000
    //buf.put(type); // fixed header


    // 固定报头:
    //  类型和标志位00010000
    //  剩余长度(待计算)


    // 可变报头: 协议名(2 + n)、协议等级(1)、连接标志(1)、保持连接(2)

    // 协议名
    put(buf, connect.getProtocolName(), "MQTT");
    // 协议等级
    buf.writeByte((byte) (connect.getProtocolLevel() & 0xFF));

    // 连接标志
    // Bit   7         6          5          4  3       2           1             0
    //  *  username  password  will Retain  will QoS  will Flag  Clean Session  Reserved
    byte connectFlags = 0b00000000;
    // 清理回话
    if (connect.isCleanSession()) {
      connectFlags |= 0b00000010;
    }
    // 遗嘱标志
    if (connect.isWillFlag()) {
      // Will Flag
      connectFlags |= 0b00000100;
      // Will QoS
      connectFlags |= ((byte) 0b00011000);
      // Will Retain
      if (connect.isWillRetain()) {
        connectFlags |= 0b00100000;
      }
    }
    if (StringUtils.isNotBlank(connect.getUsername())) {
      connectFlags |= 0b10000000;
      // 密码
      if (connect.getPassword() != null) {
        connectFlags |= 0b01000000;
      }
    }
    // 连接标志
    buf.writeByte(connectFlags);
    // 保持连接
    buf.write(shortToBytes((short) connect.getKeepAlive()));

    // 有效载荷 ...

    // 客户端标识
    put(buf, connect.getClientId());
    // will topic
    put(buf, connect.getWillTopic());
    // will message
    put(buf, connect.getWillMessage());
    // username
    put(buf, connect.getUsername());
    // password
    put(buf, connect.getPassword());

    System.err.println("readBytes ==>: " + HexUtils.bytesToHex(buf.readBytes()));

    return null;
  }


  private static ByteBuf put(ByteBuf buf, String s) {
    return put(buf, s, null);
  }

  private static ByteBuf put(ByteBuf buf, String s, String defaultValue) {
    if (StringUtils.isNotBlank(defaultValue) && StringUtils.isBlank(s)) {
      s = defaultValue;
    }
    if (StringUtils.isNotEmpty(s)) {
      return put(buf, s.getBytes(StandardCharsets.UTF_8));
    }
    return put(buf, (byte[]) null);
  }

  private static ByteBuf put(ByteBuf buf, byte[] data) {
    return put(buf, data, true);
  }

  private static ByteBuf put(ByteBuf buf, byte[] data, boolean length) {
    if (data != null) {
      buf.write(shortToBytes(data.length));
      buf.write(data);
    } else {
      if (length) {
        buf.write(shortToBytes(0));
      }
    }
    return buf;
  }

  public static byte[] shortToBytes(int v) {
    return shortToBytes((short) v);
  }

  public static byte[] shortToBytes(short v) {
    return HexUtils.shortToBytes(v);
  }

//
//  /**
//   * 控制报文头
//   *
//   * @param packet 报文
//   * @return 返回报文头
//   */
//  public static byte[] encodeFixedHeader(ControlPacket packet) {
//    return encodeFixedHeader(packet, true);
//  }
//
//  /**
//   * 控制报文头
//   *
//   * @param packet 报文
//   * @param local  是否为本地缓冲数据
//   * @return 返回报文头
//   */
//  public static byte[] encodeFixedHeader(ControlPacket packet, boolean local) {
//
//
//  }

}
