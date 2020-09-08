package com.benefitj.mqtt.packet;

/**
 * 客户端连接
 *
 * 客户端连接到服务端后的第一个报文，只能发送一次，服务端返回 {@link ControlPacketType#CONNECT} 报文；
 * 有效载荷包括客户端的唯一标识符，Will主题，Will消息，用户名和密码；除了客户端标识之外，其它的字段都是可选的。
 *
 *
 * 控制报文   保留标志位(reserved)
 * 0 0 0 1    0 0 0 0
 *
 * 可变报头 (Variable Header)
 *
 *  CONNECT报文的可变报头按下列次序包含四个字段:
 *    协议名(Protocol Name)
 *    协议级别(Protocol Level)
 *    连接标志(Connect Flags)
 *    保持连接(Keep Alive)
 *
 *
 *  协议名 Protocol Name
 *    两个字节的长度 + 协议名的UTF-8编码的字节；
 *    如：MQTT协议，长度为4个字节，MQTT的UTF-8编码字符串，"MQTT".getBytes("UTF-8")；
 *    如果协议名不正确，服务端可以断开客户端连接；
 *
 *  协议级别 Protocol Level
 *    占一个字节，3.1.1版本的协议，值为4(0x04)，如果协议级别不支持，服务端必须返
 *    回一个 0x01 的 {@link ControlPacketType#CONNACK} 响应报文，然后断开客户端连接；
 *
 *
 *  连接标志位:
 *
 *      Bit      7         6          5           4  3           2             1             0
 *           username   password  will Retain   will QoS      will Flag   Clean Session    Reserved
 *     value    X          X          X             X             X             X            0
 *
 *     Clean Session(清理会话)：
 *       连接标志的第1位
 *       1.标志位 0：服务端必须基于当前的会话进行与客户端的通信；
 *         如果此会话不存在，服务端必须创建新的会话；
 *         连接断开后，客户端和服务端必须保存会话信息；
 *         为0的标志断开后，服务端将QoS1和QoS2级别的消息保存为会话的一部分，
 *         如果消息匹配客户端的任何订阅，服务端也可以保存相同条件的QoS0级别的消息；
 *
 *       2.标志位 1：客户端和服务端必须丢弃之前的任何会话，并开始新的会话；
 *         会话仅维持和网络连接同样长的时间，并且这个会话关联的状态不能被重用；
 *
 *       客户端的会话状态:
 *         (1). 已经发送给服务端，但还没有完成确认的QoS1和QoS2级别的消息；
 *         (2). 已从服务端接收，但还没有完成确认的QoS2级别的消息；
 *       服务端的会话状态:
 *         (1). 会话是否存在；
 *         (2). 客户端的订阅信息；
 *         (3). 已经发送给客户端，但还未完成确认的QoS1和QoS2级别的消息；
 *         (4). 即将传输给客户端的QoS1和QoS2级别的消息；
 *         (5). 已从客户端接收，但还未完成确认的QoS2级别的消息；
 *         (6). 可选，准备发送给客户端的QoS0级别的消息；
 *
 *       保留消息不是服务端会话状态的一部分，会话终止时不能删除保留消息；
 *       Clean Session标志为1时，客户端和服务端的状态删除不需要是原子操作；
 *
 *
 *       PS: 一般客户端连接时，会话标志只设置一种(0或1)，两种值不交替使用；
 *         如果不希望会话在重新建立连接后，收到旧的消息；可以将标志设置为1，主题需要重新订阅；
 *         Clean Session标志为0的会话，如果连接重新建立，会收到连接断开期间所有的QoS1和QoS2级别的消息；
 *
 *         如果希望在某个时间点重连到服务端，客户端会话应该使用标志0；
 *
 *     Will Flag(遗嘱标志):
 *       连接标志的第2位
 *       Will Flag为1，如果连接请求被接收，Will Message必须被存储到服务端，并且与这个连接关联；
 *       网络连接关闭时，服务端发布这个医嘱消息，除非服务端收到 {@link ControlPacketType#DISCONNECT} 报文时删除此遗嘱消息；
 *
 *       遗嘱消息发布的条件，包括但不限于：
 *         (1). 服务端监测到I/O错误，或网络故障；
 *         (2). 客户端在 Keep Alive 的时间内未能通讯；
 *         (3). 客户端未发送 {@link ControlPacketType#DISCONNECT} 报文就关闭了网络连接；
 *         (4). 协议错误，服务端关闭了网络连接；
 *
 *       如果遗嘱标志被设置为1，连接标志中的 Will QoS 和 Will Retain 字段会被服务端用到，
 *       同时，有效载荷中必须包含 Will Topic 和 Will Message 字段；客户端发送了 {@link ControlPacketType#DISCONNECT}
 *       报文，服务端必须将会话中的遗嘱状态移除；
 *
 *       如果遗嘱标志被设置为0，连接标志中的 Will QoS 和 Will Retain 字段必须设置为0，并且
 *       有效载荷中必须不包含 Will Topic 和 Will Message 字段；遗嘱标志为0的会话，不能发送遗嘱消息；
 *
 *       服务端应该迅速发布遗嘱消息，因关闭服务端或故障等情况下，服务端可以推迟遗嘱消息的发布，直至服务重启；
 *
 *
 *     遗嘱QoS (Will QoS)
 *       连接标志的第3和第4位，用于指定发布遗嘱消息时使用的服务质量等级，服务质量取 0x00、0x01、0x02；
 *
 *     遗嘱保留 (Will Retain)
 *       标志位的第5位，遗嘱标志位为0，Will Retain必须为0，否则可以为 0 或 1 ；
 *
 *     密码标志 (Password Flag)
 *       连接标志的第6位，标志位为 1，有效载荷必须包含密码字段；用户名标志位为0，密码标志位必须为0；
 *       否则，标志位为0，有效载荷必须不能包含密码；
 *
 *     用户名标志 (User Name Flag)
 *       连接标志的第7位，标志位为 1，有效载荷必须包含用户名，否则，不能包含用户名；
 *
 *  保持连接 (Keep Alive)
 *     保持连接，2个字节，以秒为单位，它表示客户端在两个报文之间的间隔；
 *     客户端负责两次报文之间的间隔不超时，客户端可以再任何时候发送 {@link ControlPacketType#PINGREQ} 报文；
 *     如果服务端在1.5倍的保持连接时间内没有收到客户端的报文，服务端必须断开连接；
 *     如果客户端在发送了 {@link ControlPacketType#PINGREQ} 报文之后，在合理的时间内未
 *     收到{@link ControlPacketType#PINGRESP}报文，客户端应该断开连接；
 *
 *     保持连接的值为0表示关闭保持连接功能，这意味着服务端不需要关心客户端的活跃程度；
 *     任何时候，服务端都可以关闭客户端的连接；
 *
 *
 *
 * 有效载荷 (Payload)
 *    CONNECT报文的有效载荷包含一个或多个以长度为前缀的字段，这些字段必须按照顺序出现：
 *        客户端标识符、遗嘱主题、遗嘱消息、用户名、密码
 *
 *    客户端标识符 (Client Identifier)
 *        每隔客户端都必须包含一个唯一的客户端标识符，客户端和服务端使用标识符识别会话状态；
 *        客户端标识符必须是 1~23 个字母或数字(a~z、A~Z、0~9)，服务端无此限制；
 *        服务端允许客户端标识符为零字节，这时服务端必须给客户端分配一个唯一的标识符，假设客户端提供了
 *           标识符，正常处理此CONNECT报文；
 *        客户端提供了0字节的客户端标识符，必须将Clean Session标志设置为1；否则服务端将给
 *           客户端发送 0x02 的CONNACK报文，并关闭此连接；
 *
 *    遗嘱主题 Will Topic
 *        遗嘱标志被设置为1，客户端标识符之后的字段便是遗嘱主题，必须是字符串；
 *
 *    遗嘱消息 Will Message
 *        遗嘱标志被设置为1，在遗嘱主题字段之后
 *
 *    用户名 User Name（同上，略）
 *    密码 Password
 *        密码除了2个字节的长度，紧跟着最多65535个字节长度的二进制数据
 *
 *
 * 报文响应 Response
 *     服务端可以支持多个版本的协议；
 *
 *     如果协议是MQTT3.1.1，连接验证如下：
 *      1. 连接建立后，如果服务端未在合理的时间内收到CONNECT报文，服务端应该直接关闭连接；
 *      2. 服务端按照上面的报文要求验证报文，如果不符合规范，服务端应该直接关闭连接；
 *      3. 如果服务端在报文符合规范后，继续做进一步检查，如身份认证和授权时，这些检查未通过，
 *         服务端应该发送一个非零的CONNACK报文，并关闭连接；
 *
 *     验证成功后的步骤如下：
 *      1. 如果客户端ID表明已连接到服务端，服务端必须断开原有的客户端；
 *      2. 服务端按照上面的 Clean Session 要求清理回话；
 *      3. 服务端发送一个为零的CONNACK报文作为CONNECT报文的确认响应；
 *      4. 分发消息和保持连接状态；
 *
 * @author DINGXIUAN
 */
public interface CONNECT extends ControlPacket {

  /**
   * 获取控制报文的类型
   */
  @Override
  ControlPacketType getType();

  /**
   * 获取客户端标识
   */
  String getClientId();

  /**
   * 设置客户端标识
   *
   * @param clientId 客户端标识
   */
  void setClientId(String clientId);

  /**
   * 获取协议名
   */
  String getProtocolName();

  /**
   * 设置协议名
   *
   * @param protocolName 协议名
   */
  void setProtocolName(String protocolName);

  /**
   * 获取协议等级
   */
  int getProtocolLevel();

  /**
   * 设置协议等级
   *
   * @param protocolLevel 协议等级
   */
  void setProtocolLevel(int protocolLevel);

  /**
   * 是否清理回话
   */
  boolean isCleanSession();

  /**
   * 设置是否清理回话
   *
   * @param cleanSession 是否清理
   */
  void setCleanSession(boolean cleanSession);

  /**
   * 是否设置遗嘱标志
   */
  boolean isWillFlag();

  /**
   * 设置遗嘱标志
   *
   * @param willFlag 是否设置
   */
  void setWillFlag(boolean willFlag);

  /**
   * 获取 Will QoS，服务质量，
   */
  int getWillQoS();

  /**
   * 设置 Will QoS
   *
   * @param willQoS 值
   */
  void setWillQoS(int willQoS);

  /**
   * 获取 Will Retain
   */
  boolean isWillRetain();

  /**
   * 设置 Will Retain
   *
   * @param willRetain 值
   */
  void setWillRetain(boolean willRetain);

  /**
   * 获取遗嘱主题
   */
  String getWillTopic();

  /**
   * 设置遗嘱主题
   *
   * @param willTopic 遗嘱主题
   */
  void setWillTopic(String willTopic);

  /**
   * 获取遗嘱消息
   */
  String getWillMessage();

  /**
   * 设置遗嘱消息
   *
   * @param willMessage 遗嘱消息
   */
  void setWillMessage(String willMessage);

  /**
   * 获取用户名
   */
  String getUsername();

  /**
   * 设置用户名
   *
   * @param username 用户名
   */
  void setUsername(String username);

  /**
   * 获取密码
   */
  byte[] getPassword();

  /**
   * 设置密码
   *
   * @param password 密码
   */
  void setPassword(byte[] password);

  /**
   * 获取保持连接的时长，以秒为单位
   */
  int getKeepAlive();

  /**
   * 设置保持连接的时长，以秒为单位
   *
   * @param keepAlive 保持连接时长(秒)
   */
  void setKeepAlive(int keepAlive);

}
