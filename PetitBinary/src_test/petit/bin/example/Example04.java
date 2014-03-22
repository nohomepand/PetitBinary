package petit.bin.example;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import petit.bin.StructByteOrder;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.field.array.CharArray;
import petit.bin.sinks.BinaryInput;

/**
 * An example of an external structure
 * 
 * @author 俺用
 * @since 2014/03/22 PetitBinary
 *
 */
public final class Example04 extends AbstractExample {
	
	private static final String UTF8 = "utf-8";
	
	private static final Charset UTF8_CS = Charset.forName(UTF8);

	public static void main(String[] args) {
		final ComplexStructure ao = new ComplexStructure("TEXT1", "FOO BAR BAZ");
		final ByteBuffer buffer = checkSerializedObject(ao);
		System.out.println(dumpData(buffer));
	}
	
	/**
	 * 
	 * @author 俺用
	 * @since 2014/03/22 PetitBinary
	 *
	 */
	@SuppressWarnings("unused")
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	private static final class ComplexStructure {
		
		@StructMember(0)
		private StringWithLength _str1;
		
		@StructMember(1)
		private NullTerminatedString _str2;
		
		public ComplexStructure(final String str1, final String str2) {
			_str1 = new StringWithLength(str1);
			_str2 = new NullTerminatedString(str2);
		}
		
	}
	
	/**
	 * Represents a string object which is indicated by length field.
	 * 
	 * @author 俺用
	 * @since 2014/03/22 PetitBinary
	 *
	 */
	@SuppressWarnings("unused")
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public static final class StringWithLength {
		
		/**
		 * This field indicates the length of "_string"
		 */
		@StructMember(0)
		private int _length;
		
		/**
		 * This field is contents of the string.
		 * The size of this field is indicated by "_length" field.
		 */
		@StructMember(1)
		@ArraySizeByField("_length")
		private byte[] _string;
		
		public StringWithLength(final String str) {
			_string = str.getBytes(UTF8_CS);
			_length = _string.length;
		}
		
		public final String get() {
			return new String(_string, UTF8_CS);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof StringWithLength) return get().equals(((StringWithLength) obj).get());
			else if (obj instanceof NullTerminatedString) return get().equals(((NullTerminatedString) obj).get());
			else return false;
		}
		
	}
	
	/**
	 * A Null-Terminated String object
	 * 
	 * @author 俺用
	 * @since 2014/03/22 PetitBinary
	 *
	 */
	@SuppressWarnings("unused")
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public static final class NullTerminatedString {
		
		/**
		 * This field is contents of the string.
		 * Size of the array is results of "computeStringLength" method.
		 */
		@StructMember(0)
		@CharArray(UTF8)
		@ArraySizeByMethod("computeStringLength")
		private byte[] _string;
		
		/**
		 * This field represents the terminator of the string (null-character: '\0').
		 */
		@StructMember(1)
		private byte _null_char;
		
		public NullTerminatedString(final String str) {
			_string = str.getBytes(UTF8_CS);
			_null_char = 0;
		}
		
		/**
		 * This method is invoked when reading _string field.
		 * 
		 * @param bi a source buffer
		 * @param inst a instance of {@link NullTerminatedString}. This is identically equal to "this" variable in this context.
		 * @param f a field which is equal to "_string" field in this context.
		 * @return a length of the "_string" field.
		 * @throws IOException 
		 */
		private final int computeStringLength(final BinaryInput bi, final Object inst, final Field f) throws IOException {
			final int mark = bi.position();
			int size;
			for (size = 0; bi.readInt8() != 0; size++);
			bi.position(mark);
			return size;
		}
		
		public final String get() {
			return new String(_string, UTF8_CS);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof StringWithLength) return get().equals(((StringWithLength) obj).get());
			else if (obj instanceof NullTerminatedString) return get().equals(((NullTerminatedString) obj).get());
			else return false;
		}
		
	}
	
}
