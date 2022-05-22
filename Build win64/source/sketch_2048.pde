/*
TODO:
  add animations - NOT POSSIBLE IN PROCESSING

Colors:
  Table color / score / best - #bbada0
  background - #faf8ef
  text - #dbd3cd
*/
import java.*;

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

//Runs once on application launch
void setup(){
  
   size(800,800);                                                    //Size of the canvas (window-size)
   background(#faf8ef);                                              //BG color
   
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
   
   //Generates 2 random tiles on the grid
   generate();
   generate();

}

//Runs once every frame
void draw(){
  
  //Draws the background text and images of the game
  bgText();
  
  //Updates the locations and images of the tiles in the grid
  for(int i=0; i<4; i++)
     for(int j=0; j<4; j++){
       grid[i][j].show();
     }
     
  //Draw a green rectangle next to the newly generated tile   
  fill(#00FF00);
  rect(newX * 145 + 120, newY * 145 + 190, 15, 15);
  
  if(lose){
    fill(220);
    rect(160, 337, 450, 140);
    fill(255,0,0);
    textFont(f, 120);
    text("DEFEAT", 171, 437); 
    textSize(18);
    fill(255,255,255);
    text("PRESS SPACE TO RESTART", 270, 460); 
  }

}

//Executes a move function based on user input
void keyPressed(){
  if(canMove && !lose){
    if(keyCode == UP) move("UP");
    if(keyCode == DOWN) move("DOWN");
    if(keyCode == LEFT) move("LEFT");
    if(keyCode == RIGHT) move("RIGHT");
  }
    
    //Resets the game if space is pressed
    if(key == ' ') setup();
}

void keyReleased(){
    
    canMove = true;                    //Once the pressed key is released, the user can make his next move (to prevent spamming)
  
}

//Moves ALL the tiles in the grid to the chosen direction and generates a new tile on the grid
void move(String dir){
  
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
void updateScore(){
    for(int i=0; i<4; i++)
       for(int j=0; j<4; j++){
         if(!grid[i][j].isEmpty())
           score += pow(2, grid[i][j].getImgP());
       }
}

//Checks if there are no more possible moves
void checkLose(){
    if(checkIfLose(grid))  //If the user lost display a message accordingly
      lose = true;
    else
      lose = false;
      
    println(" " + lose);
}

//Checks if there are any possible moves for the user
boolean checkIfThereArePossibleMoves(Value[][] grid, Value tile){
   
   int tileImgP = tile.getImgP();
   int x = int(tile.getArrayP().x);            //Contains the X of the given tile in the grid array
   int y = int(tile.getArrayP().y);            //Contains the Y of the given tile in the grid array
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
boolean checkIfLose(Value[][] grid){
  for(int i=0; i<4; i++)
     for(int j=0; j<4; j++){
       if(checkIfThereArePossibleMoves(grid, grid[i][j])) return false;
     }
  return true;
}

//Checks if the grid is full
boolean checkIfFull(Value[][] grid){
    for(int i=0; i<4; i++)
     for(int j=0; j<4; j++){
       if(grid[i][j].isEmpty()) return false;
     }
  return true;
}

//Generates a tile in an empty spot on the grid. The kind of tile is based on chance
void generate(){
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
int chances(float chance){
   if(chance>=5.0) return 1;
   else{
      if(chance<0.025 && genCap >= 1024) return 10;
      else{
         if(chance<0.1 && genCap >= 512) return 9;
         else{
            if(chance<0.5 && genCap >= 256) return 8;
            else{
               if(chance<0.75 && genCap >= 128) return 7;
               else{
                  if(chance<1 && genCap >= 64) return 6;
                  else{
                     if(chance<1.75 && genCap >= 32) return 5;
                     else{
                        if(chance<2.5 && genCap >= 16) return 4;
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
int exponent(int genCap){
  int ex = 0;
  while(genCap>=2){
   genCap = genCap/2;
   ex++;
  }
  return ex;
}

//Draws the background images and text of the game
public void bgText(){
    background(#faf8ef);
    fill(#bbada0);
    //image(gridBG, 91, 165);
    rect(100, 170, 600, 600, 30);
    rect(500, 50, 200, 65, 15);
    image(title, 100, 40);
    fill(#dbd3cd);
    text("SCORE", 570, 75);
    text(score, 580, 100);
}
