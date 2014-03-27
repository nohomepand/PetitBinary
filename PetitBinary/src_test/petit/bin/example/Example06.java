package petit.bin.example;

import java.nio.ByteBuffer;

import petit.bin.StructByteOrder;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.field.array.ExternStructArray;
import petit.bin.sinks.BinaryInput;

/**
 * An example of an array of structure
 * 
 * @author 俺用
 * @since 2014/03/28 PetitBinary
 *
 */
@SuppressWarnings("unused")
@Struct(byteOrder = StructByteOrder.NEUTRAL)
public final class Example06 extends AbstractExample {
	
	@StructMember(0)
	private int _size;
	
	@StructMember(1)
	@ArraySizeByField("_size")
	private Example04.StringWithLength[] _str_ary1;
	
	@StructMember(2)
	@ArraySizeByField("_size")
	@ExternStructArray("typeResolver")
	private Object[] _str_ary2;
	
	public static void main(String[] args) {
		final Example06 ao = new Example06();
		ao._str_ary1 = new Example04.StringWithLength[] {
			new Example04.StringWithLength("abc"),
			new Example04.StringWithLength("def"),
			new Example04.StringWithLength("ghi"),
			new Example04.StringWithLength("foobarbaz")
		};
		ao._str_ary2 = new Object[ao._str_ary1.length];
		for (int i = 0; i < ao._str_ary1.length; i++)
			ao._str_ary2[i] = new Example04.NullTerminatedString(ao._str_ary1[i].get());
		ao._size = ao._str_ary1.length;
		
		ByteBuffer buffer = checkSerializedObject(ao);
		System.out.println(dumpData(buffer));
	}
	
	private final Class<?> typeResolver() {
		return Example04.NullTerminatedString.class;
	}
	
}
