package bin.test;


import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import petit.bin.BinaryAccessor;
import petit.bin.BinaryAccessorFactory;
import petit.bin.SerializationContext;
import petit.bin.StructByteOrder;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.field.UInt8;
import petit.bin.anno.field.array.CharArray;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.impl.InOutByteBuffer;

// 構造体宣言 Structure Definition
@Struct(packSize = 1, byteOrder = StructByteOrder.BIG_ENDIAN)
@SuppressWarnings("unused")
public class Hoge {
	
	public Hoge(final int x) {
//		piyo = new Piyo();
	}
	
	@StructMember(0) // 構造体メンバ宣言
	@UInt8 // フィールドアノテーション
	private int x;
	
	@StructMember(1)
	private int y;
	
	@StructMember(2)
	@CharArray("utf-8")
	// sjis 12 34 56 78 87 65 43 21 96 bc 91 4f 00 f5
	// utf8 12 34 56 78 87 65 43 21 e5 90 8d e5 89 8d 00 f5 
//	@ArraySizeConstant(10)
	@ArraySizeByMethod("resolveNullTerminatedStringLength")
	private String name;
	
	@StructMember(3)
	private Piyo piyo;
	
	@StructMember(4)
//	@UnionItem("resolveB")
	private byte b;
	
	@Struct(byteOrder = StructByteOrder.LITTLE_ENDIAN)
	private static final class Piyo {
		@StructMember(0)
		private int v1;
		
		@StructMember(1)
		private int v2;
	}
	
	@StructMember(5)
	private int k;
	
	public static void main(String[] args) throws Exception {
//		System.out.println(Struct.class.getMethod("packSize").getDefaultValue());
//		final Hoge hoge = new Hoge(0);
//		final Field fx = Hoge.class.getDeclaredField("x");
//		final Field fz = Hoge.class.getDeclaredField("z");
//		final Field fb = Hoge.class.getDeclaredField("b");
//		fb.setInt(hoge, 5);
//		System.out.println(hoge.b);
		final BinaryAccessorFactory baf = new BinaryAccessorFactory();
		final ByteBuffer written = testWrite(baf);
		final Hoge hoge = testRead(baf, written);
		System.out.printf("%x%n%x%n%d%n%s", hoge.x, hoge.y, hoge.b, hoge.name);
	}
	
	public static final ByteBuffer testWrite(final BinaryAccessorFactory baf) throws Exception {
		final BinaryAccessor<Hoge> hoge_ba = baf.getBinaryAccessor(Hoge.class);
		
		final InOutByteBuffer sink = new InOutByteBuffer();
		final Hoge hoge = new Hoge(10);
		hoge.x = 0x12345678;
		hoge.y = 0x87654321;
		hoge.name = "名前あ\0";
		hoge.b = (byte) -11;
		hoge.piyo = new Piyo();
//		hoge.piyo.v1 = 100;
//		hoge.piyo.v2 = 0x11223344;
		hoge.k = 0x11223344;
		final SerializationContext ctx = new SerializationContext();
		hoge_ba.writeTo(ctx, hoge, sink);
		showBuffer(sink.getFlippedShallowCopy());
		return sink.getFlippedShallowCopy();
	}
	
	public static final Hoge testRead(final BinaryAccessorFactory baf, final ByteBuffer src) throws Exception {
		final BinaryAccessor<Hoge> hoge_ba = baf.getBinaryAccessor(Hoge.class);
		final InOutByteBuffer sink = new InOutByteBuffer(src);
		final SerializationContext ctx = new SerializationContext();
		return hoge_ba.readFrom(ctx, sink);
	}
	
	public static final void testInOutBuffer() throws IOException {
		final InOutByteBuffer sink = new InOutByteBuffer();
		sink.pushByteOrder(StructByteOrder.BIG_ENDIAN);
			sink.pushByteOrder(StructByteOrder.LITTLE_ENDIAN);
				sink.writeInt64(9);
			sink.popByteOrder();
			sink.writeInt32(0x12345678);
		sink.popByteOrder();
		ByteBuffer b = sink.getFlippedShallowCopy();
		showBuffer(b);
	}
	
	public static final void showBuffer(final ByteBuffer b) {
		for (int i = 0, size = b.limit(); i < size; i++) {
			System.out.printf("%02x ", b.get());
		}
		System.out.println();
	}
	
	protected final int resolveNullTerminatedStringLength(final BinaryInput bi, final Object inst, final Field field) throws IOException {
		final int mark = bi.position();
		int size;
		for (size = 0; bi.readInt8() != (byte) 0; size++);
		bi.position(mark);
		return size + 1;
	}
	
}
