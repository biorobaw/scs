package com.github.biorobaw.scs.utils;

public class Debug {

	public static boolean sleepBeforeStart = false;
	
	public static boolean print = true;
	
	public static boolean profiling = false;
	
	public static long tic(){
		return System.nanoTime();
	}
	
	public static long toc(long stamp){
		return (System.nanoTime()-stamp)/(long)1e6;
	}
	
	public static void print(Object array[],String separator,boolean newLine){
		for(int i=0;i<array.length;i++) System.out.println(array[i].toString() + separator);
		if(newLine) System.out.println();
	}

	
}
