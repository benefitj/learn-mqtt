package com.benefitj.mqtt.packet;

/**
 * 客户端连接
 *
 * 客户端连接到服务端后的第一个报文，只能发送一次，服务端返回CONNECTED报文；
 * 有效载荷包括客户端的唯一标识符，Will主题，Will消息，用户名和密码；除了客户端标识之外，其它的字段都是可选的。
 *
 *
 * 控制报文   保留标志位(reserved)
 * 0 0 0 1    0 0 0 0

 * CONNECT报文的可变报头按下列次序包含四个字段:
 *  协议名(Protocol Name)
 *  协议级别(Protocol Level)
 *  连接标志(Connect Flags)
 *  保持连接(Keep Alive)
 *
 *  连接标志位:
 *
 *   Bit      7         6          5           4  3           2             1             0
 *        username   password  will Retain   will QoS      will Flag   Clean Session    Reserved
 *  value    X          X          X             X             X             X            0
 *
 *  Clean Session(清理会话)：
 *    1.标志为 0：服务端必须基于当前的会话进行与客户端的通信；
 *      如果此会话不存在，服务端必须创建新的会话；
 *      连接断开后，客户端和服务端必须保存会话信息；
 *      为0的标志断开后，服务端将QoS1和QoS2级别的消息保存为会话的一部分，
 *      如果消息匹配客户端的任何订阅，服务端也可以保存相同条件的QoS0级别的消息；
 *
 *    2.标志位 1：客户端和服务端必须丢弃之前的任何会话，并开始新的会话；
 *      会话仅维持和网络连接同样长的时间，并且这个会话关联的状态不能被重用；
 *
 *    客户端的会话状态:
 *      (1). 已经发送给服务端，但还没有完成确认的QoS1和QoS2级别的消息；
 *      (2). 已从服务端接收，但还没有完成确认的QoS2级别的消息；
 *    服务端的会话状态:
 *      (1). 会话是否存在；
 *      (2). 客户端的订阅信息；
 *      (3). 已经发送给客户端，但还未完成确认的QoS1和QoS2级别的消息；
 *      (4). 即将传输给客户端的QoS1和QoS2级别的消息；
 *      (5). 已从客户端接收，但还未完成确认的QoS2级别的消息；
 *      (6). 可选，准备发送给客户端的QoS0级别的消息；
 *
 *    保留消息不是服务端会话状态的一部分，会话终止时不能删除保留消息；
 *    Clean Session标志为1时，客户端和服务端的状态删除不需要是原子操作；
 *
 *
 *    PS: 一般客户端连接时，会话标志只设置一种(0或1)，两种值不交替使用；
 *        如果不希望会话在重新建立连接后，收到旧的消息；可以将标志设置为1，主题需要重新订阅；
 *        Clean Session标志为0的会话，如果连接重新建立，会收到连接断开期间所有的QoS1和QoS2级别的消息；
 *
 *        如果希望在某个时间点重连到服务端，客户端会话应该使用标志0；
 *
 * Will Flag(遗嘱标志):
 *     Will Flag为1，如果连接请求被接收，Will Message必须被存储到服务端，并且与这个连接关联；
 *     网络连接关闭时，服务端发布这个医嘱消息，除非服务端收到DISCONNECT报文时删除此遗嘱消息；
 *
 *     遗嘱消息发布的条件，包括但不限于：
 *        (1). 服务端监测到I/O错误，或网络故障；
 *        (2). 客户端在
 *
 */
public interface CONNECT<T> extends ControlPacket<T> {

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
   * 获取连接标志
   */
  byte getConnectFlags();

  /**
   * 设置连接标志
   *
   * @param connectFlags 连接标志
   */
  void setConnectFlags(byte connectFlags);



}
