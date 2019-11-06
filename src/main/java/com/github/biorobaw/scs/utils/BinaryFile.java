package com.github.biorobaw.scs.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
		    	File file = new File(filename);
		    	File parent = file.getParentFile();
		    	if(parent!=null) parent.mkdirs();
				output = new BufferedOutputStream(new FileOutputStream(file));
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
	
	
	public static void saveBinaryMatrix(float[][] matrix,int rows,int cols,String filename){
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
	
//	public static void saveBinaryMatrix(Map<Entry,Float> matrix,int rows,int cols,String filename, boolean isLittleEndian){
//		int totalSize =  2*Integer.SIZE + rows*cols*Float.SIZE; //in bits
//		totalSize/=8;
//		
//		
//		ByteBuffer data = ByteBuffer.allocate(totalSize);
//		if(isLittleEndian) data.order(ByteOrder.LITTLE_ENDIAN);
//		data.putInt(rows);
//		data.putInt(cols);
//		for(int i=0;i<rows;i++)
//			for(int j=0;j<cols;j++){
//				Float v = matrix.get(new Entry(i, j));
//				data.putFloat(v==null ? 0 : v);
//			}
//		
//		OutputStream out = openFileToWrite(filename);
//		write(out, data.array());
//		close(out);
//		
//		
//	}
	
	
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
			System.out.println("Unable to load file: "+filename);
			System.exit(-1);
		}
		
		
		
		return null;
	}

	
	
	
	public static void  saveSparseBinaryMatrix(float[][] matrix,int rows,int cols,String filename){
		
		OutputStream out = openFileToWrite(filename);		
		int intSize   = Integer.SIZE/8;
		int floatSize = Float.SIZE/8;
		int elements  = rows*cols;
		int fileSize  = (2*intSize+floatSize)*elements + 2*intSize; //Format: rows,cols, all i indexes, all j indexes, all floats
		ByteBuffer data = ByteBuffer.allocate(fileSize);
		
		
		data.putInt(rows);
		data.putInt(cols);
		int i=0;//element index
		for(int r=0;r<rows;r++)
			for(int c=0;c<cols;c++){
				if(matrix[r][c]!=0){
					data.putInt(         2*intSize        + i*intSize,r);
					data.putInt(    (elements+2)*intSize  + i*intSize,c);
					data.putFloat( (2*elements+2)*intSize + i*floatSize,matrix[r][c]);
				}
			}

		write(out,data.array());
		close(out);
		
	}
	
//	public static void saveSparseBinaryMatrix(Map<Entry,Float> matrix,int rows,int cols,String filename){
//
//		OutputStream out = openFileToWrite(filename);		
//		int intSize   = Integer.SIZE/8;
//		int floatSize = Float.SIZE/8;
//		int elements  = matrix.size();
//		int fileSize  = (2*intSize+floatSize)*elements + 2*intSize; //Format: rows,cols, all i indexes, all j indexes, all floats
//		ByteBuffer data = ByteBuffer.allocate(fileSize);
//		
//		data.putInt(rows);
//		data.putInt(cols);
//		int i=0;
//		for(Entry e : matrix.keySet()){
//			data.putInt(         2*intSize        + i*intSize,e.i);
//			data.putInt(    (elements+2)*intSize  + i*intSize,e.j);
//			data.putFloat( (2*elements+2)*intSize + i*floatSize,matrix.get(e));
//			i++;
//		}
//		write(out,data.array());
//		
//		close(out);
//		
//	}

	
//	public static Map<Entry,Float>  loadSparseMatrix(String filename){
//		DataInputStream in = read( filename);
//		
//		int intSize   = Integer.SIZE/8;
//		int floatSize = Float.SIZE/8;
//		int elements  = ((int)new File(filename).length() -2*intSize  )/(2*intSize+floatSize);//Format: rows,cols, all i indexes, all j indexes, all floats
//		
//		try {
//			
//			in.readInt(); //discard number of rows
//			in.readInt(); //discard number of cols
//			
//			int[] rows = new int[elements];
//			int[] cols = new int[elements];
//			float[] vals = new float[elements];
//			HashMap<Entry, Float> matrix = new HashMap<>();
//			
//			for(int i=0;i<elements;i++) rows[i]=in.readInt();
//			for(int i=0;i<elements;i++) cols[i]=in.readInt();
//			for(int i=0;i<elements;i++) vals[i]=in.readFloat();
//			for(int i=0;i<elements;i++) matrix.put(new Entry(rows[i], cols[i]), vals[i]);
//			
//			
//			return matrix;
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("Unable to load file: "+filename);
//			System.exit(-1);
//		}
//		
//		
//		
//		return null;
//	}
	
	public static void writeArray(OutputStream o ,int[] array,boolean isLittleIndian) {
		int totalSize =  (array.length+1)*Integer.SIZE; //in bits
		totalSize/=8;
		
		ByteBuffer data = ByteBuffer.allocate(totalSize);
		if(isLittleIndian ) data.order(ByteOrder.LITTLE_ENDIAN);
		data.putInt(array.length); //store size
		for(int datum : array) data.putInt(datum); //store data
		write(o, data.array());
	}

}
