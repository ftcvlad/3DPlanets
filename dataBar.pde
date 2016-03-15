import java.awt.Color;


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
             
            void keyPressed() {
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
                 
                 
           void draw(){
             counter2++;
             displayInfo();
             if (counter2>0){
                       velocity=startSpeed+counter2/2.0*Gravity[indexOfP];
                       
                       
                    
                       
                       if (coordY+velocity>floorBot-ballWidth/2.0){
                           startSpeed=-1*Math.abs(velocity*0.8);//0.8 balls elasticity
                       
                           counter2=0;
                           coordY=floorBot-ballWidth/2.0;
                           
                           if (Math.abs(startSpeed)<0.3){
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
                 
                 
                 
                 
                 
           
                 
                 
   
                 
            
          
                 
       void displayInfo(){
         
         

 
    
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
    float volume = ((int)((rSize[indexOfP]*rSize[indexOfP]*rSize[indexOfP]*4/3*PI)*100))/100.0;
    text("Size: " + volume + " Earths"       ,x, y+30);
    text("Gravity: " + Gravity[indexOfP]   +  " g. Press d to drop ball"           ,x, y+45);
  
    if (indexOfP==0 || indexOfP==5 || indexOfP==6 || indexOfP==7)
    {
      text("Average Temperature: " +MinTemp[indexOfP] +"°C" ,x, y+60);
    }
    else
    {
      text("Temperature: Minimum: " +MinTemp[indexOfP]+ "°C    Maximum: " +MaxTemp[indexOfP]+"°C" ,x, y+60);
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
        text("Temperature: Minimum: " +MinTempM[k]+"°C"+  " Maximum: " +MaxTempM[k]+"°C",    x, y+173);
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
         text(MaxTempM[k]+"°C",x41,y+173);
         text(MaxTempM[k+1]+"°C",x42,y+173);
        
        text("Orbital Speed: ",x, y+185);
        text(OrbitalSpeedM[k] + " km/s",x41,y+185);
        text(OrbitalSpeedM[k+1] + " km/s",x42,y+185); 
      }
      else if(tempFlag==2)
      {
        text("Temperature: ",x, y+173);
        text("Min: ",x+80,y+173);
        text(MinTempM[k]+"°C",x+110,y+173);
        //text(MinTempM[k+1]+"°C",x42,y+173);
        text("Max: ",x+160,y+173);
        text(MaxTempM[k]+"°C",x+190,y+173);
        
        text("Average: ",x+80,y+185);
        text(MaxTempM[k+1]+"°C",x42,y+185); 
       
        text("Orbital Speed: ",x, y+197);
        text(OrbitalSpeedM[k] + " km/s",x41,y+197);
        text(OrbitalSpeedM[k+1] + " km/s",x42,y+197);   
      }
  
      
    }
    

 
         
         

         
         
       }
    
             

    
} 
 
