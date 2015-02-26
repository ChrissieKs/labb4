package lab4.gui;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import lab4.data.GameGrid;
import lab4.data.GameSquareType;

/**
 * A panel providing a graphical view of the game board
 */
public class GamePanel extends JPanel implements Observer{

	final int UNIT_SIZE = 20;
	private GameGrid grid;

	/**
	 * The constructor
	 *
	 * @param grid The grid that is to be rendered
	 */
	public GamePanel(GameGrid grid){
		this.grid = grid;
		this.grid.addObserver(this);
		Dimension d = new Dimension(grid.getSize()*UNIT_SIZE+1, grid.getSize()*UNIT_SIZE+1);
		this.setMinimumSize(d);
		this.setPreferredSize(d);
		this.setBackground(Color.WHITE);
	}

	/**
	 * Returns a grid position given pixel coordinates
	 * of the panel
	 *
	 * @param x the x coordinates
	 * @param y the y coordinates
	 * @return an integer array containing the [x, y] grid position
	 */
	public int[] getGridPosition(int x, int y){
		x = x/UNIT_SIZE;
		y = y/UNIT_SIZE;

		return new int[] { x, y };
	}

	public void update(Observable o, Object arg) {
		repaint();
	}

	public void paintComponent(Graphics graphics){
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D)graphics;

		int size = grid.getSize();
		for(int x = 0; x<size; x++) {
			g.setColor(Color.GRAY);
			g.drawLine(x*UNIT_SIZE, 0, x*UNIT_SIZE, size*UNIT_SIZE);
			for(int y = 0; y<size; y++) {
				if(x == 0) {
					g.drawLine(0, y*UNIT_SIZE, size*UNIT_SIZE, y*UNIT_SIZE);
				}

				GameSquareType type = grid.getLocation(x, y);
				switch(type) {
					case Other:
						g.setColor(Color.LIGHT_GRAY);
						g.fillOval(x * UNIT_SIZE, y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
						break;

					case Me:
						g.setColor(Color.BLACK);
						g.fillOval(x*UNIT_SIZE, y*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
						break;

					default: break;
				}
			}
		}
		g.setColor(Color.GRAY);
		g.drawLine(0, size*UNIT_SIZE, size*UNIT_SIZE, size*UNIT_SIZE);
		g.drawLine(size*UNIT_SIZE, 0, size*UNIT_SIZE, size*UNIT_SIZE);
	}

}
