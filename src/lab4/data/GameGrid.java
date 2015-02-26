package lab4.data;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;

/**
 * Represents the 2D game grid
 */
public class GameGrid extends Observable{

	public final int INROW = 5;

	private int _size;
	private GameSquareType[][] _board;

	@SuppressWarnings("unchecked")
	private ArrayList<Point>[] _signatures;
	
	/**
	 * Constructor
	 * 
	 * @param size The width/height of the game grid
	 */
	public GameGrid(int size){
		_size = size;
		_board = new GameSquareType[size][size];
		clearGrid();

		// Bygg 4 signaturer som kan verifiera en vinnare horisontellt, vertikalt, och snett både åt sydväst och sydöst (från den punkt den kollar påå)
		_signatures = (ArrayList<Point>[])new ArrayList<?>[4];
		_signatures[0] = new ArrayList<Point>();
		_signatures[1] = new ArrayList<Point>();
		_signatures[2] = new ArrayList<Point>();
		_signatures[3] = new ArrayList<Point>();
		// Fyll listorna med offset-punkter
		for(int i = 0; i < INROW; i++) {
			_signatures[0].add(new Point(0,i));
			_signatures[1].add(new Point(i, 0));
			_signatures[2].add(new Point(i, i));
			_signatures[3].add(new Point(-i, i));
		}
	}
	
	/**
	 * Reads a location of the grid
	 * 
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return the value of the specified location
	 */
	public GameSquareType getLocation(int x, int y){
		return _board[x][y];
	}

	/**
	 * Returns the size of the grid
	 * 
	 * @return the grid size
	 */
	public int getSize(){
		return _size;
	}
	
	/**
	 * Enters a move in the game grid
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @param player the player to move
	 * @return true if the insertion worked, false otherwise
	 */
	public boolean move(int x, int y, GameSquareType player){
		if(x >= _size || y >= _size || x < 0 || y < 0) // Kolla om coords ligger utanför gamegrid
			return false;

		if(_board[x][y] != GameSquareType.Empty) // Kolla om rutan är tom eller ej
			return false;
		_board[x][y] = player;

		setChanged();
		notifyObservers();
		return true;
	}
	
	/**
	 * Clears the grid of pieces
	 */
	public void clearGrid(){ // Loopar igenom varje array och fyller den med tomma rutor
		for(int x = 0; x < _size; x++) {
			Arrays.fill(_board[x], GameSquareType.Empty);
		}
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Check if a player has 5 in row
	 * 
	 * @param player the player to check for
	 * @return true if player has 5 in row, false otherwise
	 */
	public boolean isWinner(GameSquareType player){
		for(int y = 0; y < _size; y++) { // för varje y
			for(int x = 0; x < _size; x++) { // för varje x
				for(ArrayList<Point> sig : _signatures) { // loopa igenom varje signatur
					if(checkSignature(x, y, sig, player)) // kolla signaturen mot x och y
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if a signature matches a certain player at the current position
	 * The signature is a list of points with offsets for each square to check
	 * If any part of the signature is outside of the board, the function returns false
	 *
	 * @param x The x coordinate for the position to check
	 * @param y The y coordinate for the position to check
	 * @param sig The signature to check
	 * @param player The player to check for
	 * @return
	 */
	private boolean checkSignature(int x, int y, ArrayList<Point> sig, GameSquareType player) {
		for(Point p : sig) { // för varje offset i signaturen
			if(x + p.x >= _size || y + p.y >= _size || x + p.x < 0 || y + p.y < 0) // ligger punkten + offset utanför gamegrid?
				return false;
			if(_board[x+p.x][y+p.y] != player) // är punkten + offset ej lika med player?
				return false;
		}
		return true;
	}
	
	
}
