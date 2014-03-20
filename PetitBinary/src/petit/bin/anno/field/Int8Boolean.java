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
@DefaultFieldAnnotationType(boolean.class)
@SupportType({boolean.class, Boolean.class})
public @interface Int8Boolean {
	
	public static final class _MA extends MemberAccessor {
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field f) {
			super(f);
		}
		
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException {
			_field.setBoolean(inst, src.readInt8() != 0);
		}
		
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException {
			if (_field.getBoolean(inst))
				dst.writeInt8((byte) 1);
			else
				dst.writeInt8((byte) 0);
		}
		
	}
	
}
