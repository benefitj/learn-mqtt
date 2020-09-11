package com.benefitj.mqtt.buf;

import java.util.Arrays;

/**
 * 字节缓冲
 */
public class ByteArrayBuf {

  public static final byte[] EMPTY = new byte[0];

  /**
   * 数据缓冲区
   */
  private byte[] array;
  /**
   * 读取的位置
   */
  private int readerIndex = 0;
  /**
   * 写入的位置
   */
  private int writerIndex = 0;
  /**
   * 是否仅仅可读取
   */
  private boolean readonly = false;
  /**
   * 标记读取
   */
  private int markedReaderIndex = 0;
  /**
   * 标记写入
   */
  private int markedWriterIndex = 0;
  /**
   * 标记是否仅仅可读取
   */
  private boolean markedReadonly = false;

  public ByteArrayBuf() {
    this(256);
  }

  public ByteArrayBuf(int capacity) {
    this(new byte[capacity]);
  }

  public ByteArrayBuf(byte[] array) {
    this(array, 0, 0);
  }

  public ByteArrayBuf(byte[] array, int readerIndex, int writerIndex) {
    this.array(array);
    this.setReaderIndex(readerIndex);
    this.setWriterIndex(writerIndex);
  }

  /**
   * @return 获取缓冲数组
   */
  protected byte[] array() {
    return array;
  }

  /**
   * 设置缓冲数组
   *
   * @param array 数组
   */
  protected ByteArrayBuf array(byte[] array) {
    this.array = array;
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
  protected ByteArrayBuf setReaderIndex(int readerIndex) {
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
  protected ByteArrayBuf setWriterIndex(int writerIndex) {
    this.writerIndex = writerIndex;
    return this;
  }

  /**
   * 标记读取的位置
   */
  public ByteArrayBuf markReaderIndex() {
    this.markedReaderIndex = this.readerIndex;
    this.markedReadonly = this.readonly;
    return this;
  }

  /**
   * 重置为标记的读取位置
   */
  public ByteArrayBuf resetReaderIndex() {
    this.setReaderIndex(this.markedReaderIndex);
    this.setReadonly(this.markedReadonly);
    return this;
  }

  /**
   * 标记写入位置
   */
  public ByteArrayBuf markWriterIndex() {
    this.markedWriterIndex = this.writerIndex;
    this.markedReadonly = this.readonly;
    return this;
  }

  /**
   * 重置为标记的写入位置
   */
  public ByteArrayBuf resetWriterIndex() {
    this.setWriterIndex(this.markedWriterIndex);
    this.setReadonly(this.markedReadonly);
    return this;
  }

  /**
   * @return 获取缓冲区大小
   */
  public int capacity() {
    return array().length;
  }

  /**
   * @return 获取是否为只读状态
   */
  protected boolean isReadonly() {
    return readonly;
  }

  /**
   * 设置只读状态
   *
   * @param readonly 状态
   */
  protected void setReadonly(boolean readonly) {
    this.readonly = readonly;
  }

  /**
   * @return 获取可写入长度
   */
  public int writableBytes() {
    if (isReadonly()) {
      return 0;
    }
    if (writerIndex == readerIndex) {
      return capacity();
    }
    return writerIndex > readerIndex
        ? capacity() - (writerIndex - readerIndex)
        : (readerIndex - writerIndex);
  }

  /**
   * @return 获取可读取长度
   */
  public int readableBytes() {
    if (isReadonly()) {
      return capacity();
    }
    if (writerIndex == readerIndex) {
      return 0;
    }
    return writerIndex > readerIndex
        ? (writerIndex - readerIndex)
        : capacity() - (readerIndex - writerIndex);
  }

  /**
   * 清空读写
   */
  public ByteArrayBuf clear() {
    this.setWriterIndex(0);
    this.setReaderIndex(0);
    this.setReadonly(false);
    fill(array(), (byte) 0x00);
    return this;
  }

  /**
   * @return 申请新的缓冲区
   */
  public ByteArrayBuf allocateNewBuf() {
    return allocateNewBuf(this);
  }

  /**
   * @return 申请新的缓冲区
   */
  public ByteArrayBuf allocateNewBuf(int newCapacity) {
    return allocateNewBuf(this, newCapacity);
  }

  /**
   * 写入
   *
   * @param value 数据
   */
  public ByteArrayBuf writeByte(byte value) {
    checkWritableBound(this, 1);
    return writeByte0(this, value);
  }

  /**
   * 写入
   *
   * @param src 数据
   */
  public ByteArrayBuf write(byte[] src) {
    return write(src, 0, src.length);
  }

  /**
   * 写入
   *
   * @param src   数据
   * @param start 开始位置
   * @param len   长度
   */
  public ByteArrayBuf write(byte[] src, int start, int len) {
    return writeBytes0(src, start, len);
  }

  /**
   * 写入
   *
   * @param src 数据
   */
  public ByteArrayBuf write(ByteArrayBuf src) {
    return write(src, src.getReaderIndex(), src.readableBytes());
  }

  /**
   * 写入
   *
   * @param src   数据
   * @param start 开始位置
   * @param len   长度
   */
  public ByteArrayBuf write(ByteArrayBuf src, int start, int len) {
    return write(src.array(), start, len);
  }

  /**
   * 写入
   *
   * @param src    原数据
   * @param srcPos 原数据开始的位置
   * @param dest   目标缓冲
   * @param len    写入数据的长度
   */
  public ByteArrayBuf write(byte[] src, int srcPos, ByteArrayBuf dest, int len) {
    return writeBytes0(src, srcPos, dest, len);
  }

  /**
   * 写入字节数组
   *
   * @param src    原数据
   * @param srcPos 原数据开始读取的位置
   * @param len    写入的长度
   * @return 返回目标缓冲
   */
  protected ByteArrayBuf writeBytes0(byte[] src, int srcPos, int len) {
    return writeBytes0(src, srcPos, this, len);
  }

  protected ByteArrayBuf wrap(byte[] buf, int start) {
    return new ByteArrayBuf(buf, 0, start);
  }

  /**
   * 跳过指定字节
   *
   * @param len 跳过的字节长度
   * @return 返回缓冲
   */
  public ByteArrayBuf skipBytes(int len) {
    modifyReaderIndex(this, Math.min(readableBytes(), len));
    return this;
  }

  /**
   * 读取字节
   *
   * @return 返回可读取的全部数据
   */
  public byte[] readBytes() {
    int readableBytes = readableBytes();
    return readableBytes > 0 ? readBytes(readableBytes) : EMPTY;
  }

  /**
   * 读取字节
   *
   * @param len 长度
   * @return 返回数据
   */
  public byte[] readBytes(int len) {
    return readBytes(new byte[len], 0, len);
  }

  /**
   * 读取字节
   *
   * @param dest    目标缓冲
   * @param destPos 目标缓冲的开始位置
   * @param len     长度
   * @return 返回读取的数据
   */
  public byte[] readBytes(byte[] dest, int destPos, int len) {
    checkReadableBound(this, len);
    for (int i = 0; i < len; i++) {
      dest[i + destPos] = readByte0(this);
    }
    return dest;
  }

  /**
   * 读取
   *
   * @param len 长度
   * @return 返回读取的数据
   */
  public ByteArrayBuf read(int len) {
    checkReadableBound(this, len);
    ByteArrayBuf dest = wrap(new byte[len], 0);
    return read(this, dest, len);
  }

  /**
   * 读取
   *
   * @param dest    目标缓冲
   * @param destPos 目标缓冲的开始位置
   * @param len     长度
   * @return 返回读取的数据
   */
  public ByteArrayBuf read(byte[] dest, int destPos, int len) {
    checkReadableBound(this, len);
    ByteArrayBuf destBuf = wrap(dest, destPos);
    return read(this, destBuf, len);
  }

  /**
   * 读取
   *
   * @param src  原数据
   * @param dest 目标缓冲
   * @param len  长度
   * @return 返回读取的数据
   */
  public static ByteArrayBuf read(ByteArrayBuf src, ByteArrayBuf dest, int len) {
    checkReadableBound(src, len);
    for (int i = 0; i < len; i++) {
      writeByte0(dest, readByte0(src));
    }
    return dest;
  }

  /**
   * @return 读取单个字节
   */
  public byte readByte() {
    checkReadableBound(this, 1);
    return readByte0(this);
  }

  /**
   * 写入数据
   *
   * @param buf   缓冲
   * @param value 数值
   */
  protected static ByteArrayBuf writeByte0(ByteArrayBuf buf, byte value) {
    return setByte0(buf, value, modifyWriterIndex(buf, 1));
  }

  /**
   * 写入字节数组
   *
   * @param src    原数据
   * @param srcPos 原数据开始读取的位置
   * @param dest   目标缓冲
   * @param len    写入的长度
   * @return 返回目标缓冲
   */
  protected static ByteArrayBuf writeBytes0(byte[] src, int srcPos, ByteArrayBuf dest, int len) {
    checkWritableBound(dest, len);

    if (len + srcPos >= src.length) {
      for (int i = srcPos; i < src.length; i++) {
        writeByte0(dest, src[i]);
      }
      // 拷贝剩余长度
      int remaining = (len + srcPos) % src.length;
      for (int i = 0; i < remaining; i++) {
        writeByte0(dest, src[i]);
      }
    } else {
      for (int i = srcPos; i < len; i++) {
        writeByte0(dest, src[i]);
      }
    }

    return dest;
  }

  /**
   * 读取数据
   *
   * @param buf 缓冲
   */
  protected static byte readByte0(ByteArrayBuf buf) {
    return getByte0(buf, modifyReaderIndex(buf, 1));
  }

  /**
   * 设置单个字节
   *
   * @param buf   缓冲区
   * @param value 值
   * @param index 索引
   * @return 返回缓冲区
   */
  protected static ByteArrayBuf setByte0(ByteArrayBuf buf, byte value, int index) {
    buf.array()[index] = value;
    return buf;
  }

  /**
   * 读取单个字节
   *
   * @param buf   缓冲区
   * @param index 索引
   * @return 返回读取的字节
   */
  protected static byte getByte0(ByteArrayBuf buf, int index) {
    return buf.array()[index];
  }

  /**
   * 设置新的写入的位置
   *
   * @param buf 缓冲区
   * @param len 长度
   * @return 返回改变之前的位置
   */
  protected static int modifyWriterIndex(ByteArrayBuf buf, int len) {
    int index = buf.writerIndex;
    buf.setWriterIndex((index + len) % buf.capacity());
    buf.setReadonly(buf.readerIndex == buf.writerIndex);
    return index;
  }

  /**
   * 设置新的读取的位置
   *
   * @param buf 缓冲区
   * @param len 长度
   * @return 返回改变之前的位置
   */
  protected static int modifyReaderIndex(ByteArrayBuf buf, int len) {
    int index = buf.readerIndex;
    len = Math.max(len, 0);
    buf.setReaderIndex((index + len) % buf.capacity());
    buf.setReadonly(len <= 0 && buf.isReadonly());
    return index;
  }

  /**
   * 检查可写入长度
   *
   * @param buf  缓冲
   * @param size 要求的大小
   */
  public static void checkWritableBound(ByteArrayBuf buf, int size) {
    if (buf.writableBytes() < size) {
      throw new IllegalStateException("剩余可写入长度不足，写入长度: " + size + ", 可写入长度: " + buf.writableBytes());
    }
  }

  /**
   * 检查可读取的长度
   *
   * @param buf  缓冲
   * @param size 要求的大小
   */
  public static void checkReadableBound(ByteArrayBuf buf, int size) {
    if (buf.readableBytes() < size) {
      throw new IllegalArgumentException("数据可读取长度不足，读取长度: " + size + ", 可读取长度: " + buf.readableBytes());
    }
  }

  /**
   * 填充
   *
   * @param array 数组
   * @param value 填充的值
   */
  public static void fill(byte[] array, byte value) {
    if (array != null && array.length > 0) {
      Arrays.fill(array, value);
    }
  }

  /**
   * 重新申请缓冲区
   *
   * @param buf 缓冲区
   */
  public static ByteArrayBuf allocateNewBuf(ByteArrayBuf buf) {
    return allocateNewBuf(buf, buf.capacity() * 2);
  }

  /**
   * 重新申请缓冲区
   *
   * @param buf         缓冲区
   * @param newCapacity 新缓冲区的大小
   */
  public static ByteArrayBuf allocateNewBuf(ByteArrayBuf buf, int newCapacity) {
    int readableBytes = buf.readableBytes();
    if (readableBytes > newCapacity) {
      throw new IllegalArgumentException("新分配的缓冲区无法存放全部数据, newCapacity: " + newCapacity + ", min size: " + readableBytes);
    }
    byte[] array = new byte[newCapacity];
    buf.readBytes(array, 0, readableBytes);
    buf.clear();
    buf.array(array);
    modifyWriterIndex(buf, readableBytes);
    return buf;
  }

}
