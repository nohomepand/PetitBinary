package petit.bin.anno.array;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import petit.bin.anno.ArraySizeIndicator.ArraySizeByMethodIndicator;
import petit.bin.sinks.BinaryInput;

/**
 * [byte, short, int, or long] MethodName({@link BinaryInput}, {@link Object}, {@link Field}) なメソッドでサイズが指定された配列であることを指示する
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 * @see ArraySizeByMethodIndicator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ArraySizeByMethod {
	
	/**
	 * メソッド名
	 * 
	 * @return メソッド名
	 */
	public abstract String value();
	
}
