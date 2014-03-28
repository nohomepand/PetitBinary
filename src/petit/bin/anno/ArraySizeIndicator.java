package petit.bin.anno;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.array.ArraySizeConstant;
import petit.bin.sinks.BinaryInput;
import petit.bin.util.ReflectionUtil;
import petit.bin.util.ReflectionUtil.VisibilityConstraint;

/**
 * {@link ArraySizeConstant} または
 * {@link ArraySizeByField} または
 * {@link ArraySizeByMethod}
 * によって長さが指定されたメンバの実際の長さを得るもの<br />
 * このインスタンスはバイナリから読み込むときのみに使われる
 * 
 * @author 俺用
 * @since 2014/03/19 PetitBinarySerialization
 *
 */
public abstract class ArraySizeIndicator {
	
	/**
	 * 配列型のフィールドに対してメンバの実際の長さを得るものを生成する
	 * 
	 * @param field 対象のフィールド(配列型でなければならない)
	 * @return 配列型のフィールドに対してメンバの実際の長さを得るもの
	 */
	public static final ArraySizeIndicator getArraySizeIndicator(final Field field) throws Exception {
		if (field.isAnnotationPresent(ArraySizeConstant.class))
			return new ArraySizeConstantIndicator(field.getAnnotation(ArraySizeConstant.class));
		else if (field.isAnnotationPresent(ArraySizeByField.class))
			return new ArraySizeByFieldIndicator(field.getDeclaringClass(), field.getAnnotation(ArraySizeByField.class));
		else if (field.isAnnotationPresent(ArraySizeByMethod.class))
			return getByMethodIndicatorResolver(field.getDeclaringClass(), field.getAnnotation(ArraySizeByMethod.class));
		else
			throw new UnsupportedOperationException("No specified array size indicator");
	}
	
	private static final ArraySizeIndicator getByMethodIndicatorResolver(final Class<?> declaring_clazz, final ArraySizeByMethod array_size_by_method_anno) throws NoSuchMethodException {
		if (array_size_by_method_anno == null)
			throw new NullPointerException("Array size method name is not assigned");
		final String method_name = array_size_by_method_anno.value();
		
		// find method: signature [byte, short, int, long, or ? extends Number](BinaryInput)
		for (final Method method 
				: ReflectionUtil.getVisibleMethods(declaring_clazz, VisibilityConstraint.INHERITED_CLASS_VIEWPOINT, method_name, null, BinaryInput.class)) {
			if (!isIndicatorType(method.getReturnType()))
				continue;
			method.setAccessible(true);
			return new ArraySizeByMethodIndicator_BinaryInput(method);
		}
		
		// find method: signature [byte, short, int, long, or ? extends Number](BinaryInput, Object, Field)
		for (final Method method
				: ReflectionUtil.getVisibleMethods(declaring_clazz, VisibilityConstraint.INHERITED_CLASS_VIEWPOINT, method_name, null, BinaryInput.class, Object.class, Field.class)) {
			if (!isIndicatorType(method.getReturnType()))
				continue;
			method.setAccessible(true);
			return new ArraySizeByMethodIndicator_BinaryInput_Object_Field(method);
		}
		
		throw new NoSuchMethodException("Cannot find a size indicator method: [byte,short,int,long, or ? extends Number] " +
				method_name + "(" + BinaryInput.class.getCanonicalName() + ") or (" + BinaryInput.class.getCanonicalName() + ", " + Object.class.getCanonicalName() + ", " + Field.class.getCanonicalName() + ")");
	}
	
	private static final boolean isIndicatorType(final Class<?> c) {
		if (c.isPrimitive()) {
			return
					int.class.equals(c) ||
					long.class.equals(c) ||
					short.class.equals(c) ||
					byte.class.equals(c);
		} else
			return Number.class.isAssignableFrom(c);
	}
	
	/**
	 * 対象の入力において対象のインスタンスの対象のフィールドのサイズを得る
	 * 
	 * @param bi 対象の入力
	 * @param src_inst 対象のインスタンス
	 * @param src_field 対象のフィールド(配列)
	 * @return 対象のフィールドのサイズ
	 * @throws Exception
	 */
	public abstract int getArraySize(final BinaryInput bi, final Object src_inst, final Field src_field) throws Exception;
	
	/**
	 * 固定サイズの配列長を表すもの
	 * 
	 * @author 俺用
	 * @since 2014/03/20 PetitBinarySerialization
	 *
	 */
	public static final class ArraySizeConstantIndicator extends ArraySizeIndicator {
		
		private final int _size;
		
		/**
		 * 初期化
		 * 
		 * @param annotation {@link ArraySizeConstant} のインスタンス
		 */
		private ArraySizeConstantIndicator(ArraySizeConstant annotation) {
			_size = annotation.value();
		}
		
		@Override
		public int getArraySize(BinaryInput bi, Object src_inst, Field src_field) throws Exception {
			return _size;
		}
		
	}
	
	/**
	 * フィールドによってサイズが指定されるもの
	 * 
	 * @author 俺用
	 * @since 2014/03/20 PetitBinarySerialization
	 *
	 */
	public static final class ArraySizeByFieldIndicator extends ArraySizeIndicator {
		
		private final Field _size_field;
		
		/**
		 * 初期化
		 * 
		 * @param declaringClass フィールドが定義されているクラス
		 * @param annotation {@link ArraySizeByField} のインスタンス
		 * @throws NoSuchFieldException
		 */
		private ArraySizeByFieldIndicator(Class<?> declaringClass, ArraySizeByField annotation) throws NoSuchFieldException {
			final String field_name = annotation.value();
			Field tmp_field = null;
			for (final Field field : ReflectionUtil.getVisibleFields(declaringClass, VisibilityConstraint.INHERITED_CLASS_VIEWPOINT, field_name, null)) {
				if (!isIndicatorType(field.getType()))
					continue;
				tmp_field = field;
				break;
			}
			
			if (tmp_field == null)
				throw new NoSuchFieldException(field_name);
			_size_field = tmp_field;
			_size_field.setAccessible(true);
		}
		
		@Override
		public int getArraySize(BinaryInput bi, Object src_inst, Field src_field) throws Exception {
			return (int) (_size_field.getLong(src_inst) & 0xffffffff);
		}
		
	}
	
	/**
	 * signature: [byte, short, int, long, or ? extends Number] [method name](BinaryInput) なメソッドによってサイズが指定されるもの
	 * 
	 * @author 俺用
	 * @since 2014/03/20 PetitBinarySerialization
	 *
	 */
	public static final class ArraySizeByMethodIndicator_BinaryInput extends ArraySizeIndicator {
		
		private final Method _size_method;
		
		/**
		 * 初期化
		 * 
		 * @param declaringClass メソッドが定義されているクラス
		 * @param annotation {@link ArraySizeByMethod} のインスタンス
		 * @throws NoSuchMethodException 
		 */
		private ArraySizeByMethodIndicator_BinaryInput(final Method method) {
			_size_method = method;
		}
		
		@Override
		public int getArraySize(BinaryInput bi, Object src_inst, Field src_field) throws Exception {
			return ((Number) _size_method.invoke(src_inst, bi)).intValue();
		}
		
	}
	
	/**
	 * signature: [byte, short, int, long, or ? extends Number] [method name](BinaryInput, Object, Field) なメソッドによってサイズが指定されるもの
	 * 
	 * @author 俺用
	 * @since 2014/03/20 PetitBinarySerialization
	 *
	 */
	public static final class ArraySizeByMethodIndicator_BinaryInput_Object_Field extends ArraySizeIndicator {
		
		private final Method _size_method;
		
		/**
		 * 初期化
		 * 
		 * @param declaringClass メソッドが定義されているクラス
		 * @param annotation {@link ArraySizeByMethod} のインスタンス
		 * @throws NoSuchMethodException 
		 */
		private ArraySizeByMethodIndicator_BinaryInput_Object_Field(final Method method) {
			_size_method = method;
		}
		
		@Override
		public int getArraySize(BinaryInput bi, Object src_inst, Field src_field) throws Exception {
			return ((Number) _size_method.invoke(src_inst, bi, src_inst, src_field)).intValue();
		}
		
	}
	
}
