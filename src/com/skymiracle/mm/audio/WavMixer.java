package com.skymiracle.mm.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.io.StreamPipe;

public class WavMixer {

	List<byte[]> wavs = new LinkedList<byte[]>();
	byte[] headerData;
	byte[] data;
	int dataLen = 0;

	public WavMixer() {

	}

	public void addWav(byte[] wavBs) {
		this.wavs.add(wavBs);
	}

	public void doMix() {
		List<WavParser> parsers = new LinkedList<WavParser>();
		int i = 0;
		for (byte[] wav : wavs) {
			WavParser parser = new WavParser(wav);
			parser.parse();
			i++;
			if (i == 1) {
				headerData = parser.cloneHeaderData();
			}
			if (parser.getDataSize() > dataLen)
				dataLen = parser.getDataSize();
			parsers.add(parser);
		}
		data = new byte[dataLen];

		byte[] buf = new byte[2];
		buf[0] = 0;
		for (i = 0; i < dataLen; i++) {
			short v = 0;
			for (WavParser parser : parsers) {
				byte bv = parser.isEnd() ? 0 : parser.read();
				buf[1] = bv;
				v += ByteBuffer.wrap(buf).asShortBuffer().get();
			}
			byte bv = 0;
			if (v > 255)
				v = 255;
			bv = (byte)v;
			data[i] = bv;
		}
	}

	public void saveTo(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(headerData);
		fos.write(data);
		fos.close();
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		WavMixer mixer = new WavMixer();
		mixer.addWav(StreamPipe.fileToBytes(new File("/temp/s8bit2.wav")));
		mixer.addWav(StreamPipe.fileToBytes(new File("/temp/s8bit1.wav")));
		mixer.doMix();
		mixer.saveTo(new File("/temp/mixed.wav"));
	}
}
