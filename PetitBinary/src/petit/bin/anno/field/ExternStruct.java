package petit.bin.anno.field;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import petit.bin.BinaryAccessor;
import petit.bin.BinaryAccessorFactory;
import petit.bin.MemberAccessor;
import petit.bin.SerializationContext;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStruct {
	
	public static final class _MA extends MemberAccessor {
		
		@SuppressWarnings("rawtypes")
		private final BinaryAccessor _ba;
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field field) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
			super(field);
			_ba = ba_fac.getBinaryAccessor(field.getType());
		}
		
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException {
			try {
				_field.set(inst, _ba.readFrom(ctx, src));
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException {
			try {
				_ba.writeTo(ctx, _field.get(inst), dst);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
	}
	
}
