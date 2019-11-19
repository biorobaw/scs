package com.github.biorobaw.scs.utils;

public class Debug {

	public static boolean sleepBeforeStart = false;
	
	public static boolean print = true;
	
	public static boolean profiling = false;
	
	public static long tic(){
		return System.nanoTime();
	}
	
	public static float toc(long stamp){
		return (float)(System.nanoTime()-stamp)/(float)1e6;
	}
	
	public static void print(Object array[],String separator,boolean newLine){
		for(int i=0;i<array.length;i++) System.out.println(array[i].toString() + separator);
		if(newLine) System.out.println();
	}
	
	public static <T> void printLArray(long array[],String separator,boolean newLine){
		for(int i=0;i<array.length;i++) System.out.print(array[i] + separator);
		if(newLine) System.out.println();
	}

	
}
