package edu.usf.ratsim.support.proofofconcepts;

import java.util.HashMap;

public class Eliminable {

	
	public Eliminable() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		System.out.println("Eliminating program");
	}

	public static void main (String[] args) throws InterruptedException{
		Eliminable a = new Eliminable();
//		Eliminable b = a;
//		a = null;
//		b = null;
		
		HashMap<String, Eliminable> map = new HashMap<String, Eliminable>();
		map.put("a", a);
		
		a = null;
		map.remove("a");
		
		System.gc();
	}
	
}
