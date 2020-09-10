package com.benefitj.mqtt.buf;

import com.benefitj.core.HexUtils;

import java.util.Random;

/**
 * 字节缓冲
 */
public class AllocableByteBuf extends ByteBuf {
  public static void main(String[] args) {

    AllocableByteBuf buf = new AllocableByteBuf(40);

    Random r = new Random();
    byte[] bytes = new byte[20];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) r.nextInt(255);
    }
    buf.write(bytes);
    buf.write(bytes);

    // 40
    System.err.println("readerIndex: " + buf.getReaderIndex());
    System.err.println("writerIndex: " + buf.getWriterIndex());
    System.err.println("readableBytes: " + buf.readableBytes());
    System.err.println("writableBytes: " + buf.writableBytes());

    // 可读30，可写10
    System.err.println("read: " + HexUtils.bytesToHex(buf.readBytes(10), " | ", 4));

    System.err.println("\n-------------------\n");

    System.err.println("readerIndex: " + buf.getReaderIndex());
    System.err.println("writerIndex: " + buf.getWriterIndex());
    System.err.println("readableBytes: " + buf.readableBytes());
    System.err.println("writableBytes: " + buf.writableBytes());

    //
    System.err.println("\n--------- before -------------");
    System.err.println("capacity: " + buf.capacity());
    buf.markReaderIndex();
    System.err.println("buf: " + HexUtils.bytesToHex(buf.readBytes(), " | ", 4));
    buf.resetReaderIndex();
    System.err.println("buf2: " + HexUtils.bytesToHex(buf.array(), " | ", 4));
    System.err.println("readerIndex: " + buf.getReaderIndex());
    System.err.println("writerIndex: " + buf.getWriterIndex());
    System.err.println("readableBytes: " + buf.readableBytes());
    System.err.println("writableBytes: " + buf.writableBytes());

    System.err.println("write before: " + HexUtils.bytesToHex(buf.readBytes(20), " | ", 4));

    buf.write(bytes);
    System.err.println("capacity2: " + buf.capacity());
    System.err.println("write2 ==> " + buf.readableBytes());
    System.err.println("\n");
    buf.skipBytes(0);
    System.err.println("capacity2: " + buf.capacity());
    System.err.println("write2 ==> " + buf.readableBytes());

    //buf.allocateNewBuf(buf.capacity() + 50);
    buf.write(bytes);

    System.err.println("\n--------- after -------------");
    buf.markReaderIndex();
    System.err.println("capacity: " + buf.capacity());
    System.err.println("buf: " + HexUtils.bytesToHex(buf.readBytes(), " | ", 4));
    buf.resetReaderIndex();
    System.err.println("buf2: " + HexUtils.bytesToHex(buf.array(), " | ", 10));
    System.err.println("readerIndex: " + buf.getReaderIndex());
    System.err.println("writerIndex: " + buf.getWriterIndex());
    System.err.println("readableBytes: " + buf.readableBytes());
    System.err.println("writableBytes: " + buf.writableBytes());

    System.err.println("\n");

  }


  // 4MB
  public static final int MAX_CAPACITY = (1024 << 10) * 4;

  /**
   * 最大内存空间
   */
  private int maxCapacity = MAX_CAPACITY;

  public AllocableByteBuf() {
  }

  public AllocableByteBuf(int capacity) {
    super(capacity);
  }

  public AllocableByteBuf(byte[] array) {
    super(array);
  }

  public AllocableByteBuf(byte[] array, int readerIndex, int writerIndex) {
    super(array, readerIndex, writerIndex);
  }

  /**
   * @return 获取最大容量
   */
  public int maxCapacity() {
    return maxCapacity;
  }

  /**
   * 最大容量
   *
   * @param maxCapacity 容量
   */
  public void maxCapacity(int maxCapacity) {
    this.maxCapacity = maxCapacity;
  }

  @Override
  protected ByteBuf writeBytes0(byte[] src, int srcPos, int len) {
    enableWriterCapacity(len);
    return super.writeBytes0(src, srcPos, len);
  }

  @Override
  protected AllocableByteBuf wrap(byte[] buf, int start) {
    return new AllocableByteBuf(buf, 0, start);
  }

  public void enableWriterCapacity(int grow) {
    int writableBytes = writableBytes();
    if (writableBytes < grow) {
      if (maxCapacity() < (capacity() + grow)) {
        throw new IllegalStateException("无法申请更大内存空间!");
      }
      // 申请新的缓冲区
      int capacity = Math.min(maxCapacity(), (int) (capacity() + grow * 1.5));
      allocateNewBuf(this, capacity);
    }
  }

}
