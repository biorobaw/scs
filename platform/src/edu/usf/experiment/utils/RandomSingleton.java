package edu.usf.experiment.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class RandomSingleton {

	private static Random instance = null;

	public static Random getInstance() {
		if (instance == null)
			instance = new Random();
		
		return instance;
	}
	
	public static void save(String logpath){
		String fileName = logpath + "random.ser";
		try {
			FileOutputStream os = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(instance);
			oos.close();
			os.close();
			System.out.println("next int is: " + instance.nextInt());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could not save random to " + fileName);
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could not save random to " + fileName);
			System.exit(-1);
		}
	}
	
	public static void load(String logpath){
		String fileName = logpath + "random.ser";
		
		
		try {
			FileInputStream is = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(is);
			instance = (Random)ois.readObject();
			ois.close();
			is.close();
//			System.out.println("next int is: " + instance.nextInt());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			System.out.println("Could not load random from " + fileName);
			System.exit(-1);
		}
		
		
	}
}
