package petit.bin.sinks;

import java.io.IOException;

import petit.bin.StructByteOrder;

/**
 * 書き込み先を表す
 * 
 * @author 俺用
 * @since 2014/03/18 PetitBinarySerialization
 *
 */
public interface BinaryOutput extends BinaryPositionLocatable {
	
	/**
	 * バイトオーダーのための階層を一つ加え，次の書き込みのバイトオーダーを変更する
	 * 
	 * @param order バイトオーダー
	 */
	public abstract void pushByteOrder(final StructByteOrder order);
	
	/**
	 * バイトオーダーのための階層を一つ巻きもどす
	 */
	public abstract void popByteOrder();
	
	/**
	 * 次の書き込みのバイトオーダーを得る
	 * 
	 * @return 次の読み込みのバイトオーダー
	 */
	public abstract StructByteOrder byteOrder();
	
	/**
	 * 8ビット符号付整数値を書き込む
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeInt8(final byte v) throws IOException;
	
	/**
	 * 16ビット符号付整数値を書き込む
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeInt16(final short v) throws IOException;
	
	/**
	 * 32ビット符号付整数値を書き込む
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeInt32(final int v) throws IOException;
	
	/**
	 * 64ビット符号付整数値を書き込む
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeInt64(final long v) throws IOException;
	
	/**
	 * 単制度浮動少数値を書き込む
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeFloat(final float v) throws IOException;
	
	/**
	 * 倍制度浮動少数値を書き込む
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeDouble(final double v) throws IOException;
	
//	/**
//	 * 対象のバイト配列を書き込む
//	 * 
//	 * @param ar 対象のバイト配列 
//	 * @param begin バイト配列の開始位置
//	 * @param length 書き込みサイズ
//	 * @throws IOException
//	 */
//	public abstract void writeByteArray(final byte[] ar, final int begin, final int length) throws IOException;
	
//	/**
//	 * 対象の配列全体を書き込む
//	 * 
//	 * @param ar 対象の配列
//	 * @throws IOException
//	 */
//	public abstract void writeInt8Array(final byte[] ar) throws IOException;
//	
//	/**
//	 * 対象の配列全体を書き込む
//	 * 
//	 * @param ar 対象の配列
//	 * @throws IOException
//	 */
//	public abstract void writeInt16Array(final short[] ar) throws IOException;
//	
//	/**
//	 * 対象の配列全体を書き込む
//	 * 
//	 * @param ar 対象の配列
//	 * @throws IOException
//	 */
//	public abstract void writeInt32Array(final int[] ar) throws IOException;
//	
//	/**
//	 * 対象の配列全体を書き込む
//	 * 
//	 * @param ar 対象の配列
//	 * @throws IOException
//	 */
//	public abstract void writeInt64Array(final long[] ar) throws IOException;
	
}
