package com.skymiracle.image.metadata.jpeg;

import java.io.Serializable;

import com.skymiracle.image.metadata.MetadataException;

/**
 * Created by IntelliJ IDEA. User: dnoakes Date: 09-Oct-2003 Time: 17:04:07 To
 * change this template use Options | File Templates.
 */
public class JpegComponent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3597983659734181328L;
	private final int _componentId;
	private final int _samplingFactorByte;
	private final int _quantizationTableNumber;

	public JpegComponent(int componentId, int samplingFactorByte,
			int quantizationTableNumber) {
		this._componentId = componentId;
		this._samplingFactorByte = samplingFactorByte;
		this._quantizationTableNumber = quantizationTableNumber;
	}

	public int getComponentId() {
		return this._componentId;
	}

	public String getComponentName() throws MetadataException {
		switch (this._componentId) {
		case 1:
			return "Y";
		case 2:
			return "Cb";
		case 3:
			return "Cr";
		case 4:
			return "I";
		case 5:
			return "Q";
		}

		throw new MetadataException("Unsupported component id: "
				+ this._componentId);
	}

	public int getQuantizationTableNumber() {
		return this._quantizationTableNumber;
	}

	public int getHorizontalSamplingFactor() {
		return this._samplingFactorByte & 0x0F;
	}

	public int getVerticalSamplingFactor() {
		return (this._samplingFactorByte >> 4) & 0x0F;
	}
}
