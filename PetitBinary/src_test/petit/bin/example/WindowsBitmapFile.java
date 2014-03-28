package petit.bin.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import petit.bin.BinaryAccessor;
import petit.bin.BinaryAccessorFactory;
import petit.bin.SerializationContext;
import petit.bin.StructByteOrder;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.array.ArraySizeConstant;
import petit.bin.anno.field.ExternStruct;
import petit.bin.anno.field.UInt16;
import petit.bin.anno.field.array.Int8Array;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.impl.InOutByteBuffer;

@Struct(byteOrder = StructByteOrder.LITTLE_ENDIAN)
public final class WindowsBitmapFile {
	
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public static final class BITMAPFILEHEADER {
		// typedef struct tagBITMAPFILEHEADER {
		// unsigned short bfType;
		// unsigned long bfSize;
		// unsigned short bfReserved1;
		// unsigned short bfReserved2;
		// unsigned long bfOffBits;
		// } BITMAPFILEHEADER
		
		@StructMember(0)
		@UInt16
		private int bfType;
		
		@StructMember(1)
		private int bfSize;
		
		@StructMember(2)
		private short bfReserved1;
		
		@StructMember(3)
		private short bfReserved2;
		
		@StructMember(4)
		private int bfOffBits;
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("bfType=").append(Integer.toHexString(bfType))
					.append(", bfSize=").append(bfSize)
					.append(", bfOffBits=").append(bfOffBits)
					.toString();
		}
		
	}
	
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public static final class BITMAPINFOHEADER {
		// typedef struct tagBITMAPINFOHEADER{
		// unsigned long biSize;
		// long biWidth;
		// long biHeight;
		// unsigned short biPlanes;
		// unsigned short biBitCount;
		// unsigned long biCompression;
		// unsigned long biSizeImage;
		// long biXPixPerMeter;
		// long biYPixPerMeter;
		// unsigned long biClrUsed;
		// unsigned long biClrImporant;
		// } BITMAPINFOHEADER;
		@StructMember(0)
		private int biSize;
		
		@StructMember(1)
		private int biWidth;
		
		@StructMember(2)
		private int biHeight;
		
		@StructMember(3)
		private short biPlanes;
		
		@StructMember(4)
		private short biBitCount;
		
		@StructMember(5)
		private int biCompression;
		
		@StructMember(6)
		private int biSizeImage;
		
		@StructMember(7)
		private int biXPixelPerMeter;
		
		@StructMember(8)
		private int biYPixelPerMeter;
		
		@StructMember(9)
		private int biColorUsed;
		
		@StructMember(10)
		private int biColorImportant;
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("biSize=").append(biSize)
					.append(", biWidth=").append(biWidth)
					.append(", biHeight=").append(biHeight)
					.append(", biPlanes=").append(biPlanes)
					.append(", biBitCount=").append(biBitCount)
					.append(", biCompression=").append(biCompression)
					.append(", biSizeImage=").append(biSizeImage)
					.append(", biXPixelPerMeter=").append(biXPixelPerMeter)
					.append(", biYPixelPerMeter=").append(biYPixelPerMeter)
					.append(", biColorUsed=").append(biColorUsed)
					.append(", biColorImportant=").append(biColorImportant)
					.toString();
		}
		
	}
	
	public static interface ColorPalette {
		public abstract int paletteSize();
	}
	
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public final class NullColorPalette implements ColorPalette {
		@Override
		public int paletteSize() {
			return 0;
		}
		
		@Override
		public String toString() {
			return "NoColorPalette";
		}
	}
	
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public static final class RGBQUAD {
		
		@StructMember(0)
//		@Int8Array
//		@ArraySizeConstant(4)
//		private byte[] color;
		private int color;
		
		@Override
		public String toString() {
			return Integer.toHexString(color);
		}
		
	}
	
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public final class IndexedColorPalette implements ColorPalette {
		
		private int _palette_size;
		
		@StructMember(0)
		@ArraySizeByField("_palette_size")
		private RGBQUAD[] _palette;
		
		public IndexedColorPalette(final int palette_size) {
			_palette_size = palette_size;
			_palette = new RGBQUAD[palette_size];
		}
		
		@Override
		public int paletteSize() {
			return _palette_size;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("Palette[");
			for (int i = 0; i < _palette_size; i++) {
				sb.append(_palette[i]);
				if (i != _palette.length - 1)
					sb.append(", ");
			}
			sb.append("]");
			return sb.toString();
		}
		
	}
	
	@StructMember(0)
	private BITMAPFILEHEADER bfHeader;
	
	@StructMember(1)
	private BITMAPINFOHEADER bfInfo;
	
	@StructMember(2)
	@ExternStruct("resolveColorPalette")
	private ColorPalette bfColorPalette;
	
	private ColorPalette resolveColorPalette() {
		if (bfInfo.biBitCount > 8)
			return new NullColorPalette();
		else
			return new IndexedColorPalette(1 << bfInfo.biBitCount);
	}
	
	@StructMember(3)
	@ArraySizeByMethod("resolveDataSize")
	private byte[] bfData;
	
	private int resolveDataSize(final BinaryInput bi) {
		int line_length = (bfInfo.biWidth * bfInfo.biBitCount) / 8;
		if (line_length % 4 != 0)
			line_length = ((line_length / 4) + 1) * 4;
		bi.position(bfHeader.bfOffBits);
		return line_length * (bfInfo.biHeight < 0 ? -bfInfo.biHeight : bfInfo.biHeight);
	}
	
	@Override
	public String toString() {
		return "Header=" + bfHeader + "\nInfo=" + bfInfo + "\nPalette=" + bfColorPalette + "\nData=" + bfData.length;
	}
	
	public static final class Main extends AbstractExample {
		public static void main(String[] args) throws SecurityException, NoSuchMethodException, IOException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
			final BinaryAccessorFactory baf = new BinaryAccessorFactory();
			final FileChannel ch = new RandomAccessFile(new File("c:/windows/system32/winpe.bmp"), "r").getChannel();
			final InOutByteBuffer buf = new InOutByteBuffer(ch.map(MapMode.READ_ONLY, 0, ch.size()));
			BinaryAccessor<WindowsBitmapFile> ba = baf.getBinaryAccessor(WindowsBitmapFile.class);
			
			final WindowsBitmapFile bmp = ba.readFrom(new SerializationContext() {
				@Override
				protected void afterRead(Object inst, Field field, BinaryInput in) {
					if (inst.getClass().equals(BITMAPINFOHEADER.class)) {
						if (field.getName().equals("biCompression")) {
							final int comp = ((BITMAPINFOHEADER) inst).biCompression;
							if (!(comp == 0 || comp == 3))
								throw new UnsupportedOperationException("Supports only BITMAPINFOHEADER.biCompression == 0 or 3");
						}
					}
					super.afterRead(inst, field, in);
				}
			}, buf);
			System.out.println(bmp);
		}
	}
	
}
