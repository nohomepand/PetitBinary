package petit.bin.example;

import java.nio.ByteBuffer;

import petit.bin.StructByteOrder;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.array.ArraySizeConstant;

/**
 * An example of primitive type arrays
 * 
 * @author 俺用
 * @since 2014/03/22 PetitBinary
 *
 */
@SuppressWarnings("unused")
@Struct(byteOrder = StructByteOrder.NEUTRAL)
public final class Example02 extends AbstractExample {
	
	@StructMember(0)
	private int _size_field;
	
	@StructMember(1)
	@ArraySizeConstant(5)
	private byte[] _byte_array;
	
	@StructMember(2)
	@ArraySizeConstant(10)
	private short[] _short_array;
	
	@StructMember(3)
	@ArraySizeByField("_size_field")
	private char[] _char_array;
	
	@StructMember(4)
	@ArraySizeConstant(10)
	private int[] _int_array;
	
	@StructMember(5)
	@ArraySizeByField("_size_field")
	private long[] _long_array;
	
	@StructMember(6)
	@ArraySizeByField("_size_field")
	private float[] _float_array;
	
	@StructMember(7)
	@ArraySizeByField("_size_field")
	private double[] _double_array;
	
	public static void main(String[] args) {
		final String text = "foobarbaz";
		final Example02 ao = new Example02();
		ao._size_field = text.length();
		ao._byte_array = new byte[] {0, 1, 2, 3, 4};
		ao._short_array = new short[] { -1, -2, -3, -4, -5, -6, -7, -8, -9, -10 };
		ao._char_array = text.toCharArray();
		ao._int_array = new int[] { 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 }; for (int i = 0; i < ao._int_array.length; ao._int_array[i++] = i);
		ao._long_array = new long[ao._size_field]; for (int i = 0; i < ao._long_array.length; ao._long_array[i++] = i);
		ao._float_array = new float[ao._size_field]; for (int i = 0; i < ao._float_array.length; ao._float_array[i++] = 5.0f / i);
		ao._double_array = new double[ao._size_field]; for (int i = 0; i < ao._double_array.length; ao._double_array[i++] = 5.0 / i);
		final ByteBuffer buffer = checkSerializedObject(ao);
		System.out.println(dumpData(buffer));
	}
	
}
