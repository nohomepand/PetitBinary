package petit.bin.anno.field.array;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import petit.bin.BinaryAccessor;
import petit.bin.BinaryAccessorFactory;
import petit.bin.MemberAccessor;
import petit.bin.SerializationContext;
import petit.bin.anno.ArraySizeIndicator;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStructArray {
	
	public static final class _MA extends MemberAccessor {
		
		private final Class<?> _component_type;
		
		@SuppressWarnings("rawtypes")
		private final BinaryAccessor _component_type_ba;
		
		private final ArraySizeIndicator _size_ind;
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field f) throws Exception {
			super(f);
			_component_type = f.getType().getComponentType();
			_component_type_ba = ba_fac.getBinaryAccessor(_component_type);
			_size_ind = ArraySizeIndicator.getArraySizeIndicator(f);
		}
		
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException {
			try {
				final int size = _size_ind.getArraySize(src, inst, _field);
				Object ar = _field.get(inst);
				if (ar == null || Array.getLength(ar) != size)
					ar = Array.newInstance(_component_type, size);
				for (int i = 0; i < size; Array.set(ar, i++, _component_type_ba.readFrom(ctx, src)));
				_field.set(inst, ar);
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
				for (int i = 0; i < ar.length; _component_type_ba.writeTo(ctx, ar[i++], dst));
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
	}
	
}
