import processing.core.*; 
import processing.xml.*; 

import java.awt.Color; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class sketch_3dplanets extends PApplet {


PImage imgBack;
PFont Ocrb ,SansSerif;
 
int count=0;

float camCenX;

int ind=0;
float camX, centX, camZ;
float deltaCamX, deltaCentX, deltaCamZ;

My2dStuff sketch;
My2dInfo infoB;

Sphere[] allPlanets;
Sphere[] allMoons=new Sphere[13];
Rings rS, rS2, rS3, rS4;
Rings rU, rU2, rU3, rU4;
Rings rN, rN2;
Rings rJ;

boolean lightsOn=false;

int FPS =40;
float earthDegChangePerDraw = 360/FPS/16.0f;//8.0 -- kind of time for 1 Earth rotation in seconds. But actual is dfferent, as my comp slow :). Other planets relative to Earth

int radScale=1000;
int moonDistanceScale=30;//divide by this real moon distance

float zCamInitial = 160000;//can easily increase distances between planets, as just planets look smaller
 //(this is not limited, but planets size range is limited, see further)
 
 float sunAdjust=0;
 float planetPosition=0;
 float factor=0;
 public int indexOfP=-1;//currently selected planet
 
 
public void setup() {
                              size(1200, 700, P3D);
                              frameRate(FPS);
                              imgBack = loadImage("background.png");//for some strange reason image doesn't load from embedded sketch...
                              Ocrb = loadFont("OCRB-48.vlw");
                              SansSerif = loadFont("SansSerif.plain-48.vlw");
                              createInfoBar();
                              factor=zCamInitial/( (height/2) / tan(PI*30.0f / 180.0f) );//real screen width at scale z==zCamInitial
                         
                              
                              //create planets
                              allPlanets = new Sphere[9];
                              
                              
                              for (int i=0;i<allPlanets.length;i++){
                               
                                          
                                        //z in frustum makes zClose fit screen. When we move to a planet, we move to a distance from which planet's radius is seen
                                        //at 15 degrees (so that all planets look same when zoomed). But if planet is too small, we should be very close to it, 
                                        //and it may end up in front of zClose (so, invisible). That's why planets should be big enough. Minimal possible radScale
                                        // is given by (height/2)/((ctan(PI*15/180)*rx-rx)*tan(PI*30/180)). It is larger for smaller planets, so for the smallest planet
                                        //Mercury it is approx 580 for screen height 700, and angle 15 degr.
                                        // So, each planet's radius should be multiplied by at least radScale == 580 . But 
                                        //if screen height becomes 1000, minimal radScale== 827. So we take radScalle==1000 to have gap, and we don't make it dependent
                                        //on screen size, as just 1000 is simpler
                                         
                                         
                                        
                                          
                                       
                                        if (i==1){
                                            planetPosition=0;
                                            sunAdjust=28000;//random number found by trial and error
                                            planetPosition+=sunAdjust;//we adjust distance from planets to sun so that they are not over sun, even thoug perspective works this way
                                      
                                         }
                                         
                                         if (i>0){
                                               // cannot map taking into account planet sizes as they are not to scale (earth radiuses vs mln km)
               
                                                 planetPosition+= map(SunDistance[i], 0,4500, 0,factor*width-sunAdjust)-map(SunDistance[i-1], 0,4500, 0,factor*width-sunAdjust);
                                         }
                                         else{  
                                                planetPosition= -1*rSize[0]*radScale*1;
                                         }
                                         
                                         //another adjustment for Neptune to be inside frame. 1.6 also random. If we move it just by
                                         //  rSize[8]*radScale , the perspective (probably) again makes it appear outside of the screen
                                         if (i==8){
                                           planetPosition-=rSize[8]*radScale*1.6f;
                                         }
                                             
                                        allPlanets[i]= new Sphere("textures/"+PlanetNames[i]+".jpg", rSize[i]*radScale,rSize[i]*radScale,rSize[i]*radScale, planetPosition, -1, -1);
                                
                                
                              }
                              
                               //create rings
                               
                               //10851 based on real rings, 22679 as well real (in relation to radius). For Saturn
                               rS = new Rings(10.851f*radScale, 12.000f*radScale, "rings/closeRing.JPG", true);
                               rS2 = new Rings(12.000f*radScale, 13.000f*radScale, "rings/rings2.bmp", true);
                               rS3 = new Rings(13.200f*radScale, 18.500f*radScale, "rings/rings3.bmp", true);
                               rS4 = new Rings(19.000f*radScale, 22.679f*radScale, "rings/rings2.bmp", true);
                               
                               
                               //for Uranus 5918 - 16087
                               rU = new Rings(5.918f*radScale, 6.300f*radScale, "zz", false);
                               rU2 = new Rings(8.000f*radScale, 8.020f*radScale, "zzz", false);
                               rU3 = new Rings(10.500f*radScale, 11.000f*radScale, "zzz", false);
                               rU4 = new Rings(13.760f*radScale, 15.087f*radScale, "zzz", false);
                             
                             
                               //for Neptune...
                               rN = new Rings(6.287f*radScale, 6.600f*radScale, "zz", false);
                               rN2 = new Rings(8.539f*radScale, 9.200f*radScale, "zzz", false);
                             
                               //and Jupiter..
                               rJ  = new Rings(19.753f*radScale, 20.8f*radScale, "zz", false);
                              
                              
                              
                              //create moons
                              radScale*=2;//break scale, enlarge moons (?)
                              
                              allMoons[0] = new Sphere("moons/luna.jpg",0.27f*radScale, 0.27f*radScale, 0.27f*radScale, allPlanets[3].planetPosition+planetDistance[0]/moonDistanceScale, 3, planetDistance[0] );//planetDistance[0]/moonDistanceScale is major axis (conic sections)
                              allMoons[3] = new Sphere("moons/europa.jpg",0.245f*radScale, 0.245f*radScale, 0.245f*radScale, allPlanets[5].planetPosition+planetDistance[3]/moonDistanceScale, 5, planetDistance[3] );
                              allMoons[4] = new Sphere("moons/ganymede.jpg",0.413f*radScale, 0.413f*radScale, 0.413f*radScale, allPlanets[5].planetPosition+planetDistance[4]/moonDistanceScale, 5, planetDistance[4] );
                             
                             
                              allMoons[6] = new Sphere("moons/titan.jpg",0.404f*radScale, 0.404f*radScale, 0.404f*radScale, allPlanets[6].planetPosition+planetDistance[6]/moonDistanceScale, 6, planetDistance[6] );
                             
                             
                              allMoons[7] = new Sphere("moons/titania.jpg",0.1235f*radScale, 0.1235f*radScale, 0.1235f*radScale, allPlanets[7].planetPosition+planetDistance[7]/moonDistanceScale, 7, planetDistance[7] );
                              allMoons[8] = new Sphere("moons/oberon.jpg",0.1194f*radScale, 0.1194f*radScale, 0.1194f*radScale, allPlanets[7].planetPosition+planetDistance[8]/moonDistanceScale, 7, planetDistance[8] );
                            
                              allMoons[9] = new Sphere("moons/triton.jpg",0.2122f*radScale, 0.2122f*radScale, 0.2122f*radScale, allPlanets[8].planetPosition+planetDistance[9]/moonDistanceScale, 8, planetDistance[9] );
                             
                              allMoons[11] = new Sphere("moons/io.jpg",0.286f*radScale, 0.286f*radScale, 0.286f*radScale, allPlanets[5].planetPosition+planetDistance[11]/moonDistanceScale, 5, planetDistance[11] );
                              allMoons[12] = new Sphere("moons/callisto.jpg",0.378f*radScale, 0.378f*radScale, 0.378f*radScale, allPlanets[5].planetPosition+planetDistance[12]/moonDistanceScale, 5, planetDistance[12] );
                             
                              
                              
                              
                              
                                                          
                             //set initial camera and frustum
                              camX=factor*width/2.0f;
                              centX=factor*width/2.0f;
                              
                              camZ=zCamInitial;
                              frustum(-width/2.0f, width/2.0f,-height/2.0f ,height/2.0f, (height/2) / tan(PI*30.0f / 180.0f)/1.0f ,camZ*30);//end plane doesn't affect look if it's not too small. If endPlane==camZ, sun is just not rendered. /1.0 , but /20.0 is fun
                              camera(camX, height/2.0f, camZ, centX, height/2.0f, 0, 0, 1, 0);
                             
  
}



public void createInfoBar(){

                             infoB = new My2dInfo();
                             infoB.setFocusable(false);
               
                              // Adding 2D stuff to the current sketch 
                              infoB.setBounds(0, 0, width, 40); 
                               
                              this.add(infoB); //this adds the embedded sketch to the main sketch 
                              
                               
                              infoB.init();
                              
                              setComponentZOrder(infoB, 0);

}



public void keyPressedAnywhere(boolean parent){
  
                      if (parent==true){//if the key pressed within overal view
                        
                                if (Character.getNumericValue(key)>=0 && Character.getNumericValue(key)<9){
                                       indexOfP = Character.getNumericValue(key);
                                     
                                     
                                         sketch=null;
                                        
                                         deltaCamZ = ((allPlanets[indexOfP].rx / tan(PI*15.0f / 180.0f)) -camZ)/100.0f;//see planet radius at 15 deg
       
                                      
                                         
                                        deltaCamX = (allPlanets[indexOfP].planetPosition +allPlanets[indexOfP].rx*1.8f -camX)/100.0f;//100 steps
                                        deltaCentX = (allPlanets[indexOfP].planetPosition + allPlanets[indexOfP].rx*1.8f-centX)/100.0f;
                                        
                                      //to make sure that if we were flying in space (with 0 adjusted sun), when we press 0-8 keys, tempposition depends only on planets flying away from each other
                                        for (int i=0;i<allPlanets.length;i++){
                                                     allPlanets[i].tempPosition=0;
                                        }
                          
                                        ind=1;
                                  
                                     
                                }
                               else if (key==' '){//if space character                                        
                                        getToSpaceView(); 
                                }
                               else if (key=='l'){
               
                                    lightsOn= !lightsOn;//change lights to opposite
                
                               }
                       
                      }
                      else if (parent==false){  //if key (space) pressed within data view                   
                                 getToSpaceView();
                      }
  
  
  
}


public void getToSpaceView(){
                       camZ=zCamInitial;
                       factor=camZ/( (height/2) / tan(PI*30.0f / 180.0f) );
                        
                       camX=factor*width/2.0f;
                       centX=factor*width/2.0f;
                       
                       camera(camX, height/2.0f, camZ, centX, height/2.0f, 0, 0, 1, 0);
                       
                        for (int i=0;i<allPlanets.length;i++){
                               allPlanets[i].tempPosition=0;
                        }
  
  
}





public void mouseReleased(){//request focus by data window if mouse released (after we flied around)
  if (ind==102 && sketch!=null){
    sketch.requestFocusInWindow();
  }
  
  
}





public void draw() {
  
                           //movement is based on frame rate, which is not good
                           count++;
               
                           background(0);
                   
                          
                 
                  
                           if (mousePressed){
                          
                             //if mouse is pressed, and we are in space view, move all planets to the left, so that they are still close to sun (remove sunAdjust);
                             if ( ind==0){
                                    for (int i=0;i<allPlanets.length;i++){
                                            if (i!=0){
                                                   allPlanets[i].tempPosition=-1*sunAdjust;
                                            }
                                    }
                               
                               
                             }
                             
                             
                             if (indexOfP==-1){
                               camCenX=allPlanets[0].planetPosition;
                               
                             }
                             else{
                               camCenX=allPlanets[indexOfP].planetPosition;
                               
                             }
                               camera(camX+map(mouseX*factor, 0, width*factor, -2*width*factor, 2*width*factor),  height*factor/2+map(mouseY*factor, 0, height*factor, -2*height*factor, 2*height*factor),    height*factor/2/tan(PI*30.0f / 180.0f), //if camera Z is close enough to a planet (without factor), the planet should become invisible with opengl
                               camCenX, height*factor/2.0f, 0, 
                               0, 1, 0);
                             
                           }
                           else{
                                       if (ind>0 && ind<=100){
                                                 camX=camX+deltaCamX;
                                                 camZ+=deltaCamZ;
                                                 centX+=deltaCentX;
                                                 
                                                 factor=camZ/( (height/2) / tan(PI*30.0f / 180.0f) );
                                         
                                                 camera(camX, height/2.0f, camZ, centX, height/2.0f, 0, 0, 1, 0);
                                                
                                                
                                              
                                                //move planets away from each other when zooming
                                                for (int i=0;i<allPlanets.length;i++){
                                                  
                                                  
                                                   if (indexOfP==0 && i!=0){
                                                       allPlanets[i].tempPosition-=sunAdjust/100.0f;
                                                     
                                                   }
                                                   else if ( i<indexOfP){
                                                      allPlanets[i].tempPosition-=1700;
                                                   }
                                                   else if (i>indexOfP) {
                                                      allPlanets[i].tempPosition+=1700;
                                                   }
                                                 
                                                   
                                                  
                                                }
                                                ind++;
                                     
                                         
                                        
                                       }
                                       if (ind==101){//make data appear
                                       
                                          sketch = new My2dStuff();
                                          sketch.setParent(this);  
                                           
                           
                                          // Adding 2D stuff to the current sketch 
                                          sketch.setBounds(width-540, height-320, 420, 300); 
                                           
                                          this.add(sketch); //this adds the embedded sketch to the main sketch 
                                          
                                           
                                          sketch.init();
                                          setComponentZOrder(sketch, 0);
                                           
                                          ind=102;//means we arrived to close planet view
                                            
                                       }
                                           
                                      
                                       
                             
                                       
                             }
                             
                           
                         
                     
                      
                   //DO DRAWING   
                      
               
                          //draw planets 
                         for (int i=0;i<allPlanets.length;i++){
                         
                          
                             if (i==1 && lightsOn==true){
                              directionalLight(255, 255, 255, 1, 0, -0.5f);
                              ambientLight(32, 32, 32);
                            }
                           
                            pushMatrix();
                            translate(allPlanets[i].planetPosition+allPlanets[i].tempPosition,height/2.0f, 0);//to right place
                            
                            rotateZ(radians(axialTilt[i]));//make axial tilt
                            
                            rotateY(radians(earthDegChangePerDraw/dayLength[i]*count));//make spin
                            
                            

                            allPlanets[i].textureSphere();
                    
                           
                          
                           //draw rings
                           rotateX(radians(90));//as rings initially drawn in y plane
                           if (i==6){
                             
                               rS.textureRings();
                               rS2.textureRings();
                               rS3.textureRings();
                               rS4.textureRings();
                             
                           }
                           
                           else if (i==7){
                            
                               rU.textureRings();
                               rU2.textureRings();
                               rU3.textureRings();
                               rU4.textureRings();
                           }
                           else if(i==8){
                               rN.textureRings();
                               rN2.textureRings();
                           }
                           
                           else if (i==5){
                               rJ.textureRings();
                           }
                           
                           popMatrix();
                        
                         }
                         
                         
                         
                             //draw moons
                   if (indexOfP>0 && ind==102){//not general view and not sun
                       for (int i=0;i<allMoons.length;i++){
                          
                         if (i==1 || i==2 || i==5 || i==10){//no lightspheres for Deimos, Phobos, Enceladus, Proteus (too small to see)
                           continue;
                         }
                         
                          pushMatrix();
                          
                          
                          
                          allMoons[i].angle=(allMoons[i].angle+earthDegChangePerDraw/moonYearLength[i])%360;
                         
                          
                          //relation between x and z is given by z^2==(1-eccentricity^2)*(a^2-x^2)
                          allMoons[i].x = cos(radians(allMoons[i].angle))*(allMoons[i].a);
                          allMoons[i].z = sqrt(( pow(allMoons[i].a,2) - pow(allMoons[i].x,2))*(1-pow(orbitEccentricity[i],2)));
                          
                          if (Math.abs(allMoons[i].angle)<180){
                            allMoons[i].z*=-1;
                          }
                        
                          //to correctly rotate moon orbit plane
                          translate(allPlanets[allMoons[i].planetOwner].planetPosition+allPlanets[allMoons[i].planetOwner].tempPosition, height/2.0f,0);
                          rotateZ(radians(orbitInclination[i]));
                          
                          //translate moon to right place on orbit
                          translate( allMoons[i].x    ,height/2.0f, allMoons[i].z);
                          
                          
                          //moon's axial spin relative to Earth (as day length in earth days)
                          rotateY(radians(count*earthDegChangePerDraw/moonDayLength[i]));
                      
                          if (i==0){//specially for moon to make black seas face Earth :) 
                             rotateY(radians(90));
                          }
                          
                          //Moons don't have right axial tilts. this would be too much...
                          
                          
                          allMoons[i].textureSphere(); 
                          
                          popMatrix();
                         
                       }
                   }

}
         

public void keyPressed() {
  if (indexOfP==-1){
  //not the best way to distinguish between key pressed in parent and in child, but for some reason I can't find simpler (without use of additional classes etc) way to trigger key event
  //in parent from child.  
   keyPressedAnywhere(true);
                  
  }
}




//Information Arrays
//position '0' is data for the Sun
float[] axialTilt = {7.25f, 0, 177.36f, 23.4f, 25.19f, 3.13f, 26.73f, 97.77f, 28.32f};

String[] PlanetNames = {  
 "Sun", "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"
};

int[] SunDistance= { //in Million kilometres
  0, 58, 108, 150, 230, 778, 1400, 3000, 4500
};

float[] rSize= { //in Earths radiuses
  108.72f, 0.383f, 0.945f, 1, 0.531f, 11.172f, 9.766f, 3.992f, 3.867f
};



float[] Gravity= { //in g
  27.94f, 0.38f, 0.904f, 1, 0.376f, 2.528f, 1.065f, 0.886f, 1.14f
};

String[] MinTemp= { //in \u00b0C
 "~5505","-173", "380", "-88", "-143", "-145", "-178", "-224", "-200"  
};

String[] MaxTemp= { //in \u00b0C
  "~5505", "427", "462", "58", "35", "-145", "-178", "-224", "-190"
};

float[] dayLength= { //in days. for sun at equator
  24.47f, 58.65f, 243, 1, 1.03f, 0.41f, 0.44f, 0.72f, 0.67f
};



float[] YearLength= { //in years
  0.069f, 0.241f, 0.615f, 1, 1.881f, 11.86f, 29.46f, 84.01f, 164.8f
};  

float[] OrbitalSpeed= { //in km/s
 251, 47.362f, 35.02f, 29.78f, 24.077f, 13.07f, 9.69f, 6.80f, 5.43f
};



//Information Arrays for MOONS
String[] MoonNames = {  
 "Moon", "Phobos", "Deimos", "Europa", "Ganymede", "Enceladus", "Titan", "Titania", "Oberon", "Triton", "Proteus", "io", "callisto"//io, callisto
};

float[] planetDistance= { //in kilometres
  400000, 6000, 23460, 670900, 1070400, 238000, 1221830, 436000, 584000, 354760, 117600 , 421700, 1880000
};

float[] MoonSize= { //in Earths radiuses
  0.273f, 0.00177f, 0.001f, 0.245f, 0.413f, 0.0395f, 0.404f, 0.1235f, 0.1194f, 0.2122f, 0.03296f,0.286f, 0.378f  
};

String[] MoonGravity= { //in g
  "0.1654", "581.4 \u00b5", "306 \u00b5", "0.134", "0.146", "0.0113", "0.14", "0.0387", "0.03547", "0.0794", "0.007", "0.183", "0.126" 
};

String[] MinTempM= { //in \u00b0C
 "\u2212238","-40", "-40", "\u2212223", "\u2212203", "-240", "-180", "-213", "-198", "-235", "-222"  
};

String[] MaxTempM= { //in \u00b0C
 "117", "-40", "-40", "\u2212148", "\u2212121", "-128", "-180", "-184", "-198", "-235", "-222"
};


float[] OrbitalSpeedM= { //in km/s
  1.02f, 2.14f, 1.35f, 13.74f, 10.88f, 12.64f, 5.57f, 3.64f, 3.15f, 4.39f, 7.62f, 17.33f ,8.20f
};

float[] moonYearLength = {27.322f, 0, 0, 3.55f,7.15f, 1.37f, 16,8.7f, 13.5f,-6,0, 1.77f, 16.68f} ;//in earths days

float[] orbitInclination = {5.145f,0 ,0,3.60f, 3.37f, 26.75f, 27, 98, 98.25f, -130,0, 2.21f, 3};//not to planet's equator, but to main ecliptic

float[] moonDayLength = {27.322f, 0 , 0, 3.55f, 7.15f ,1.37f, 16,8.7f, 13.5f, -6,0, 1.77f, 16.68f};

float[] orbitEccentricity={0.0549f,0,0, 0.009f ,0.0013f, 0.0047f, 0.0288f, 0.0011f, 0.0014f ,0 ,0, 0.0041f, 0.0074f};
/*
*/




class My2dStuff extends PApplet implements PConstants 
{ 
  
             PApplet parent;
             float coordY =37;
             float velocity=0;
             float startSpeed=0;
             float floorBot = 292;
             float ballWidth = 20;
             int counter2=-1;
        
             public void setup() 
             { 
                        frameRate(40);
                        
                        displayInfo();
                        noLoop();
                      
                        
                      
                       
             }
             
             public void setParent(PApplet parent){
               this.parent=parent;
               
             }
               

             @Override
             public void init(){
               super.init();
               size(420,300);
              //background(1,20,112);
             }
             
            public void keyPressed() {
              if (key==' '){
                   noLoop();
                  parent.remove(this);
                  parent.requestFocusInWindow();
                  
                   indexOfP=-1;
                   ind=0;
                   keyPressedAnywhere(false);
              }
              else if (key=='d'){
                
               loop(); 
                
              }
              else if (key=='l'){
               
                lightsOn= !lightsOn;//change lights to opposite
                
              }
            } 
                 
                 
           public void draw(){
             counter2++;
             displayInfo();
             if (counter2>0){
                       velocity=startSpeed+counter2/2.0f*Gravity[indexOfP];
                       
                       
                    
                       
                       if (coordY+velocity>floorBot-ballWidth/2.0f){
                           startSpeed=-1*Math.abs(velocity*0.8f);//0.8 balls elasticity
                       
                           counter2=0;
                           coordY=floorBot-ballWidth/2.0f;
                           
                           if (Math.abs(startSpeed)<0.3f){
                            noLoop(); 
                           }
                         
                       }
                       else{
                         
                            coordY+=velocity;
                         
                       }
             
             }
             fill(255,0,0);
             ellipse(380, coordY, ballWidth,ballWidth);
            
            
           }      
                 
                 
                 
                 
                 
           
                 
                 
   
                 
            
          
                 
       public void displayInfo(){
         
         

 
    
        int x=10;
        int y=70;


 
  

    
    //image for information box
    image(imgBack, 0, 0, width, height);//??
    
    fill(255,255,255); 
   textFont(Ocrb, 20);
    text(PlanetNames[indexOfP]            ,x, y-40);
    
    //textSize(13);
  textFont(SansSerif, 13);
    if (indexOfP!=0)
    {
      text("Distance from Sun: " + SunDistance[indexOfP]+" million km"      ,x, y+15); 
    }
    else if (indexOfP==0)
    {
      text("Distance from Sun: " + SunDistance[indexOfP]     ,x, y+15);
    }
    float volume = ((int)((rSize[indexOfP]*rSize[indexOfP]*rSize[indexOfP]*4/3*PI)*100))/100.0f;
    text("Size: " + volume + " Earths"       ,x, y+30);
    text("Gravity: " + Gravity[indexOfP]   +  " g. Press d to drop ball"           ,x, y+45);
  
    if (indexOfP==0 || indexOfP==5 || indexOfP==6 || indexOfP==7)
    {
      text("Average Temperature: " +MinTemp[indexOfP] +"\u00b0C" ,x, y+60);
    }
    else
    {
      text("Temperature: Minimum: " +MinTemp[indexOfP]+ "\u00b0C    Maximum: " +MaxTemp[indexOfP]+"\u00b0C" ,x, y+60);
    }
    text("Day Length: " +dayLength[indexOfP]+ " days",    x, y+75);
    text("Year Length: " +YearLength[indexOfP]+ " years",  x, y+90);
    text("Orbital Speed: " +OrbitalSpeed[indexOfP]+ " km/s", x, y+105);
    

    
    //information for moons (if planet has moons)
    if (indexOfP==3 || indexOfP==4 || indexOfP==5  || indexOfP==6 || indexOfP==7 || indexOfP==8)
    {
      textSize(12);
      int x41=x+140;
      int x42=x+250;
      int tempFlag=0;
      int k=-1;
      
      //determines whether the moons of a planet have a max/min or average temperature
      if (indexOfP==4 || indexOfP==8)
      {
         tempFlag=1; //average temperature
      }
      if (indexOfP==6 || indexOfP==7)
      {
         tempFlag=2; 
      }
      
      if (indexOfP==3)
      { k=0; }
      if (indexOfP==4)
      { k=1; }
      if (indexOfP==5)
      { k=3; }
      if (indexOfP==6)
      { k=5; }
      if (indexOfP==7)
      { k=7; }
      if (indexOfP==8)
      { k=9; }
      
      
      if (indexOfP==3)
      {
     textFont(Ocrb, 14);
        text(MoonNames[k], x, y+125);
     textFont(SansSerif, 13);
        text("Distance From " +PlanetNames[indexOfP]+ ": " +planetDistance[k]+ " km",    x, y+137);
        text("Size: " +MoonSize[k] + " Earth",    x, y+149);
        text("Gravity: " +MoonGravity[k] + " g",   x, y+161);
        text("Temperature: Minimum: " +MinTempM[k]+"\u00b0C"+  " Maximum: " +MaxTempM[k]+"\u00b0C",    x, y+173);
        text("Day Length: 27.32 days",    x, y+185);
        text("Year Length: 0.0748 year",    x, y+197);
        text("Orbital Speed:" +OrbitalSpeedM[k]+ " km/s",    x, y+209);
      }
      
      
      
      
      
      else if (indexOfP==5){
        text("Moons: " ,x, y+125);
        textFont(Ocrb, 14);
        text(MoonNames[11],70,y+125);
        text(MoonNames[3],160,y+125);
        text(MoonNames[4],250,y+125);
        text(MoonNames[12],330,y+125);
        textFont(SansSerif, 13);
        
        text("Orbit rad.: ",x, y+137);
        text(planetDistance[11]+" km",70,y+137);
        text(planetDistance[3]+ " km",160,y+137);
        text(planetDistance[4]+"",250,y+137);
        text(planetDistance[12]+ " km",330,y+137);
       
        text("Size: ",x, y+149);
        text(MoonSize[11] + "Earth r.",70,y+149);
        text(MoonSize[3] +"Earth r.",160,y+149);
        text(MoonSize[4] + "Earth r.",250,y+149);
        text(MoonSize[12] +"Earth r.",330,y+149);
      
        text("Gravity: ",x, y+161);
        text(MoonGravity[11] + "g",70,y+161);
        text(MoonGravity[3]+"g",160,y+161);
        text(MoonGravity[4] + "g",250,y+161);
        text(MoonGravity[12]+"g",330,y+161);
      
      
        text("Orb.Sp.: ",x, y+197);
        text(OrbitalSpeedM[11] + " km/s",70,y+197);
        text(OrbitalSpeedM[3] + " km/s",160,y+197);
        text(OrbitalSpeedM[4] + " km/s",250,y+197);
        text(OrbitalSpeedM[12] + " km/s",330,y+197);
      
      
      }
      
      
      
      else
      {
      text("Moons: " ,x, y+125);
      textFont(Ocrb, 14);
      text(MoonNames[k],x41,y+125);
      text(MoonNames[k+1],x42,y+125);
      textFont(SansSerif, 13);
      
      
      text("Distance From " +PlanetNames[indexOfP]+ ": ",x, y+137);
      text(planetDistance[k]+" km",x41,y+137);
      text(planetDistance[k+1]+ " km",x42,y+137);
      
      
      text("Size: ",x, y+149);
      text(MoonSize[k] + "Earth r.",x41,y+149);
      text(MoonSize[k+1] +"Earth r.",x42,y+149);
      
      text("Gravity: ",x, y+161);
      text(MoonGravity[k] + "g",x41,y+161);
      text(MoonGravity[k+1]+"g",x42,y+161);
      
      
      }
      
      
      
      
      
      if (tempFlag==1)
      {
         text("Temperature: ",x, y+173);
         text(MaxTempM[k]+"\u00b0C",x41,y+173);
         text(MaxTempM[k+1]+"\u00b0C",x42,y+173);
        
        text("Orbital Speed: ",x, y+185);
        text(OrbitalSpeedM[k] + " km/s",x41,y+185);
        text(OrbitalSpeedM[k+1] + " km/s",x42,y+185); 
      }
      else if(tempFlag==2)
      {
        text("Temperature: ",x, y+173);
        text("Min: ",x+80,y+173);
        text(MinTempM[k]+"\u00b0C",x+110,y+173);
        //text(MinTempM[k+1]+"\u00b0C",x42,y+173);
        text("Max: ",x+160,y+173);
        text(MaxTempM[k]+"\u00b0C",x+190,y+173);
        
        text("Average: ",x+80,y+185);
        text(MaxTempM[k+1]+"\u00b0C",x42,y+185); 
       
        text("Orbital Speed: ",x, y+197);
        text(OrbitalSpeedM[k] + " km/s",x41,y+197);
        text(OrbitalSpeedM[k+1] + " km/s",x42,y+197);   
      }
  
      
    }
    

 
         
         

         
         
       }
    
             

    
} 
 
class Rings{
  
  
  
 float[] xs ;
 float[] ys ;
 PImage img;
 private int pts=30;
 float endR, startR;
 boolean useTexture;
 
 
 public Rings(float startR, float endR, String rTexture, boolean useTexture){
        this.useTexture=useTexture;
        this.endR=endR;
        this.startR = startR;
       
        if (useTexture){
           img=loadImage(rTexture); 
        }
    
  
        xs = new float[pts+1];
        ys = new float[pts+1];
    
       float angle = 2*PI/pts;
 
       for (int i=0;i<pts+1;i++){
    
           xs[i] = cos(i*angle);
          ys[i] = sin(i*angle);
    
      }
  
  }
  
  
  
  
  
 public void textureRings(){
    
 
    noStroke();
    beginShape(TRIANGLE_STRIP);
    //saturn rings have texture
     if (useTexture){  
       
             float changeV=img.height/(float)(pts-1); 
             float v=0;  // Height variable for the texture
             
            texture(img);
            for (int i=0;i<pts+1;i++){
              
               vertex(xs[i]*startR, ys[i]*startR, 0, 0,v);
               vertex(xs[i]*endR, ys[i]*endR,0,img.width,v); 
             
             
               v+=changeV; 
            }
     }
     //other rings just filled shape
     else{
           fill(125,125,125,125);
           for (int i=0;i<pts+1;i++){
                  
                   vertex(xs[i]*startR, ys[i]*startR, 0);
                   vertex(xs[i]*endR, ys[i]*endR,0);
           }
      
       
     }
            
     endShape();
    
  
    
    
  }
  
  
  
 
 
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}
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
  
public void initializeSphere(int numPtsW, int numPtsH_2pi) {

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
    if (PApplet.parseInt(numPointsH_2pi/2) != (float)numPointsH_2pi/2 && i==numPointsH-1) {  // If the numPointsH_2pi is odd and it is at the last pt. Simply get old coorY and radius of horiz circle is 0.
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


public void textureSphere() { 
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

class My2dInfo extends PApplet implements PConstants 
{ 
  
            
           
        
             public void setup() 
             { 
                    
                       background(0);
                       fill(255);
                       textSize(14);
                       text("Use 1-8 for planets, 0 for Sun. Press Space (what else?) to get back into Space view. Use mouse to fly around. Planets and distances to scale (NOT planets to distances) ", 10,20);
                     
                       
             }
          
         

          
             
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#D4D0C8", "sketch_3dplanets" });
  }
}
