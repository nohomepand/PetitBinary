package petit.bin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.EnumSet;

import petit.bin.anno.StructMember;
import petit.bin.anno.StructMember.MarkAction;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

/**
 * メンバアクセサ<br />
 * {@link BinaryInput} からのフィールドへの読み込み，フィールドからの {@link BinaryOutput} への書き込みを行うクラス
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
public abstract class MemberAccessor {
	
	/**
	 * 対象のフィールド
	 */
	protected final Field _field;
	
	/**
	 * 位置マーカ名
	 */
	protected final String _pos_marker_name;
	
	/**
	 * 位置マーカを書き込むタイミング
	 */
	protected final EnumSet<MarkAction> _pos_marker_actions;
	
	/**
	 * 初期化
	 * 
	 * @param field フィールド
	 */
	public MemberAccessor(final Field field) {
		_field = field;
		_field.setAccessible(true);
		
		final StructMember struct_member = _field.getAnnotation(StructMember.class);
		final String marker = struct_member.marker();
		_pos_marker_name = marker.isEmpty() ? null : marker;	
		_pos_marker_actions = EnumSet.copyOf(Arrays.asList(struct_member.markAction()));
	}
	
	/**
	 * 読み込み元からこのフィールドへ読み込む
	 * 
	 * @param ctx コンテキスト情報
	 * @param inst フィールドの元のインスタンス
	 * @param src 読み込み元
	 */
	public final void readFrom(final SerializationContext ctx, final Object inst, final BinaryInput src) throws IllegalArgumentException, IOException, IllegalAccessException {
		if (ctx != null) {
			if (_pos_marker_name != null && _pos_marker_actions.contains(MarkAction.BEFORE_READING))
				ctx.addMarker(_pos_marker_name, MarkAction.BEFORE_READING, src.position());
		}
		
		_readFrom(ctx, inst, src);
		
		if (ctx != null) {
			if (_pos_marker_name != null && _pos_marker_actions.contains(MarkAction.AFTER_READ))
				ctx.addMarker(_pos_marker_name, MarkAction.AFTER_READ, src.position());
		}
	}
	
	/**
	 * 読み込み元からこのフィールドへ読み込む
	 * 
	 * @param ctx コンテキスト情報(null可)
	 * @param inst フィールドの元のインスタンス
	 * @param src 読み込み元
	 */
	protected abstract void _readFrom(final SerializationContext ctx, final Object inst, final BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException;
	
	/**
	 * 書き込み先へフィールドを書き込む
	 * 
	 * @param ctx コンテキスト情報(null可)
	 * @param inst フィールドの元のインスタンス
	 * @param dst 書き込み先
	 */
	public final void writeTo(final SerializationContext ctx, final Object inst, final BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException {
		if (ctx != null) {
			if (_pos_marker_name != null && _pos_marker_actions.contains(MarkAction.BEFORE_WRITING))
				ctx.addMarker(_pos_marker_name, MarkAction.BEFORE_WRITING, dst.position());
		}
		
		_writeTo(ctx, inst, dst);
		
		if (ctx != null) {
			if (_pos_marker_name != null && _pos_marker_actions.contains(MarkAction.AFTER_WRITTEN))
				ctx.addMarker(_pos_marker_name, MarkAction.AFTER_WRITTEN, dst.position());
		}
	}
	
	/**
	 * 書き込み先へフィールドを書き込む
	 * 
	 * @param ctx コンテキスト情報
	 * @param inst フィールドの元のインスタンス
	 * @param dst 書き込み先
	 */
	protected abstract void _writeTo(final SerializationContext ctx, final Object inst, final BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException;
	
}
