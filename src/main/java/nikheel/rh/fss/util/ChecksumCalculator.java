package nikheel.rh.fss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import nikheel.rh.fss.exception.FSSRuntimeException;

public class ChecksumCalculator {

	public static String calculateChecksum(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(data);
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				hexString.append(Integer.toHexString(0xff & b));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new FSSRuntimeException("Error calculating checksum", e);
		}
	}

	public static String calculateChecksum(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] byteArray = new byte[1024];
			int bytesCount;
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			}
			byte[] hash = digest.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				hexString.append(Integer.toHexString(0xff & b));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new FSSRuntimeException("Error calculating checksum", e);
		}
	}
}
