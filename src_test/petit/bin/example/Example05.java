package petit.bin.example;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import petit.bin.StructByteOrder;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.field.ExternStruct;

/**
 * An example of C like union member.
 * 
 * @author 俺用
 * @since 2014/03/22 PetitBinary
 *
 */
@SuppressWarnings("unused")
@Struct(byteOrder = StructByteOrder.NEUTRAL)
public final class Example05 extends AbstractExample {
	
	@StructMember(0)
	private int _type;
	
	@StructMember(1)
	@ExternStruct("unionMemberResolver")
	private Object _union_member;
	
//	before 2014/03/23 PetitBinary ver.(this type of signature is also acceptable)
//	private final Object unionMemberResolver(final Object inst, final Field f) {
//		switch (_type) {
//		case 0: return new Example04.StringWithLength("");
//		case 1: return new Example04.NullTerminatedString("");
//		default: throw new RuntimeException("Unsupported type " + _type);
//		}
//	}
	private final Class<?> unionMemberResolver() {
		switch (_type) {
		case 0: return Example04.StringWithLength.class;
		case 1: return Example04.NullTerminatedString.class;
		default: throw new RuntimeException("Unsupported type " + _type);
		}
	}
	
	public static void main(String[] args) {
		final Example05 ao = new Example05();
		ByteBuffer buffer;
		ao._type = 0;
		ao._union_member = new Example04.StringWithLength("abcdefg");
		buffer = checkSerializedObject(ao);
		System.out.println(dumpData(buffer));
		
		System.out.println();
		
		ao._type = 1;
		ao._union_member = new Example04.NullTerminatedString("hijklmn");
		buffer = checkSerializedObject(ao);
		System.out.println(dumpData(buffer));
	}
}
