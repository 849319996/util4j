package net.jueb.bandConversion;

import java.util.Arrays;

/**
 * ��ѧ����
 * @author Administrator
 *
 */
public class Numeral {
	/**
	 * �÷��Ŷ�Ӧ�ĳ־û�����
	 */
	private final byte[] data;
	private  String viewStr="null";
	
	public Numeral(byte[] data,String viewStr) {
		this(data);
		this.viewStr=viewStr;
	}
	public Numeral(byte[] data) {
		if(data.length<=0)
		{
			throw new RuntimeException("һ�����Ż��������ж�Ӧ�ĳ־û�����");
		}else
		{
			this.data=data;
		}
	}

	public byte[] getData()
	{
		return this.data;
	}
	
	/**
	 * ��ȡ���ŵ��ַ�����ʾ��ʽ
	 * @return
	 */
	public String getViewStr()
	{
		return this.viewStr;
	}
	
	public String toString() {
		return "Radix [data=" + Arrays.toString(data) + ", viewStr=" + viewStr
				+ "]";
	}
	
}
