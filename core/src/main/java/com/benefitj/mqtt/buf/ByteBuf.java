package com.benefitj.mqtt.buf;

public class ByteBuf {

  /**
   * 数据缓冲区
   */
  private byte[] buf;
  /**
   * 读取的位置
   */
  private int readerIndex = 0;
  /**
   * 写入的位置
   */
  private int writerIndex = 0;

  public ByteBuf() {
    this(new byte[256], 0, 0);
  }

  public ByteBuf(byte[] buf) {
    this(buf, 0, buf.length);
  }

  public ByteBuf(byte[] buf, int readerIndex, int writerIndex) {
    this.setBuf(buf);
    this.setReaderIndex(readerIndex);
    this.setWriterIndex(writerIndex);
  }

  /**
   * @return 获取缓冲数组
   */
  public byte[] getBuf() {
    return buf;
  }

  /**
   * 设置缓冲数组
   *
   * @param buf 数组
   */
  public ByteBuf setBuf(byte[] buf) {
    this.buf = buf;
    return this;
  }

  /**
   * @return 获取读取的下标
   */
  public int getReaderIndex() {
    return readerIndex;
  }

  /**
   * 设置读取的下标
   *
   * @param readerIndex 下标
   */
  public ByteBuf setReaderIndex(int readerIndex) {
    this.readerIndex = readerIndex;
    return this;
  }

  /**
   * @return 获取写入的下标
   */
  public int getWriterIndex() {
    return writerIndex;
  }

  /**
   * 设置写入的下标
   *
   * @param writerIndex 下标
   */
  public ByteBuf setWriterIndex(int writerIndex) {
    this.writerIndex = writerIndex;
    return this;
  }

  /**
   * 设置位置
   *
   * @param readerIndex 读取位置
   * @param writerIndex 写入位置
   */
  public ByteBuf setIndex(int readerIndex, int writerIndex) {
    this.setReaderIndex(readerIndex);
    this.setWriterIndex(writerIndex);
    return this;
  }

  /**
   * @return 获取缓冲区大小
   */
  public int capacity() {
    return getBuf().length;
  }

  public boolean isPositive() {
    return writerIndex > readerIndex;
  }

  /**
   * @return 获取可读取长度
   */
  public int readableBytes() {
    return isPositive()
        ? writerIndex - readerIndex
        : capacity() - (readerIndex - writerIndex);
  }

  /**
   * @return 获取可写入长度
   */
  public int writableBytes() {
    int remaining = isPositive()
        ? writerIndex - readerIndex
        : readerIndex - writerIndex;
    return capacity() - remaining;
  }

  /**
   * 清空读写
   */
  public ByteBuf clear() {
    this.setIndex(0, 0);
    return this;
  }

  /**
   * 写入
   *
   * @param value 数据
   */
  public ByteBuf writeByte(byte value) {
    checkWritableBound(this, 1);
    return writeByte0(this, value);
  }


  /**
   * 写入
   *
   * @param src 数据
   */
  public ByteBuf write(byte[] src) {
    return write(src, 0, src.length);
  }

  /**
   * 写入
   *
   * @param src   数据
   * @param start 开始位置
   * @param len   长度
   */
  public ByteBuf write(byte[] src, int start, int len) {
    return write(src, start, this, len);
  }

  /**
   * 写入
   *
   * @param src 数据
   */
  public ByteBuf write(ByteBuf src) {
    return write(src, src.getReaderIndex(), src.readableBytes());
  }

  /**
   * 写入
   *
   * @param src   数据
   * @param start 开始位置
   * @param len   长度
   */
  public ByteBuf write(ByteBuf src, int start, int len) {
    return write(src.getBuf(), start, this, len);
  }

  /**
   * 写入
   *
   * @param src    原数据
   * @param srcPos 原数据开始的位置
   * @param dest   目标缓冲
   * @param len    写入数据的长度
   */
  public ByteBuf write(byte[] src, int srcPos, ByteBuf dest, int len) {
    checkWritableBound(dest, len);
    // copy bytes
    byte[] buf = dest.getBuf();
    if (dest.isPositive()) {
      int positiveLen = dest.capacity() - dest.getWriterIndex();
      // from writerIndex ~> capacity
      dest.setBytes(src, srcPos, buf, dest.getWriterIndex(), positiveLen);
      // from 0 ~> readerIndex
      dest.setBytes(src, srcPos, buf, 0, len - positiveLen);
      setWriterIndex(dest, len);
    } else {
      // from writerIndex ~> readerIndex
      dest.setBytes(src, srcPos, buf, dest.getWriterIndex(), len);
      setWriterIndex(dest, len);
    }
    return dest;
  }

  protected ByteBuf setBytes(byte[] src, int srcPos, byte[] dest, int destPos, int len) {
    if (len >= 0) {
      System.arraycopy(src, srcPos, dest, destPos, len);
    }
    return this;
  }

  private ByteBuf wrap(byte[] buf, int start) {
    return new ByteBuf(buf, start, start);
  }

  public byte[] read(int size) {
    ByteBuf src = read(new byte[size], 0, size);
    return src.getBuf();
  }

  public ByteBuf read(byte[] dest, int destPos, int len) {
    ByteBuf src = wrap(dest, destPos);
    return read(this, src, len);
  }

  public static ByteBuf read(ByteBuf src, ByteBuf dest, int len) {
    checkReadableBound(src, len);
    // 读取
    // 如果 writerIndex > readerIndex ? 直接读取 : 计算读取逻辑
    if (src.isPositive()) {
      for (int i = 0; i < len; i++) {
        dest.writeByte(src.readByte());
      }
    }


    return dest;
  }

  public byte readByte() {
    checkReadableBound(this, 1);
    return readByte0(this);
  }

  /**
   * 写入数据
   *
   * @param buf   字节缓冲对象
   * @param value 数值
   */
  protected static ByteBuf writeByte0(ByteBuf buf, byte value) {
    return setByte0(buf, value, setWriterIndex(buf, 1));
  }

  /**
   * 设置单个字节
   *
   * @param buf   缓冲区
   * @param value 值
   * @param index 索引
   * @return 返回缓冲区
   */
  protected static ByteBuf setByte0(ByteBuf buf, byte value, int index) {
    buf.getBuf()[index] = value;
    return buf;
  }

  /**
   * 设置新的写入的位置
   *
   * @param buf 缓冲区
   * @param len 长度
   * @return 返回改变之前的位置
   */
  protected static int setWriterIndex(ByteBuf buf, int len) {
    int index = buf.writerIndex;
    buf.setWriterIndex(buf.capacity() % (index + len));
    return index;
  }

  /**
   * 读取数据
   *
   * @param buf 缓冲区
   */
  protected static byte readByte0(ByteBuf buf) {
    return getByte0(buf, setReaderIndex(buf, 1));
  }

  /**
   * 读取单个字节
   *
   * @param buf   缓冲区
   * @param index 索引
   * @return 返回读取的字节
   */
  protected static byte getByte0(ByteBuf buf, int index) {
    return buf.getBuf()[index];
  }

  /**
   * 设置新的读取的位置
   *
   * @param buf 缓冲区
   * @param len 长度
   * @return 返回改变之前的位置
   */
  protected static int setReaderIndex(ByteBuf buf, int len) {
    int index = buf.readerIndex;
    buf.setReaderIndex(buf.capacity() % (index + len));
    return index;
  }

  protected static void checkWritableBound(ByteBuf buf, int size) {
    if (buf.writableBytes() < size) {
      throw new IllegalStateException("剩余可写入长度不足，写入长度: " + size + ", 可写入长度: " + buf.writableBytes());
    }
  }

  protected static void checkReadableBound(ByteBuf buf, int size) {
    if (buf.readableBytes() < size) {
      throw new IllegalArgumentException("数据可读取长度不足， 读取长度: " + size + ", 可读取长度: " + buf.readableBytes());
    }
  }


}
