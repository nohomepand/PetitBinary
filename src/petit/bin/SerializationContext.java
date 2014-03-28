package petit.bin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import petit.bin.anno.Marker.MarkAction;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

/**
 * {@link MemberAccessor#_readFrom(SerializationContext, Object, BinaryInput)}， {@link MemberAccessor#_writeTo(SerializationContext, Object, BinaryOutput)} のコンテキスト情報
 * 
 * @author 俺用
 * @since 2014/03/19 PetitBinarySerialization
 *
 */
public class SerializationContext {
	
	/**
	 * 名前付の位置マーカ(実体)
	 */
	private final Map<String, EnumMap<MarkAction, List<Integer>>> _pos_marker;
	
	/**
	 * 初期化
	 */
	public SerializationContext() {
		_pos_marker = new HashMap<String, EnumMap<MarkAction,List<Integer>>>();
	}
	
	/**
	 * 名前付の位置マーカを得る
	 * 
	 * @return 名前付の位置マーカ
	 */
	public final Map<String, EnumMap<MarkAction, List<Integer>>> getMarker() {
		return _pos_marker;
	}
	
	/**
	 * 名前付の位置マーカを追加する
	 * 
	 * @param marker 名前
	 * @param action マークアクション
	 * @param position 位置
	 */
	public final void addMarker(final String marker, final MarkAction action, final int position) {
		EnumMap<MarkAction, List<Integer>> maybe_marker = _pos_marker.get(marker);
		if (maybe_marker == null) {
			maybe_marker = new EnumMap<MarkAction, List<Integer>>(MarkAction.class);
			_pos_marker.put(marker, maybe_marker);
		}
		
		List<Integer> maybe_mark = maybe_marker.get(action);
		if (maybe_mark == null) {
			maybe_mark = new ArrayList<Integer>();
			maybe_marker.put(action, maybe_mark);
		}
		maybe_mark.add(position);
	}
	
	/**
	 * handles before reading field<br />
	 * default operation: do nothing
	 * 
	 * @param inst an instance
	 * @param field a field of the instance
	 * @param in source
	 */
	protected void beforeRead(final Object inst, final Field field, final BinaryInput in) {
		return;
	}
	
	/**
	 * handles after reading field<br />
	 * default operation: do nothing
	 * 
	 * @param inst an instance
	 * @param field a field of the instance
	 * @param in source
	 */
	protected void afterRead(final Object inst, final Field field, final BinaryInput in) {
		return;
	}
	
	/**
	 * handles before writing field<br />
	 * default operation: do nothing
	 * 
	 * @param inst an instance
	 * @param field a field of the instance
	 * @param out destination
	 */
	protected void beforeWrite(final Object inst, final Field field, final BinaryOutput out) {
		return;
	}
	
	/**
	 * handles after writing field<br />
	 * default operation: do nothing
	 * 
	 * @param inst an instance
	 * @param field a field of the instance
	 * @param out destination
	 */
	protected void afterWrite(final Object inst, final Field field, final BinaryOutput out) {
		return;
	}
	
	
}