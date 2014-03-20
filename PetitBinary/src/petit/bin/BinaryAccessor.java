package petit.bin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;
import petit.bin.util.InstanceConstructor;
import petit.bin.util.ReflectionUtil;
import petit.bin.util.ReflectionUtil.VisibilityConstraint;

/**
 * 構造体の読み書きを行うためのアクセサ
 * 
 * @author 俺用
 * @since 2014/03/19 PetitBinarySerialization
 * 
 * @param <T> 対応する型
 */
public final class BinaryAccessor<T> {
	
	private final BinaryAccessorFactory _factory;
	
	private final Class<? extends T> _clazz;
	
	private final InstanceConstructor _ctor;
	
	private final Struct _clazz_struct_anno;
	
	private final List<MemberAccessor> _struct_fields;
	
	/**
	 * 初期化
	 * 
	 * @param factory 親のファクトリ
	 * @param clazz 対応するクラス
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 */
	BinaryAccessor(final BinaryAccessorFactory factory, final Class<? extends T> clazz) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if (!clazz.isAnnotationPresent(Struct.class))
			throw new IllegalArgumentException(clazz + " is not present " + Struct.class.getCanonicalName() + " annotation");
		
		_factory = factory;
		_clazz = clazz;
		_ctor = ReflectionUtil.getNullaryConstructor(clazz);
		
		_clazz_struct_anno = _clazz.getAnnotation(Struct.class);
		_struct_fields = new ArrayList<MemberAccessor>();
		for (final Field field : ReflectionUtil.getVisibleFields(clazz, VisibilityConstraint.ANY, null, null)) {
			if (!field.isAnnotationPresent(StructMember.class))
				continue;
			
			final StructMember item_anno = field.getAnnotation(StructMember.class);
			final int idx = item_anno.value();
			ensureFieldAnnotationlistSize(idx);
			if (_struct_fields.get(idx) != null)
				throw new IllegalArgumentException("Struct member (index=" + idx + ") is already defined as " + _struct_fields.get(idx));
			_struct_fields.set(idx, _factory.createMemberAccessor(field));
		}
		
		for (int i = 0, size = _struct_fields.size(); i < size; i++)
			if (_struct_fields.get(i) == null)
				throw new IllegalArgumentException("Struct member (index=" + i + ") is not defined");
	}
	
	private final void ensureFieldAnnotationlistSize(final int size) {
		for (int i = 0, p = size - _struct_fields.size() + 1; i < p; i++)
			_struct_fields.add(null);
	}
	
	/**
	 * 対応するクラスを得る
	 * 
	 * @return 対応するクラス
	 */
	public final Class<?> getTargetClass() {
		return _clazz;
	}
	
	/**
	 * {@link #getTargetClass()} で得られるクラスの新たなインスタンスを得る
	 * 
	 * @return {@link #getTargetClass()} で得られるクラスの新たなインスタンス
	 * @throws InstantiationException
	 */
	public final Object createTargetObject() throws InstantiationException {
		if (_ctor == null)
			throw new InstantiationException("Cannot create instance of " + _clazz);
		
		try {
			return _ctor.newInstance();
		} catch (Exception e) {
			throw new InstantiationException(e.getLocalizedMessage());
		}
	}
	
	/**
	 * 読み込み元から構造体のフィールドを読み込んだ，{@link #getTargetClass()} で得られるクラスの新たなインスタンスを得る
	 * 
	 * @param ctx コンテキスト情報
	 * @param src 読み込み元
	 * @return {@link #getTargetClass()} で得られるクラスの新たなインスタンス
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public final T readFrom(final SerializationContext ctx, final BinaryInput src) throws InstantiationException, IllegalArgumentException, IOException, IllegalAccessException {
		final StructByteOrder bo = _clazz_struct_anno.byteOrder();
		if (bo != StructByteOrder.NEUTRAL)
			src.pushByteOrder(_clazz_struct_anno.byteOrder());
		
		final Object ao = createTargetObject();
		for (final MemberAccessor ma : _struct_fields)
			ma.readFrom(ctx, ao, src);
		
		if (bo != StructByteOrder.NEUTRAL)
			src.popByteOrder();
		
		return (T) ao;
	}
	
	/**
	 * 書き込み先へ構造体フィールドの設定に基づいて，インスタンスを書き込む
	 * 
	 * @param ctx コンテキスト情報
	 * @param ao インスタンス
	 * @param dst 書き込み先
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws IllegalAccessException
	 */
	public final void writeTo(final SerializationContext ctx, final T ao, final BinaryOutput dst) throws IllegalArgumentException, IOException, IllegalAccessException {
//		if (ao == null)
//			return;
		if (ao == null)
			throw new NullPointerException(_clazz.getCanonicalName() + " is null");
		
		final StructByteOrder bo = _clazz_struct_anno.byteOrder();
		if (bo != StructByteOrder.NEUTRAL)
			dst.pushByteOrder(bo);
		
		for (final MemberAccessor ma : _struct_fields)
			ma.writeTo(ctx, ao, dst);
		
		if (bo != StructByteOrder.NEUTRAL)
			dst.popByteOrder();
	}
	
}
