package game2048;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/***************************************************************************
 * This program is a GUI for the popular mobile game 2048. It allows the
 * user to resize the board dimensions, pick a number base, and a winning 
 * value.
 * @author Anthony Sciarini
 **************************************************************************/
public class GUI2048 extends JPanel{
	
	/*The engine for 2048*/
	private NumberSlider game = new NumberGame();
	
	/*Panel that holds all the other panels*/
	private JPanel mainPanel;
	
	/*Panel used to display game statistics */
	private JPanel[] statPanel;
	
	/*Used to display the game board*/
	private JPanel gridPanel;
	
	/*Two Dimensional array of JLabels used to display the board*/
	private JLabel[][] grid;
	
	/*Array all the labels used in the statistics panels*/
	private JLabel[] statLabel;
	
	/*Integer that holds the number of wins and losses, numerical base, and
	 * the score of the game.*/
	private int win = 0, loss = 0, base = 2, winValue = 2048 , score = 0;
	
	/*Checks if the game has been won||lost, used to help render the board*/
	private boolean gameIncomplete = true;
	
	/***********************************************************************
	 * Sets up each panel of the GUI and adds a key board listener.
	 **********************************************************************/
	public GUI2048(){
		/*Set up main Panel*/
		setLayout(new BorderLayout());
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		addKeyListener(new SlideListener());
		setFocusable(true);
		add(mainPanel);

		/*Set up statistics labels*/
		statLabel = new JLabel[20];
		/*Instantiates each statistics label, centers the text of the label
		 * and then sets the font size of the label*/
		for(int x = 0; x < statLabel.length; x++){
			statLabel[x] = new JLabel("", SwingConstants.CENTER);
			formatLabel(statLabel[x],60);
		}
		/*Set up statistics panel(s)*/
		statPanel = new JPanel[4];
		/*This is used to set the layout of each statistics panel*/
		for(int x = 0; x < statPanel.length; x++){
			if(x == 0 || x == 2){
				setLayout(new FlowLayout());
				statPanel[x] = new JPanel();
				statPanel[x].setLayout(new FlowLayout());
			}
			else{
				setLayout(new GridLayout());
				statPanel[x] = new JPanel();
				statPanel[x].setLayout(new GridLayout(6,1));
			}
		}
		/*Set up north panel statPanel[0]*/
		setUpNorthPanel();
		
		/*Set up east panel	statPanel[1]*/
		setUpEastPanel();
		
		/*Set up south panel statPanel[2]*/
		setUpSouthPanel();
		
		/*Set up west panel statPanel[3]*/
		setUpWestPanel();
		
		/*Set up grid panel*/
		setUpGridPanel(4,4);
		
		/*Set up a new game*/
		game.resizeBoard2_0(4,4,2,2048);
		game.reset();
		
		/*Add each panel to main panel*/
		mainPanel.add(statPanel[0], BorderLayout.NORTH);
		mainPanel.add(statPanel[1], BorderLayout.EAST);
		mainPanel.add(statPanel[2], BorderLayout.SOUTH);
		mainPanel.add(statPanel[3], BorderLayout.WEST);
		/*render the board*/
		renderBoard();
	}
	/***********************************************************************
	 * This method is used to set up the north panel, statPanel[0]. It is
	 * used to display the game status
	 **********************************************************************/
	private void setUpNorthPanel(){
		statLabel[0].setText("Game in progress"); //game status
		statPanel[0].add(statLabel[0]);
		//last label 0
	}
	
	/***********************************************************************
	 * This method is used to set up the east panel, statPanel[1]. It is
	 * used to display the controls of the game.
	 **********************************************************************/
	private void setUpEastPanel(){
		/*Holds the text for each label on the */
		String[] text = {"Use the arrow keys move","Press 'U' to undo "
				+ "the last turn","Press 'R' to reset the board","Press"
						+ "'N' to resize the board","Press 'Q' to quit the"
								+ " game"};
		statLabel[1].setText("" + text[0]);//arrow keys label
		statPanel[1].add(statLabel[1]);
		statLabel[2].setText("" + text[1]);//Undo Label
		statPanel[1].add(statLabel[2]);
		statLabel[3].setText("" + text[2]);//Reset board label
		statPanel[1].add(statLabel[3]);
		statLabel[4].setText("" + text[3]);//Resize board label
		statPanel[1].add(statLabel[4]);
		statLabel[5].setText("" + text[4]);//Quit game label
		statPanel[1].add(statLabel[5]);
		//last label 5
	}
	
	/***********************************************************************
	 * This method is used to display the south panel, statPanel[2]. It is
	 * used to display the number of wins and losses for the current gaming
	 * session. 
	 **********************************************************************/
	private void setUpSouthPanel(){
		/*Win and Loss label*/
		statLabel[6].setText("Wins: " + win + " Losses: " + loss);
		statPanel[2].add(statLabel[6]);
		//last label 6
	}
	
	/***********************************************************************
	 * This method is used to display the west statistics panel, 
	 * statPanel[3]. This panel will contain the base, winning value and 
	 * score of the game.
	 **********************************************************************/
	private void setUpWestPanel(){
		/*List of the text for the labels*/
		String[] text = {"Score: " + score,
				"Winning value: " + winValue, 
				"Base: " + base};
		statLabel[7].setText("" + text[0]);//score label
		statPanel[3].add(statLabel[7]);
		statLabel[8].setText("" + text[1]);//winning value label
		statPanel[3].add(statLabel[8]);
		statLabel[9].setText("" + text[2]);//base label
		statPanel[3].add(statLabel[9]);
	}
	
	/***********************************************************************
	 * This method dynamically Resizes the board. Based off input from the
	 * the user it obtains via JOption Pane, it will size the grid, grid 
	 * panel, and use game.resize to make a new game.
	 **********************************************************************/
	private void dynamicResize(){
		int[] specs = {0,0,0,0};
		//array of the text for each label in each JOptionPane
		String[] text = {"Enter the number of rows for the new board.",
				"Enter the number of colummns for the new board.",
				"Enter the numerical base for the new game.",
		"Enter the winning value of the game"};
		
		String input = "";
		
		/*changes the size of each JOptionPane*/
		UIManager.put("OptionPane.minimumSize",new Dimension(500,500));
		for(int x = 0; x < text.length; x++){
			input = JOptionPane.showInputDialog(""+text[x]);
			specs[x] = Integer.parseInt(input);
		}
		
		try//Tries setting up a game with the given parameters
		{
			game.resizeBoard2_0(specs[0],specs[1],specs[2],specs[3]);
		}
		catch(IllegalArgumentException exception)
		{
			JOptionPane.showMessageDialog
			(mainPanel,"One of the parameters was not compatable,"
					+ "Please enter valid parameters");
		}
		
		base = specs[2];
		winValue = specs[3];
		game.reset();
		mainPanel.remove(gridPanel);
		setUpGridPanel(specs[0],specs[1]);
	}
	
	/***********************************************************************
	 * This method will set up the grid panel and the grid used to display
	 * the board of the game.
	 * @param h the height of the board
	 * @param w the width of the board
	 **********************************************************************/
	private void setUpGridPanel(int h, int w){
		/*Set up grid panel*/
		setLayout(new GridLayout());
		gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(h,w));
		
		/*Set up grid*/
		grid = new JLabel[h][w];
		
		/*Loops through grid setting the font,border, and adding each
		 * JLabel of grid to the grid panel*/
		for(int r = 0; r < grid.length; r ++)
			for(int c = 0; c < grid[0].length; c++){
				//instantiate label
				grid[r][c] = new JLabel("69", SwingConstants.CENTER);
				//set font
				grid[r][c].setFont(new Font("serif", Font.PLAIN, 60));
				//set color 
				grid[r][c].setForeground(Color.white);
				//set border
				grid[r][c].setBorder
				(BorderFactory.createLineBorder(Color.black));
				grid[r][c].setOpaque(true);
				//add to grid panel
				gridPanel.add(grid[r][c]);
			}
		mainPanel.add(gridPanel, BorderLayout.CENTER);//add to center of main panel
	}
	
	/***********************************************************************
	 * This method will render all the tiles of the board, each time a valid
	 * move is made.
	 **********************************************************************/
	private void renderBoard(){
		int temp = 0;
		/* reset all the 2D array elements to ZERO */
		for (int r = 0; r < grid.length; r++)
			for (int c = 0; c < grid[0].length; c++){
				grid[r][c].setText("");	
				grid[r][c].setBackground(Color.WHITE);
			}
		
		/*Update Base, Winning Value, Win Loss, and Score*/
		statLabel[6].setText("Wins: " + win + " Losses: " + loss);
		statLabel[8].setText("Winning value: " + winValue);//winning value label
		statLabel[9].setText("Base: " + base);//base label
		
		/* fill in the 2D array using information for non-empty tiles */
		for (Cell c : game.getNonEmptyTiles()){
			grid[c.row][c.column].setText("" + c.value);
			temp += c.value;
			/*Assigns each tile a color based on its exponential*/
			switch(findPowerOfBase(c.value))
			{
			case 1: grid[c.row][c.column].setBackground(Color.BLUE);
			break;
			case 2: grid[c.row][c.column].setBackground(Color.BLUE);
			break;
			case 3: grid[c.row][c.column].setBackground(Color.CYAN);
			break;
			case 4: grid[c.row][c.column].setBackground(Color.CYAN);
			break;
			case 5: grid[c.row][c.column].setBackground(Color.YELLOW);
			break;
			case 6: grid[c.row][c.column].setBackground(Color.YELLOW);
			break;
			case 7: grid[c.row][c.column].setBackground(Color.ORANGE);
			break;
			case 8: grid[c.row][c.column].setBackground(Color.ORANGE);
			break;
			case 9: grid[c.row][c.column].setBackground(Color.MAGENTA);
			break;
			case 10: grid[c.row][c.column].setBackground(Color.MAGENTA);
			break;
			case 11: grid[c.row][c.column].setBackground(Color.RED);
			default: grid[c.row][c.column].setBackground(Color.RED);	
			}
		}
		score = temp;
		statLabel[7].setText("Score: " + score);//update score label	
	}
	
	/***********************************************************************
	 * This will calculate the exponential of the base
	 * @return the exponential of the base.
	 **********************************************************************/
	private int findPowerOfBase(int value){
		return (int)(Math.round(Math.log(value) / Math.log(base)));
	}
	
	/***********************************************************************
	 * This Class is used to receive input from the key board and respond
	 * accordingly.
	 * @author implemented by Anthony Sciarini
	 **********************************************************************/
	private class SlideListener implements KeyListener{
		@Override
		public void keyPressed(KeyEvent e) {
			
			switch(e.getKeyCode()){
			
			case KeyEvent.VK_UP://slide up
				game.slide(SlideDirection.UP);
				break;
				
			case KeyEvent.VK_RIGHT://slide right
				game.slide(SlideDirection.RIGHT);
				break;
				
			case KeyEvent.VK_DOWN://slide down
				game.slide(SlideDirection.DOWN);
				break;
				
			case KeyEvent.VK_LEFT://slide left
				game.slide(SlideDirection.LEFT);
				break;
				
			case KeyEvent.VK_U://undo last turn
				try{
				game.undo();
				}
				catch(IllegalStateException exception){
					System.out.println("EASY THERE LADDY!");
				}
				break;
				
			case KeyEvent.VK_R://reset the game
				statLabel[0].setText("Game in progress");
				gameIncomplete = true;
				game.reset();
				break;
				
			case  KeyEvent.VK_N://resize the board
				statLabel[0].setText("Game in progress");
				dynamicResize();//resizes the board
				renderBoard();//renders board
				gameIncomplete = true;
				break;
				
			case KeyEvent.VK_Q://exit the game
				System.exit(0);
				break;
			}
			
			/*Updates game status and renders board*/
			if(gameIncomplete){
				switch(game.getStatus()){
				
				case USER_LOST:
					statLabel[0].setText("Game Over!");
					loss++;
					gameIncomplete = false;
					break;
					
				case USER_WON:
					statLabel[0].setText("Victory!");
					win++;
					gameIncomplete = false;
					break;
				}
				renderBoard();
			}
		}
		
		/*******************************************************************
		 * This method is used to respond to when a key is released
		 ******************************************************************/
		@Override
		public void keyReleased(KeyEvent e) {}
		
		/*******************************************************************
		 * This method is used to respond the when a key is typed
		 ******************************************************************/
		@Override
		public void keyTyped(KeyEvent e) {}
	}
	/***********************************************************************
	 * This method is used to set the font size of a label
	 * @param label a JLabel
	 * @param fontSize the size the font will be set to
	 **********************************************************************/
	private void formatLabel(JLabel label, int fontSize){
		label.setFont(new Font("Serif", Font.PLAIN, fontSize));
	}
}