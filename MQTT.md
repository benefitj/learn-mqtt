## MQTT

### 定义

1. MQTT是一个客户端服务端架构的发布/订阅模式的消息传输协议
2. 应用消息通过网络传输到订阅的相关主题(topic)与服务质量(QoS)的客户端

### 客户端

客户端指发布消息的设备或消费消息的订阅者

#### 功能

- 连接服务端，订阅服务端消息
- 设备将数据发送给服务端
- 订阅者订阅服务端的消息

### 服务端

服务端是实现MQTT协议的代理程序，用于转发设备和订阅者之间的消息

#### 功能

- 接收客户端的连接，接收设备消息
- 接收客户端的订阅，将消息分发给订阅者

### 一些名词

#### 订阅（Subscription）

- 订阅包括一个主题过滤器(Topic Filter)和一个最大的服务质量等级(QoS)。
- 每个会话(Session)可以包含一个或多个订阅，每个订阅都有一个不同的主题过滤器。

#### 主题名（Topic Name）

- 附加在消息上，与订阅匹配的标签

#### 主题过滤器（Topic Filter）

- 订阅的表达式，用于匹配订阅时符合的消息

#### 会话（Session）

- 客户端和服务端的交互状态
- 一些会话与网络连接保持一致，另一些可以在客户端和服务端的多个网络连接之间扩展

#### 控制报文（MQTT Control Packet）

- 通过网络连接发送的信息数据包
- MQTT定义14种不同类型的控制报文

### 数据表示(Data representations)

#### 字节

#### 整数：16位，大端存储(高字节在前，低字节在后)

#### UTF-8编码字符串

- 编码长度在0~65535范围之内
- 不能包含的字符

	- U=D800和U+DFFF之间的字符
	- 空字符 U+0000
	- U+0001和U+001F之间的控制字符
	- U+007F和U+009F之间的控制字符
	- Unicode规范定义的非字符代码点（ 例如U+0FFFF）
	- Unicode规范定义的保留字符（ 例如U+0FFFF）

### 控制报文格式(MQTT Control Packet format)

#### 报文结构

- 控制报文由三部分组成：固定报文头 + 可变报文头 + 有效载荷

##### 固定报文头

 - 第一个字节的前4位用于指定控制报文的标志位，后4位表示MQTT控制报文的类型；第二个字节为剩余长度，剩余长度可能包含多个字节；
 - 控制报文类型


    名字          值     报文流动方向                 描述
    Reserved      0      禁止                       保留
    CONNECT       1      客户端到服务端              客户端请求连接服务端
    CONNACK       2      服务端到客户端              连接报文确认
    PUBLISH       3      两个方向都允许              发布消息
    PUBACK        4      两个方向都允许              QoS 1消息发布收到确认
    PUBREC        5      两个方向都允许              发布收到（ 保证交付第一步）
    PUBREL        6      两个方向都允许              发布释放（ 保证交付第二步）
    PUBCOMP       7      两个方向都允许              QoS 2消息发布完成（ 保证交互第三步）
    SUBSCRIBE     8      客户端到服务端              客户端订阅请求
    SUBACK        9      服务端到客户端              订阅请求报文确认
    UNSUBSCRIBE   10     客户端到服务端              客户端取消订阅请求
    UNSUBACK      11     服务端到客户端              取消订阅报文确认
    PINGREQ       12     客户端到服务端              心跳请求
    PINGRESP      13     服务端到客户端              心跳响应
    DISCONNECT    14     客户端到服务端              客户端断开连接
    Reserved      15     禁止                       保留


 - 标志 Flags

    第一个字节的前4位(0~3)，收到非法的标志位必须关闭连接；


    控制报文     固定报头标志        Bit 3        Bit 2        Bit 1        Bit 0
    CONNECT     Reserved           0            0            0            0
    CONNACK     Reserved           0            0            0            0
    PUBLISH    Used in MQTT 3.1.1  DUP         QoS          QoS          RETAIN
    PUBACK      Reserved           0            0            0            0
    PUBREC      Reserved           0            0            0            0
    PUBREL      Reserved           0            0            1            0
    PUBCOMP     Reserved           0            0            0            0
    SUBSCRIBE   Reserved           0            0            1            0
    SUBACK      Reserved           0            0            0            0
    UNSUBSCRIBE Reserved           0            0            1            0
    UNSUBACK    Reserved           0            0            0            0
    PINGREQ     Reserved           0            0            0            0
    PINGRESP    Reserved           0            0            0            0
    DISCONNECT  Reserved           0            0            0            0

 - DUP = 控制报文的重复分发标志
 - QoS = PUBLISH报文的服务质量等级
 - RETAIN = PUBLISH报文的保留标志


- 剩余长度(Remaining Length)
  
    - 固定报文头的第二个字节开始，表示当前报文的剩余字节数，包括可变报文头和有效载荷；
    - 第二个字节的前7位(0~127)表示长度，如果长度超过127，延续一个字节，以此类推，最多4个字节；
    - 128为 0x80,0x01       0x80 + 0x01 = 128     如果最高位无符号左移7位等于1，表示还有数据，依次类推

    PS. 剩余长度的计算可参考Java代码


##### 可变报文头

  可变报头在固定报头之后，根据报头的内容报文类型而不同，可变报头的标识符(Packet Identifier)存在于多个类型的报文里；

  1. `PUBLISH(QoS > 0时), PUBACK, PUBREC, PUBREL, PUBCOMP, SUBSCRIBE, SUBACK, UNSUBSCRIBE, UNSUBACK`的控制报文的可变报头包含一个两字节的报文标识符;
  2. SUBSCRIBE, UNSUBSCRIBE和PUBLISH(QoS大于0)控制报文必须包含一个非零的16位报文标识符(Packet Identifier);
  3. 客户端每次发送一个新的这些类型的报文时都必须分配一个当前未使用的报文标识符；如果客户端对报文重发，需要标识符与发送时一致；
  4. 客户端发送完报文得到确认后，可释放标识符；
  5. QoS 1的PUBLISH对应的是PUBACK，QoS 2的PUBLISH对应的是PUBCOMP，与SUBSCRIBE或UNSUBSCRIBE对应的分别是SUBACK或UNSUBACK；
  6. QoS 0的PUBLISH报文的条件也适用于服务端；
  7. 客户端与服务端可独立的使用标识符，以提高并发性；

  下面是控制报文是否需要报文标识符

    控制报文      报文标识符字段
    CONNECT      不需要
    CONNACK      不需要
    PUBLISH      需要（ 如果QoS > 0）
    PUBACK       需要
    PUBREC       需要
    PUBREL       需要
    PUBCOMP      需要
    SUBSCRIBE    需要
    SUBACK       需要
    UNSUBSCRIBE  需要
    UNSUBACK     需要
    PINGREQ      不需要
    PINGRESP     不需要
    DISCONNECT   不需要




##### 有效载荷

  包含有效载荷的控制报文(Control Packets that contain a Payload)

    控制报文      有效载荷
    CONNECT      需要
    CONNACK      不需要
    PUBLISH      可选
    PUBACK       不需要
    PUBREC       不需要
    PUBREL       不需要
    PUBCOMP      不需要
    SUBSCRIBE    需要
    SUBACK       需要
    UNSUBSCRIBE  需要
    UNSUBACK     不需要
    PINGREQ      不需要
    PINGRESP     不需要
    DISCONNECT   不需要




