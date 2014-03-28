package petit.bin;

/**
 * バイトオーダーの種類を表す
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
public enum StructByteOrder {
	/**
	 * Big Endianで格納されていることを表す
	 */
	BIG_ENDIAN,
	
	/**
	 * Little Endianで格納されていることを表す
	 */
	LITTLE_ENDIAN,
	
	/**
	 * 直前の要素の {@link ByteOrder} と同じバイトオーダーで格納されていることを表す
	 */
	NEUTRAL;
}
