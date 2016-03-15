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
