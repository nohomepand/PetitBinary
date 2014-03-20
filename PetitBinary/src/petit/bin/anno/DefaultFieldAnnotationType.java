package petit.bin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Struct} が指示されたクラスの {@link StructMember} が指示されたフィールドに，
 * フィールドアノテーションが明示的に指示されていないフィールドの場合にデフォルトで使用されるフィールドアノテーションであることを指示するもの
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface DefaultFieldAnnotationType {
	
	/**
	 * フィールドアノテーションが明示的に指示されていないフィールドが，この型の場合，元のフィールドアノテーションが自動的に選択される
	 * 
	 * @return このフィールドアノテーションがデフォルトのフィールドアノテーションとなるような型
	 */
	public abstract Class<?>[] value();
	
}
