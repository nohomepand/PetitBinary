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
import petit.bin.anno.DefaultFieldAnnotationType;
import petit.bin.anno.SupportType;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@DefaultFieldAnnotationType(char.class)
@SupportType({
	char.class, Character.class,
	int.class, Integer.class,
	long.class, Long.class})
public @interface UInt16 {
	
	public static final class _MA extends MemberAccessor {
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field f) {
			super(f);
		}
		
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException {
			_field.setChar(inst, (char) (src.readInt16() & 0xffff));
		}
		
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException {
			dst.writeInt16((short) _field.getLong(inst));
		}
		
	}
	
}
