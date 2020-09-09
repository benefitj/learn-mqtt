//package com.benefitj.mqtt.buf;
//
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.nio.ByteBuffer;
//import java.nio.channels.FileChannel;
//import java.nio.channels.GatheringByteChannel;
//import java.nio.charset.Charset;
//
//public class ByteArrayBuf {
//
//  private int maxCapacity;
//
//  private int readerIndex;
//  private int writerIndex;
//  private int markedReaderIndex;
//  private int markedWriterIndex;
//
//  private byte[] buf;
//
//  protected ByteArrayBuf() {
//    this(4096);
//  }
//
//  public ByteArrayBuf(int maxCapacity) {
//    this(256, maxCapacity);
//  }
//
//  public ByteArrayBuf(int capacity, int maxCapacity) {
//    this.maxCapacity = maxCapacity;
//    this.capacity(capacity);
//  }
//
//  private static void checkIndexBounds(final int readerIndex, final int writerIndex, final int capacity) {
//    if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > capacity) {
//      throw new IndexOutOfBoundsException(String.format(
//          "readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))",
//          readerIndex, writerIndex, capacity));
//    }
//  }
//
//  public byte[] buf() {
//    return buf;
//  }
//
//  public ByteArrayBuf buf(byte[] buf) {
//    this.buf = buf;
//    return this;
//  }
//
//  /**
//   * Returns the number of bytes (octets) this buffer can contain.
//   */
//  public int capacity() {
//    return this.buf().length;
//  }
//
//  public ByteArrayBuf capacity(int newCapacity) {
//    byte[] newBuf = new byte[newCapacity];
//    byte[] buf = this.buf();
//    if (buf != null) {
//      System.arraycopy(buf, 0, newBuf, 0, buf.length);
//    }
//    return this.buf(newBuf);
//  }
//
//  public int maxCapacity() {
//    return maxCapacity;
//  }
//
//  public int readerIndex() {
//    return readerIndex;
//  }
//
//  public ByteArrayBuf readerIndex(int readerIndex) {
//    checkIndexBounds(readerIndex, writerIndex, capacity());
//    this.readerIndex = readerIndex;
//    return this;
//  }
//
//  public int writerIndex() {
//    return writerIndex;
//  }
//
//  public ByteArrayBuf writerIndex(int writerIndex) {
//    checkIndexBounds(readerIndex, writerIndex, capacity());
//    this.writerIndex = writerIndex;
//    return this;
//  }
//
//  protected ByteArrayBuf setIndex(int readerIndex, int writerIndex) {
//    checkIndexBounds(readerIndex, writerIndex, capacity());
//    setIndex0(readerIndex, writerIndex);
//    return this;
//  }
//
//  public ByteArrayBuf clear() {
//    readerIndex = writerIndex = 0;
//    return this;
//  }
//
//  public boolean isReadable() {
//    return writerIndex > readerIndex;
//  }
//
//  public boolean isReadable(int numBytes) {
//    return writerIndex - readerIndex >= numBytes;
//  }
//
//  public boolean isWritable() {
//    return capacity() > writerIndex;
//  }
//
//  public boolean isWritable(int numBytes) {
//    return capacity() - writerIndex >= numBytes;
//  }
//
//  public int readableBytes() {
//    return writerIndex - readerIndex;
//  }
//
//  public int writableBytes() {
//    return capacity() - writerIndex;
//  }
//
//  public int maxWritableBytes() {
//    return maxCapacity() - writerIndex;
//  }
//
//  public int maxFastWritableBytes() {
//    return writableBytes();
//  }
//
//  public ByteArrayBuf markReaderIndex() {
//    markedReaderIndex = readerIndex;
//    return this;
//  }
//
//  public ByteArrayBuf resetReaderIndex() {
//    readerIndex(markedReaderIndex);
//    return this;
//  }
//
//  public ByteArrayBuf markWriterIndex() {
//    markedWriterIndex = writerIndex;
//    return this;
//  }
//
//  public ByteArrayBuf resetWriterIndex() {
//    writerIndex(markedWriterIndex);
//    return this;
//  }
//
//  public ByteArrayBuf discardReadBytes() {
//    if (readerIndex == 0) {
//      return this;
//    }
//
//    if (readerIndex != writerIndex) {
//      setBytes(0, this, readerIndex, writerIndex - readerIndex);
//      writerIndex -= readerIndex;
//      adjustMarkers(readerIndex);
//      readerIndex = 0;
//    } else {
//      ensureAccessible();
//      adjustMarkers(readerIndex);
//      writerIndex = readerIndex = 0;
//    }
//    return this;
//  }
//
//  protected final void adjustMarkers(int decrement) {
//    int markedReaderIndex = this.markedReaderIndex;
//    if (markedReaderIndex <= decrement) {
//      this.markedReaderIndex = 0;
//      int markedWriterIndex = this.markedWriterIndex;
//      if (markedWriterIndex <= decrement) {
//        this.markedWriterIndex = 0;
//      } else {
//        this.markedWriterIndex = markedWriterIndex - decrement;
//      }
//    } else {
//      this.markedReaderIndex = markedReaderIndex - decrement;
//      markedWriterIndex -= decrement;
//    }
//  }
//
//  // Called after a capacity reduction
//  protected final void trimIndicesToCapacity(int newCapacity) {
//    if (writerIndex() > newCapacity) {
//      setIndex0(Math.min(readerIndex(), newCapacity), newCapacity);
//    }
//  }
//
//  /**
//   * Calculate the new capacity of a {@link ByteBuf} that is used when a {@link ByteBuf} needs to expand by the
//   * {@code minNewCapacity} with {@code maxCapacity} as upper-bound.
//   */
//  public int calculateNewCapacity(int minNewCapacity, int maxCapacity) {
//    if (minNewCapacity > maxCapacity) {
//      throw new IllegalArgumentException(String.format(
//          "minNewCapacity: %d (expected: not greater than maxCapacity(%d)",
//          minNewCapacity, maxCapacity));
//    }
//    final int threshold = 1048576 * 4; // 4 MiB page
//
//    if (minNewCapacity == threshold) {
//      return threshold;
//    }
//
//    // If over threshold, do not double but just increase by threshold.
//    if (minNewCapacity > threshold) {
//      int newCapacity = minNewCapacity / threshold * threshold;
//      if (newCapacity > maxCapacity - threshold) {
//        newCapacity = maxCapacity;
//      } else {
//        newCapacity += threshold;
//      }
//      return newCapacity;
//    }
//
//    // Not over threshold. Double up to 4 MiB, starting from 64.
//    int newCapacity = 64;
//    while (newCapacity < minNewCapacity) {
//      newCapacity <<= 1;
//    }
//
//    return Math.min(newCapacity, maxCapacity);
//  }
//
//  public ByteArrayBuf ensureWritable(int minWritableBytes) {
//    ensureWritable0(checkPositiveOrZero(minWritableBytes, "minWritableBytes"));
//    return this;
//  }
//
//  final void ensureWritable0(int minWritableBytes) {
//    final int writerIndex = writerIndex();
//    final int targetCapacity = writerIndex + minWritableBytes;
//    if (targetCapacity <= capacity()) {
//      ensureAccessible();
//      return;
//    }
//    if (targetCapacity > maxCapacity) {
//      ensureAccessible();
//      throw new IndexOutOfBoundsException(String.format(
//          "writerIndex(%d) + minWritableBytes(%d) exceeds maxCapacity(%d): %s",
//          writerIndex, minWritableBytes, maxCapacity, this));
//    }
//
//    // Normalize the target capacity to the power of 2.
//    final int fastWritable = maxFastWritableBytes();
//    int newCapacity = fastWritable >= minWritableBytes ? writerIndex + fastWritable
//        : calculateNewCapacity(targetCapacity, maxCapacity);
//
//    // Adjust to the new capacity.
//    capacity(newCapacity);
//  }
//
//  public int ensureWritable(int minWritableBytes, boolean force) {
//    ensureAccessible();
//    checkPositiveOrZero(minWritableBytes, "minWritableBytes");
//
//    if (minWritableBytes <= writableBytes()) {
//      return 0;
//    }
//
//    final int maxCapacity = maxCapacity();
//    final int writerIndex = writerIndex();
//    if (minWritableBytes > maxCapacity - writerIndex) {
//      if (!force || capacity() == maxCapacity) {
//        return 1;
//      }
//
//      capacity(maxCapacity);
//      return 3;
//    }
//
//    int fastWritable = maxFastWritableBytes();
//    int newCapacity = fastWritable >= minWritableBytes ? writerIndex + fastWritable
//        : calculateNewCapacity(writerIndex + minWritableBytes, maxCapacity);
//
//    // Adjust to the new capacity.
//    capacity(newCapacity);
//    return 2;
//  }
//
//  public byte getByte(int index) {
//    checkIndex(index);
//    return buf()[index];
//  }
//
//  public boolean getBoolean(int index) {
//    return getByte(index) != 0;
//  }
//
//  public short getUnsignedByte(int index) {
//    return (short) (getByte(index) & 0xFF);
//  }
//
//  public short getShort(int index) {
//    checkIndex(index, 2);
//    return _getShort(index);
//  }
//
//  protected abstract short _getShort(int index);
//
//  public short getShortLE(int index) {
//    checkIndex(index, 2);
//    return _getShortLE(index);
//  }
//
//  protected abstract short _getShortLE(int index);
//
//  public int getUnsignedShort(int index) {
//    return getShort(index) & 0xFFFF;
//  }
//
//  public int getUnsignedShortLE(int index) {
//    return getShortLE(index) & 0xFFFF;
//  }
//
//  public int getUnsignedMedium(int index) {
//    checkIndex(index, 3);
//    return _getUnsignedMedium(index);
//  }
//
//  protected abstract int _getUnsignedMedium(int index);
//
//  public int getUnsignedMediumLE(int index) {
//    checkIndex(index, 3);
//    return _getUnsignedMediumLE(index);
//  }
//
//  protected abstract int _getUnsignedMediumLE(int index);
//
//  public int getMedium(int index) {
//    int value = getUnsignedMedium(index);
//    if ((value & 0x800000) != 0) {
//      value |= 0xff000000;
//    }
//    return value;
//  }
//
//  public int getMediumLE(int index) {
//    int value = getUnsignedMediumLE(index);
//    if ((value & 0x800000) != 0) {
//      value |= 0xff000000;
//    }
//    return value;
//  }
//
//  public int getInt(int index) {
//    checkIndex(index, 4);
//    return _getInt(index);
//  }
//
//  protected abstract int _getInt(int index);
//
//  public int getIntLE(int index) {
//    checkIndex(index, 4);
//    return _getIntLE(index);
//  }
//
//  protected abstract int _getIntLE(int index);
//
//  public long getUnsignedInt(int index) {
//    return getInt(index) & 0xFFFFFFFFL;
//  }
//
//  public long getUnsignedIntLE(int index) {
//    return getIntLE(index) & 0xFFFFFFFFL;
//  }
//
//  public long getLong(int index) {
//    checkIndex(index, 8);
//    return _getLong(index);
//  }
//
//  protected abstract long _getLong(int index);
//
//  public long getLongLE(int index) {
//    checkIndex(index, 8);
//    return _getLongLE(index);
//  }
//
//  protected abstract long _getLongLE(int index);
//
//  public char getChar(int index) {
//    return (char) getShort(index);
//  }
//
//  public float getFloat(int index) {
//    return Float.intBitsToFloat(getInt(index));
//  }
//
//  public double getDouble(int index) {
//    return Double.longBitsToDouble(getLong(index));
//  }
//
//  public ByteArrayBuf getBytes(int index, byte[] dst) {
//    getBytes(index, dst, 0, dst.length);
//    return this;
//  }
//
//  public ByteArrayBuf getBytes(int index, ByteArrayBuf dst) {
//    getBytes(index, dst, dst.writableBytes());
//    return this;
//  }
//
//  public ByteArrayBuf getBytes(int index, ByteArrayBuf dst, int length) {
//    getBytes(index, dst, dst.writerIndex(), length);
//    dst.writerIndex(dst.writerIndex() + length);
//    return this;
//  }
//
//  public abstract ByteArrayBuf getBytes(int index, ByteArrayBuf dst, int dstIndex, int length);
//
//  public CharSequence getCharSequence(int index, int length, Charset charset) {
//    if (CharsetUtil.US_ASCII.equals(charset) || CharsetUtil.ISO_8859_1.equals(charset)) {
//      // ByteBufUtil.getBytes(...) will return a new copy which the AsciiString uses directly
//      return new AsciiString(ByteBufUtil.getBytes(this, index, length, true), false);
//    }
//    return toString(index, length, charset);
//  }
//
//  public CharSequence readCharSequence(int length, Charset charset) {
//    CharSequence sequence = getCharSequence(readerIndex, length, charset);
//    readerIndex += length;
//    return sequence;
//  }
//
//  public ByteArrayBuf setByte(int index, int value) {
//    checkIndex(index);
//    _setByte(index, value);
//    return this;
//  }
//
//  protected abstract void _setByte(int index, int value);
//
//  public ByteArrayBuf setBoolean(int index, boolean value) {
//    setByte(index, value ? 1 : 0);
//    return this;
//  }
//
//  public ByteArrayBuf setShort(int index, int value) {
//    checkIndex(index, 2);
//    _setShort(index, value);
//    return this;
//  }
//
//  protected abstract void _setShort(int index, int value);
//
//  public ByteArrayBuf setShortLE(int index, int value) {
//    checkIndex(index, 2);
//    _setShortLE(index, value);
//    return this;
//  }
//
//  protected abstract void _setShortLE(int index, int value);
//
//  public ByteArrayBuf setChar(int index, int value) {
//    setShort(index, value);
//    return this;
//  }
//
//  public ByteArrayBuf setMedium(int index, int value) {
//    checkIndex(index, 3);
//    _setMedium(index, value);
//    return this;
//  }
//
//  protected abstract void _setMedium(int index, int value);
//
//  public ByteArrayBuf setMediumLE(int index, int value) {
//    checkIndex(index, 3);
//    _setMediumLE(index, value);
//    return this;
//  }
//
//  protected abstract void _setMediumLE(int index, int value);
//
//  public ByteArrayBuf setInt(int index, int value) {
//    checkIndex(index, 4);
//    _setInt(index, value);
//    return this;
//  }
//
//  protected abstract void _setInt(int index, int value);
//
//  public ByteArrayBuf setIntLE(int index, int value) {
//    checkIndex(index, 4);
//    _setIntLE(index, value);
//    return this;
//  }
//
//  protected abstract void _setIntLE(int index, int value);
//
//  public ByteArrayBuf setFloat(int index, float value) {
//    setInt(index, Float.floatToRawIntBits(value));
//    return this;
//  }
//
//  public ByteArrayBuf setLong(int index, long value) {
//    checkIndex(index, 8);
//    _setLong(index, value);
//    return this;
//  }
//
//  protected abstract void _setLong(int index, long value);
//
//  public ByteArrayBuf setLongLE(int index, long value) {
//    checkIndex(index, 8);
//    _setLongLE(index, value);
//    return this;
//  }
//
//  protected abstract void _setLongLE(int index, long value);
//
//  public ByteArrayBuf setDouble(int index, double value) {
//    setLong(index, Double.doubleToRawLongBits(value));
//    return this;
//  }
//
//  public ByteArrayBuf setBytes(int index, byte[] src) {
//    setBytes(index, src, 0, src.length);
//    return this;
//  }
//
//  public ByteArrayBuf setBytes(int index, ByteArrayBuf src) {
//    setBytes(index, src, src.readableBytes());
//    return this;
//  }
//
//  private static void checkReadableBounds(final ByteArrayBuf src, final int length) {
//    if (length > src.readableBytes()) {
//      throw new IndexOutOfBoundsException(String.format(
//          "length(%d) exceeds src.readableBytes(%d) where src is: %s", length, src.readableBytes(), src));
//    }
//  }
//
//  public ByteArrayBuf setBytes(int index, ByteArrayBuf src, int length) {
//    checkIndex(index, length);
//    ObjectUtil.checkNotNull(src, "src");
//    if (checkBounds) {
//      checkReadableBounds(src, length);
//    }
//
//    setBytes(index, src, src.readerIndex(), length);
//    src.readerIndex(src.readerIndex() + length);
//    return this;
//  }
//
//  public ByteArrayBuf setZero(int index, int length) {
//    if (length == 0) {
//      return this;
//    }
//
//    checkIndex(index, length);
//
//    int nLong = length >>> 3;
//    int nBytes = length & 7;
//    for (int i = nLong; i > 0; i--) {
//      _setLong(index, 0);
//      index += 8;
//    }
//    if (nBytes == 4) {
//      _setInt(index, 0);
//      // Not need to update the index as we not will use it after this.
//    } else if (nBytes < 4) {
//      for (int i = nBytes; i > 0; i--) {
//        _setByte(index, (byte) 0);
//        index++;
//      }
//    } else {
//      _setInt(index, 0);
//      index += 4;
//      for (int i = nBytes - 4; i > 0; i--) {
//        _setByte(index, (byte) 0);
//        index++;
//      }
//    }
//    return this;
//  }
//
//  public byte readByte() {
//    checkReadableBytes0(1);
//    int i = readerIndex;
//    byte b = _getByte(i);
//    readerIndex = i + 1;
//    return b;
//  }
//
//  public boolean readBoolean() {
//    return readByte() != 0;
//  }
//
//  public short readUnsignedByte() {
//    return (short) (readByte() & 0xFF);
//  }
//
//  public short readShort() {
//    checkReadableBytes0(2);
//    short v = _getShort(readerIndex);
//    readerIndex += 2;
//    return v;
//  }
//
//  public short readShortLE() {
//    checkReadableBytes0(2);
//    short v = _getShortLE(readerIndex);
//    readerIndex += 2;
//    return v;
//  }
//
//  public int readUnsignedShort() {
//    return readShort() & 0xFFFF;
//  }
//
//  public int readUnsignedShortLE() {
//    return readShortLE() & 0xFFFF;
//  }
//
//  public int readMedium() {
//    int value = readUnsignedMedium();
//    if ((value & 0x800000) != 0) {
//      value |= 0xff000000;
//    }
//    return value;
//  }
//
//  public int readMediumLE() {
//    int value = readUnsignedMediumLE();
//    if ((value & 0x800000) != 0) {
//      value |= 0xff000000;
//    }
//    return value;
//  }
//
//  public int readUnsignedMedium() {
//    checkReadableBytes0(3);
//    int v = _getUnsignedMedium(readerIndex);
//    readerIndex += 3;
//    return v;
//  }
//
//  public int readUnsignedMediumLE() {
//    checkReadableBytes0(3);
//    int v = _getUnsignedMediumLE(readerIndex);
//    readerIndex += 3;
//    return v;
//  }
//
//  public int readInt() {
//    checkReadableBytes0(4);
//    int v = _getInt(readerIndex);
//    readerIndex += 4;
//    return v;
//  }
//
//  public int readIntLE() {
//    checkReadableBytes0(4);
//    int v = _getIntLE(readerIndex);
//    readerIndex += 4;
//    return v;
//  }
//
//  public long readUnsignedInt() {
//    return readInt() & 0xFFFFFFFFL;
//  }
//
//  public long readUnsignedIntLE() {
//    return readIntLE() & 0xFFFFFFFFL;
//  }
//
//  public long readLong() {
//    checkReadableBytes0(8);
//    long v = _getLong(readerIndex);
//    readerIndex += 8;
//    return v;
//  }
//
//  public long readLongLE() {
//    checkReadableBytes0(8);
//    long v = _getLongLE(readerIndex);
//    readerIndex += 8;
//    return v;
//  }
//
//  public char readChar() {
//    return (char) readShort();
//  }
//
//  public float readFloat() {
//    return Float.intBitsToFloat(readInt());
//  }
//
//  public double readDouble() {
//    return Double.longBitsToDouble(readLong());
//  }
//
//  public ByteArrayBuf readBytes(int length) {
//    checkReadableBytes(length);
//    if (length == 0) {
//      return Unpooled.EMPTY_BUFFER;
//    }
//
//    ByteArrayBuf buf = alloc().buffer(length, maxCapacity);
//    buf.writeBytes(this, readerIndex, length);
//    readerIndex += length;
//    return buf;
//  }
//
//  public ByteArrayBuf readSlice(int length) {
//    checkReadableBytes(length);
//    ByteArrayBuf slice = slice(readerIndex, length);
//    readerIndex += length;
//    return slice;
//  }
//
//  public ByteArrayBuf readRetainedSlice(int length) {
//    checkReadableBytes(length);
//    ByteArrayBuf slice = retainedSlice(readerIndex, length);
//    readerIndex += length;
//    return slice;
//  }
//
//  public ByteArrayBuf readBytes(byte[] dst, int dstIndex, int length) {
//    checkReadableBytes(length);
//    getBytes(readerIndex, dst, dstIndex, length);
//    readerIndex += length;
//    return this;
//  }
//
//  public ByteArrayBuf readBytes(byte[] dst) {
//    readBytes(dst, 0, dst.length);
//    return this;
//  }
//
//  public ByteArrayBuf readBytes(ByteArrayBuf dst) {
//    readBytes(dst, dst.writableBytes());
//    return this;
//  }
//
//  public ByteArrayBuf readBytes(ByteArrayBuf dst, int length) {
//    if (checkBounds) {
//      if (length > dst.writableBytes()) {
//        throw new IndexOutOfBoundsException(String.format(
//            "length(%d) exceeds dst.writableBytes(%d) where dst is: %s", length, dst.writableBytes(), dst));
//      }
//    }
//    readBytes(dst, dst.writerIndex(), length);
//    dst.writerIndex(dst.writerIndex() + length);
//    return this;
//  }
//
//  public ByteArrayBuf readBytes(ByteArrayBuf dst, int dstIndex, int length) {
//    checkReadableBytes(length);
//    getBytes(readerIndex, dst, dstIndex, length);
//    readerIndex += length;
//    return this;
//  }
//
//  public ByteArrayBuf readBytes(ByteBuffer dst) {
//    int length = dst.remaining();
//    checkReadableBytes(length);
//    getBytes(readerIndex, dst);
//    readerIndex += length;
//    return this;
//  }
//
//  public ByteArrayBuf skipBytes(int length) {
//    checkReadableBytes(length);
//    readerIndex += length;
//    return this;
//  }
//
//  public ByteArrayBuf writeBoolean(boolean value) {
//    writeByte(value ? 1 : 0);
//    return this;
//  }
//
//  public ByteArrayBuf writeByte(int value) {
//    ensureWritable0(1);
//    _setByte(writerIndex++, value);
//    return this;
//  }
//
//  public ByteArrayBuf writeShort(int value) {
//    ensureWritable0(2);
//    _setShort(writerIndex, value);
//    writerIndex += 2;
//    return this;
//  }
//
//  public ByteArrayBuf writeShortLE(int value) {
//    ensureWritable0(2);
//    _setShortLE(writerIndex, value);
//    writerIndex += 2;
//    return this;
//  }
//
//  public ByteArrayBuf writeMedium(int value) {
//    ensureWritable0(3);
//    _setMedium(writerIndex, value);
//    writerIndex += 3;
//    return this;
//  }
//
//  public ByteArrayBuf writeMediumLE(int value) {
//    ensureWritable0(3);
//    _setMediumLE(writerIndex, value);
//    writerIndex += 3;
//    return this;
//  }
//
//  public ByteArrayBuf writeInt(int value) {
//    ensureWritable0(4);
//    _setInt(writerIndex, value);
//    writerIndex += 4;
//    return this;
//  }
//
//  public ByteArrayBuf writeIntLE(int value) {
//    ensureWritable0(4);
//    _setIntLE(writerIndex, value);
//    writerIndex += 4;
//    return this;
//  }
//
//  public ByteArrayBuf writeLong(long value) {
//    ensureWritable0(8);
//    _setLong(writerIndex, value);
//    writerIndex += 8;
//    return this;
//  }
//
//  public ByteArrayBuf writeLongLE(long value) {
//    ensureWritable0(8);
//    _setLongLE(writerIndex, value);
//    writerIndex += 8;
//    return this;
//  }
//
//  public ByteArrayBuf writeChar(int value) {
//    writeShort(value);
//    return this;
//  }
//
//  public ByteArrayBuf writeFloat(float value) {
//    writeInt(Float.floatToRawIntBits(value));
//    return this;
//  }
//
//  public ByteArrayBuf writeDouble(double value) {
//    writeLong(Double.doubleToRawLongBits(value));
//    return this;
//  }
//
//  public ByteArrayBuf writeBytes(byte[] src, int srcIndex, int length) {
//    ensureWritable(length);
//    setBytes(writerIndex, src, srcIndex, length);
//    writerIndex += length;
//    return this;
//  }
//
//  public abstract ByteBuf setBytes(int index, byte[] src, int srcIndex, int length);
//
//  public ByteArrayBuf writeBytes(byte[] src) {
//    writeBytes(src, 0, src.length);
//    return this;
//  }
//
//  public ByteArrayBuf writeBytes(ByteArrayBuf src) {
//    writeBytes(src, src.readableBytes());
//    return this;
//  }
//
//  public ByteArrayBuf writeBytes(ByteArrayBuf src, int length) {
//    checkReadableBounds(src, length);
//    writeBytes(src, src.readerIndex(), length);
//    src.readerIndex(src.readerIndex() + length);
//    return this;
//  }
//
//  public ByteArrayBuf writeBytes(ByteArrayBuf src, int srcIndex, int length) {
//    ensureWritable(length);
//    setBytes(writerIndex, src, srcIndex, length);
//    writerIndex += length;
//    return this;
//  }
//
//  public abstract ByteArrayBuf setBytes(int index, ByteArrayBuf src, int srcIndex, int length);
//
//  public ByteArrayBuf writeBytes(ByteBuffer src) {
//    int length = src.remaining();
//    ensureWritable0(length);
//    setBytes(writerIndex, src);
//    writerIndex += length;
//    return this;
//  }
//
//  public abstract ByteArrayBuf setBytes(int index, ByteBuffer src);
//
//  public ByteArrayBuf writeZero(int length) {
//    if (length == 0) {
//      return this;
//    }
//
//    ensureWritable(length);
//    int wIndex = writerIndex;
//    checkIndex0(wIndex, length);
//
//    int nLong = length >>> 3;
//    int nBytes = length & 7;
//    for (int i = nLong; i > 0; i--) {
//      _setLong(wIndex, 0);
//      wIndex += 8;
//    }
//    if (nBytes == 4) {
//      _setInt(wIndex, 0);
//      wIndex += 4;
//    } else if (nBytes < 4) {
//      for (int i = nBytes; i > 0; i--) {
//        _setByte(wIndex, (byte) 0);
//        wIndex++;
//      }
//    } else {
//      _setInt(wIndex, 0);
//      wIndex += 4;
//      for (int i = nBytes - 4; i > 0; i--) {
//        _setByte(wIndex, (byte) 0);
//        wIndex++;
//      }
//    }
//    writerIndex = wIndex;
//    return this;
//  }
//
//  public int writeCharSequence(CharSequence sequence, Charset charset) {
//    int written = setCharSequence0(writerIndex, sequence, charset, true);
//    writerIndex += written;
//    return written;
//  }
//
//  public ByteArrayBuf copy() {
//    return copy(readerIndex, readableBytes());
//  }
//
//  public abstract ByteArrayBuf copy(int index, int length);
//
//  public ByteArrayBuf slice() {
//    return slice(readerIndex, readableBytes());
//  }
//
//  public ByteArrayBuf slice(int index, int length) {
//    return new UnpooledSlicedByteBuf(this, index, length);
//  }
//
//  public String toString(Charset charset) {
//    return toString(readerIndex, readableBytes(), charset);
//  }
//
//  public String toString(int index, int length, Charset charset) {
//    copyByteArray(index, length);
//    return ByteBufUtil.decodeString(this, index, length, charset);
//  }
//
//  @Override
//  public int hashCode() {
//    return ByteBufUtil.hashCode(this);
//  }
//
//  @Override
//  public boolean equals(Object o) {
//    return this == o || (o instanceof ByteArrayBuf && ByteBufUtil.equals(this, (ByteBuf) o));
//  }
//
//  public int compareTo(ByteArrayBuf that) {
//    return ByteBufUtil.compare(this, that);
//  }
//
//  @Override
//  public String toString() {
//    if (refCnt() == 0) {
//      return StringUtil.simpleClassName(this) + "(freed)";
//    }
//
//    StringBuilder buf = new StringBuilder()
//        .append(StringUtil.simpleClassName(this))
//        .append("(ridx: ").append(readerIndex)
//        .append(", widx: ").append(writerIndex)
//        .append(", cap: ").append(capacity());
//    if (maxCapacity != Integer.MAX_VALUE) {
//      buf.append('/').append(maxCapacity);
//    }
//
//    ByteArrayBuf unwrapped = unwrap();
//    if (unwrapped != null) {
//      buf.append(", unwrapped: ").append(unwrapped);
//    }
//    buf.append(')');
//    return buf.toString();
//  }
//
//  protected final void checkIndex(int index) {
//    checkIndex(index, 1);
//  }
//
//  protected final void checkIndex(int index, int fieldLength) {
//    checkIndex0(index, fieldLength);
//  }
//
//  private static void checkRangeBounds(final String indexName, final int index,
//                                       final int fieldLength, final int capacity) {
//    if (isOutOfBounds(index, fieldLength, capacity)) {
//      throw new IndexOutOfBoundsException(String.format(
//          "%s: %d, length: %d (expected: range(0, %d))", indexName, index, fieldLength, capacity));
//    }
//  }
//
//  final void checkIndex0(int index, int fieldLength) {
//    checkRangeBounds("index", index, fieldLength, capacity());
//  }
//
//  /**
//   * Throws an {@link IndexOutOfBoundsException} if the current
//   * {@linkplain #readableBytes() readable bytes} of this buffer is less
//   * than the specified value.
//   */
//  protected final void checkReadableBytes(int minimumReadableBytes) {
//    checkReadableBytes0(checkPositiveOrZero(minimumReadableBytes, "minimumReadableBytes"));
//  }
//
//  protected final void checkNewCapacity(int newCapacity) {
//    if (newCapacity < 0 || newCapacity > maxCapacity()) {
//      throw new IllegalArgumentException("newCapacity: " + newCapacity +
//          " (expected: 0-" + maxCapacity() + ')');
//    }
//  }
//
//  private void checkReadableBytes0(int minimumReadableBytes) {
//    if (readerIndex > writerIndex - minimumReadableBytes) {
//      throw new IndexOutOfBoundsException(String.format(
//          "readerIndex(%d) + length(%d) exceeds writerIndex(%d): %s",
//          readerIndex, minimumReadableBytes, writerIndex, this));
//    }
//  }
//
//  final void setIndex0(int readerIndex, int writerIndex) {
//    this.readerIndex = readerIndex;
//    this.writerIndex = writerIndex;
//  }
//
//  final void discardMarks() {
//    markedReaderIndex = markedWriterIndex = 0;
//  }
//
//  /**
//   * Determine if the requested {@code index} and {@code length} will fit within {@code capacity}.
//   *
//   * @param index    The starting index.
//   * @param length   The length which will be utilized (starting from {@code index}).
//   * @param capacity The capacity that {@code index + length} is allowed to be within.
//   * @return {@code true} if the requested {@code index} and {@code length} will fit within {@code capacity}.
//   * {@code false} if this would result in an index out of bounds exception.
//   */
//  public static boolean isOutOfBounds(int index, int length, int capacity) {
//    return (index | length | (index + length) | (capacity - (index + length))) < 0;
//  }
//
//  /**
//   * Checks that the given argument is positive or zero. If it is not , throws {@link IllegalArgumentException}.
//   * Otherwise, returns the argument.
//   */
//  public static int checkPositiveOrZero(int i, String name) {
//    if (i < 0) {
//      throw new IllegalArgumentException(name + ": " + i + " (expected: >= 0)");
//    }
//    return i;
//  }
//}
