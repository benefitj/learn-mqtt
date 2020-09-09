package com.benefitj.mqtt.buf;

import java.util.Arrays;

public class ByteBuf {

  /**
   * 最大容量
   */
  private int maxCapacity;
  /**
   * 读取位置
   */
  private int readerIndex;
  /**
   * 写入位置
   */
  private int writerIndex;
  /**
   * 读取位置标记
   */
  private int markedReaderIndex;
  /**
   * 写入位置标记
   */
  private int markedWriterIndex;

  /**
   * 字节缓冲
   */
  private byte[] buf;

  public ByteBuf(int maxCapacity) {
    this(16, maxCapacity);
  }

  public ByteBuf(int capacity, int maxCapacity) {
    this.maxCapacity = maxCapacity;
    this.createNewBuf(capacity);
  }

  protected ByteBuf createNewBuf(int newCapacity) {
    if (newCapacity > getMaxCapacity()) {
      throw new IllegalArgumentException("The newCapacity must less than maxCapacity");
    }
    byte[] newBuf = new byte[newCapacity];
    byte[] buf = this.getBuf();
    if (buf != null) {
      System.arraycopy(buf, 0, newBuf, 0, buf.length);
    }
    return this.setBuf(newBuf);
  }

  public byte[] getBuf() {
    return buf;
  }

  public ByteBuf setBuf(byte[] buf) {
    this.buf = buf;
    return this;
  }

  /**
   * Returns the number of bytes (octets) this buffer can contain.
   */
  public int capacity() {
    return this.getBuf().length;
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }

  public ByteBuf setMaxCapacity(int maxCapacity) {
    this.maxCapacity = maxCapacity;
    return this;
  }

  public int getReaderIndex() {
    return readerIndex;
  }

  protected ByteBuf setReaderIndex(int readerIndex) {
    checkIndexBounds(readerIndex, getWriterIndex(), capacity());
    this.readerIndex = readerIndex;
    return this;
  }

  public int getWriterIndex() {
    return writerIndex;
  }

  protected ByteBuf setWriterIndex(int writerIndex) {
    checkIndexBounds(getReaderIndex(), writerIndex, capacity());
    this.writerIndex = writerIndex;
    return this;
  }

  public int getMarkedReaderIndex() {
    return markedReaderIndex;
  }

  protected ByteBuf setMarkedReaderIndex(int markedReaderIndex) {
    this.markedReaderIndex = markedReaderIndex;
    return this;
  }

  public int getMarkedWriterIndex() {
    return markedWriterIndex;
  }

  protected ByteBuf setMarkedWriterIndex(int markedWriterIndex) {
    this.markedWriterIndex = markedWriterIndex;
    return this;
  }

  /**
   * 清空标记
   */
  public ByteBuf clear() {
    this.setReaderIndex(0);
    this.setWriterIndex(0);
    this.setMarkedReaderIndex(0);
    this.setMarkedWriterIndex(0);
    byte[] buf = getBuf();
    if (buf != null) {
      Arrays.fill(buf, (byte) 0x00);
    }
    return this;
  }

  public boolean isReadable() {
    return getWriterIndex() > getReaderIndex();
  }

  public boolean isReadable(int numBytes) {
    return getWriterIndex() - getReaderIndex() >= numBytes;
  }

  public boolean isWritable() {
    return capacity() > getWriterIndex();
  }

  public boolean isWritable(int numBytes) {
    return capacity() - getWriterIndex() >= numBytes;
  }

  public int readableBytes() {
    return getWriterIndex() - getReaderIndex();
  }

  /**
   * @return 写入的字节数，已被读取过的不含在内
   */
  public int writableBytes() {
    return remainingCapacity() - getWriterIndex();
  }

  private int remainingCapacity() {
    capacity() - getWriterIndex();

  }

  /**
   * @return 获取剩余最大可写入字节数
   */
  public int maxWritableBytes() {
    return getMaxCapacity() - getWriterIndex();
  }

  /**
   * 标记读取位置
   */
  public ByteBuf markReaderIndex() {
    this.markedReaderIndex = this.readerIndex;
    return this;
  }

  /**
   * 重置为读取标记之前位置
   */
  public ByteBuf resetReaderIndex() {
    setReaderIndex(this.markedReaderIndex);
    return this;
  }

  /**
   * 标记写入位置
   */
  public ByteBuf markWriterIndex() {
    this.markedWriterIndex = this.writerIndex;
    return this;
  }

  /**
   * 重置为写入标记之前位置
   */
  public ByteBuf resetWriterIndex() {
    setWriterIndex(this.markedWriterIndex);
    return this;
  }


  private static void checkIndexBounds(final int readerIndex, final int writerIndex, final int capacity) {
    if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > capacity) {
      throw new IndexOutOfBoundsException(String.format(
          "readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))",
          readerIndex, writerIndex, capacity));
    }
  }

  public static class Buf {
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

    public Buf() {
      this(new byte[256], 0, 0);
    }

    public Buf(byte[] buf) {
      this(buf, 0, buf.length);
    }

    public Buf(byte[] buf, int readerIndex, int writerIndex) {
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
    public void setBuf(byte[] buf) {
      this.buf = buf;
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
    public void setReaderIndex(int readerIndex) {
      this.readerIndex = readerIndex;
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
    public void setWriterIndex(int writerIndex) {
      this.writerIndex = writerIndex;
    }

    /**
     * 设置位置
     *
     * @param readerIndex 读取位置
     * @param writerIndex 写入位置
     */
    public void setIndex(int readerIndex, int writerIndex) {
      this.setReaderIndex(readerIndex);
      this.setWriterIndex(writerIndex);
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
    public int readerBytes() {
      return isPositive()
          ? writerIndex - readerIndex
          : capacity() - (readerIndex - writerIndex);
    }

    /**
     * @return 获取可写入长度
     */
    public int writerBytes() {
      int len = isPositive()
          ? writerIndex - readerIndex
          : readerIndex - writerIndex;
      return capacity() - len;
    }

    /**
     * 清空读写
     */
    public void clear() {
      this.setIndex(0, 0);
    }

    /**
     * 写入
     *
     * @param src 数据
     */
    public void write(byte[] src) {
      write(src, 0, src.length);
    }

    /**
     * 写入
     *
     * @param src   数据
     * @param start 开始位置
     * @param len   长度
     */
    public void write(byte[] src, int start, int len) {
      write(src, start, this, len);
    }

    /**
     * 写入
     *
     * @param src 数据
     */
    public void write(Buf src) {
      write(src, src.getReaderIndex(), src.readerBytes());
    }

    /**
     * 写入
     *
     * @param src   数据
     * @param start 开始位置
     * @param len   长度
     */
    public void write(Buf src, int start, int len) {
      write(src.buf, start, this, len);
    }

    /**
     * 写入
     *
     * @param src    原数据
     * @param srcPos 原数据开始的位置
     * @param dest   目标缓冲
     * @param len    写入数据的长度
     */
    public void write(byte[] src, int srcPos, Buf dest, int len) {
      if (dest.writerBytes() < len) {
        throw new IllegalStateException("剩余可写入长度不足，写入" + len + ", 可写入" + dest.writerBytes());
      }

      // copy bytes
      byte[] buf = dest.buf;
      int capacity = dest.capacity();
      if (dest.isPositive()) {
        int positiveLen = capacity - dest.getWriterIndex();
        // from writerIndex ~> capacity
        dest._setBytes(src, srcPos, buf, dest.getWriterIndex(), positiveLen);
        // from 0 ~> readerIndex
        dest._setBytes(src, srcPos, buf, 0, len - positiveLen);
        dest.setWriterIndex(capacity % (dest.getWriterIndex() + len));
      } else {
        // from writerIndex ~> readerIndex
        dest._setBytes(src, srcPos, buf, dest.getWriterIndex(), len);
        dest.setWriterIndex(dest.getWriterIndex() + len);
      }
    }

    protected void _setBytes(byte[] src, int srcPos, byte[] dest, int destPos, int len) {
      if (len >= 0) {
        System.arraycopy(src, srcPos, dest, destPos, len);
      }
    }

    public byte[] get(int size) {
      byte[] dest = new byte[size];
      return get(dest, 0, size);
    }

    public byte[] get(byte[] dest, int destPos, int len) {
      return get(this, dest, destPos, len);
    }

    public static byte[] get(Buf src, byte[] dest, int destPos, int len) {
      if (src.readerBytes() < len) {
        throw new IllegalArgumentException("数据可读取长度不足， 读取" + len + ", 可读取: " + src.readerBytes());
      }
      // 读取
      // 如果 writerIndex > readerIndex ? 直接读取 : 计算读取逻辑
      if (src.isPositive()) {

        src.writerBytes();

      }


      return dest;
    }

  }


}
