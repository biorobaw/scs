package edu.usf.ratsim.proofofconcepts;

public class ThisClassName {

	public ThisClassName() {
		System.out.println(this.getClass().getName());
		System.out.println(this.getClass().getSimpleName());
	}

	public static void main(String[] args) {
		new ThisClassName2();
	}
}

class ThisClassName2 extends ThisClassName {

}