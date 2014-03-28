package petit.bin.sinks.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import petit.bin.StructByteOrder;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

/**
 * {@link BinaryInput} および {@link BinaryOutput} の {@link ByteBuffer}による実装
 *  
 * @author 俺用
 * @since 2014/03/19 PetitBinarySerialization
 *
 */
public final class InOutByteBuffer implements BinaryInput, BinaryOutput {
	
	/**
	 * 自動拡張する際の初期サイズ
	 */
	public static final int BUFFER_EXPAND_SIZE = 128;
	
	/**
	 * 自動拡張時において，その回数に応じた {@link BUFFER_EXPAND_SIZE} の最大乗数
	 */
	public static final int BUFFER_EXPAND_MAX_SCALE_COUNT = 16;
	
	private ByteBuffer _sink;
	
	private int _expand_count;
	
	private final LinkedList<StructByteOrder> _bo_stack;
	
	/**
	 * 初期化
	 * 
	 * @param sink 入出力先のバッファ
	 */
	public InOutByteBuffer(final ByteBuffer sink) {
		_sink = sink;
		_expand_count = 0;
		_bo_stack = new LinkedList<StructByteOrder>();
	}
	
	/**
	 * 空のバッファで初期化
	 */
	public InOutByteBuffer() {
		_sink = ByteBuffer.allocate(0);
		_expand_count = 0;
		_bo_stack = new LinkedList<StructByteOrder>();
	}
	
	private final void ensureByteBuffer(final int bytes_to_be_add) {
		if (_sink.remaining() >= bytes_to_be_add)
			return;
		
		if (_expand_count < BUFFER_EXPAND_MAX_SCALE_COUNT)
			_expand_count++;
		
		final ByteBuffer new_sink = ByteBuffer.allocate(_sink.capacity() + BUFFER_EXPAND_SIZE * _expand_count);
		_sink.flip();
		new_sink.put(_sink);
		new_sink.order(_sink.order());
		_sink = new_sink;
	}
	
	/**
	 * 現在のバッファのシャローコピーを得る<br />
	 * 得られるバッファは現在のバッファをflipしたものと等しい(position = 0, limit = original.position)
	 * 
	 * @return 現在のバッファのシャローコピー
	 */
	public final ByteBuffer getFlippedShallowCopy() {
		final ByteBuffer dup = _sink.duplicate();
		dup.flip();
		return dup;
	}
	
	@Override
	public int position() {
		return _sink.position();
	}
	
	@Override
	public void position(int pos) {
		_sink.position(pos);
	}
	
	@Override
	public void pushByteOrder(StructByteOrder order) {
		_bo_stack.push(order);
		switch (byteOrder()) {
		case BIG_ENDIAN: _sink.order(ByteOrder.BIG_ENDIAN); break;
		case LITTLE_ENDIAN: _sink.order(ByteOrder.LITTLE_ENDIAN); break;
		}
	}
	
	@Override
	public void popByteOrder() {
		_bo_stack.pop();
		switch (byteOrder()) {
		case BIG_ENDIAN: _sink.order(ByteOrder.BIG_ENDIAN); break;
		case LITTLE_ENDIAN: _sink.order(ByteOrder.LITTLE_ENDIAN); break;
		}
	}
	
	@Override
	public StructByteOrder byteOrder() {
		if (_bo_stack.isEmpty())
			return StructByteOrder.NEUTRAL;
		else
			return _bo_stack.getFirst();
	}
	
	@Override
	public byte readInt8() throws IOException {
		return _sink.get();
	}
	
	@Override
	public short readInt16() throws IOException {
		return _sink.getShort();
	}
	
	@Override
	public int readInt32() throws IOException {
		return _sink.getInt();
	}
	
	@Override
	public long readInt64() throws IOException {
		return _sink.getLong();
	}
	
	@Override
	public float readFloat() throws IOException {
		return _sink.getFloat();
	}
	
	@Override
	public double readDouble() throws IOException {
		return _sink.getDouble();
	}
	
	@Override
	public void writeInt8(byte v) throws IOException {
		ensureByteBuffer(1);
		_sink.put(v);
	}
	
	@Override
	public void writeInt16(short v) throws IOException {
		ensureByteBuffer(2);
		_sink.putShort(v);
	}
	
	@Override
	public void writeInt32(int v) throws IOException {
		ensureByteBuffer(4);
		_sink.putInt(v);
	}
	
	@Override
	public void writeInt64(long v) throws IOException {
		ensureByteBuffer(8);
		_sink.putLong(v);
	}
	
	@Override
	public void writeFloat(float v) throws IOException {
		ensureByteBuffer(8);
		_sink.putFloat(v);
	}
	
	@Override
	public void writeDouble(double v) throws IOException {
		ensureByteBuffer(16);
		_sink.putDouble(v);
	}
	
}
