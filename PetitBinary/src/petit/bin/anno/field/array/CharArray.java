package petit.bin.anno.field.array;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import petit.bin.BinaryAccessorFactory;
import petit.bin.MemberAccessor;
import petit.bin.SerializationContext;
import petit.bin.anno.DefaultFieldAnnotationType;
import petit.bin.anno.SupportType;
import petit.bin.anno.array.ArraySizeIndicator;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

/**
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@DefaultFieldAnnotationType({String.class, char[].class})
@SupportType({
	String.class,
	byte[].class,
	char[].class})
public @interface CharArray {
	
	/**
	 * 文字列のエンコーディング<br />
	 * null の場合は実行環境のデフォルトのエンコーディング
	 * 
	 * @return 文字列のエンコーディング
	 * @see Charset#defaultCharset()
	 */
	public abstract String value();
	
	public static final class _MA extends MemberAccessor {
		
		private final Charset _str_cs;
		
		private final ArraySizeIndicator _size_ind;
		
		private final Class<?> _field_type;
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field f) throws Exception {
			super(f);
			final CharArray ca_anno = f.getAnnotation(CharArray.class);
			if (ca_anno == null || ca_anno.value() == null)
				_str_cs = Charset.defaultCharset();
			else
				_str_cs = Charset.forName(ca_anno.value());
			
			_size_ind = ArraySizeIndicator.getArraySizeIndicator(f);
			_field_type = f.getType();
		}
		
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException {
			try {
				final int size = _size_ind.getArraySize(src, inst, _field);
				final byte[] ar = new byte[size];
				for (int i = 0; i < ar.length; ar[i++] = src.readInt8());
				
				if (String.class.equals(_field_type))
					_field.set(inst, new String(ar, _str_cs));
				else if (char[].class.equals(_field_type))
					_field.set(inst, new String(ar, _str_cs).toCharArray());
				else if (byte[].class.equals(_field_type))
					_field.set(inst, ar);
				else
					throw new IllegalStateException(_field_type + " is not applicable(MAY BE BUG!!)");
			} catch (Exception e) {
				throw new IOException(e);
			}
			_field.get(inst);
		}
		
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException {
			try {
				final byte[] ar;
				if (String.class.equals(_field_type))
					ar = ((String) _field.get(inst)).getBytes(_str_cs);
				else if (char[].class.equals(_field_type))
					ar = new String((char[]) _field.get(inst)).getBytes(_str_cs);
				else if (byte[].class.equals(_field_type))
					ar = (byte[]) _field.get(inst);
				else
					throw new IllegalStateException(_field_type + " is not applicable(MAY BE BUG!!)");
				
				for (int i = 0; i < ar.length; dst.writeInt8(ar[i++]));
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
	}
	
}
