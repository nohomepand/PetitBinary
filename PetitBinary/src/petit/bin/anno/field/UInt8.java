package petit.bin.anno.field;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import petit.bin.BinaryAccessorFactory;
import petit.bin.MemberAccessor;
import petit.bin.SerializationContext;
import petit.bin.anno.SupportType;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

/**
 * 符号なし 32ビット整数値を表す
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SupportType({
	short.class, Short.class,
	int.class, Integer.class,
	long.class, Long.class})
public @interface UInt8 {
	
	public static final class _MA extends MemberAccessor {
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field f) {
			super(f);
		}
		
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException {
			_field.setShort(inst, (short) (src.readInt8() & 0xff));
		}
		
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException {
			dst.writeInt8((byte) _field.getShort(inst)); // TODO 大丈夫？
		}
		
	}
	
}
