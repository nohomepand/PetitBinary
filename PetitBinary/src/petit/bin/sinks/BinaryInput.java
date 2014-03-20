package petit.bin.sinks;

import java.io.IOException;

import petit.bin.StructByteOrder;

/**
 * 読み込み元を表す
 * 
 * @author 俺用
 * @since 2014/03/18 PetitBinarySerialization
 *
 */
public interface BinaryInput extends BinaryPositionLocatable {
	
	/**
	 * バイトオーダーのための階層を一つ加え，次の読み込みのバイトオーダーを変更する
	 * 
	 * @param order バイトオーダー
	 */
	public abstract void pushByteOrder(final StructByteOrder order);
	
	/**
	 * バイトオーダーのための階層を一つ巻きもどす
	 */
	public abstract void popByteOrder();
	
	/**
	 * 次の読み込みのバイトオーダーを得る
	 * 
	 * @return 次の読み込みのバイトオーダー
	 */
	public abstract StructByteOrder byteOrder();
	
	/**
	 * @return 1 バイトを読み込み，それを 8ビット符号付整数値として解釈した値
	 * @throws IOException
	 */
	public abstract byte readInt8() throws IOException;
	
	/**
	 * @return 2 バイトを読み込み，それを現在のバイトオーダーで 16ビット符号付整数値として解釈した値
	 * @throws IOException
	 */
	public abstract short readInt16() throws IOException;
	
	/**
	 * @return 4 バイトを読み込み，それを現在のバイトオーダーで 32ビット符号付整数値として解釈した値
	 * @throws IOException
	 */
	public abstract int readInt32() throws IOException;
	
	/**
	 * @return 8 バイトを読み込み，それを現在のバイトオーダーで 64ビット符号付整数値として解釈した値
	 * @throws IOException
	 */
	public abstract long readInt64() throws IOException;
	
//	/**
//	 * @return 1 バイトを読み込み，それを 8ビット符号なし整数値として解釈した値
//	 * @throws IOException
//	 */
//	public abstract short readUInt8() throws IOException;
//	
//	/**
//	 * @return 2 バイトを読み込み，それを現在のバイトオーダーで 16ビット符号なし整数値として解釈した値
//	 * @throws IOException
//	 */
//	public abstract int readUInt16() throws IOException;
//	
//	/**
//	 * @return 4 バイトを読み込み，それを現在のバイトオーダーで 32ビット符号なし整数値として解釈した値
//	 * @throws IOException
//	 */
//	public abstract long readUInt32() throws IOException;
	
	/**
	 * @return 4 バイトを読み込み単精度浮動少数値として解釈した値
	 * @throws IOException
	 */
	public abstract float readFloat() throws IOException;
	
	/**
	 * @return 8 バイトを読み込み倍精度浮動少数値として解釈した値
	 * @throws IOException
	 */
	public abstract double readDouble() throws IOException;
	
//	/**
//	 * 読み込みサイズ分のバイトを読み込む
//	 * 
//	 * @param length 読み込みサイズ
//	 * @param out 戻り値
//	 * @return outそのもの，または戻り値の配列のサイズが length 未満の場合は新たな配列
//	 * @throws IOException
//	 */
//	public abstract byte[] readByteArray(final int length, final byte[] out) throws IOException;
	
//	/**
//	 * out.length 分の値を読み込む
//	 * 
//	 * @param out 戻り値
//	 * @return outそのもの
//	 * @throws IOException
//	 */
//	public abstract byte[] readInt8Array(final byte[] out) throws IOException;
//	
//	/**
//	 * out.length 分の値を読み込む
//	 * 
//	 * @param out 戻り値
//	 * @return outそのもの
//	 * @throws IOException
//	 */
//	public abstract byte[] readInt16Array(final short[] out) throws IOException;
//	
//	/**
//	 * out.length 分の値を読み込む
//	 * 
//	 * @param out 戻り値
//	 * @return outそのもの
//	 * @throws IOException
//	 */
//	public abstract byte[] readInt32Array(final int[] out) throws IOException;
//	
//	/**
//	 * out.length 分の値を読み込む
//	 * 
//	 * @param out 戻り値
//	 * @return outそのもの
//	 * @throws IOException
//	 */
//	public abstract byte[] readInt64Array(final long[] out) throws IOException;
	
}
