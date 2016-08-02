package net.jueb.util4j.security;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityUtil {
	protected final Logger log=LoggerFactory.getLogger(getClass());
	/**
	 * base 64 encode
	 * @param bytes 待编码的byte[]
	 * @return 编码后的base 64 code
	 */
	public static String Base64Encode(byte[] binaryData){
		return Base64.encodeBase64String(binaryData);
	}
	
	/**
	 * base 64 decode
	 * @param base64Data 待解码的base 64 code
	 * @return 解码后的byte[]
	 * @throws Exception
	 */
	public static byte[] Base64Decode(byte[] base64Data) throws Exception{
		return Base64.decodeBase64(base64Data);
	}
	
	public static byte[] Base64Decode(String base64String) throws Exception{
		return Base64.decodeBase64(base64String);
	}
}
