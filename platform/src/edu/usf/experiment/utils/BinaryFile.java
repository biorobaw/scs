package edu.usf.experiment.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;

public class BinaryFile {	
	
	public static DataInputStream read(String aInputFileName){
		DataInputStream in = null;
	    File file = new File(aInputFileName);
	    try {
	        in =  new DataInputStream(new BufferedInputStream(new FileInputStream(file)));  
	    }
	    catch (FileNotFoundException ex) {
	    }

	    return in;
	  }
	
	public static OutputStream openFileToWrite(String filename){

		    OutputStream output = null;
		    try {
				output = new BufferedOutputStream(new FileOutputStream(filename));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return output;


	}
	
	public static void write(OutputStream out,byte[] data){
		try {
			out.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void close(OutputStream out){
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void saveBinaryMatrix(float[][] matrix,String filename){
		int rows= matrix.length;
		int cols= matrix[0].length;
		int totalSize =  2*Integer.SIZE + rows*cols*Float.SIZE; //in bits
		totalSize/=8;
		
		
		ByteBuffer data = ByteBuffer.allocate(totalSize);
		data.putInt(rows);
		data.putInt(cols);
		for(float[] r : matrix)
			for(float v : r)
				data.putFloat(v);
		
		OutputStream out = openFileToWrite(filename);
		write(out, data.array());
		close(out);
		
		
	}
	
//	static int it = 0;
	public static void  saveSparseBinaryMatrix(float[][] matrix,String filename){
		int rows= matrix.length;
		int cols= matrix[0].length;
		
		
		
		OutputStream out = openFileToWrite(filename);
		ByteBuffer data = ByteBuffer.allocate(2*Integer.SIZE/8);
		data.putInt(rows);
		data.putInt(cols);
		write(out, data.array());
		
		
		int size = (2*Integer.SIZE+Float.SIZE)/8;
		
//		int cant = 0;
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++){
				if(matrix[i][j]!=0){
					data = ByteBuffer.allocate(size);
					data.putInt(i);
					data.putInt(j);
					data.putFloat(matrix[i][j]);
					write(out,data.array());
//					cant++;
				}
				
			}
		
//		if(it==60){
//			System.out.println("float size: "+Float.SIZE);
//			System.out.println("int size:   "+Integer.SIZE);
//			System.out.println("cant:       "+cant);
//		}
		
		
		close(out);
//		it++;
		
		
	}
	
	public static float[][] loadMatrix(String filename){
		DataInputStream in = read( filename);
		
		try {
			int rows = in.readInt();
			int cols = in.readInt();
			
			float[][] matrix = new float[rows][cols];
			
			for(int i=0;i<rows;i++)
				for(int j=0;j<cols;j++)
					matrix[i][j]=in.readFloat();
			
			return matrix;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return null;
	}
	
	public static float[][] loadSparseMatrix(String filename){
		DataInputStream in = read( filename);
		
		try {
			int rows = in.readInt();
			int cols = in.readInt();
//			System.out.println("r,c "+rows+" "+ cols);
			
			float[][] matrix = new float[rows][cols];
			
			while(in.available()!=0){
				int i = in.readInt();
				int j = in.readInt();
//				System.out.println("i,j: "+i+" "+j);
				matrix[i][j] = in.readFloat();
			}
			
			return matrix;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return null;
	}
	

}
