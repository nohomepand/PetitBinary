package petit.bin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 共用体でることを示す
 * 
 * @author 俺用
 * @since 2014/03/16 PetitBinarySerialization
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UnionItem {
	
	/**
	 * 共用体の実体の型を解決する次のシグネチャを持つMethod Nameを指定する<br />
	 * {@literal Class<? extends }<b>{@literal [Type which is a super type of this field's]}</b>{@literal>}<b>{@literal [Method Name]}</b>{@literal ()}
	 * 
	 * @return 共用体の実体の型を解決するMethod Name
	 */
	public abstract String value();
	
}
