// ====================================================
// Jason Williamson (20552360)
// CS 349 Winter 2017
// Assignment 01
// File: snake.cpp
// ====================================================
//

/*
CS 349 A1 Skeleton Code - Snake

- - - - - - - - - - - - - - - - - - - - - -

Commands to compile and run:

    g++ -o snake snake.cpp -L/usr/X11R6/lib -lX11 -lstdc++
    ./snake

Note: the -L option and -lstdc++ may not be needed on some machines.
*/

#include <iostream>
#include <list>
#include <cstdlib>
#include <sys/time.h>
#include <math.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sstream>
#include <ctime>

/*
 * Header files for X functions
 */
#include <X11/Xlib.h>
#include <X11/Xutil.h>

using namespace std;

enum DirectionState {
	kDirectionUp,
	kDirectionDown,
	kDirectionLeft,
	kDirectionRight
};

enum GameState {
	kPauseState,
	kGameOverState,
	kRestartState,
	kIntroState,
	kPlayState
};
/*
 * Global game state variables
 */
const int Border = 5;
const int BufferSize = 10;
const int width = 800;
const int height = 600;
const int upperBounds = -10;
const int leftBounds = -10;
const int lowerBounds = height;
const int rightBounds = width;
const int bonusMultiplier = 2;
const int scoreValue = 10;
const int scoreBonus = 50;

int bonusMod = 5;
int fruitAte = 0;
GameState gameState;
int score = 0;
int timer;
int speed;
int FPS;
bool pauseEvent = false;
bool fresh = true;
const int kZero = 0;
const int kOne = 1;
const int kTwo = 2;
const int kThree = 3;
const int kFour = 4;
const int kFive = 5;
const int kSix = 6;
const int kSeven = 7;
const int kEight = 8;
const int kNine = 9;
const int kTen = 10;

/*
 * Information to draw on the window.
 */
struct XInfo {
	Display	 *display;
	int		 screen;
	Window	 window;
	GC		 gc[3];
	int		width;		// size of window
	int		height;
};

namespace patch
{
	template < typename T > std::string to_string( const T& n )
	{
		std::ostringstream stm ;
		stm << n ;
		return stm.str() ;
	}
}

/*
 * Function to put out a message on error exits.
 */
void error( string str ) {
  cerr << str << endl;
  exit(0);
}


/*
 * An abstract class representing displayable things. 
 */
class Displayable {
	public:
		virtual void paint(XInfo &xinfo) = 0;
};


class Fruit : public Displayable {
	public:
		virtual void paint(XInfo &xinfo) {
			XFillRectangle(xinfo.display, xinfo.window, xinfo.gc[0], x, y, 10, 10);
        }

        Fruit() {
            // ** ADD YOUR LOGIC **
            // generate the x and y value for the fruit 
			setRandomCoord();
        }

        // ** ADD YOUR LOGIC **
        /*
         * The fruit needs to be re-generated at new location every time a snake eats it. See the assignment webpage for more details.
         */
		 void setRandomCoord(){
			 unsigned int height = 59;
			 unsigned int width = 79;
			 int blockSize = 10;
			 srand(time(NULL));
			 int randX = 1 + rand() % (width - blockSize);
			 int randY = 1 + rand() % (height - blockSize);
             x = randX * 10;
             y = randY * 10;
		 }

		 int getX() {
			return x;
		}
		
		int getY() {
			return y;
		}

    private:
        int x;
        int y;
};

class SnakeBlock : public Displayable {
	public:
		virtual void paint(XInfo &xinfo) {
			XFillRectangle(xinfo.display, xinfo.window, xinfo.gc[0], x, y, 10, blockSize);
		}

		int getX() {
			return x;
		}
		
		int getY() {
			return y;
		}

		void setX(int x){
			this->x = x;
		}

		void setY(int y){
			this->y = y;
		}

		void setDirection(DirectionState dir){
			direction = dir;
		}

		DirectionState getDirection(){
			return direction;
		}

		SnakeBlock(int x, int y, int blockSize): x(x), y(y), blockSize(blockSize){
			this->blockSize = blockSize;
		}
	
	private:
		int x;
		int y;
		int blockSize;
		DirectionState direction;
};    

list<Displayable *> dList;           // list of Displayables
Fruit fruit;  

class Snake : public Displayable {
	public:
		virtual void paint(XInfo &xinfo) {

			//XFillRectangle(xinfo.display, xinfo.window, xinfo.gc[0], x, y, 10, blockSize);
			list<SnakeBlock *>::const_iterator begin = sList.begin();
			list<SnakeBlock *>::const_iterator end = sList.end();
			while( begin != end ) {
				SnakeBlock *sb = *begin;
				sb->paint(xinfo);
				#ifdef DEBUG
					printf( "sb->X sb->Y = %d %d \n", sb->getX(), sb->getY());
				#endif
				begin++;
			}
			#ifdef DEBUG
				printf("paint done!\n");
			#endif
		
		}
		
		void move(XInfo &xinfo) {

			SnakeBlock *tmpHead= sList.back();
			int curHeadX = sList.front()->getX();
			int curHeadY = sList.front()->getY();
			#ifdef DEBUG
				printf( "curHeadX curHeadY = %d %d \n", curHeadX, curHeadY);
			#endif
			sList.pop_back();
			sList.push_front(tmpHead);
			int newX, newY;
			switch(newDirection)
			{
				case kDirectionUp:
					newX = curHeadX;
					newY = curHeadY - direction;
					break;
				case kDirectionDown:
					newX = curHeadX;
					newY = curHeadY + direction;
					break;
				case kDirectionLeft:
					newX = curHeadX - direction;
					newY = curHeadY;
					break;
				case kDirectionRight:
					newX = curHeadX + direction;
					newY = curHeadY;
					break;
				default:
				    //nothing
					break;
			}

			tmpHead->setX(newX);
			tmpHead->setY(newY);
			#ifdef DEBUG
				printf( "newX newY = %d %d \n", newX, newY);
			#endif
			setCurrentDirection(newDirection);
			tmpHead->setDirection(newDirection);

			if((fruit.getX() == newX) && (fruit.getY() == newY)){
				this->didEatFruit();
				this->move(xinfo);
			}

			//Check collistion with walls
			if((upperBounds == newY) || (lowerBounds == newY) || 
			  (leftBounds == newX) || (rightBounds == newX)){
				  #ifdef DEBUG
				  	printf("Wall Collision \n");
				  #endif
				  didHitObstacle(xinfo);
			}

			//Check collision with snake body
			list<SnakeBlock *>::const_iterator begin = sList.begin();
			list<SnakeBlock *>::const_iterator end = sList.end();
			int sbX, sbY;
			begin++; //don't check the head'
			while( begin != end ) {
				SnakeBlock *sb = *begin;
				sbX = sb->getX();
				sbY = sb->getY();
				if((sbX == sList.front()->getX()) && (sbY == sList.front()->getY())){
					#ifdef DEBUG
						printf("Snake Collision \n");
						printf("x: %d, y: %d \n", sbX, sbY);
					#endif
					didHitObstacle(xinfo);
					
				}
				begin++;
			}
			#ifdef DEBUG
				printf("move done \n");
			#endif
		}
		
		int getX() {
			return x;
		}
		
		int getY() {
			return y;
		}

        /*
         * ** ADD YOUR LOGIC **
         * Use these placeholder methods as guidance for implementing the snake behaviour. 
         * You do not have to use these methods, feel free to implement your own.
         */ 
        void didEatFruit() {
			fruit.setRandomCoord();
			DirectionState tailDirection = sList.back()->getDirection();
			int tailX = sList.back()->getX();
			int tailY = sList.back()->getY();
			int newX, newY;
			if(kDirectionUp == tailDirection){
				newX = tailX;
				newY = tailY + blockSize;
			}else if(kDirectionDown == tailDirection){
				newX = tailX;
				newY = tailY - blockSize;
			}else if(kDirectionLeft == tailDirection){
				newX = tailX + blockSize;
				newY = tailY;
			}else{//kDirectionRight
				newX = tailX - blockSize;
				newY = tailY;
			}

			//update score
			++fruitAte;
			int onBonus = fruitAte % bonusMod;
			if(kZero == score){
				++bonusMod;
				score = scoreBonus;
			}else if(kZero == onBonus){
				score *= bonusMultiplier;
			}else{
				score += scoreValue;
			}

			sList.push_back(new SnakeBlock(newX, newY, blockSize));
			sList.back()->setDirection(tailDirection);
        }

        void didHitObstacle(XInfo &xinfo) {
			pauseEvent = true;
			gameState = kGameOverState;
        }

		void setNewDirection(DirectionState nd) {
			if(((kDirectionUp == nd) && (kDirectionDown != currentDirection)) ||
			   ((kDirectionDown == nd) && (kDirectionUp != currentDirection)) ||
			   ((kDirectionLeft == nd) && (kDirectionRight != currentDirection)) ||
			   ((kDirectionRight == nd) && (kDirectionLeft != currentDirection)))
			{
				newDirection = nd;
			}
		}

		DirectionState getNewDirection() {
			return newDirection;
		}

		DirectionState getCurrentDirection(){
			return currentDirection;
		}

		void setCurrentDirection(DirectionState dir){
			currentDirection = dir;
		}

		void restart(){
			
			while(!sList.empty()) delete sList.front(), sList.pop_front();

			initSnakeBody();
			fruit.setRandomCoord();
			pauseEvent = false;
			gameState = kPlayState;
			#ifdef DEBUG
				printf("snake.restart() %d \n", sList.empty());
			#endif
			score = kZero;
			fruitAte = kZero;
			bonusMod = kFive;
		}

		void initSnakeBody(){
			initialSize = 3;
			currentDirection = newDirection = kDirectionUp;
			for(int i = 0; i < initialSize; ++i){
				sList.push_front(new SnakeBlock(x, y - (i*blockSize), blockSize));
				sList.front()->setDirection(currentDirection);
			}
		}
		
		Snake(int x, int y): x(x), y(y) {
			direction = blockSize = 10;
			currentDirection = newDirection = kDirectionUp;
			initSnakeBody();
		}
	
	private:
		int x;
		int y;
		int blockSize;
		int direction;
		int initialSize;
		DirectionState newDirection;
		DirectionState currentDirection;
		list<SnakeBlock *> sList;
};

Snake snake(100, 450);          //Snake displayable

/*
 * Initialize X and create a window
 */
void initX(int argc, char *argv[], XInfo &xInfo) {
	XSizeHints hints;
	unsigned long white, black;

   /*
	* Display opening uses the DISPLAY	environment variable.
	* It can go wrong if DISPLAY isn't set, or you don't have permission.
	*/	
	xInfo.display = XOpenDisplay( "" );
	if ( !xInfo.display )	{
		error( "Can't open display." );
	}
	
   /*
	* Find out some things about the display you're using.
	*/
	xInfo.screen = DefaultScreen( xInfo.display );

	white = XWhitePixel( xInfo.display, xInfo.screen );
	black = XBlackPixel( xInfo.display, xInfo.screen );

	hints.x = 100;
	hints.y = 100;
	hints.width = 800;
	hints.height = 600;
	hints.flags = PPosition | PSize;

	xInfo.window = XCreateSimpleWindow( 
		xInfo.display,				// display where window appears
		DefaultRootWindow( xInfo.display ), // window's parent in window tree
		hints.x, hints.y,			// upper left corner location
		hints.width, hints.height,	// size of the window
		Border,						// width of window's border
		black,						// window border colour
		white );					// window background colour
		
	XSetStandardProperties(
		xInfo.display,		// display containing the window
		xInfo.window,		// window whose properties are set
		"Snake game",		// window's title
		"Animate",			// icon's title
		None,				// pixmap for the icon
		argv, argc,			// applications command line args
		&hints );			// size hints for the window

	/* 
	 * Create Graphics Contexts
	 */
	int i = 0;
	xInfo.gc[i] = XCreateGC(xInfo.display, xInfo.window, 0, 0);
	XSetForeground(xInfo.display, xInfo.gc[i], BlackPixel(xInfo.display, xInfo.screen));
	XSetBackground(xInfo.display, xInfo.gc[i], WhitePixel(xInfo.display, xInfo.screen));
	XSetFillStyle(xInfo.display, xInfo.gc[i], FillSolid);
	XSetLineAttributes(xInfo.display, xInfo.gc[i],
	                     1, LineSolid, CapButt, JoinRound);
	
	i = 1;
	xInfo.gc[i] = XCreateGC(xInfo.display, xInfo.window, 0, 0);
	XSetForeground(xInfo.display, xInfo.gc[i], BlackPixel(xInfo.display, xInfo.screen));
	XSetBackground(xInfo.display, xInfo.gc[i], WhitePixel(xInfo.display, xInfo.screen));
	XSetFillStyle(xInfo.display, xInfo.gc[i], FillSolid);
	XSetLineAttributes(xInfo.display, xInfo.gc[i],
	                     7, LineSolid, CapRound, JoinMiter);

	i = 2;
	xInfo.gc[i] = XCreateGC(xInfo.display, xInfo.window, 0, 0);
	XSetForeground(xInfo.display, xInfo.gc[i], BlackPixel(xInfo.display, xInfo.screen));
	XSetBackground(xInfo.display, xInfo.gc[i], WhitePixel(xInfo.display, xInfo.screen));
	XSetFillStyle(xInfo.display, xInfo.gc[i], FillSolid);
	XSetLineAttributes(xInfo.display, xInfo.gc[i],
	                     7, LineOnOffDash, CapButt, JoinBevel);

	XSelectInput(xInfo.display, xInfo.window, 
		ButtonPressMask | KeyPressMask | 
		PointerMotionMask | 
		EnterWindowMask | LeaveWindowMask |
		StructureNotifyMask);  // for resize events

	XFontStruct * font;
	font = XLoadQueryFont (xInfo.display, "12x24");
	XSetFont (xInfo.display, xInfo.gc[0], font->fid);

	XFontStruct * font2;
	font2 = XLoadQueryFont (xInfo.display, "8x16");
	XSetFont (xInfo.display, xInfo.gc[1], font2->fid);
	
	/*
	 * Put the window on the screen.
	 */
	XMapRaised( xInfo.display, xInfo.window );
	XFlush(xInfo.display);
}

/*
 * Function to repaint a display list
 */
void repaint( XInfo &xinfo) {
	
	list<Displayable *>::const_iterator begin = dList.begin();
	list<Displayable *>::const_iterator end = dList.end();


	XClearWindow( xinfo.display, xinfo.window );
	
	
	//unsigned int maxHeight = xinfo.height - 1;
	//unsigned int maxWidth = xinfo.width - 1;
	unsigned int maxWidth = 800 - 1;
	unsigned int maxHeight = 600 - 1;
	XDrawRectangle(xinfo.display, xinfo.window, xinfo.gc[1], 0, 0, maxWidth, maxHeight);

	//display score
	if(kPlayState == gameState){
		std::string s = patch::to_string(score);
		std::string scoreTxt("Score: " + s);
		s = patch::to_string(FPS);
		std::string fpsTxt("FPS: " + s);
		s = patch::to_string(speed);
		std::string speedTxt("Speed: " + s);
		
    	XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[1], width/20 - 10, height - 30, scoreTxt.c_str(), scoreTxt.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[1], width/20 * 18, height - 50, fpsTxt.c_str(), fpsTxt.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[1], width/20 * 18, height - 30, speedTxt.c_str(), speedTxt.length() );
	}

	// draw display list
	while( begin != end ) {
		Displayable *d = *begin;
		d->paint(xinfo);
		begin++;
	}

	#ifdef DEBUG
		printf("repainting\n");
	#endif
	if(pauseEvent && (kGameOverState == gameState)){
		XDrawRectangle(xinfo.display, xinfo.window, xinfo.gc[2], width/3, (height/2) - 125, (width/3) + 35 , height/2);
		// draw text
		std::string text("GAME OVER");
		std::string textb2("r to restart");
		std::string textb3("q to quit");
		std::string s = patch::to_string(score);
		std::string scoreTxt("SCORE: " + s);
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 45, height/2 - 75, text.c_str(), text.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 55, height/2 - 25, textb2.c_str(), textb2.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 55, height/2, textb3.c_str(), textb3.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 45, height/2 +125, scoreTxt.c_str(), scoreTxt.length() );
	}else if(pauseEvent && (kPauseState == gameState)){
		#ifdef DEBUG
			printf("inside pause repaint \n");
		#endif
		XDrawRectangle(xinfo.display, xinfo.window, xinfo.gc[2], width/3, (height/2) - 125, (width/3) + 35 , height/2);
		// draw text
		std::string text("PAUSED");
		std::string textb1("p to resume");
		std::string textb2("r to restart");
		std::string textb3("q to quit");
		std::string s = patch::to_string(score);
		std::string scoreTxt("SCORE: " + s);
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 30, height/2 - 75, text.c_str(), text.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 55, height/2 - 25, textb1.c_str(), textb1.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 55, height/2, textb2.c_str(), textb2.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 55, height/2 + 25, textb3.c_str(), textb3.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 45, height/2 +125, scoreTxt.c_str(), scoreTxt.length() );
	}

	XFlush( xinfo.display );

}

void handleKeyPress(XInfo &xinfo, XEvent &event) {
	KeySym key;
	char text[BufferSize];
	char keyValue;
	/*
	 * Exit when 'q' is typed.
	 * This is a simplified approach that does NOT use localization.
	 */
	int i = XLookupString( 
		(XKeyEvent *)&event, 	// the keyboard event
		text, 					// buffer when text will be written
		BufferSize, 			// size of the text buffer
		&key, 					// workstation-independent key symbol
		NULL );					// pointer to a composeStatus structure (unused)

	if ( i == 1) {
		keyValue = text[0];
		#ifdef DEBUG
			printf("Got key press -- %c\n", keyValue);
		#endif
		if (keyValue == 'q') {
			error("Terminating normally.");
			//cleanup
		}else if(keyValue == 'a'){
			#ifdef DEBUG
				printf("Move left \n");
			#endif
			snake.setNewDirection(kDirectionLeft);
		}else if(keyValue == 'd'){
			#ifdef DEBUG
				printf("Move right \n");
			#endif
			snake.setNewDirection(kDirectionRight);
		}else if(keyValue == 'w'){
			#ifdef DEBUG
				printf("Move UP! \n");
			#endif
			snake.setNewDirection(kDirectionUp);
		}else if(keyValue == 's'){
			#ifdef DEBUG
				printf("Move down \n");
			#endif
			snake.setNewDirection(kDirectionDown);
		}else if(keyValue == 'r'){
			if(kIntroState != gameState){
				#ifdef DEBUG
					printf("Restart \n");
				#endif
				pauseEvent = true;
				gameState = kRestartState;
				usleep(200);
				snake.restart();
			}
		}else if(keyValue == 'p'){
			if((kIntroState != gameState) && (kGameOverState != gameState)) {
				if(kPauseState != gameState){
					#ifdef DEBUG
						printf("Pause \n");
					#endif
					pauseEvent = true;
					gameState = kPauseState;
					repaint(xinfo);
				}else{
					pauseEvent = false;
					gameState = kPlayState;
				}
			}
		}else if(keyValue =='c'){
			if(kIntroState == gameState){
				#ifdef DEBUG
					printf("INTRO C");
				#endif
				gameState = kPlayState;
				pauseEvent = false;
			}
		}
	}
}

void handleAnimation(XInfo &xinfo, int inside) {

    /*
     * ADD YOUR OWN LOGIC
     * This method handles animation for different objects on the screen and readies the next frame before the screen is re-painted.
     */ 
	++timer;
	if(((kOne == speed) && (timer == kTen)) ||
	   ((kTwo == speed) && (timer == kNine)) ||
	   ((kThree == speed) && (timer == kEight)) ||
	   ((kFour == speed) && (timer == kSeven)) ||
	   ((kFive == speed) && (timer == kSix)) ||
	   ((kSix == speed) && (timer == kFive)) ||
	   ((kSeven == speed) && (timer == kFour)) ||
	   ((kEight == speed) && (timer == kThree)) ||
	   ((kNine == speed) && (timer == kTwo)) ||
	   ((kTen == speed) && (timer == kOne))) {
		snake.move(xinfo);
		timer = 0;
		#ifdef DEBUG
			printf("handleAnimation - move\n");
		#endif
	}
}

void displaySplashScreen(XInfo &xinfo){
	#ifdef DEBUG
		printf("inside INTRO repaint \n");
	#endif
		
		XDrawRectangle(xinfo.display, xinfo.window, xinfo.gc[2], width/3, (height/2) - 125, (width/3) + 35 , height/2);
		// draw text
		std::string t0("SNAKE GAME");
		std::string t0_1("Created by j43willi , 205 52 360");
		std::string t0_2("Eat the fruit. Stay away from walls and yourself!");
		std::string t1("w move up");
		std::string t2("a move left");
		std::string t3("s move down");
		std::string t4("d move right");
		std::string t5("p to resume");
		std::string t6("r to restart");
		std::string t7("q to quit");
		std::string t8("PRESS c TO CONTINUE");
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 60, height/2 - 225, t0.c_str(), t0.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[1], width/2 - 112, height/2 - 195, t0_1.c_str(), t0_1.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[1], width/2 - 175, height/2 - 150, t0_2.c_str(), t0_2.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 60, height/2 - 100, t1.c_str(), t1.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 60, height/2 - 75, t2.c_str(), t2.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 60, height/2 - 50, t3.c_str(), t3.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 60, height/2 - 25, t4.c_str(), t4.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 60, height/2 + 25, t5.c_str(), t5.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 60, height/2 + 50, t6.c_str(), t6.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 60, height/2 + 75, t7.c_str(), t7.length() );
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc[0], width/2 - 99, height/2 + 150, t8.c_str(), t8.length() );
}

// get microseconds
unsigned long now() {
	timeval tv;
	gettimeofday(&tv, NULL);
	return tv.tv_sec * 1000000 + tv.tv_usec;
}

void eventLoop(XInfo &xinfo) {
	// Add stuff to paint to the display list
	dList.push_front(&snake);
    dList.push_front(&fruit);
	
	XEvent event;
	unsigned long lastRepaint = 0;
	int inside = 0;

	while( true ) {
		/*
		 * This is NOT a performant event loop!  
		 * It needs help!
		 */
		
		if (XPending(xinfo.display) > 0) {
			XNextEvent( xinfo.display, &event );
			cout << "event.type=" << event.type << "\n";
			switch( event.type ) {
				case KeyPress:
					handleKeyPress(xinfo, event);
					break;
				case EnterNotify:
					inside = 1;
					break;
				case LeaveNotify:
					inside = 0;
					break;
			}
		} 

		//Splash Screen
		if((kIntroState == gameState) && (fresh)){
			displaySplashScreen(xinfo);
			fresh = false;
		}

		unsigned long tick = now();
		if((!pauseEvent) && (tick - lastRepaint > 1000000/FPS)){
			handleAnimation(xinfo, inside);
			repaint(xinfo);
			lastRepaint = tick;
		}

		usleep(1000000/FPS);
		
	}
}


/*
 * Start executing here.
 *	 First initialize window.
 *	 Next loop responding to events.
 *	 Exit forcing window manager to clean up - cheesy, but easy.
 */
int main ( int argc, char *argv[] ) {
	XInfo xInfo;
	#ifdef DEBUG
		printf("argc: %d \n", argc);
	#endif
	if(kThree == argc){
		std::string framerate = argv[1];
		std::string gamespeed = argv[2];
		#ifdef DEBUG
			printf("FPS: %s speed: %s\n", framerate.c_str(), gamespeed.c_str());
		#endif
		std::istringstream ( framerate ) >> FPS;
		std::istringstream ( gamespeed ) >> speed;
	}else{
		//default settings
		FPS = 30;
		speed = 9;
	}
	
	gameState = kIntroState;
	pauseEvent = true;

	#ifdef DEBUG
		printf("Debug mode\n");
	#else
		printf("Release mode\n");
	#endif
	
	initX(argc, argv, xInfo);
	repaint(xInfo);
	eventLoop(xInfo);
	XCloseDisplay(xInfo.display);
}
