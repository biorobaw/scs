package edu.usf.micronsl.port.twodimensional;

import edu.usf.micronsl.module.Module;

/**
 * A two dimensional port using a two dimensional array to hold the data
 * A single Block Matrix expects that only a subblock of the matrix will be different than 0
 * 
 * The non zero block is the result of the intersection between the matrix and a window
 *
 */
public class Float2dSingleBlockMatrixPort extends Float2dPort {

	/**
	 * The array to hold the values
	 */
	float[][] data;
	
	
	
	int rows=0,cols=0; //size of the matrix
	
	int windowRows=0, windowCols=0; 	     //size of the window
	int windowStartRow = 0, windowStartCol=0;//window starting position
	int blockRows=0,blockCols=0;             //size of the non zero block
	int blockStartRow = 0,  blockStartCol =0;//starting position of the block in the original data
	
	
	
	

	/**
	 * Create the port using the data array as the structure to hold the data
	 * 
	 * @param owner
	 *            The owner module
	 * @param data
	 *            A 2d array that will be used to hold the data. Notice that
	 *            this array is passed by reference.
	 */
	public Float2dSingleBlockMatrixPort(Module owner, int rows, int cols, float[][] data , int startRow,int startCol) {
		super(owner);

		if (rows == 0 || cols == 0)
			throw new IllegalArgumentException("Cannot use matrix with 0 rows or cols");
		
		
		this.data 		= data;
		this.rows 		= rows;
		this.cols 		= cols;
		this.windowRows = data.length;
		this.windowCols = data[0].length;
		
		
		if(windowRows>rows || windowCols>cols)
			throw new IllegalArgumentException("Window cannot exceed matrix dimensions");
		
		setWindowOrigin(startRow, startCol);

	}
	
	
	public Float2dSingleBlockMatrixPort(Module owner, int rows, int cols ,int windowRows, int windowCols,int windowStartRow,int windowStartCol) {
		this(owner,rows,cols,new float[windowRows][windowCols],windowStartRow,windowStartCol);
	}
	
	public void setWindowOrigin(int row,int col){
		
		windowStartRow = row;
		windowStartCol = col;
				
		if(col+windowCols <= 0 || col >= cols){
			//theres no intersection
			blockStartCol = 0;
			blockCols = 0;
		
		}else {
			
			blockStartCol = Math.max(0, col);
			int end = Math.min(cols, col+windowCols);
			blockCols = end - blockStartCol;
			
		}
		
		
		if(row + windowRows <=0 || row >= rows){
			blockStartRow = 0;
			blockRows = 0;
		}else{
			blockStartRow = Math.max(0, row);
			int end = Math.min(rows, row+windowRows);
			blockRows = end - blockStartRow;
		}
		
		//create new array or use fixed size? for now use fixed size		
		
		
	}
	
	

	

	

	@Override
	public float get(int i, int j) {
		if ( i<blockStartRow || i >=blockStartRow + blockRows || j < blockStartCol || j>=blockStartCol+blockCols) return 0;
		return data[i-blockStartRow][j-blockStartCol];
	}

	@Override
	public void set(int i, int j, float x) {
		data[i-blockStartRow][j-blockStartCol] = x;
	}
	
	public void setBlock(int i,int j,float  x){
		data[i][j] = x;
	}

	public float getBlock(int i,int j){
		return data[i][j];
	}
	
	
	@Override
	public int getNRows() {
		return rows;
	}

	@Override
	public int getNCols() {
		return cols;
	}
	
	public int getBlockCols(){
		return blockCols;
	}
	
	public int getBlockRows(){
		return blockRows;
	}
	
	public int getStartRow(){
		return blockStartRow;
	}
	
	public int getStartCol() {
		return blockStartCol;
	}
	
	public int getEndRow(){
		return blockStartRow + blockRows;
	}
	
	public int getEndCol() {
		return blockStartCol + blockCols;
	}
	
	public int getBlockIndex(int i,int j){
		return (i+blockStartRow)*cols + j + blockStartCol;
	}
	
	public int getIndex(int i,int j){
		return i*cols+j;
	}
	

	@Override
	public void clear() {
		for (int i = 0; i < windowRows; i++)
			for (int j = 0; j < windowCols; j++)
				data[i][j] = 0f;
		setWindowOrigin(0, 0);
	}
	
	public void clearBlock(){
		for (int i = 0; i < blockRows; i++)
			for (int j = 0; j < blockCols; j++)
				data[i-blockStartRow][j-blockStartCol] = 0f;
	}

	@Override
	public float[][] get2dData() {
		return data;
	}

	@Override
	public void get2dData(float[][] data) {
		if (data.length != this.data.length || (data[0].length != this.data[0].length))
			throw new IllegalArgumentException("The data array should be of size getNRows() by getNCols()");

		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				data[i][j] = this.data[i][j];
	}
	
	public Float2dSingleBlockMatrixPort copyPort(){
		return new Float2dSingleBlockMatrixPort(this.getOwner(), rows, cols,  windowRows,  windowCols, windowStartRow, windowStartCol);
	}

	public void copyDataTo(Float2dSingleBlockMatrixPort destiny){
		destiny.setWindowOrigin(windowStartRow, windowStartCol);
		for(int i=0;i<blockRows;i++)
			for(int j=0;j<blockCols;j++)
				destiny.setBlock(i, j, data[i][j]);
	}
	
}
