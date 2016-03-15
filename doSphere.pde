class Sphere {
  
int ptsW, ptsH;

PImage img;
PShape s;
float rx, ry, rz;

int numPointsW;
int numPointsH_2pi; 
int numPointsH;
int dayLength;
float planetPosition;
float tempPosition=0;

int planetOwner;//for moons. for planets -1
float distanceFromP;//for moons. for planets -1;
float orbitEccentricity;//for moons. for planets -1;
float angle=0;
float x,z;
float a;


float[] coorX;
float[] coorY;
float[] coorZ;
float[] multXZ;
  
public Sphere(String texture, float rx, float ry, float rz,  float planetPosition, int planetOwner, float distanceFromP){
   noStroke();
   this.dayLength=dayLength;
   img=loadImage(texture);
   
   ptsW=30;
   ptsH=30;
   this.rx=rx;
   this.ry=ry;
   this.rz=rz;
   
   this.planetPosition=planetPosition; 
   
   this.planetOwner=planetOwner;
   this.distanceFromP=distanceFromP;
   this.orbitEccentricity=orbitEccentricity;
   
   this.a=distanceFromP/moonDistanceScale;//for planets this doesn't make sense, just to avoid 2 constructors
       
   
  // Parameters below are the number of vertices around the width and height
   initializeSphere(ptsW, ptsH); 
  }
  
void initializeSphere(int numPtsW, int numPtsH_2pi) {

  // The number of points around the width and height
  numPointsW=numPtsW+1;
  numPointsH_2pi=numPtsH_2pi;  // How many actual pts around the sphere (not just from top to bottom)
  numPointsH=ceil((float)numPointsH_2pi/2)+1;  // How many pts from top to bottom (abs(....) b/c of the possibility of an odd numPointsH_2pi)

  coorX=new float[numPointsW];   // All the x-coor in a horizontal circle radius 1
  coorY=new float[numPointsH];   // All the y-coor in a vertical circle radius 1
  coorZ=new float[numPointsW];   // All the z-coor in a horizontal circle radius 1
  multXZ=new float[numPointsH];  // The radius of each horizontal circle (that you will multiply with coorX and coorZ)

  for (int i=0; i<numPointsW ;i++) {  // For all the points around the width
    float thetaW=i*2*PI/(numPointsW-1);//equal angles between dots
    coorX[i]=sin(thetaW);//on a unit circle ofc
    coorZ[i]=cos(thetaW);
  }
  
  for (int i=0; i<numPointsH; i++) {  // For all points from top to bottom 
    if (int(numPointsH_2pi/2) != (float)numPointsH_2pi/2 && i==numPointsH-1) {  // If the numPointsH_2pi is odd and it is at the last pt. Simply get old coorY and radius of horiz circle is 0.
      float thetaH=(i-1)*2*PI/(numPointsH_2pi);
      coorY[i]=cos(PI+thetaH); 
      multXZ[i]=0;
    } 
    else {
      //The numPointsH_2pi and 2 below allows there to be a flat bottom if the numPointsH is odd
      float thetaH=i*2*PI/(numPointsH_2pi);

      //PI+ below makes the top always the point instead of the bottom.
      coorY[i]=cos(PI+thetaH); //no difference, just coorY[0]==PI, not 0, so, the 1st point on top. So, probably as we connect points 1 by 1, bottom points are connected, and we get flat bottom (o_o). no eto smotria kak mi mutim s to4kami, no vrodi poetomu
      multXZ[i]=sin(thetaH);//this is radius of horizontal circle at height of ith vertice. it's some Z value at coorY[i] (but different z from before)
    } 
  }
}


void textureSphere() { 
  // These are so we can map certain parts of the image on to the shape 
  float changeU=img.width/(float)(numPointsW-1); 
  float changeV=img.height/(float)(numPointsH-1); 
  float u=0;  // Width variable for the texture
  float v=0;  // Height variable for the texture

  
  beginShape(TRIANGLE_STRIP);
  texture(img);
 
  for (int i=0; i<(numPointsH-1); i++) {  // For all the rings but top and bottom
    // Goes into the array here instead of loop to save time
   
    float coory=coorY[i];
    float cooryPlus=coorY[i+1];

    float multxz=multXZ[i];
    float multxzPlus=multXZ[i+1];

    for (int j=0; j<numPointsW; j++) {  // For all the pts in the ring
      normal(coorX[j]*multxz, coory, coorZ[j]*multxz);//normal vector (for lighting)
      vertex(coorX[j]*multxz*rx, coory*ry, coorZ[j]*multxz*rz, u, v);//as each x and z point is on circumference of unit circle, coorX[j]*multxz adjusts to make radius at certain height
      normal(coorX[j]*multxzPlus, cooryPlus, coorZ[j]*multxzPlus);
      vertex(coorX[j]*multxzPlus*rx, cooryPlus*ry, coorZ[j]*multxzPlus*rz, u, v+changeV);//this is next value of coorY. So, we alternate vertices from 2 adjacent horizontal planes, so that everything is connected together
      u+=changeU;
    }
    v+=changeV;
    u=0;
  }
  endShape();
}

  
}

