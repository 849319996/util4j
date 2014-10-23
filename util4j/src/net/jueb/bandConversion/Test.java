package net.jueb.bandConversion;
import java.math.BigDecimal;
import java.util.Vector;

import net.jueb.bandConversion.ClockDial;
import net.jueb.bandConversion.Numeral;
import net.jueb.bandConversion.Rotor;




public class Test {

	public static void main(String[] args) {
		//����������ż���
		Vector<Numeral> Numeral=new Vector<Numeral>();
		for(int i=0;i<10;i++)
		{
			new String();
			Numeral rd=new Numeral(new byte[]{(byte)i},Character.toString((char) ('0'+i)));
			Numeral.add(rd);
		}
		for(int i=0;i<90;i++)
		{
			new String();
			Numeral rd=new Numeral(new byte[]{(byte)i},Character.toString((char) ('A'+i)));
			Numeral.add(rd);
		}
		
		//�������ת��
		Rotor rt=new Rotor(Numeral);
		System.out.println("����ת�̶������:");
		System.out.println(rt.toString());
		
		//�������
		final ClockDial cd=new ClockDial(rt, 10);
		System.out.println("���̶������");
		System.out.println(cd.getViewStrs());
		
		System.out.println("��ǰ���̶���:"+cd.getViewStrs());
		System.out.println("��ǰ����ʮ����ֵ:"+cd.getValue().intValue());
		
		System.out.println("���ñ���ֵΪLong.MAX_VALUE");
		BigDecimal out=cd.setValue(new BigDecimal(Long.MAX_VALUE));
		System.out.println("��ǰ���̶���:"+cd.getViewStrs());
		System.out.println("��ǰ����ʮ����ֵ:"+cd.getValue().longValue());
		System.out.println("�����ֵ��"+out.longValue());
		
	}
}
