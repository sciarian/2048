
package game2048;
import java.util.ArrayList;
/***************************************************************************
 * 
 * This Program is the engine for a TUI and a GUI for the popular mobile
 * game 2048.
 * 
 * @author Anthony Sciarini
 * @version Winter 2017
 *
 **************************************************************************/

public class NumberGame implements NumberSlider{

	/** Stores parameter for the number of the rows of the board */

	private int height;

	/** Stores parameter for the number of columns the board has */

	private int width;

	/** Winning value of the game */

	private int win;

	/** Numeric base of the tiles example: 2, 3, 4 */

	private int base;

	/** Records the score of the game*/

	//private int score;

	/** 2D array used to hold the values of each tile */

	private int gameBoard[][];

	/** Used to store the location and value of each random tile in the game
	 */

	private ArrayList<Cell> cellList;

	/** Stores all the previous states of the gameBoard after each turn is
	 * made
	 */

	private ArrayList<int[][]> undoList;

	/** Used to determine if the game board actually changed after the board
	 * was slid in one of the four directions
	 */

	private boolean validMove;

	/***********************************************************************
	 * Reset the game logic to handle a board of a given dimension
	 *
	 * @param height the number of rows in the board
	 * @param width the number of columns in the board
	 * @param winningValue the value that must appear on the board to
	 *                     win the game
	 * @throws IllegalArgumentException when the winning value is not 
	 * power of two or negative
	 **********************************************************************/

	@Override
	public void resizeBoard(int height, int width, int winningValue) 
			throws IllegalArgumentException {
		base = 2;

		//check winning value
		if(winningValue < 0 && powerCheck(winningValue)) 
			throw new IllegalArgumentException();

		//check boundaries
		if(height < 0 && width < 0)
			throw new IllegalArgumentException();

		//setting height and width board, set winning value, 
		//instantiate undoList
		//resets the score
		gameBoard = new int[this.height = height][this.width = width];
		win = winningValue;
		undoList = new ArrayList<int[][]>();
	}

	/***********************************************************************
	 * Reset the game logic to handle a board of a given dimension and
	 * base
	 *
	 * @param height the number of rows in the board
	 * @param width the number of columns in the board
	 * @param winningValue the value that must appear 
	 * 			on the board to win the game
	 * @param base the base of the winning value
	 * 
	 * @throws IllegalArgumentException when the winning value 
	 * 			is not power of two or negative
	 **********************************************************************/

	@Override
	public void resizeBoard2_0(int height, int width, 
			int base, int winningValue) throws IllegalArgumentException {
		//sets the base
		this.base = base;

		//check that base is positive and not zero
		if(base <= 0)
			throw new IllegalArgumentException();

		//check winning value
		if(winningValue <= 0 || powerCheck(winningValue)) 
			throw new IllegalArgumentException();

		//check boundaries
		if(height <= 0 || width <= 0)
			throw new IllegalArgumentException();

		//setting height and width board, set winning value, 
		//instantiate undoList
		gameBoard = new int[this.height = height][this.width = width];
		win = winningValue;
		undoList = new ArrayList<int[][]>();
	}

	/***********************************************************************
	 * Remove all numbered tiles from the board and place
	 * TWO non-zero values at random location
	 **********************************************************************/

	@Override
	public void reset() {
		//sets all values of game board to zero
		for(int r=0; r < gameBoard.length; r++){
			for(int c=0; c < gameBoard[0].length; c++){
				gameBoard[r][c] = 0;
			}
		}

		//make random x and y coordinate, assign random value to 
		//that coordinate
		for(int count = 0; count < 2; count++)
			placeRandomValue();
		undoList.clear();
	}

	/***********************************************************************
	 * Set the game board to the desired values given in the 2D array.
	 * This method should use nested loops to copy each element from the
	 * provided array to your own internal array. Do not just assign the
	 * entire array object to your internal array object. Otherwise, your
	 * internal array may get corrupted by the array used in the JUnit
	 * test file. This method is mainly used by the JUnit tester.
	 * 
	 * @param ref used to set the elements of the game board to various 
	 * 		different set ups. Used for JUnit testing.
	 **********************************************************************/

	@Override
	public void setValues(int[][] ref) {

		//sets values of  game board to the values of ref
		for(int r=0; r < gameBoard.length; r++){
			for(int c=0; c < gameBoard[0].length;c++){
				gameBoard[r][c] = ref[r][c];
			}
		}
	}

	/***********************************************************************
	 * Insert one random tile into an empty spot on the board.
	 *
	 * @return a Cell object with its row, column, and value attributes
	 *  initialized properly
	 *
	 * @throws IllegalStateException when the board has no empty cell
	 **********************************************************************/

	@Override		
	public Cell placeRandomValue() throws IllegalStateException {
		int r2, c2;
		r2 = (int) (Math.random()*gameBoard.length);
		c2 = (int) (Math.random()*gameBoard[0].length);
		boolean openSpace = false;
		//checks if their is a blank space in board
		for(int r = 0; r < gameBoard.length; r++) 
			for(int c = 0; c < gameBoard[0].length; c++)
				if(gameBoard[r][c] == 0)
					openSpace = true;

		//if their is a open space, find random open space	
		if(openSpace){
			while(gameBoard[r2][c2] != 0){ //makes sure that tile is empty
				r2 = (int) (Math.random()*gameBoard.length);
				c2 = (int) (Math.random()*gameBoard[0].length);			
			}

			//set value of open space to a random value
			gameBoard[r2][c2] = (int) (Math.pow(base,(int)
					(Math.random()*2 + 1)));
			return new Cell(r2,c2, gameBoard[r2][c2] );
		}
		else
			throw new IllegalStateException();
	}

	/***********************************************************************
	 * Slide all the tiles in the board in the requested direction
	 * @param dir move direction of the tiles
	 *
	 * @return true when the board changes, else return false
	 **********************************************************************/

	@Override
	public boolean slide(SlideDirection dir) {
		validMove = false;
		int[][] temp2D = new int[height][width];
		int[] temp = new int[height]; //temporary array used to help shift

		array("copy",gameBoard,temp2D); 
		undoList.add(temp2D);//saves game board


		//	This switch case is used to determine what private helper 
		//	methods to call based on the direction the user is sliding.
		//	For each direction each sun array of the game board is shifted 
		//	in that direction to remove blank tiles. Then equivalent tiles
		//	are added together. Once this is done the board is once again
		//	shifted in specified direction.

		switch(dir){

		case RIGHT :

			//loops through board, shifting and combining values
			for(int r = 0; r < height; r++){
				shiftRight(gameBoard[r]);
				combineRight(gameBoard[r]);
				shiftRight(gameBoard[r]);
			}
			break;

		case LEFT :

			//loops through board, shifting and combining values
			for(int r = 0; r < height; r++){
				shiftLeft(gameBoard[r]);
				combineLeft(gameBoard[r]);
				shiftLeft(gameBoard[r]);
			}
			break;

		case DOWN :

			//loops through board, shifting and combining values
			for(int c = 0; c < width; c++){
				for(int r = 0; r < height; r++){
					temp[r] = gameBoard[r][c];
				}
				shiftRight(temp);
				combineRight(temp);
				shiftRight(temp);

				//copy's in shifted version of game board 
				for(int r = 0; r < height; r++)
					gameBoard[r][c] = temp[r];
			}
			break;

		case UP :
			//loops through board, shifting and combining values
			for(int c = 0; c < width; c++){
				for(int r = 0; r < height; r++){
					temp[r] = gameBoard[r][c];
				}
				shiftLeft(temp);
				combineLeft(temp);
				shiftLeft(temp);
				//copy's in shifted version of game board 
				for(int r = 0; r < height; r++)
					gameBoard[r][c] = temp[r];
			}
			break;
		}
		//checks if board changed
		array("validMove",gameBoard,temp2D);

		//checks if a valid move occurred
		if(validMove == true)
			placeRandomValue(); //places a random value

		return validMove;
	}

	/***********************************************************************
	 *Puts all the locations and values of the non empty tiles into array
	 *list cell list.
	 *
	 * @return an array list of Cells. Each cell holds the (row,column) and
	 * value of a tile
	 **********************************************************************/

	@Override
	public ArrayList<Cell> getNonEmptyTiles() {

		cellList = new ArrayList<Cell>();

		//adds all non blank tiles to to array list cell list
		for(int r=0; r < gameBoard.length; r++){
			for(int c=0; c < gameBoard[0].length;c++){
				if(gameBoard[r][c] != 0)
					cellList.add(new Cell(r,c,gameBoard[r][c]));
			}
		}

		return cellList;
	}

	/***********************************************************************
	 * Return the current state of the game
	 * @return one of the possible values of GameStatus enum
	 **********************************************************************/
	@Override
	public GameStatus getStatus() { 

		//Check if winning value was reached
		for(int r = 0; r < gameBoard.length; r++)
			for(int c = 0; c < gameBoard[0].length; c++){
				if(gameBoard[r][c] == this.win)
					return GameStatus.USER_WON;			}

		//Check if blank spot on board
		for(int r = 0; r < gameBoard.length; r++)
			for(int c = 0; c < gameBoard[0].length; c++)
				if(gameBoard[r][c] == 0)
					return GameStatus.IN_PROGRESS;

		//Check if horizontal move possible 
		for(int r = 0; r < gameBoard.length; r++)
			for(int c = 0; c < gameBoard[0].length; c++){
				if( c+1 <  gameBoard[0].length && 
						gameBoard[r][c] == gameBoard[r][c+1])
					return GameStatus.IN_PROGRESS;	}

		//Check if vertical move possible 
		for(int c = 0; c < gameBoard[0].length; c++)
			for(int r = 0; r < gameBoard.length; r++){
				if( r+1 <  gameBoard.length && 
						gameBoard[r][c] == gameBoard[r+1][c])
					return GameStatus.IN_PROGRESS;
			}
		return GameStatus.USER_LOST; 
	}

	/***********************************************************************
	 * Undo the most recent action, i.e. restore the board to its previous
	 * state. Calling this method multiple times will ultimately restore
	 * the game to the very first initial state of the board holding two
	 * random values. Further attempt to undo beyond this state will throw
	 * an IllegalStateException.
	 *
	 * @throws IllegalStateException when undo is not possible
	 **********************************************************************/

	@Override
	public void undo() throws IllegalStateException
	{
		int[][] temp = new int[gameBoard.length][gameBoard[0].length];

		//checks if list is empty
		if(undoList.size() - 1 < 0 )
			throw new IllegalStateException();

		//retrieves last slide
		temp = undoList.get((undoList.size() -1));

		//copy's the board to its previous 
		array("copy",temp,gameBoard);

		// removes last instance
		undoList.remove(undoList.size() -1); 
	}

	//HELPERS

	/***********************************************************************
	 * Each array of the game board is passed to this function to have all
	 * of it's non blank tiles shifted to the right, if their is blank space
	 * between tiles.
	 * 
	 * @param array row or column form the game board
	 **********************************************************************/

	private void shiftRight(int [] array){
		int temp[] = new int[array.length];
		int marker = array.length-1;

		//shifts temporary array to the right
		for(int x = array.length-1; x >= 0;  x--){ 
			if(array[x] > 0){				   
				temp[marker] = array[x];
				marker--;
			}
		}

		//copy's temporary to array
		for(int x = 0; x < array.length; x++)
			array[x] = temp[x];
	}

	/***********************************************************************
	 * This performs the same actions as shift right, but the instead it
	 * shifts the non blank tiles of the board to the left
	 * 
	 * @param array row or column from the game board
	 **********************************************************************/

	private void shiftLeft(int [] array){
		int temp[] = new int[array.length];
		int marker = 0;

		//shifts temporary array to the left
		for(int x = 0; x < array.length; x++)
			if(array[x] > 0){
				temp[marker] = array[x];
				marker++;
			}

		//copy's temporary array to array
		for(int x = 0; x < array.length; x++)
			array[x] = temp[x];
	}


	/***********************************************************************
	 * Checks if two equal tiles are adjacent in the passed array and 
	 * combines the two. Once this is done the left most tile is set to zero
	 * 
	 * @param array row or column of the board
	 **********************************************************************/

	private void combineRight(int [] array){
		//combines equal tiles together, sets right most to zero
		for(int x = array.length - 1; x >= 0; x--)
			if(x-1 >=0 && array[x] + array[x-1] > 0){
				if(array[x] == array[x-1]){
					array[x] = (int) 
							Math.pow(base,findPowerOfBase(array[x]));
					array[x-1] = 0;
				}
			}
	}

	/***********************************************************************
	 * Has the same functionality as combineRight, this just combines values
	 * values to the left.
	 * 
	 * @param array a row or column of the game board
	 **********************************************************************/

	private void combineLeft(int [] array){

		//combine equals terms together, sets left most tile to zero
		for(int x = 0; x < array.length; x++)
			if(x < array.length-1 && array[x] + array[x+1] > 0){
				if(array[x] == array[x+1]){
					array[x] = (int) 
							Math.pow(base,findPowerOfBase(array[x]));
					array[x+1] = 0;
				}
			}
	}

	/***********************************************************************
	 * This method returns the value of the exponential of the base 
	 * incremented by one.
	 * 
	 * @param value the value of a tile on the board
	 * @return exponential of the base plus one
	 **********************************************************************/

	private int findPowerOfBase(int value){
		//returns exponential of the base plus one
		return (int)(Math.round(Math.log(value) / Math.log(base))) + 1 ;
	}

	/*********************************************************************** 
	 * This takes a string that is used to navigate a switch statement. 
	 * The switch statement allows the used to copy one array to another,
	 * or to check if one two dimensional array is different than the other
	 * 
	 * @param choice string that is used to select what case to use
	 * @param array1 a two dimensional array
	 * @param array2 a two dimensional array
	 **********************************************************************/

	private void array(String choice, int[][] array1, int[][] array2) {

		switch(choice){

		case "copy" : 

			//copy array 1 into array 2
			for(int r = 0; r < height; r++)
				for(int c = 0; c < width; c++)
					array2[r][c] = array1[r][c];
			break;

		case "validMove" :	

			//case to compare equality 
			for(int r = 0; r < height; r++)
				for(int c = 0; c < width; c++)
					if(array2[r][c] != array1[r][c])
						validMove = true;
			break;
		}

	}

	/***********************************************************************
	 * Checks if the winning value is one of the first 20 powers of 2
	 * 
	 * @param value the winning values chosen by the user
	 * @return true if false, false if true
	 **********************************************************************/

	private boolean powerCheck( int value){

		//checks if value is a power of two
		for(int x = 0; x < 20; x++){
			if(Math.pow(base,x) == value)
				return false;

		}
		return true;
	}

	/***********************************************************************
	 * prints the current state of the board.
	 * @param board a two dimensional array, usually game board
	 **********************************************************************/

	private void printBoard(int[][] board){

		//prints the game board
		for (int k = 0; k < gameBoard.length; k++) {
			for (int m = 0; m < gameBoard[k].length; m++){
				System.out.print(" " +  gameBoard[k][m]);
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}

	//SETTERS AND GETTERS

	/***********************************************************************
	 * sets the height of the board, the number of rows
	 * @return
	 **********************************************************************/
	private int getHeight() {
		return height;
	}

	/***********************************************************************
	 * sets the width of the board, the number of columns
	 * @return
	 **********************************************************************/
	private int getWidth() {
		return width;
	}

	/***********************************************************************
	 * Gets the winning value of the game
	 * @return winning value
	 **********************************************************************/

	private int getWin() {
		return this.win;
	}

	/***********************************************************************
	 * sets the winning values of the game
	 * @param win new winning value for the game
	 **********************************************************************/

	private void setWin(int win) {
		this.win = win;
	}

	/***********************************************************************
	 * Sets the base of the tiles 
	 * @param base the new base for the tiles
	 **********************************************************************/

	private void setBase(int base){
		this.base = base;
	}

	/***********************************************************************
	 * Main method for the class, used for testing the basic functionality
	 * of the slide method.
	 * @param args
	 **********************************************************************/

	public static void main(String[] args){

		//create's a new object of number game
		NumberGame tester = new NumberGame();
		int R = 10;
		tester.resizeBoard(R, R,128);

		//sets the values of the game board accordingly
		for(int r = 0; r < tester.height; r++)
			for(int c = 0; c < tester.width; c++)
				if(c == 0)
					tester.gameBoard[r][c] = 1;
		//slides board in specified direction, x number of times

		//Used to time how long it took to shift the board right
		System.out.println("START");
		double startTime = System.currentTimeMillis() / 1000.0;
		tester.slide(SlideDirection.RIGHT);
		double endTime = System.currentTimeMillis() / 1000.0;
		System.out.println("FINISH: " + (endTime - startTime));


		//		while(x < 1){
		//			tester.slide(SlideDirection.LEFT);
		//			tester.printBoard(tester.gameBoard);
		//			x++;
		//		}
		//
		//		x = 0;
		//
		//		while(x < 2){
		//			tester.slide(SlideDirection.UP);
		//			tester.printBoard(tester.gameBoard);
		//			x++;
		//		}
		//
		System.out.println("Anthony is cool");
	}
}