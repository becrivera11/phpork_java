package com.jlrfid.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class SampleMap {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//InputStream input = null;
		//FileInputStream in = new FileInputStream("input.txt");
		Map<Integer, String> tenDivisibles = new LinkedHashMap<Integer, String>();
		Map<Integer, String> ones = new LinkedHashMap<Integer, String>();
		Map<Integer, String> endTys = new LinkedHashMap<Integer, String>();
		Map<Integer, String> afterTen = new LinkedHashMap<Integer, String>();
		String[] names1 = {"ten", "hundred", "thousand", "million", "billion", "trillion"};
		String[] names2 = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
		String[] names3 = {"eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
				"seventeen", "eighteen", "nineteen"};
		String[] names4 = {"twenty", "thirty", "fourty", "fifty", "sixty", "seventy", "eighty", "ninety"};
		/*
		int counter = 0, counter2 = 0;
		for(int i = 1;i < 10;i++)
		{
			ones.put(i, names2[i-1]);
		}
		
		for(int i = 11;i <= 19;i++)
		{
			ones.put(i, names3[counter2]);
			counter2++;
		}
		for(int i = 20;i <= 90;i+=10)
		{
			
			endTys.put(i, names4[counter]);
			counter++;
		}
		
		for(int i = 1;i <= 5;i++)
		{	
			int base = 10 * i;
			tenDivisibles.put(base, names1[i-1]);
			
		}*/
		
		/*
		try{
			input = new FileInputStream("input.txt");				
		}
		catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			*/
		int num;
		
		Scanner in = new Scanner(System.in);
		for(;;)
		{
			String inName = "";
			num = in.nextInt();
			String temp = String.valueOf(num);
			System.out.println(temp);
			int size = temp.length();
			System.out.println(size);
			inName = inName + names1[(size-2)];
			System.out.println(inName);
			/*
			for(int i = 0;i < size;i++)
			{
				int temp2 = (int) Math.floor(num);
				
			}*/
			
		}
		
		
	}
	
	public static int pow(int base, int exp)
	{
		for(int i = 0;i<exp;i++)
		{
			base = base*10;
		}
		return base;
	}

}
 