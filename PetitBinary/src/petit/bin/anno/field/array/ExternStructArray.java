package petit.bin.anno.field.array;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import petit.bin.BinaryAccessor;
import petit.bin.BinaryAccessorFactory;
import petit.bin.MemberAccessor;
import petit.bin.SerializationContext;
import petit.bin.anno.array.ArraySizeIndicator;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStructArray {
	
	public static final class _MA extends MemberAccessor {
		
		@SuppressWarnings("rawtypes")
		private final BinaryAccessor _ba;
		
		private final ArraySizeIndicator _size_ind;
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field f) throws Exception {
			super(f);
			_ba = ba_fac.getBinaryAccessor(f.getType());
			_size_ind = ArraySizeIndicator.getArraySizeIndicator(f);
		}
		
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException {
			try {
				final int size = _size_ind.getArraySize(src, inst, _field);
				Object[] ar = (Object[]) _field.get(inst);
				if (ar == null || ar.length != size)
					ar = new Object[size];
				for (int i = 0; i < ar.length; _ba.readFrom(ctx, src), i++);
				_field.set(src, ar);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException {
			try {
				final Object[] ar = (Object[]) _field.get(inst);
				if (ar == null)
					return;
				for (int i = 0; i < ar.length; _ba.writeTo(ctx, ar[i++], dst));
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
	}
	
}
