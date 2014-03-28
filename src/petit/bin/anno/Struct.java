package petit.bin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import petit.bin.StructByteOrder;

/**
 * 構造体であることを指示する
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Struct {
	
	/**
	 * 構造体のアライメントのByte Packingのサイズ
	 * 
	 * @return 構造体のアライメント
	 */
	@Deprecated
	public abstract int packSize() default 1;
	
	/**
	 * 構造体全体のバイトオーダー
	 * 
	 * @return 構造体全体のバイトオーダー
	 */
	public abstract StructByteOrder byteOrder() default StructByteOrder.NEUTRAL;
	
}
