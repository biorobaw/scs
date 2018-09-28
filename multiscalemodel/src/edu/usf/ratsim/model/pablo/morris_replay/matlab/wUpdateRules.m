
%PARAMETERS
stepDistance = 0.08;
pcRadius   = 0.08; %radius of pc
cellsPerSide = 30; %30 cells per side
sideLength = 2; %2 meters
pcDistance = 2/(cellsPerSide-1); %distance between consecutive pcCenters

numCells = 2;
xCenters = (0:(numCells-1))'*pcDistance;
yCenters = zeros(numCells,1);

pcCenters = [xCenters yCenters];  





cirXs = pcRadius*cos(0:0.05:(2*pi+0.05));
cirYs = pcRadius*sin(0:0.05:(2*pi+0.05));

%%

pointsXs = 0:0.1:0.51;


%%
figure(1)
clf

hold on

for i=1:numCells
    
    plot(cirXs + pcCenters(i,1) , cirYs+pcCenters(i,2))
    
end


hold off
axis([xCenters(1)-2*pcRadius  xCenters(end)+2*pcRadius -2*pcRadius 2*pcRadius])
axis equal

