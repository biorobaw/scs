package edu.usf.micronsl.test;

import edu.usf.micronsl.Module;

public class DummyModule extends Module {

	public DummyModule(String name) {
		super(name);
	}

	@Override
	public void run() {
		System.out.println("Executing module " + getName());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done executing module " + getName());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
