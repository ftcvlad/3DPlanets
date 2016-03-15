
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
float earthDegChangePerDraw = 360/FPS/16.0;//8.0 -- kind of time for 1 Earth rotation in seconds. But actual is dfferent, as my comp slow :). Other planets relative to Earth

int radScale=1000;
int moonDistanceScale=30;//divide by this real moon distance

float zCamInitial = 160000;//can easily increase distances between planets, as just planets look smaller
 //(this is not limited, but planets size range is limited, see further)
 
 float sunAdjust=0;
 float planetPosition=0;
 float factor=0;
 public int indexOfP=-1;//currently selected planet
 
 
void setup() {
                              size(1200, 700, P3D);
                              frameRate(FPS);
                              imgBack = loadImage("background.png");//for some strange reason image doesn't load from embedded sketch...
                              Ocrb = loadFont("OCRB-48.vlw");
                              SansSerif = loadFont("SansSerif.plain-48.vlw");
                              createInfoBar();
                              factor=zCamInitial/( (height/2) / tan(PI*30.0 / 180.0) );//real screen width at scale z==zCamInitial
                         
                              
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
                                           planetPosition-=rSize[8]*radScale*1.6;
                                         }
                                             
                                        allPlanets[i]= new Sphere("textures/"+PlanetNames[i]+".jpg", rSize[i]*radScale,rSize[i]*radScale,rSize[i]*radScale, planetPosition, -1, -1);
                                
                                
                              }
                              
                               //create rings
                               
                               //10851 based on real rings, 22679 as well real (in relation to radius). For Saturn
                               rS = new Rings(10.851*radScale, 12.000*radScale, "rings/closeRing.JPG", true);
                               rS2 = new Rings(12.000*radScale, 13.000*radScale, "rings/rings2.bmp", true);
                               rS3 = new Rings(13.200*radScale, 18.500*radScale, "rings/rings3.bmp", true);
                               rS4 = new Rings(19.000*radScale, 22.679*radScale, "rings/rings2.bmp", true);
                               
                               
                               //for Uranus 5918 - 16087
                               rU = new Rings(5.918*radScale, 6.300*radScale, "zz", false);
                               rU2 = new Rings(8.000*radScale, 8.020*radScale, "zzz", false);
                               rU3 = new Rings(10.500*radScale, 11.000*radScale, "zzz", false);
                               rU4 = new Rings(13.760*radScale, 15.087*radScale, "zzz", false);
                             
                             
                               //for Neptune...
                               rN = new Rings(6.287*radScale, 6.600*radScale, "zz", false);
                               rN2 = new Rings(8.539*radScale, 9.200*radScale, "zzz", false);
                             
                               //and Jupiter..
                               rJ  = new Rings(19.753*radScale, 20.8*radScale, "zz", false);
                              
                              
                              
                              //create moons
                              radScale*=2;//break scale, enlarge moons (?)
                              
                              allMoons[0] = new Sphere("moons/luna.jpg",0.27*radScale, 0.27*radScale, 0.27*radScale, allPlanets[3].planetPosition+planetDistance[0]/moonDistanceScale, 3, planetDistance[0] );//planetDistance[0]/moonDistanceScale is major axis (conic sections)
                              allMoons[3] = new Sphere("moons/europa.jpg",0.245*radScale, 0.245*radScale, 0.245*radScale, allPlanets[5].planetPosition+planetDistance[3]/moonDistanceScale, 5, planetDistance[3] );
                              allMoons[4] = new Sphere("moons/ganymede.jpg",0.413*radScale, 0.413*radScale, 0.413*radScale, allPlanets[5].planetPosition+planetDistance[4]/moonDistanceScale, 5, planetDistance[4] );
                             
                             
                              allMoons[6] = new Sphere("moons/titan.jpg",0.404*radScale, 0.404*radScale, 0.404*radScale, allPlanets[6].planetPosition+planetDistance[6]/moonDistanceScale, 6, planetDistance[6] );
                             
                             
                              allMoons[7] = new Sphere("moons/titania.jpg",0.1235*radScale, 0.1235*radScale, 0.1235*radScale, allPlanets[7].planetPosition+planetDistance[7]/moonDistanceScale, 7, planetDistance[7] );
                              allMoons[8] = new Sphere("moons/oberon.jpg",0.1194*radScale, 0.1194*radScale, 0.1194*radScale, allPlanets[7].planetPosition+planetDistance[8]/moonDistanceScale, 7, planetDistance[8] );
                            
                              allMoons[9] = new Sphere("moons/triton.jpg",0.2122*radScale, 0.2122*radScale, 0.2122*radScale, allPlanets[8].planetPosition+planetDistance[9]/moonDistanceScale, 8, planetDistance[9] );
                             
                              allMoons[11] = new Sphere("moons/io.jpg",0.286*radScale, 0.286*radScale, 0.286*radScale, allPlanets[5].planetPosition+planetDistance[11]/moonDistanceScale, 5, planetDistance[11] );
                              allMoons[12] = new Sphere("moons/callisto.jpg",0.378*radScale, 0.378*radScale, 0.378*radScale, allPlanets[5].planetPosition+planetDistance[12]/moonDistanceScale, 5, planetDistance[12] );
                             
                              
                              
                              
                              
                                                          
                             //set initial camera and frustum
                              camX=factor*width/2.0;
                              centX=factor*width/2.0;
                              
                              camZ=zCamInitial;
                              frustum(-width/2.0, width/2.0,-height/2.0 ,height/2.0, (height/2) / tan(PI*30.0 / 180.0)/1.0 ,camZ*30);//end plane doesn't affect look if it's not too small. If endPlane==camZ, sun is just not rendered. /1.0 , but /20.0 is fun
                              camera(camX, height/2.0, camZ, centX, height/2.0, 0, 0, 1, 0);
                             
  
}



void createInfoBar(){

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
                                        
                                         deltaCamZ = ((allPlanets[indexOfP].rx / tan(PI*15.0 / 180.0)) -camZ)/100.0;//see planet radius at 15 deg
       
                                      
                                         
                                        deltaCamX = (allPlanets[indexOfP].planetPosition +allPlanets[indexOfP].rx*1.8 -camX)/100.0;//100 steps
                                        deltaCentX = (allPlanets[indexOfP].planetPosition + allPlanets[indexOfP].rx*1.8-centX)/100.0;
                                        
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


void getToSpaceView(){
                       camZ=zCamInitial;
                       factor=camZ/( (height/2) / tan(PI*30.0 / 180.0) );
                        
                       camX=factor*width/2.0;
                       centX=factor*width/2.0;
                       
                       camera(camX, height/2.0, camZ, centX, height/2.0, 0, 0, 1, 0);
                       
                        for (int i=0;i<allPlanets.length;i++){
                               allPlanets[i].tempPosition=0;
                        }
  
  
}





void mouseReleased(){//request focus by data window if mouse released (after we flied around)
  if (ind==102 && sketch!=null){
    sketch.requestFocusInWindow();
  }
  
  
}





void draw() {
  
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
                               camera(camX+map(mouseX*factor, 0, width*factor, -2*width*factor, 2*width*factor),  height*factor/2+map(mouseY*factor, 0, height*factor, -2*height*factor, 2*height*factor),    height*factor/2/tan(PI*30.0 / 180.0), //if camera Z is close enough to a planet (without factor), the planet should become invisible with opengl
                               camCenX, height*factor/2.0, 0, 
                               0, 1, 0);
                             
                           }
                           else{
                                       if (ind>0 && ind<=100){
                                                 camX=camX+deltaCamX;
                                                 camZ+=deltaCamZ;
                                                 centX+=deltaCentX;
                                                 
                                                 factor=camZ/( (height/2) / tan(PI*30.0 / 180.0) );
                                         
                                                 camera(camX, height/2.0, camZ, centX, height/2.0, 0, 0, 1, 0);
                                                
                                                
                                              
                                                //move planets away from each other when zooming
                                                for (int i=0;i<allPlanets.length;i++){
                                                  
                                                  
                                                   if (indexOfP==0 && i!=0){
                                                       allPlanets[i].tempPosition-=sunAdjust/100.0;
                                                     
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
                              directionalLight(255, 255, 255, 1, 0, -0.5);
                              ambientLight(32, 32, 32);
                            }
                           
                            pushMatrix();
                            translate(allPlanets[i].planetPosition+allPlanets[i].tempPosition,height/2.0, 0);//to right place
                            
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
                          translate(allPlanets[allMoons[i].planetOwner].planetPosition+allPlanets[allMoons[i].planetOwner].tempPosition, height/2.0,0);
                          rotateZ(radians(orbitInclination[i]));
                          
                          //translate moon to right place on orbit
                          translate( allMoons[i].x    ,height/2.0, allMoons[i].z);
                          
                          
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
         

void keyPressed() {
  if (indexOfP==-1){
  //not the best way to distinguish between key pressed in parent and in child, but for some reason I can't find simpler (without use of additional classes etc) way to trigger key event
  //in parent from child.  
   keyPressedAnywhere(true);
                  
  }
}



