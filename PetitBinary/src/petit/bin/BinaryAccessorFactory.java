package petit.bin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import petit.bin.anno.DefaultFieldAnnotationType;
import petit.bin.anno.SupportType;
import petit.bin.anno.field.ExternStruct;
import petit.bin.anno.field.Float32;
import petit.bin.anno.field.Float64;
import petit.bin.anno.field.Int16;
import petit.bin.anno.field.Int32;
import petit.bin.anno.field.Int64;
import petit.bin.anno.field.Int8;
import petit.bin.anno.field.Int8Boolean;
import petit.bin.anno.field.UInt16;
import petit.bin.anno.field.UInt32;
import petit.bin.anno.field.UInt8;
import petit.bin.anno.field.array.CharArray;
import petit.bin.anno.field.array.ExternStructArray;
import petit.bin.anno.field.array.Float32Array;
import petit.bin.anno.field.array.Float64Array;
import petit.bin.anno.field.array.Int16Array;
import petit.bin.anno.field.array.Int32Array;
import petit.bin.anno.field.array.Int64Array;
import petit.bin.anno.field.array.Int8Array;

/**
 * サポートするメンバアクセスアノテーションを使って {@link BinaryAccessor} を生成するもの
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
public final class BinaryAccessorFactory {
	
	/**
	 * {@link MemberAccessor} のサブクラスを生成するもの<br />
	 * 
	 * @author 俺用
	 * @since 2014/03/14 PetitBinarySerialization
	 *
	 */
	private final class MemberAccessorFactory {
		
		/**
		 * コンストラクタ
		 */
		private final Constructor<? extends MemberAccessor> _ctor;
		
		private final Set<Class<?>> _support_type;
		
		@SuppressWarnings("unchecked")
		public MemberAccessorFactory(final Class<? extends Annotation> anno_clazz) throws SecurityException, NoSuchMethodException {
			Class<? extends MemberAccessor> clazz = null;
			for (final Class<?> c : anno_clazz.getDeclaredClasses()) {
				if (MemberAccessor.class.isAssignableFrom(c)) {
					clazz = (Class<? extends MemberAccessor>) c;
					break;
				}
			}
			
			if (clazz == null)
				throw new UnsupportedOperationException(anno_clazz + " does not contain a sub-class of " + MemberAccessor.class + ", or not have a suitable constructor(" + BinaryAccessorFactory.class + ", " + Field.class + ")");
			
			_ctor = clazz.getConstructor(BinaryAccessorFactory.class, Field.class);
			_ctor.setAccessible(true);
			
			if (!anno_clazz.isAnnotationPresent(SupportType.class))
				_support_type = null;
			else
				_support_type = new HashSet<Class<?>>(Arrays.asList(anno_clazz.getAnnotation(SupportType.class).value()));
		}
		
		/**
		 * フィールドに対する {@link MemberAccessor} の(サブクラスの)インスタンスを得る
		 * 
		 * @param field フィールド
		 * @return 新たに生成されたフィールドに対する {@link MemberAccessor} の(サブクラスの)インスタンス
		 * @throws IllegalArgumentException
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 * @throws InvocationTargetException
		 */
		public final MemberAccessor createInstance(final Field field) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
			return _ctor.newInstance(BinaryAccessorFactory.this, field);
		}
		
		/**
		 * 対象の型がこのメンバアクセスファクトリの対応する型か検証する
		 * 
		 * @param type 対象の型
		 * @return 対象の型がこのメンバアクセスファクトリの対応する型の場合は true
		 */
		public final boolean isSupportType(final Class<?> type) {
			if (_support_type == null)
				return true;
			else
				return _support_type.contains(type);
		}
		
		/**
		 * このメンバアクセスファクトリの対応する型を得る
		 * 
		 * @return このメンバアクセスファクトリの対応する型
		 */
		public final Set<Class<?>> getSupportType() {
			return _support_type;
		}
		
	}
	
	/**
	 * メンバアクセスアノテーションと，フィールドの実体をもってメンバアクセサを生成するもののマッピング
	 */
	private final Map<Class<? extends Annotation>, MemberAccessorFactory> _supported_annotation_memberaccessorfactory_map;
	
	/**
	 * 一般の構造体のメンバアクセサを生成するもの
	 */
	private final MemberAccessorFactory _extern_struct_memberaccessorfactory;
	
	/**
	 * 一般の構造体の配列のメンバアクセサを生成するもの
	 */
	private final MemberAccessorFactory _extern_struct_array_memberaccessorfactory;
	
	/**
	 * メンバアクセスアノテーションが指定されていないフィールドに対して，
	 * フィールドの型からデフォルトのメンバアクセスアノテーションを得るためのマッピング
	 */
	private final Map<Class<?>, Class<? extends Annotation>> _default_field_annotation_map;
	
	/**
	 * 型とそれに対応した {@link BinaryAccessor} のマッピング
	 */
	private final Map<Class<?>, BinaryAccessor<?>> _class_binary_accessor_map;
	
	/**
	 * 全てのフィールドアノテーションを使う様に初期化
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	@SuppressWarnings("unchecked")
	public BinaryAccessorFactory() throws SecurityException, NoSuchMethodException {
		this(
			Int8.class,
			Int16.class,
			Int32.class,
			Int64.class,
			UInt8.class,
			UInt16.class,
			UInt32.class,
			Float32.class,
			Float64.class,
			Int8Boolean.class,
//			ExternStruct.class,
			
			Int8Array.class,
			Int16Array.class,
			Int32Array.class,
			Int64Array.class,
			CharArray.class,
			Float32Array.class,
			Float64Array.class
//			ExternStructArray.class
		);
	}
	
	/**
	 * 初期化
	 * 
	 * @param supported_field_annotations サポートするフィールドアノテーション
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public BinaryAccessorFactory(final Class<? extends Annotation> ... supported_field_annotations) throws SecurityException, NoSuchMethodException {
		if (supported_field_annotations == null || supported_field_annotations.length == 0)
			throw new NullPointerException("supported_field_annotations must not be null or zero size");
		
		_supported_annotation_memberaccessorfactory_map = new HashMap<Class<? extends Annotation>, BinaryAccessorFactory.MemberAccessorFactory>();
		_default_field_annotation_map = new HashMap<Class<?>, Class<? extends Annotation>>();
		_class_binary_accessor_map = new HashMap<Class<?>, BinaryAccessor<?>>();
		
		_extern_struct_memberaccessorfactory = new MemberAccessorFactory(ExternStruct.class);
		_extern_struct_array_memberaccessorfactory = new MemberAccessorFactory(ExternStructArray.class);
		
		// fill _supported_annotation_memberaccessorfactory_map & _default_field_annotation_map
		for (final Class<? extends Annotation> anno : supported_field_annotations)
			installMemberAccessAnotation(anno);
	}
	
	/**
	 * メンバアクセスアノテーションを追加する
	 * 
	 * @param maa メンバアクセスアノテーション
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public final void installMemberAccessAnotation(final Class<? extends Annotation> maa) throws SecurityException, NoSuchMethodException {
		_supported_annotation_memberaccessorfactory_map.put(maa, new MemberAccessorFactory(maa));
		
		if (!maa.isAnnotationPresent(DefaultFieldAnnotationType.class))
			return;
		final Class<?>[] def_type_clazz = maa.getAnnotation(DefaultFieldAnnotationType.class).value();
		if (def_type_clazz == null)
			return;
		
		for (final Class<?> clazz : def_type_clazz) {
			if (_default_field_annotation_map.containsKey(clazz))
				throw new IllegalArgumentException("Default field annotation for " + clazz + " is already installed");
			_default_field_annotation_map.put(clazz, maa);
		}
	}
	
	/**
	 * フィールドに対する {@link MemberAccessor} の(サブクラスの)実体を得る
	 * 
	 * @param field フィールド
	 * @return 生成された {@link MemberAccessor}
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("null")
	final MemberAccessor createMemberAccessor(final Field field) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		// 1. フィールドアノテーションを得る
		Class<? extends Annotation> field_anno_clazz = null;
		MemberAccessorFactory maf = null;
		for (final Annotation anno : field.getAnnotations()) {
			final Class<? extends Annotation> anno_clazz = anno.annotationType();
			if (_supported_annotation_memberaccessorfactory_map.containsKey(anno_clazz)) {
				field_anno_clazz = anno_clazz;
				maf = _supported_annotation_memberaccessorfactory_map.get(anno_clazz);
				break;
			}
		}
		
		if (field_anno_clazz == null) {
			final Class<?> field_type = field.getType();
			if (_default_field_annotation_map.containsKey(field_type)) {
				field_anno_clazz = _default_field_annotation_map.get(field_type);
				maf = _supported_annotation_memberaccessorfactory_map.get(field_anno_clazz);
			} else if (field_type.isArray()) {
				field_anno_clazz = ExternStructArray.class;
				maf = _extern_struct_array_memberaccessorfactory;
			} else {
				field_anno_clazz = ExternStruct.class;
				maf = _extern_struct_memberaccessorfactory;
			}
			
		}
		
		if (!maf.isSupportType(field.getType()))
			throw new IllegalArgumentException("Field type of " + field.getType() + " is not applicable for the " + field_anno_clazz + " (" + maf.getSupportType() + " is not contains " + field.getType() + ")");
		
		return maf.createInstance(field);
	}
	
	/**
	 * 型として対象のクラスを持つフィールドに対するデフォルトのフィールドアノテーションを得る
	 * 
	 * @param clazz 対象のクラス
	 * @return デフォルトのフィールドアノテーション
	 */
	public final Class<? extends Annotation> getDefaultFieldAnnotation(final Class<?> clazz) {
		if (clazz == null)
			throw new NullPointerException("clazz must not be null");
		
		return _default_field_annotation_map.get(clazz);
	}
	
	/**
	 * クラスに対する構造体のアクセサである {@link BinaryAccessor} を得る
	 * 
	 * @param clazz クラス
	 * @return クラスに対する構造体のアクセサ
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public final <T> BinaryAccessor<T> getBinaryAccessor(final Class<? extends T> clazz) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if (clazz == null)
			throw new NullPointerException("clazz must not be null");
		
		BinaryAccessor<T> maybe_ba = (BinaryAccessor<T>) _class_binary_accessor_map.get(clazz);
		if (maybe_ba == null) {
			maybe_ba = new BinaryAccessor<T>(this, clazz);
			_class_binary_accessor_map.put(clazz, maybe_ba);
		}
		return maybe_ba;
	}
	
}
