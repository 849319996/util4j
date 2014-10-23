package net.jueb.bandConversion;

import java.math.BigDecimal;
import java.util.Vector;

/**
 * װ�л�������ת�̵ı���
 * @author Administrator
 *
 */
public class ClockDial {
	
	/**
	 * ���������ת��
	 */
	private final Vector<Rotor> rotors;
	/**
	 *����ʹ�õ���ѧ���ż���
	 */
	private final Vector<Numeral> numerals;
	
	/**
	 * ������ʹ�õĽ��ƻ���
	 */
	private final int radix;
	/**
	 * ������ֵλ��
	 */
	private final int digit;
	
	/**
	 * ����λ��
	 * @param rotor
	 * @param digit
	 */
	public ClockDial(Rotor rotor,int digit) {
		this(rotor.getNumerals(), digit);
	}
	
	
	/**
	 * @param numerals ��ֵ����ת�̼���
	 * @param digit ����λ�����������̸���
	 */
	public ClockDial(Vector<Numeral> numerals,int digit) {
		if(digit<=0)
		{
			throw new RuntimeException("�޷�����û�з���ת�̵ı���");
		}else if(numerals!=null && numerals.size()<=1)
		{
			throw new RuntimeException("�޷���������2�ַ��ŵı���");
		}else
		{
			this.numerals=numerals;
			this.radix=numerals.size();//����(����)
			this.digit=digit;//λ��
			this.rotors=new Vector<Rotor>();
			for(int i=0;i<digit;i++)
			{//װ�ض�����
				this.rotors.add(new Rotor(numerals));
			}
		}
	}
	
	
	/**
	 * ��������ʾֵ��1
	 * �������true,��ʾ���һ��ת�̵Ļ������˷����˽�λ
	 * ���ܻᵼ��ȫ����Ϊ��ʼ���
	 */
	public synchronized boolean add()
	{
		synchronized (rotors) {
			for(int i=0;i<rotors.size();i++)
			{
				Rotor r=rotors.get(i);
				boolean addNext=r.add();
				if(addNext)
				{//���������λ,�����һ��ת��+1
					if(i==rotors.size())
					{
						//������������һ��ת��,�ұ���1���򷵻�true��֪�������
						return true;
					}
					continue;
				}else
				{//���û�з�����λ�����˳�
					break;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * һ��������
	 * @param num
	 * @return
	 */
	public synchronized boolean add(int num)
	{
		//��һ��һ����
		return false;
	}
	
	public Vector<Rotor> getRotors() {
		return rotors;
	}


	public Vector<Numeral> getNumerals() {
		return numerals;
	}


	public int getRadix() {
		return radix;
	}


	public int getDigit() {
		return digit;
	}


	/**
	 * ��ȡ���̶����ַ���
	 * @return
	 */
	public String getViewStrs()
	{
		StringBuffer sb=new StringBuffer();
		for(int i=rotors.size()-1;i>=0;i--)
		{
			sb.append("["+rotors.get(i).getCurrentViewStr()+"]");
		}
		return sb.toString();
	}
	
	/**
	 * ��ȡ����10������ֵ
	 * @return
	 */
	public synchronized BigDecimal getValue()
	{
		BigDecimal value=new BigDecimal(0);
		for(int i=0;i<rotors.size();i++)
		{
			int v=rotors.get(i).getCurrentIndex();//��ǰת��ֵ
			int m=i;//��ǰλ��,������Ϊ���Ƶ�ָ��
			BigDecimal vb=new BigDecimal(v);
			value=value.add(vb.multiply(new BigDecimal(radix).pow(m)));
		}
		return value;
	}
	
	/**
	 * ����ָ����ֵ��ֻ��������
	 * @param value
	 * @return �����������ֵ
	 */
	public synchronized BigDecimal setValue(BigDecimal value)
	{
		BigDecimal in=value.abs();//ȡ����
		BigDecimal br=new BigDecimal(radix);//����
		for(int i=rotors.size()-1;i>=0;i--)
		{
			BigDecimal v=new BigDecimal(radix).pow(i);//��ǰλ������ֵ������10���Ƶ�1100�е����λΪ1000
			BigDecimal count=in.divide(v,3);//����䵱ǰλֵ������
			if(count.compareTo(new BigDecimal(0))==0)
			{//�����0����ǰλ��ֵ
				in=in.remainder(v);//������������һ������
			}else if(count.compareTo(new BigDecimal(0))>0 && count.compareTo(br)<0)
			{//�����0-���Ƹ���ǰλ��ֵ��(��������λֵ������10���Ƶ�10)
				rotors.get(i).setCurrenIndex(count.intValue());//���ø�λ��������Ϊ�̵�ֵ
				in=in.subtract(v.multiply(count));//ʣ�����
			}else if(count.compareTo(br)>=0)
			{//������ڵ��ڽ��Ƶĵ�ǰλ��ֵ��(����10,11��ǧ��λ)
				rotors.get(i).setCurrenIndex(rotors.get(i).getMaxIndex());//���ø�λ��������Ϊ��ȥ��������ֺ��ֵ(���������)
				in=in.remainder(v);//ȡ������
				in=in.add(v.multiply(count.subtract(br).add(new BigDecimal(1))));//�������������ֵ������һλֵ
			}
		}
		return in;//����ʣ���ֵ
	}
}
