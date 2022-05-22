
# 2048

This is a replica of the mobile game 2048 made in Java & Processing. 

I used the processing IDE to make this project (for no real reason).  
Only regret I have using this IDE for this project is the lack of animation suppport,  
so you'll have to make do without them. Sorry <3


## How to Install and Run the Project

* Download and install [processing](https://processing.org/download).
* Download the ZIP file for the project from my [GitHub Page](https://github.com/oren9561/2048).  
* Launch the game and play
* If you want to review the code, launch the sketch through processing.
## How to play the game
### Keys

* Use the arrow keys to decide which in which direction the tiles will shift.
* Press space to restart the game at any point

### How the game works

Initially, there are two tiles which spawn on the grid.  
With each move, a new tile spawns in a random empty location on the board, with a random value 
ranging from 2 to 2048. However, the tile's available value range is determined based on the 
user's largest value tile so far, for example:  
 * My biggest value on the board currently is 32, so a new tile can spawn with the max value of 32.
 Additionaly, the chances for a tile to spawn with a certain value go down the bigger the value is.

The win condition is to get to the 2048 tile, once you do that you get the win screen.
## Notes
* The gameplay will feel a little clunky initially since there are no animations but you get used to it pretty quickly.
* Whenever a new tile spawns it will have a little green box next to it indicating it's a new one to make the user experience easier.

## Credits

- [@oren9561](https://github.com/oren9561)

### Connect with me:

[![website](https://i.gyazo.com/7c244728088109ecda95a87017e30012.png)](https://www.linkedin.com/in/oren9561/)
[![website](https://i.gyazo.com/01810428375ef3b58190c80979bda9a9.png)](https://github.com/oren9561)


# **ENJOY**