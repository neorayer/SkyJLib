package com.skymiracle.mm.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.skymiracle.io.StreamPipe;

public class WavParser {
	private WaveChunk waveChunk = new WaveChunk();
	private FormatChunk formatChunk = new FormatChunk();
	private FactChunk factChunk = new FactChunk();
	private DataChunk dataChunk = new DataChunk();
	int offset;
	int dataOffset = 0;
	int headOffset = 0;
	private byte[] data;
	
	public class ByteFlowReader {
		byte[] bs;
		int offset = 0;

		public ByteFlowReader(byte[] bs) {
			this.bs = bs;
		}

		public int readInt() {
			int v = ByteBuffer.wrap(bs, offset, 4).asIntBuffer().get();
			offset += 4;
			return Integer.reverseBytes(v);
		}

		public short readShort() {
			short v = ByteBuffer.wrap(bs, offset, 2).asShortBuffer().get();
			offset += 2;
			return Short.reverseBytes(v);
		}

		public String readString(int len) {
			String s = new String(bs, offset, len);
			offset += len;
			return s;
		}

		public byte[] readBytes(int len) {
			byte[] destBs = new byte[len];
			System.arraycopy(bs, offset, destBs, 0, len);
			offset += len;
			return destBs;
		}
	}

	public class WaveChunk {
		String id;
		int size;
		String type;

		@Override
		public String toString() {
			String s = "";
			s += "id=" + id + "\r\n";
			s += "size=" + size + "\r\n";
			s += "type=" + type + "\r\n";
			return s;
		}
	}

	public class FormatChunk {
		String id;
		int size;
		int formatTag;
		int channels;
		int samplesPerSec;
		int avgBytesPerSec;
		int blockAlign;
		int bitsPerSample;
		int other;

		@Override
		public String toString() {
			String s = "";
			s += "id=" + id + "\r\n";
			s += "size=" + size + "\r\n";
			s += "formatTag=" + formatTag + "\r\n";
			s += "channels=" + channels + "\r\n";
			s += "samplesPerSec=" + samplesPerSec + "\r\n";
			s += "avgBytesPerSec=" + avgBytesPerSec + "\r\n";
			s += "blockAlign=" + blockAlign + "\r\n";
			s += "bitsPerSample=" + bitsPerSample + "\r\n";
			s += "other=" + other + "\r\n";
			return s;
		}
	}

	public class FactChunk {
		String id;
		int size;
		byte[] data;

		@Override
		public String toString() {
			String s = "";
			s += "id=" + id + "\r\n";
			s += "size=" + size + "\r\n";
			s += "len(data)=" + data.length + "\r\n";
			return s;
		}
	}

	public class DataChunk {
		String id;
		int size;

		@Override
		public String toString() {
			String s = "";
			s += "id=" + id + "\r\n";
			s += "size=" + size + "\r\n";
			return s;
		}
	}

	public WavParser(File file) throws FileNotFoundException, IOException {
		this.data = StreamPipe.fileToBytes(file);
	}

	public WavParser(byte[] data) {
		this.data = data;
	}

	public void parse() {
		ByteFlowReader bfr = new ByteFlowReader(data);
		int off = 0;

		// WaveChunk
		waveChunk.id = bfr.readString(4);
		waveChunk.size = bfr.readInt();
		waveChunk.type = bfr.readString(4);
		System.out.println(waveChunk);

		// FormatChunk
		formatChunk.id = bfr.readString(4);
		formatChunk.size = bfr.readInt();
		formatChunk.formatTag = bfr.readShort();
		formatChunk.channels = bfr.readShort();
		formatChunk.samplesPerSec = bfr.readInt();
		formatChunk.avgBytesPerSec = bfr.readInt();
		formatChunk.blockAlign = bfr.readShort();
		formatChunk.bitsPerSample = bfr.readShort();
		if (formatChunk.size == 18)
			formatChunk.other = bfr.readShort();
		System.out.println(formatChunk);

		// FactChunk
		if (data[bfr.offset] == 'f') {
			factChunk.id = bfr.readString(4);
			factChunk.size = bfr.readInt();
			factChunk.data = bfr.readBytes(4);
			System.out.println(factChunk);
		}
		
		//DataChunk
		dataChunk.id = bfr.readString(4);
		dataChunk.size = bfr.readInt();
//		dataChunk.data = bfr.readBytes((int)dataChunk.size);
		System.out.println(dataChunk);

		this.headOffset = bfr.offset;
		this.offset = bfr.offset;
	}
	
	public byte read() {
		dataOffset ++;
		return data[offset ++];
	}
	
	public boolean isEnd() {
		return dataOffset == dataChunk.size;
	}

	public int getDataSize() {
		return dataChunk.size;
	}
	
	public byte[] cloneHeaderData() {
		byte[] bs = new byte[headOffset];
		System.arraycopy(data, 0, bs, 0, headOffset);
		return bs;
	}
	
	public static void main(String[] args) throws Exception,
			IOException {
		WavParser wp = new WavParser(new File("/temp/s8bit1.wav"));
		wp.parse();
	}

}
