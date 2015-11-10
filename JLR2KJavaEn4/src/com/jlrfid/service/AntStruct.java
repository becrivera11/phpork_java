package com.jlrfid.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

public class AntStruct extends Structure{
	
	/**
	 * antenna enable the array or not, 4 antenna has 4 related variable of array, 0 is not enabled, 1 is enabled.
	 */
    public byte[] antEnable = new byte[4];
    
    /**
	 * antenna time array, 4 antenna has 4 related variable of array, time range 50-10000, the unit is ms.
	 */
    public NativeLong[] dwellTime = new NativeLong[4];
    
    /**
	 * antenna power array, 4 antenna has 4 related variable of array, power range 20-33
	 */
    public NativeLong[] power = new NativeLong[4];
    
	public static class ByReferenceextends extends AntStruct implements Structure.ByReference {}
	
	public static class ByValue extends AntStruct implements Structure.ByValue{}
	
	protected List getFieldOrder() {
		 List fields = new ArrayList();
	     fields.addAll(Arrays.asList(new String[] {"antEnable","dwellTime","power"}));
	     return fields;
	}
}