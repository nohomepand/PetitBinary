package petit.bin.example;

import java.nio.ByteBuffer;

import petit.bin.StructByteOrder;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;

/**
 * An example of primitive types
 * 
 * @author 俺用
 * @since 2014/03/22 PetitBinary
 *
 */
@SuppressWarnings("unused")
@Struct(byteOrder = StructByteOrder.NEUTRAL)
public class Example01 extends AbstractExample {
	
	@StructMember(0) // Index of a structure member begins 0
	// default member annotation: @Int8Boolean 
	private boolean _boolean;
	
	@StructMember(1)
	// default member annotation: @Int8
	private byte _byte;
	
	@StructMember(2)
	// default member annotation: @Int16
	private short _short;
	
	@StructMember(3)
	// default member annotation: @UInt16
	private char _char;
	
	@StructMember(4)
	// default member annotation: @Int32
	private int _int;
	
	@StructMember(5)
	// default member annotation: @Int64
	private long _long;
	
	@StructMember(6)
	// default member annotation: @Float32
	private float _float;
	
	@StructMember(7)
	// default member annotation: @Float64
	private double _double;
	
	/**
	 * This field is not serialized
	 */
	private int _no_serialize_field;
	
	public static void main(String[] args) {
		final Example01 ao = new Example01();
		ao._boolean = true;
		ao._byte = -1;
		ao._short = -2;
		ao._char = 'a';
		ao._int = -3;
		ao._long = -4;
		ao._float = 1.234f;
		ao._double = 1.234;
		final ByteBuffer buffer = checkSerializedObject(ao);
		System.out.println(dumpData(buffer));
	}
}
