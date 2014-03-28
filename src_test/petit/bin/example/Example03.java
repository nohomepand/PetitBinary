package petit.bin.example;

import java.nio.ByteBuffer;

import petit.bin.StructByteOrder;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.field.Float32;
import petit.bin.anno.field.UInt16;
import petit.bin.anno.field.UInt32;
import petit.bin.anno.field.UInt8;

/**
 * An example of usage of specified type annotation
 * 
 * @author 俺用
 * @since 2014/03/22 PetitBinary
 *
 */
@SuppressWarnings("unused")
@Struct(byteOrder = StructByteOrder.NEUTRAL)
public final class Example03 extends AbstractExample {
	
	@StructMember(0)
	private long _long_default;
	
	@StructMember(1)
	@UInt8
	private long _long_uint8;
	
	@StructMember(2)
	@UInt16
	private long _long_uint16;
	
	@StructMember(3)
	@UInt32
	private long _long_uint32_1;
	
	@StructMember(4)
	@UInt32
	private long _long_uint32_2;
	
//	you cannot annotate "long" field with Float32.
//	Float32.class defines its support type but there are no "long" type,
//	 only "float", "Float", "double", and "Double" types are valid.
//	@StructMember(5)
//	@Float32
//	private long _long_float32;
	
	public static void main(String[] args) {
		final Example03 ao = new Example03();
		ao._long_default = 10;
		ao._long_uint8 = 257;    // "257 != 1" is displayed
		ao._long_uint16 = 65535;
		ao._long_uint32_1 = -8;  // "-8 != 4294967288" is displayed
		ao._long_uint32_2 = 8;
		final ByteBuffer buffer = checkSerializedObject(ao);
		System.out.println(dumpData(buffer));
	}
	
}
