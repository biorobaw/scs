package com.github.biorobaw.scs.maze.mazes;

import java.util.LinkedList;
import java.util.Random;

import com.github.biorobaw.scs.maze.Maze;
import com.github.biorobaw.scs.simulation.object.maze_elements.walls.Wall;
import com.github.biorobaw.scs.utils.files.XML;
import com.github.biorobaw.scs.utils.math.RandomSingleton;

public class MultipleT extends Maze {
	
	//      ____________________
	//     |________    ________| width
	//     <-tSide->|  |<-tSide-> 
	//              |  |          
	//              |  |
	//              |  |length
	//              |  |
	//              |__|
	//              *width
	//
	//        * = (x,y) on xml file
	//	When T gets connected a square of side width is overposed between T'
	
	
	
	private float width;
	private float length;
	private float tSide;
	private float x;
	private float y;
	private float bufferRadius=0;
	private float bufferWidth;
	private float bufferLength;
	private LinkedList<String> directions = new LinkedList<String>(); //example: left,right,right
	private LinkedList<Float[]> startPoints = new LinkedList<Float[]>();//used for calculating
	private Random random = RandomSingleton.getInstance();
	
	
	LinkedList<Wall> fixed_walls = new LinkedList<>();
	

	private float[] generateWalls(float startX,float startY,XML xml){
		//assume startY is shifted by +width from (x,y) on drawing
		//walls should not close the T, The T should be closed outside this function (due to overlaps between T's)
		startPoints.push(new Float[] {startX-bufferRadius,startY+bufferRadius});
		float minx = startX - tSide;
		float maxx = startX + tSide + width;
		float miny = startY - width;
		float maxy = startY + length;
		
		fixed_walls.add(new Wall(startX,  startY,  startX			, startY+=(length-width)));
		fixed_walls.add(new Wall(startX,  startY,  startX -= tSide, startY));
		fixed_walls.add(new Wall(startX,  startY,  startX 		, startY+=width));
				
		XML left = xml.getChild("left");
		if (left!=null) {
			directions.push("left");
			float[] boundbox = generateWalls(startX, startY, left);
			minx = boundbox[0] < minx ? boundbox[0] : minx;
			maxx = boundbox[1] > maxx ? boundbox[1] : maxx;
			miny = boundbox[2] < miny ? boundbox[2] : miny;
			maxy = boundbox[3] > maxy ? boundbox[3] : maxy;
			startX+=width;
		}
		else{
			fixed_walls.add(new Wall(startX,  startY,  startX+=width  , startY));
		}
		
		
		fixed_walls.add(new Wall(startX,  startY,  startX += (2*tSide-width), startY));
		
		XML right = xml.getChild("right");
		if (right!=null) {
			directions.push("right");
			float[] boundbox = generateWalls(startX, startY, right);
			minx = boundbox[0] < minx ? boundbox[0] : minx;
			maxx = boundbox[1] > maxx ? boundbox[1] : maxx;
			miny = boundbox[2] < miny ? boundbox[2] : miny;
			maxy = boundbox[3] > maxy ? boundbox[3] : maxy;
			startX+=width;
		}
		else{
			fixed_walls.add(new Wall(startX,  startY,  startX+=width  , startY));
		}
		
		fixed_walls.add(new Wall(startX,  startY,  startX 		, startY-=width));
		fixed_walls.add(new Wall(startX,  startY,  startX -= tSide, startY));
		fixed_walls.add(new Wall(startX,  startY,  startX			, startY-=(length-width)));
		
		
				
		return new float[] {minx,maxx,miny,maxy};
		
	}
	
	
	private void generateBufferedWalls(float width,float length,float startX,float startY,XML e){
		//assume startY is shifted by +width from (x,y) on drawing
		//walls should not close the T, The T should be closed outside this function (due to overlaps between T's)

		fixed_walls.add(new Wall(startX,  startY,  startX			, startY+=length));
		fixed_walls.add(new Wall(startX,  startY,  startX -= tSide, startY));
		fixed_walls.add(new Wall(startX,  startY,  startX 		, startY+=width));	
		
		XML left = e.getChild("left");
		if (left!=null) {
			generateBufferedWalls(width,length,startX, startY, left);
			startX+=width;
		}
		else{
			
			fixed_walls.add(new Wall(startX,  startY,  startX+=width  , startY));
		}
		
		fixed_walls.add(new Wall(startX,  startY,  startX += (2*tSide-width), startY));
		
		XML right = e.getChild("right");
		if (right!=null) {
			generateBufferedWalls(width,length,startX, startY, right);
			startX+=width;
		}
		else {
			fixed_walls.add(new Wall(startX,  startY,  startX+=width  , startY));
		}

		fixed_walls.add(new Wall(startX,  startY,  startX 		, startY-=width));
		fixed_walls.add(new Wall(startX,  startY,  startX -= tSide, startY));
		fixed_walls.add(new Wall(startX,  startY,  startX			, startY-=length));
			
	}
	
	

	public MultipleT(XML xml) {
		super(xml);
		width = xml.getFloatAttribute("width");
		length = xml.getFloatAttribute("length");
		tSide  = xml.getFloatAttribute("tSide");
		
		x = xml.getFloatAttribute("x");
		y = xml.getFloatAttribute("y");
		
		
		XML bufferedWalls = xml.getChild("bufferedWalls");
		boolean showBufferedWalls = false;
		if(bufferedWalls!=null){
			//startPoints.clear();
			bufferRadius = bufferedWalls.getFloatAttribute("bufferedRadius");
			showBufferedWalls = bufferedWalls.getBooleanAttribute("showWalls");
		}
			
		bufferWidth = 2*bufferRadius + width;
		bufferLength = length-width - 2*bufferRadius;
		
		fixed_walls.add(new Wall(x,  y,  x  , y+width));
		generateWalls(x, y+width, xml); //needs to be called after setting buffer radiusw
		fixed_walls.add(new Wall(x+width,  y+width,  x+width  , y));
		fixed_walls.add(new Wall(x+width,  y,  x  , y));
		
			
		if(showBufferedWalls){
			fixed_walls.add(new Wall(x-bufferRadius,  y-bufferRadius,  x-bufferRadius  , y-bufferRadius+length-bufferLength));
			generateBufferedWalls(bufferWidth, bufferLength, x-bufferRadius, y-bufferRadius+length-bufferLength, xml);
			fixed_walls.add(new Wall( x+width+bufferRadius, y-bufferRadius+length-bufferLength  ,  x+width+bufferRadius  , y-bufferRadius ));
			fixed_walls.add(new Wall(x+width+bufferRadius, y-bufferRadius, x-bufferRadius, y-bufferRadius ));
		}
			

		
		for(var w : fixed_walls) addWall(w);
		
	}
	
	public float[] getRandomPosition()
	{
		
		//since everything is multiplied by width to get area, and then will be divided by total, we ignore the multiplication
		int cantTs = directions.size()+1;
		float squareArea = bufferWidth;       
		float lengthArea = bufferLength;  
		float tSideArea  = 2*tSide+bufferWidth;
		float tArea 	 = lengthArea+tSideArea;
		//1 square and n Ts (I assume T's overlap in a square area)
		float totalArea = cantTs*tArea + squareArea; //since each
		
		float squareProbability = squareArea/totalArea;
		float tProbability = tArea/totalArea;
			
		float rndMazeSegment = random.nextFloat();
		float rnd1 = random.nextFloat();
		float rnd2 = random.nextFloat();
		
		if (rndMazeSegment<squareProbability) return new float[] {x-bufferRadius+rnd1*bufferWidth, y-bufferRadius+rnd2*bufferWidth};
		else
		{
			rndMazeSegment-=squareProbability;
			float rndMazeSegmentNormalized = rndMazeSegment/tProbability;
			int tId = (int)(rndMazeSegmentNormalized);
			
			Float[] startingPos = startPoints.get(tId);
			if  (( rndMazeSegmentNormalized - tId ) < (lengthArea/(lengthArea+tSideArea)) )
				 return new float[] {startingPos[0]+rnd1*bufferWidth,startingPos[1]+rnd2*lengthArea};
			else return new float[] {startingPos[0]-tSide+rnd1*tSideArea,startingPos[1]+lengthArea+rnd2*bufferWidth};
			
			
		}
		
		
		
		
	}

}
