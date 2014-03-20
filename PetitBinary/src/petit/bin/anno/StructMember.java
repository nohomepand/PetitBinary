package petit.bin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * フィールドが構造体の要素であることを指示する<br />
 * {@link #value()} に要素の順番を与える
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StructMember {
	
	/**
	 * フィールドの位置(base 0)
	 * 
	 * @return フィールドの位置(base 0)
	 */
	public abstract int value();
	
	/**
	 * フィールドの位置マーカ名
	 * 
	 * @return フィールドの位置マーカ名
	 */
	public abstract String marker() default "";
	
//	/**
//	 * フィールドのバイトオーダー
//	 * 
//	 * @return フィールドのバイトオーダー
//	 */
//	public abstract StructByteOrder byteOrder() default StructByteOrder.NEUTRAL;
	
//	/**
//	 * フィールドのアライメント
//	 * 
//	 * @return
//	 */
//	public abstract int alignment() default 1;
	
}
