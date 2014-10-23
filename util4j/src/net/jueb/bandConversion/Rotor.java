package net.jueb.bandConversion;
import java.util.Vector;

/**
 * ��ֵ����ת�̣����з��ŵĻ�����
 * @author Administrator
 *
 */
public class Rotor{
	
	/**
	 * ������ת���ϵķ��ż���
	 * �÷��ż�����������
	 */
	private final Vector<Numeral> numerals;
	
	private final int radix;
	
	/**
	 * ��ǰָ��Ļ������ŵ����
	 */
	private volatile int currenIndex;
	private volatile int maxIndex;
	
	
	public Rotor(Vector<Numeral> numerals) {
		if(numerals.size()<=0)
		{
			throw new RuntimeException("�޷�����û�л������ŵ�ת��");
		}else
		{
			this.numerals=numerals;
			this.maxIndex=this.numerals.size()-1;
			this.currenIndex=0;
			this.radix=this.numerals.size();
		}
	}	
	/**
	 * ��ȡ��ǰָ��Ļ������ŵ�����
	 * @return
	 */
	public synchronized int getCurrentIndex()
	{
		return this.currenIndex;
	}
	
	public synchronized void setCurrenIndex(int index)
	{
		this.currenIndex=index;
	}
	
	
	/**
	 * ��ȡ��ǰָ��Ļ�������
	 * @return
	 */
	public synchronized Numeral getCurrentNumeral()
	{
		return this.numerals.get(currenIndex);
	}
	
	/**
	 * ��ȡת�̵Ľ�����
	 * @return
	 */
	public synchronized int getRadix() {
		return this.radix;
	}
	
	/**
	 * ��ȡ�����Ʒ�������
	 * @return
	 */
	public synchronized int getMaxIndex() {
		return this.maxIndex;
	}
	/**
	 * ��ȡ��ǰָ��Ļ��������ַ���
	 * @return
	 */
	public synchronized String getCurrentViewStr()
	{
		return this.numerals.get(currenIndex).getViewStr();
	}	
	/**
	 * ��ȡ���ż���
	 * @return
	 */
	public synchronized Vector<Numeral> getNumerals() {
		return numerals;
	}
	/**
	 * ʹת��������Ļ�����һ
	 * ������������ż�һ���򷵻�true,ͬʱ��ǰλ�ûص���С����
	 */
	public synchronized boolean add()
	{
		if(this.currenIndex<maxIndex)
		{
			this.currenIndex++;
			return false;
		}else
		{//�������֮ǰ�Ѿ��������ŵĻ������ţ���������λ
			this.currenIndex=0;
			return true;
		}
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<this.numerals.size();i++)
		{
			sb.append("["+this.numerals.get(i).getViewStr()+"]");
		}
		return sb.toString();
	}
}
