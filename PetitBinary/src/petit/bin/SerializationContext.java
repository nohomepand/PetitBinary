package petit.bin;

import java.util.HashMap;
import java.util.Map;

import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

/**
 * {@link MemberAccessor#_readFrom(SerializationContext, Object, BinaryInput)}， {@link MemberAccessor#_writeTo(SerializationContext, Object, BinaryOutput)} のコンテキスト情報
 * 
 * @author 俺用
 * @since 2014/03/19 PetitBinarySerialization
 *
 */
public final class SerializationContext {
	
	/**
	 * 名前付の位置マーカの名前の添え字を決定するもの
	 */
	private final Map<String, Integer> _pos_marker_indexer;
	
	/**
	 * 名前付の位置マーカ(実体)
	 */
	private final Map<String, Integer> _pos_marker;
	
	/**
	 * 初期化
	 */
	public SerializationContext() {
		_pos_marker_indexer = new HashMap<String, Integer>();
		_pos_marker = new HashMap<String, Integer>();
	}
	
	/**
	 * 名前付の位置マーカを得る
	 * 
	 * @return 名前付の位置マーカ
	 */
	public final Map<String, Integer> getMarker() {
		return _pos_marker;
	}
	
	/**
	 * 読み込み位置として，名前付の位置マーカを追加する
	 * 
	 * @param marker 名前
	 * @param position 位置
	 */
	public final void addReadMarker(final String marker, final int position) {
		_pos_marker.put(indexedNextMarker(marker + ".r"), position);
	}
	
	/**
	 * 書き込み位置として，名前付の位置マーカを追加する
	 * 
	 * @param marker 名前
	 * @param position 位置
	 */
	public final void addWriteMarker(final String marker, final int position) {
		_pos_marker.put(indexedNextMarker(marker + ".w"), position);
	}
	
	private final String indexedNextMarker(final String marker) {
		Integer maybe_index = _pos_marker_indexer.get(marker);
		if (maybe_index == null)
			maybe_index = Integer.valueOf(0);
		
		_pos_marker_indexer.put(marker, maybe_index + 1);
		return marker + "." + maybe_index;
	}
	
}