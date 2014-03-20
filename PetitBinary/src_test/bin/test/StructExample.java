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
import petit.bin.anno.field.UInt16;
import petit.bin.anno.field.array.CharArray;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.impl.InOutByteBuffer;

/**
 * 構造体のサンプル<br />
 * 構造体として扱いたいクラスには {@literal @Struct} アノテーションを設定する<br />
 * <br />
 * 読み込み時に，このクラスのインスタンスは nullary なコンストラクタ(可視性は問わない)で初期化されるか，{@link com.sun.Unsafe} によって生成される．
 * 
 * @author 俺用
 * @since 2014/03/20 PetitBinarySerialization
 *
 */
@Struct(byteOrder = StructByteOrder.BIG_ENDIAN)
public class StructExample {
	
	/**
	 * 構造体のメンバ位置 = 0<br />
	 * デフォルトの {@literal @Int32} で読み書き<br />
	 * final で修飾してはならない
	 */
	@StructMember(0)
	public int b1; // public, protected, private, またはデフォルトのアクセスのいずれであってもかまわない
	
	/**
	 * 構造体のメンバ位置 = 1<br />
	 * デフォルトの {@literal @Int64} で読み書き<br />
	 * 読み書き時に位置マーカをコンテキスト情報に生成する
	 */
	@StructMember(value = 1, marker = "marker_1")
	protected long b2;
	
	/**
	 * 構造体のメンバ位置 = 2<br />
	 * {@literal @Int16} で読み書き
	 */
	@StructMember(2)
	@UInt16
	private int b3;
	
	/**
	 * 構造体のメンバではないフィールドも定義できる
	 */
	@SuppressWarnings("unused")
	private int p;
	
	/**
	 * 構造体のメンバ位置 = 3<br />
	 * 外部で定義された構造体を利用できる
	 */
	@StructMember(3)
	private InternalStruct b4;
	
	/**
	 * 初期化<br />
	 * このクラスはデフォルトコンストラクタが無いため， com.sun.Unsafe でインスタンスが生成される
	 */
	public StructExample(final int x, final long y, final int z) {
		b1 = x;
		b2 = y;
		b3 = z;
		b4 = new InternalStruct("StructExample");
	}
	
	/**
	 * null-terminated な文字列を表す構造体
	 * 
	 * @author 俺用
	 * @since 2014/03/20 PetitBinarySerialization
	 *
	 */
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public static final class InternalStruct {
		
		/**
		 * 構造体のメンバ位置 = 0<br />
		 * 文字列の本体を表す．{@literal @CharArray} は String, char[], byte[] なフィールドに適用できる．<br />
		 * petit.bin.anno.field.array パッケージに定義された配列を示すアノテーション({@literal Int8Array}など)は，
		 * 読み込み時に読み込まれるサイズを決定する必要がある．<br />
		 * サイズの決定方法は petit.bin.anno.array パッケージに定義された配列サイズを示すアノテーション({@literal @ArraySizeByConstant}など)で指定される．
		 */
		@StructMember(0)
		@CharArray("utf-8")
		@ArraySizeByMethod("getStrLen")
		private String _str;
		
		/**
		 * 構造体のメンバ位置 = 1<br />
		 * 文字列の本体の直後に置かれる null-char を表す
		 */
		@SuppressWarnings("unused")
		@StructMember(1)
		private byte _null_char;
		
		/**
		 * 初期化
		 * 
		 * @param str 文字列
		 */
		public InternalStruct(final String str) {
			if (str == null)
				throw new NullPointerException("str must not be null");
			
			_str = str;
			_null_char = 0;
		}
		
		/**
		 * {@link #_str} に与えられた {@literal @ArraySizeByMethod} アノテーションが示す，配列のサイズの指定方法<br />
		 * 次のシグネチャを持たなくてはならない<br />
		 * <pre>
		 * return type: [byte, short, int, long]
		 * parameters: {@link BinaryInput}, {@link Object}, {@link Field}
		 * </pre>
		 * 
		 * @param in 読み込み元
		 * @param inst 元のフィールドを持つインスタンス
		 * @param f 元のフィールド
		 * @return
		 * @throws IOException
		 */
		@SuppressWarnings("unused")
		private final int getStrLen(final BinaryInput in, final Object inst, final Field f) throws IOException {
			final int mark = in.position();
			int size;
			for (size = 0; in.readInt8() != 0; size++);
			in.position(mark);
			System.out.println("(NullChar StrLen=" + size + ")");
			return size;
		}
		
		public final String get() {
			return _str;
		}
		
		public final void set(final String str) {
			_str = str;
		}
		
	}
	
	/**
	 * {@link StructExample} の派生型では，元の
	 * @author 俺用
	 * @since 2014/03/20 PetitBinarySerialization
	 *
	 */
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public static final class SubStruct extends StructExample {
		
		/**
		 * 構造体のメンバ位置 = 4<br />
		 * 0-3までのメンバ位置は，親クラスである {@link StructExample} によって占有されているため，
		 * サブクラスではそれ以降のメンバ位置を指定しなければならない
		 */
		@StructMember(4)
		private InternalStruct s1;
		
		public SubStruct(final int x, final long y, final int z, final String str) {
			super(x, y, z);
			s1 = new InternalStruct(str);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		final BinaryAccessorFactory baf = new BinaryAccessorFactory();
		testStructExample(baf);
		testSubStruct(baf);
	}
	
	private static final void testStructExample(final BinaryAccessorFactory baf) throws Exception {
		final BinaryAccessor<StructExample> ba = baf.getBinaryAccessor(StructExample.class);
		final InOutByteBuffer write_buf = new InOutByteBuffer();
		final StructExample written = new StructExample(1, 2L, -1);
		written.b4.set("Hello");
		ba.writeTo(null, written, write_buf);
		
		showBuffer(write_buf.getFlippedShallowCopy());
		
		final InOutByteBuffer read_buf = new InOutByteBuffer(write_buf.getFlippedShallowCopy());
		final SerializationContext ctx = new SerializationContext();
		final StructExample read = ba.readFrom(ctx, read_buf);
		System.out.println("Read b1=" + read.b1);
		System.out.println("Read b2=" + read.b2);
		System.out.println("Read b3=" + read.b3);
		System.out.println("Read b4=" + read.b4.get());
		System.out.println("marker: " + ctx.getMarker());
	}
	
	private static final void testSubStruct(final BinaryAccessorFactory baf) throws Exception {
		final BinaryAccessor<SubStruct> ba = baf.getBinaryAccessor(SubStruct.class);
		final InOutByteBuffer write_buf = new InOutByteBuffer();
		final SerializationContext ctx = new SerializationContext();
		final SubStruct written = new SubStruct(1, 2, -1, "hoge");
		
		ba.writeTo(ctx, written, write_buf);
		System.out.println("marker: " + ctx.getMarker());
		
		showBuffer(write_buf.getFlippedShallowCopy());
		
		final InOutByteBuffer read_buf = new InOutByteBuffer(write_buf.getFlippedShallowCopy());
		
		final StructExample read = ba.readFrom(null, read_buf);
		System.out.println("Read b1=" + read.b1);
		System.out.println("Read b2=" + read.b2);
		System.out.println("Read b3=" + read.b3);
		System.out.println("Read b4=" + read.b4.get());
		System.out.println("Read s1=" + (read instanceof SubStruct ? ((SubStruct) read).s1.get() : "(not a SubStruct)"));
	}
	
	public static final void showBuffer(final ByteBuffer b) {
		for (int i = 0, size = b.limit(); i < size; i++) {
			System.out.printf("%02x ", b.get());
		}
		System.out.println();
	}
	
}
