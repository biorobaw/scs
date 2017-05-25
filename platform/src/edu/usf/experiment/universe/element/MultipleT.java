package edu.usf.experiment.universe.element;

import java.util.LinkedList;
import java.util.Random;

import edu.usf.experiment.Globals;
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

public class MultipleT extends MazeElement{
	
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

	private float[] generateWalls(float startX,float startY,ElementWrapper e){
		//assume startY is shifted by +width from (x,y) on drawing
		//walls should not close the T, The T should be closed outside this function (due to overlaps between T's)
		startPoints.push(new Float[] {startX-bufferRadius,startY+bufferRadius});
		float minx = startX - tSide;
		float maxx = startX + tSide + width;
		float miny = startY - width;
		float maxy = startY + length;
		
		walls.add(new Wall(startX,  startY,  startX			, startY+=(length-width)));
		walls.add(new Wall(startX,  startY,  startX -= tSide, startY));
		walls.add(new Wall(startX,  startY,  startX 		, startY+=width));
				
		ElementWrapper left = e.getChild("left");
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
			walls.add(new Wall(startX,  startY,  startX+=width  , startY));
		}
		
		
		walls.add(new Wall(startX,  startY,  startX += (2*tSide-width), startY));
		
		ElementWrapper right = e.getChild("right");
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
			walls.add(new Wall(startX,  startY,  startX+=width  , startY));
		}
		
		walls.add(new Wall(startX,  startY,  startX 		, startY-=width));
		walls.add(new Wall(startX,  startY,  startX -= tSide, startY));
		walls.add(new Wall(startX,  startY,  startX			, startY-=(length-width)));
		
		
				
		return new float[] {minx,maxx,miny,maxy};
		
	}
	
	
	private void generateBufferedWalls(float width,float length,float startX,float startY,ElementWrapper e){
		//assume startY is shifted by +width from (x,y) on drawing
		//walls should not close the T, The T should be closed outside this function (due to overlaps between T's)

		walls.add(new Wall(startX,  startY,  startX			, startY+=length));
		walls.add(new Wall(startX,  startY,  startX -= tSide, startY));
		walls.add(new Wall(startX,  startY,  startX 		, startY+=width));	
		
		ElementWrapper left = e.getChild("left");
		if (left!=null) {
			generateBufferedWalls(width,length,startX, startY, left);
			startX+=width;
		}
		else{
			
			walls.add(new Wall(startX,  startY,  startX+=width  , startY));
		}
		
		walls.add(new Wall(startX,  startY,  startX += (2*tSide-width), startY));
		
		ElementWrapper right = e.getChild("right");
		if (right!=null) {
			generateBufferedWalls(width,length,startX, startY, right);
			startX+=width;
		}
		else {
			walls.add(new Wall(startX,  startY,  startX+=width  , startY));
		}

		walls.add(new Wall(startX,  startY,  startX 		, startY-=width));
		walls.add(new Wall(startX,  startY,  startX -= tSide, startY));
		walls.add(new Wall(startX,  startY,  startX			, startY-=length));
			
	}
	
	

	public MultipleT(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub

		width = params.getChildFloat("width");
		length = params.getChildFloat("length");
		tSide  = params.getChildFloat("tSide");
		
		x = params.getChildFloat("x");
		y = params.getChildFloat("y");
		
		
		ElementWrapper bufferedWalls = params.getChild("bufferedWalls");
		boolean showBufferedWalls = false;
		if(bufferedWalls!=null){
			//startPoints.clear();
			bufferRadius = bufferedWalls.getChildFloat("buffereRadius");
			showBufferedWalls = bufferedWalls.getChildBoolean("showWalls");
		}
			
		bufferWidth = 2*bufferRadius + width;
		bufferLength = length-width - 2*bufferRadius;
		
		walls.add(new Wall(x,  y,  x  , y+width));
		float[] boundbox = generateWalls(x, y+width, params); //needs to be called after setting buffer radiusw
		walls.add(new Wall(x+width,  y+width,  x+width  , y));
		walls.add(new Wall(x+width,  y,  x  , y));
		
			
		if(showBufferedWalls){
			walls.add(new Wall(x-bufferRadius,  y-bufferRadius,  x-bufferRadius  , y-bufferRadius+length-bufferLength));
			generateBufferedWalls(bufferWidth, bufferLength, x-bufferRadius, y-bufferRadius+length-bufferLength, params);
			walls.add(new Wall( x+width+bufferRadius, y-bufferRadius+length-bufferLength  ,  x+width+bufferRadius  , y-bufferRadius ));
			walls.add(new Wall(x+width+bufferRadius, y-bufferRadius, x-bufferRadius, y-bufferRadius ));
		}
			
		
		Globals g = Globals.getInstance();
		g.put("tMaze", this);
		
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
