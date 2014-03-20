package petit.bin.anno.field.array;

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
import petit.bin.anno.array.ArraySizeIndicator;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@DefaultFieldAnnotationType(long[].class)
@SupportType(long[].class)
public @interface Int64Array {
	
	public static final class _MA extends MemberAccessor {
		
		private final ArraySizeIndicator _size_ind;
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field f) throws Exception {
			super(f);
			_size_ind = ArraySizeIndicator.getArraySizeIndicator(f);
		}
		
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException {
			try {
				final int size = _size_ind.getArraySize(src, inst, _field);
				long[] ar = (long[]) _field.get(inst);
				if (ar == null || ar.length != size)
					ar = new long[size];
				for (int i = 0; i < ar.length; ar[i++] = src.readInt64());
				_field.set(inst, ar);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException {
			final long[] ar = (long[]) _field.get(inst);
			if (ar == null)
				return;
			
			for (int i = 0; i < ar.length; dst.writeInt64(ar[i++]));
		}
		
	}
	
}
