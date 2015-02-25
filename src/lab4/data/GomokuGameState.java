/*
 * Created on 2007 feb 8
 */
package lab4.data;

import java.util.Observable;
import java.util.Observer;

import lab4.client.GomokuClient;

/**
 * Represents the state of a game
 */

public class GomokuGameState extends Observable implements Observer {

   // Game variables
	private final int DEFAULT_SIZE = 15;
	private GameGrid gameGrid;
	
    //Possible game states
	private final int NOT_STARTED = 0;
	private final int MY_TURN = 1;
	private final int OTHER_TURN = 2;
	private final int FINISHED = 3;

	private int currentState = 0;
	
	private GomokuClient client;
	
	private String message;
	
	/**
	 * The constructor
	 * 
	 * @param gc The client used to communicate with the other player
	 */
	public GomokuGameState(GomokuClient gc){
		client = gc;
		client.addObserver(this);
		gc.setGameState(this);
		currentState = NOT_STARTED;
		gameGrid = new GameGrid(DEFAULT_SIZE);
	}
	

	/**
	 * Returns the message string
	 * 
	 * @return the message string
	 */
	public String getMessageString(){
		return message;
	}

	/**
	 * Sets the message string and notifies observers
	 *
	 * @param msg the message to set
	 */
	public void setMessageString(String msg) {
		message = msg;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Returns the game grid
	 * 
	 * @return the game grid
	 */
	public GameGrid getGameGrid(){
		return gameGrid;
	}

	/**
	 * This player makes a move at a specified location
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void move(int x, int y){
		if(currentState == NOT_STARTED) {
			setMessageString("Game not started");
			return;
		}
		if(currentState == FINISHED) {
			return;
		}
		if(currentState != MY_TURN) {
			setMessageString("Not your turn");
			return;
		}

		if(gameGrid.move(x, y, GameSquareType.Me)) {
			client.sendMoveMessage(x, y);
			setMessageString(String.format("You moved to (%d,%d)", x, y));
			if(gameGrid.isWinner(GameSquareType.Me)) {
				currentState = FINISHED;
				setMessageString("You won!");
			}
			currentState = OTHER_TURN;
		} else {
			setMessageString("Could not move there!");
		}
	}
	
	/**
	 * Starts a new game with the current client
	 */
	public void newGame(){
		client.sendNewGameMessage();
		gameGrid.clearGrid();
		currentState = OTHER_TURN;
		setMessageString("Game started, others turn");
	}
	
	/**
	 * Other player has requested a new game, so the 
	 * game state is changed accordingly
	 */
	public void receivedNewGame() {
		gameGrid.clearGrid();
		currentState = MY_TURN;
		setMessageString("Game started, your turn");
	}
	
	/**
	 * The connection to the other player is lost, 
	 * so the game is interrupted
	 */
	public void otherGuyLeft(){
		gameGrid.clearGrid();
		currentState = NOT_STARTED;
		setMessageString("Other guy left, GGWP");
	}
	
	/**
	 * The player disconnects from the client
	 */
	public void disconnect(){
		client.disconnect();
		gameGrid.clearGrid();
		currentState = NOT_STARTED;
		setMessageString("Disconnected");
	}
	
	/**
	 * The player receives a move from the other player
	 * 
	 * @param x The x coordinate of the move
	 * @param y The y coordinate of the move
	 */
	public void receivedMove(int x, int y){
		if(gameGrid.move(x, y, GameSquareType.Other)) {
			setMessageString(String.format("Other guy moved to (%d,%d)", x, y));
			if(gameGrid.isWinner(GameSquareType.Other)) {
				currentState = FINISHED;
				setMessageString("Other guy won!");
			}
			currentState = MY_TURN;
		} else {
			setMessageString("Other guy sent illegal move, GG");
		}
	}

	/**
	 * Function that is called when an observable notifies the observers,
	 * responsible for determining the current state after a connection to a client has been made.
	 *
	 * @param o The observable that sent it
	 * @param arg The object that is being observed
	 */
	public void update(Observable o, Object arg) {
		
		switch(client.getConnectionStatus()){
		case GomokuClient.CLIENT:
			message = "Game started, it is your turn!";
			currentState = MY_TURN;
			break;
		case GomokuClient.SERVER:
			message = "Game started, waiting for other player...";
			currentState = OTHER_TURN;
			break;
		}
		setChanged();
		notifyObservers();
		
		
	}
	
}
