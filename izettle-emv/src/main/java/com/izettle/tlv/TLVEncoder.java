package com.izettle.tlv;

import static com.izettle.java.ValueChecks.*;

/**
 * ISO 7816 / ASN.1 compliantish encoder.
 * @author fidde
 */
public class TLVEncoder {

	public TLVEncoder() { }

	/**
	 * Validates tag and creates a properly encoded length.
	 * @param tag Tag bytes
	 * @param value Data bytes, nullable.
	 * @throws TLVException On malformed inputs
	 */
	public TLV encode(byte[] tag, byte[] value) throws TLVException {
		TLVUtils.validateTag(tag);
		byte[] length = encodeLength(null != value ? value.length : 0);
		return new TLV(tag, length, value);
	}

	/**
	 * Encodes an integer length into ASN.1 format
	 * @param l Length to encode
	 * @throws TLVException If input is less then zero
	 */
	static byte[] encodeLength(int l) throws TLVException {

		if(l < 0) {
			throw new TLVException("Length less than zero");
		}

		byte[] out;

		if (l > 0x00FFFFFF) {
			out = new byte[5];
			out[0] = (byte)0x84;
			out[1] = (byte)((l >> 24) & 0xff);
			out[2] = (byte)((l >> 16) & 0xff);
			out[3] = (byte)((l >> 8) & 0xff);
			out[4] = (byte)((l >> 0) & 0xff);
		} else if (l > 0x0000FFFF) {
			out = new byte[4];
			out[0] = (byte)0x83;
			out[1] = (byte)((l >> 16) & 0xff);
			out[2] = (byte)((l >> 8) & 0xff);
			out[3] = (byte)((l >> 0) & 0xff);
		} else if (l > 0x000000FF) {
			out = new byte[3];
			out[0] = (byte)0x82;
			out[1] = (byte)((l >> 8) & 0xff);
			out[2] = (byte)((l >> 0) & 0xff);
		} else if (l > 0x0000007F) {
			out = new byte[2];
			out[0] = (byte)0x81;
			out[1] = (byte)((l >> 0) & 0xff);
		} else {
			out = new byte[]{(byte)l};
		}

		return out;
	}
}
