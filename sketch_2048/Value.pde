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
      float xspeed = .5, yspeed = .5;
      
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
      float xspeed = .5, yspeed = .5;
      
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
      float xspeed = .5, yspeed = .5;
      
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
