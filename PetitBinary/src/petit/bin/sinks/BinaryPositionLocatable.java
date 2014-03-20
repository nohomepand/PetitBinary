package petit.bin.sinks;

/**
 * 位置指定できるもの
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
public interface BinaryPositionLocatable {
	
	/**
	 * 現在の位置を得る
	 * 
	 * @return 現在の読み取り位置
	 */
	public abstract int position();
	
	/**
	 * 位置を設定する
	 * 
	 * @param pos 位置
	 */
	public abstract void position(final int pos);
	
}
