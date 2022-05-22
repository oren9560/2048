import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch_2048 extends PApplet {

/*
TODO:
  add animations - NOT POSSIBLE IN PROCESSING

Colors:
  Table color / score / best - #bbada0
  background - #faf8ef
  text - #dbd3cd
*/


PImage gridBG;
PImage title;
PImage[] tiles = new PImage[12];      //Array of images containing all the possible tiles in the game - each with an appropriate name (i.e 8.png will be in index 3 because 2^3=8)
PFont f;                              //The font used in the game
Value[][] grid = new Value[4][4];     //The actual grid of the game

int genCap;                           //Contains the current gencap - which tiles can spawn at this stage of the game (more in the README file)

int score;                            //Keeps track of the user's score

int newX, newY;                             //Contains the place in the grid array of the newly generated tiles

boolean canMove;                      //Used to allow user movement only when the backend calculations are done
boolean lose;                         //True if there are no more possible moves left
boolean win;                          //True if there's a 2048 tile on the grid

//Runs once on application launch
public void setup(){
  
                                                       //Size of the canvas (window-size)
   background(0xfffaf8ef);                                              //BG color
   
   frameRate(20);                                                    //NO ANIMATIONS YET SO THIS SLOWS THE FRAMERATE DOWN
   
   gridBG = loadImage("grid.png");                                   //Contains the img of the empty grid (for background)
   title = loadImage("2048Title.png");                               //Contains the img of the title of the game
   
   //Assigns the aformentioned tile images to their appropriate spots in the array (i.e 8.png will be in index 3 because 2^3=8)
   for(int i=0;i<12; i++){
      if(i == 0) tiles[i] = loadImage("empty.png"); 
      else       tiles[i] = loadImage((int)Math.pow(2, i) + ".png");
   }
   
   //Assigning the font that we'll be using
   f = createFont("fontB.ttf", 18);
   textFont(f);
   
   //Filling the grid with empty tiles
   for(int i=0; i<4; i++)
     for(int j=0; j<4; j++){
       PVector place = new PVector(120 + 145*j, 190 + 145*i);        //Refers to the tile's physical location on the canvas
       PVector arrayP = new PVector(j, i);                           //Refers to the tile's location in the grid array using indexes
       grid[i][j] = new Value(arrayP, place, tiles);                 //Creating & placing an empty tile in the grid
     }
     
   genCap = 4;                                                       //Update the initial gencap to be 4
   
   canMove = true;
   
   score = 0;
   
   lose = false;
   win = false;
   
   //Generates 2 random tiles on the grid
   generate();
   generate();

}

//Runs once every frame
public void draw(){
  
  //Draws the background text and images of the game
  bgText();
  
  //Updates the locations and images of the tiles in the grid
  for(int i=0; i<4; i++)
     for(int j=0; j<4; j++){
       grid[i][j].show();
     }
     
  //Draw a green rectangle next to the newly generated tile   
  fill(0xff00FF00);
  rect(newX * 145 + 120, newY * 145 + 190, 15, 15);
  
  if(lose || win){
    fill(220);
    rect(160, 337, 450, 140);
    textFont(f, 120);
    if(lose){
      fill(255,0,0);
      text("DEFEAT", 171, 437); 
    }
    else{
      fill(0,255,0);
      text("VICTORY", 171, 437);
    }
    textSize(18);
    fill(255,255,255);
    text("PRESS SPACE TO RESTART", 270, 460); 
  }

}

//Executes a move function based on user input
public void keyPressed(){
  if(canMove && !lose && !win){
    if(keyCode == UP) move("UP");
    if(keyCode == DOWN) move("DOWN");
    if(keyCode == LEFT) move("LEFT");
    if(keyCode == RIGHT) move("RIGHT");
  }
    
    //Resets the game if space is pressed
    if(key == ' ') setup();
}

public void keyReleased(){
    
    canMove = true;                    //Once the pressed key is released, the user can make his next move (to prevent spamming)
  
}

//Moves ALL the tiles in the grid to the chosen direction and generates a new tile on the grid
public void move(String dir){
  
    canMove = false;    //Disables user movement until all backend calculations are done
  
    if(dir == "UP"){
      int space;
      for(int i=1; i<4; i++)
       for(int j=0; j<4; j++)
         if(grid[i][j].getImg() != tiles[0]){
           space = grid[i][j].checkSpace(grid, dir);
           grid[i][j].move(grid, dir, space);
         }

      generate();
    }
    
    if(dir == "DOWN"){
      int space;
      for(int i=2; i>-1; i--)
       for(int j=0; j<4; j++)
         if(grid[i][j].getImg() != tiles[0]){
           space = grid[i][j].checkSpace(grid, dir);
           grid[i][j].move(grid, dir, space);
         }

      generate();
    }
    
    if(dir == "LEFT"){
      int space;
      for(int j=1; j<4; j++)
       for(int i=0; i<4; i++)
         if(grid[i][j].getImg() != tiles[0]){
           space = grid[i][j].checkSpace(grid, dir);
           grid[i][j].move(grid, dir, space);
         }
            
      generate();
    }
    
    if(dir == "RIGHT"){
      int space;
      for(int j=2; j>-1; j--)
       for(int i=0; i<4; i++)
         if(grid[i][j].getImg() != tiles[0]){
           space = grid[i][j].checkSpace(grid, dir);
           grid[i][j].move(grid, dir, space);
         }

       generate();
    }
    
    //Resets the changed status of the tiles to false after all movement has ceased
    for(int i=0; i<4; i++)
       for(int j=0; j<4; j++)
         grid[i][j].setChanged(false);
    
}

//Updates the score of the user to be the sum of all the tiles on the grid
public void updateScore(){
    for(int i=0; i<4; i++)
       for(int j=0; j<4; j++){
         if(!grid[i][j].isEmpty())
           score += pow(2, grid[i][j].getImgP());
       }
}

//Checks if there are no more possible moves
public void checkLose(){
    if(checkIfLose(grid))  //If the user lost display a message accordingly
      lose = true;
    else
      lose = false;
      
    println(" " + lose);
}

//Checks if there are any possible moves for the user
public boolean checkIfThereArePossibleMoves(Value[][] grid, Value tile){
   
   int tileImgP = tile.getImgP();
   int x = PApplet.parseInt(tile.getArrayP().x);            //Contains the X of the given tile in the grid array
   int y = PApplet.parseInt(tile.getArrayP().y);            //Contains the Y of the given tile in the grid array
   int i, j;                                   //Refers to the neighboring indexes
   
   //Checks UP neighbor
   j = x + 0;
   i = y + -1;
   if(j > -1 && j < 4 && i > -1 && i < 4)
      if(tileImgP == grid[i][j].getImgP()) {
        print("the move is from: " + x + "," + y + " to: " + j + "," + i);
        return true;
      }
      
   //Checks DOWN neighbor
   j = x + 0;
   i = y + 1;
   if(j > -1 && j < 4 && i > -1 && i < 4)
      if(tileImgP == grid[i][j].getImgP()){
        print("the move is from: " + x + "," + y + " to: " + j + "," + i);
        return true;
      }
      
   //Checks LEFT neighbor
   j = x + -1;
   i = y + 0;
   if(j > -1 && j < 4 && i > -1 && i < 4)
      if(tileImgP == grid[i][j].getImgP()) {
        print("the move is from: " + x + "," + y + " to: " + j + "," + i);
        return true;
      }
      
   //Checks RIGHT neighbor
   j = x + 1;
   i = y + 0;
   if(j > -1 && j < 4 && i > -1 && i < 4)
      if(tileImgP == grid[i][j].getImgP()) {
        print("the move is from: " + x + "," + y + " to: " + j + "," + i);
        return true;
      }
      
   return false;
}

//Checks if the user lost the game
public boolean checkIfLose(Value[][] grid){
  for(int i=0; i<4; i++)
     for(int j=0; j<4; j++){
       if(checkIfThereArePossibleMoves(grid, grid[i][j])) return false;
     }
  return true;
}

//Checks if the grid is full
public boolean checkIfFull(Value[][] grid){
    for(int i=0; i<4; i++)
     for(int j=0; j<4; j++){
       if(grid[i][j].isEmpty()) return false;
     }
  return true;
}

//Generates a tile in an empty spot on the grid. The kind of tile is based on chance
public void generate(){
    int x = -1, y = -1;
    
    //Finding a random empty spot in the grid
    while (x<0 && y<0){
      x = floor(random(4));
      y = floor(random(4));
      if(grid[y][x].getImg() != tiles[0]){
        x = -1;
        y = -1;
      }
    }
    
    //Assigning that spot with a random tile based on chance
    float chance = random(20);
    int num = chances(chance);
    
    grid[y][x].setImgP(num);
    
    //Update the location of the newly generated tiles
    newX = x;
    newY = y;
    
    //Adds the new tile's num to the score
    score += pow(2, num);
    
    //If the board is full, this checks if there are no more possible moves
    if(checkIfFull(grid))
      checkLose();
}


//Returns an index refference to a tile to be generated in an empty spot on the grid based on chance & gencap
public int chances(float chance){
   if(chance>=5.0f) return 1;
   else{
      if(chance<0.025f && genCap >= 1024) return 10;
      else{
         if(chance<0.1f && genCap >= 512) return 9;
         else{
            if(chance<0.5f && genCap >= 256) return 8;
            else{
               if(chance<0.75f && genCap >= 128) return 7;
               else{
                  if(chance<1 && genCap >= 64) return 6;
                  else{
                     if(chance<1.75f && genCap >= 32) return 5;
                     else{
                        if(chance<2.5f && genCap >= 16) return 4;
                        else{
                           if(chance<3 && genCap >= 8) return 3;
                           else if(chance<5 && genCap >= 4) return 2;
                        }
                     }
                  }
               }
            }
         }
      }
   }
   return -1;
}

//Returns the exponent of the current gencap
public int exponent(int genCap){
  int ex = 0;
  while(genCap>=2){
   genCap = genCap/2;
   ex++;
  }
  return ex;
}

//Draws the background images and text of the game
public void bgText(){
    background(0xfffaf8ef);
    fill(0xffbbada0);
    //image(gridBG, 91, 165);
    rect(100, 170, 600, 600, 30);
    rect(500, 50, 200, 65, 15);
    image(title, 100, 40);
    fill(0xffdbd3cd);
    text("SCORE", 570, 75);
    text(score, 580, 100);
}
class Value{
 
  PVector arrayP;          //Contains the place of the tile in the grid array
  PVector place;           //Contains the place of the tile in the actual game window
  PImage[] images;         //Array of images containing all the possible tiles in the game - each with an appropriate name (i.e 8.png will be in index 3 because 2^3=8)
  int imgP;            //Contains the index of the tile's img in the tile images array
  PImage img;              //Contains the current img of the tile
  boolean changed;         //True if the tile has been changed in this movement already (to prevent multiple collisions of the same tile in the same move)
  
  //Default constructor - Makes an empty tile
  public Value(PVector arrayP, PVector place, PImage[] images){
      this. arrayP = arrayP;
      this.place = place;
      this.images = images;
      this.img = images[0];
      this.changed = false;
      this.imgP = 0;
  }
  
  //Deaws the tile in the game window
  public void show(){
      image(this.img, this.place.x, this.place.y);
      this.img.resize(120,120);
  }
  
  //Sets the place of the tile in the game window
  public void setPlace(PVector place){
     this.place = place; 
  }
  
  //Sets the place of the tile in grid array
  public void setArrayP(PVector arrayP){
     this.arrayP = arrayP; 
  }
  
  //Checks if the tile is empty
  public boolean isEmpty(){
     if(this.imgP == 0) return true;
     return false;
  }
  
  //Returns the current img of the tile
  public PImage getImg(){
    return this.img; 
  }
  
  //Returns the current location of the tile in the game window
  public PVector getPlace(){
    return this.place; 
  }
  
  //Returns the current location of the tile in the grid array
  public PVector getArrayP(){
    return this.arrayP; 
  }
  
  //Sets the tile's img to a given img
  public void setImgP(int imgP){
    this.imgP = imgP;
    this.img = this.images[imgP];
  }
  
  //Returns the index of the tile's current img in the images array
  public int getImgP(){
     return this.imgP; 
  }
  
  //Sets the changed status of the tile
  public void setChanged(boolean a){
     this.changed = a; 
  }
  
  //Returns the current changed status of a tile
  public boolean changed(){
     return this.changed; 
  }
  
  //Move a given tile to it's destination in the grid array and merge it with the tile in the destination
  public void collide(Value tile, String dir, Value[][] grid){
      
      //Storing the original location on the game window of the tile that's moving in the vector V
      int x1 = (int)this.place.x;
      int y1 = (int)this.place.y;
      PVector v = new PVector(x1,y1);
      
      //Setting the speed in which the movement animation will execute
      float xspeed = .5f, yspeed = .5f;
      
      //Storing the location on the game window of the destination tile in X & Y
      int x = (int)tile.getPlace().x;
      int y = (int)tile.getPlace().y;
      
      //Setting the status of the destination tile to changed (since it's being merged with another tile)
      int j = (int)tile.getArrayP().x;
      int i = (int)tile.getArrayP().y;
      grid[i][j].setChanged(true);
      
      switch(dir){
       case "UP":
         //Moving the tile on the screen to it's destination
         while(this.place.y != y){
          if(this.place.y > y) this.place.y -= yspeed;
          else this.place.y += yspeed;
         }
         
        //Updating the destination tile's img 
        grid[i][j].setImgP(this.getImgP() + 1);
        
        //Setting the tile that has been moved to be empty
        this.setImgP(0);
        
        //Reseting the moved tile's location to it's original spot
        this.setPlace(v);
        break;
        
       case "DOWN":
         while(this.place.y != y){
          if(this.place.y > y) this.place.y -= yspeed;
          else this.place.y += yspeed;
         }
        grid[i][j].setImgP(this.getImgP() + 1);
        this.setImgP(0);
        this.setPlace(v);
        break;
        
       case "LEFT":
         while(this.place.x != x){
          if(this.place.x > x) this.place.x -= xspeed;
          else this.place.x += xspeed;
         }
        grid[i][j].setImgP(this.getImgP() + 1);
        this.setImgP(0);
        this.setPlace(v);
        break;
        
       case "RIGHT":
         while(this.place.x != x){
          if(this.place.x > x) this.place.x -= xspeed;
          else this.place.x += xspeed;
         }
        grid[i][j].setImgP(this.getImgP() + 1);
        this.setImgP(0);
        this.setPlace(v);
        break;
      }
      
      //If the newly minted tile's number is bigger than the current gencap, update the gencap to the number
      if(pow(2, grid[i][j].getImgP())> genCap)
          genCap = (int)(pow(2, grid[i][j].getImgP()));
          
      if(genCap == 2048)
          win = true;

  }
  
  //Move a given tile to it's destination in the grid array by transfering it to the empty tile - the destination is empty
  public void takeEmpty(Value tile, String dir, Value[][] grid){
    
      //Storing the original location on the game window of the tile that's moving in the vector V
      int x1 = (int)this.place.x;
      int y1 = (int)this.place.y;
      PVector v = new PVector(x1,y1);
      
      //Setting the speed in which the movement animation will execute 
      float xspeed = .5f, yspeed = .5f;
      
      //Storing the location on the game window of the destination tile in X & Y
      int x = (int)tile.getPlace().x;
      int y = (int)tile.getPlace().y; 
      
      //Storing the location of the destination tile in the grid array in J & I
      int j = (int)tile.getArrayP().x;
      int i = (int)tile.getArrayP().y;
      
      //NO NEED TO SET CHANGED STATUS TO TRUE SINCE THIS DOESN'T COUNT AS A MERGER
      
      switch(dir){
       case "UP":
        //Moving the tile on the screen to it's destination
        while(this.place.y != y){
          if(this.place.y > y) this.place.y -= yspeed;
          else this.place.y += yspeed;
        }
        
        //Updating the destination tile's img 
        grid[i][j].setImgP(this.getImgP());
        
        //Setting the tile that has been moved to be empty
        this.setImgP(0);
        
        //Reseting the moved tile's location to it's original spot
        this.setPlace(v);
        break;
        
      case "DOWN":
        while(this.place.y != y){
          if(this.place.y > y) this.place.y -= yspeed;
          else this.place.y += yspeed;
        }
        grid[i][j].setImgP(this.getImgP());
        this.setImgP(0);
        this.setPlace(v);
        break;
        
      case "LEFT":
        while(this.place.x != x){
          if(this.place.x > x) this.place.x -= xspeed;
          else this.place.x += xspeed;
        }
        grid[i][j].setImgP(this.getImgP());
        this.setImgP(0);
        this.setPlace(v);
        break;
        
      case "RIGHT":
        while(this.place.x != x){
          if(this.place.x > x) this.place.x -= xspeed;
          else this.place.x += xspeed;
        }
        grid[i][j].setImgP(this.getImgP());
        this.setImgP(0);
        this.setPlace(v);
        break;
      }
      
  }
  
  //Move a given tile to it's destination in the grid array without any collisions
  public void noCollide(Value tile, String dir, Value[][] grid){
    
      //Storing the original location on the game window of the tile that's moving in the vector V
      int x1 = (int)this.place.x;
      int y1 = (int)this.place.y;
      PVector v = new PVector(x1,y1);
      
      //Setting the speed in which the movement animation will execute
      float xspeed = .5f, yspeed = .5f;
      
      //Storing the location on the game window of the destination tile in X & Y
      int x = (int)tile.getPlace().x;
      int y = (int)tile.getPlace().y; 
      
      //Storing the location of the destination tile in the grid array in J & I
      int j = (int)tile.getArrayP().x;
      int i = (int)tile.getArrayP().y;
      
      //NO NEED TO SET CHANGED STATUS TO TRUE SINCE THIS DOESN'T COUNT AS A MERGER
      
      switch(dir){
       case "UP":
         //Changing the destination Y and I to the neighbor of the given destination tile since no colide is meant to bring a tile NEXT to the destination, and not exactly to it
         y += 145;
         i++;
         
         //Moving the tile on the screen to it's destination
         while(this.place.y != y){
          if(this.place.y > y) this.place.y -= yspeed;
          else this.place.y += yspeed;
        }
        
        //Updating the destination tile's img 
        grid[i][j].setImgP(this.getImgP());
        
        //Setting the tile that has been moved to be empty
        this.setImgP(0);
        
        //Reseting the moved tile's location to it's original spot
        this.setPlace(v);
        break;
        
       case "DOWN":
         y -= 145;
         i--;
         while(this.place.y != y){
          if(this.place.y > y) this.place.y -= yspeed;
          else this.place.y += yspeed;
         }
        grid[i][j].setImgP(this.getImgP());
        this.setImgP(0);
        this.setPlace(v);
        break;
        
       case "LEFT":
         x += 145;
         j++;
         while(this.place.x != x){
          if(this.place.x > x) this.place.x -= xspeed;
          else this.place.x += xspeed;
        }
        grid[i][j].setImgP(this.getImgP());
        this.setImgP(0);
        this.setPlace(v);
        break;
        
       case "RIGHT":
         x -= 145;
         j--;
         while(this.place.x != x){
          if(this.place.x > x) this.place.x -= xspeed;
          else this.place.x += xspeed;
        }
        grid[i][j].setImgP(this.getImgP());
        this.setImgP(0);
        this.setPlace(v);
        break;
      }
      
  }
  
  //Moves a tile to a given direction
  public void move(Value[][] grid, String dir, int space){
    
    int x,y;        //Contains the place in the grid array of the closest tile to the given tile
    
    switch(dir){
      
     case "UP":
       x = (int)this.arrayP.x;
       y = (int)this.arrayP.y - space;
       //If the tiles are identical, and havent been already changed in this move, merge them
       if(this.img == grid[y][x].getImg() && !grid[y][x].changed()) this.collide(grid[y][x], dir, grid);
       else {
        //If the destination tile is empty, transfer the given tile to the empty one
        if(grid[y][x].isEmpty()) this.takeEmpty(grid[y][x], dir, grid); 
        //If all else didn't happen, move the tile to the destination without collision
        else if(space > 1) this.noCollide(grid[y][x], dir, grid);
       }
       break;
       
     case "DOWN":
      x = (int)this.arrayP.x;
      y = (int)this.arrayP.y + space;
      if(this.img == grid[y][x].getImg() && !grid[y][x].changed()) this.collide(grid[y][x], dir, grid);
      else {
       if(grid[y][x].isEmpty()) this.takeEmpty(grid[y][x], dir, grid); 
       else if(space > 1) this.noCollide(grid[y][x], dir, grid);
      }
      break;
      
     case "LEFT":
      x = (int)this.arrayP.x - space;
      y = (int)this.arrayP.y;
      if(this.img == grid[y][x].getImg() && !grid[y][x].changed()) this.collide(grid[y][x], dir, grid);
      else {
       if(grid[y][x].isEmpty()) this.takeEmpty(grid[y][x], dir, grid);
       else if(space > 1) this.noCollide(grid[y][x], dir, grid);
      }
      break;
      
     case "RIGHT":
      x = (int)this.arrayP.x + space;
      y = (int)this.arrayP.y;
      if(this.img == grid[y][x].getImg() && !grid[y][x].changed()) this.collide(grid[y][x], dir, grid);
      else {
       if(grid[y][x].isEmpty()) this.takeEmpty(grid[y][x], dir, grid);
       else if(space > 1) this.noCollide(grid[y][x], dir, grid);
      }
      break;
    }
  }
  
  //Checks the space between a given tile to the closest existing tile / border in the chosen direction --------------------NEEDS FIXING--------------------
  public int checkSpace(Value[][] grid, String dir){
    
    //Initializes space to be 1 since the checking process begins at a minimum distance of 1 in any direction
    int space = 1;
    
    //Contains the location of the given tile in the grid array
    int x = (int)this.arrayP.x;
    int y = (int)this.arrayP.y;
    
    switch(dir){
     case "UP":
      for(int i=y-space; i>-1; i--){
        if(i == 0) return space;
        if(!grid[i][x].isEmpty()) return space;
        space++;
      }
      break;
      
     case "DOWN":
      for(int i=y+space; i<4; i++){
        if(i == 3) return space;
        if(!grid[i][x].isEmpty()) return space;
        space++;
      }
      break;
      
     case "LEFT":
      for(int j=x-space; j>-1; j--){
        if(j == 0) return space;
        if(!grid[y][j].isEmpty()) return space;
        space++;
      }
      break;
      
     case "RIGHT":
      for(int j=x+space; j<4; j++){
        if(j == 3) return space;
        if(!grid[y][j].isEmpty()) return space;
        space++;
      }
      break;
    }
    
    return space;
  }
  
}
  public void settings() {  size(800,800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch_2048" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
