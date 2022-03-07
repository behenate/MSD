import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
 * Board with Points that may be expanded (with automatic change of cell
 * number) with mouse event listener
 */

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	private Point[][] points;
	private int size = 14;
//	Determines the current simulation mode
	private String mode = "standard";
	public Board(int length, int height) {
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);
	}

	// single iteration
	public void iteration() {
		if (Objects.equals(mode, "rain")){
			for (Point[] point : points) {
				point[0].drop();
			}
		}

		for (Point[] point : points)
			for (Point value : point) {
				value.calculateNewState();
				value.changeState();
			}
		this.repaint();
	}

	// clearing board
	public void clear() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y].setState(0);
			}
		this.repaint();
	}
//  Inits the simulation
	private void initialize(int length, int height) {
		points = new Point[length][height];

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y] = new Point();

		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y) {
				if (!Objects.equals(mode, "rain"))
					initCell(x,y);
				else
					initCellRain(x,y);
			}
		}
	}

//	Used for cell initialization in standard simulation
	private void initCell(int x, int y){
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (i==0 && j==0){
					continue;
				}
				if ( x+i >= 0 && y+j >= 0 && x+i < points.length && y+j < points[x].length){
					points[x][y].addNeighbor(points[x+i][y+j]);
				}
			}
		}
	}

//	Used for cell initialization in rain simulation
	private void initCellRain(int x,int y){
		if (y+1 < points[x].length){
			points[x][y].addNeighbor(points[x][y+1]);
		}
	}

//	Sets mode for all points
	private void setPointsMode(String mode){
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y].setMode(mode);
	}

//	Sets rule for all points
	private void setPointsRule(String rule){
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y].setRule(rule);
	}

	//paint background and separators between cells
	protected void paintComponent(Graphics g) {
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g.setColor(Color.GRAY);
		drawNetting(g, size);
	}

	// draws the background netting
	private void drawNetting(Graphics g, int gridSpace) {
		Insets insets = getInsets();
		int firstX = insets.left;
		int firstY = insets.top;
		int lastX = this.getWidth() - insets.right;
		int lastY = this.getHeight() - insets.bottom;

		int x = firstX;
		while (x < lastX) {
			g.drawLine(x, firstY, x, lastY);
			x += gridSpace;
		}

		int y = firstY;
		while (y < lastY) {
			g.drawLine(firstX, y, lastX, y);
			y += gridSpace;
		}

//		Color palettes
		for (x = 0; x < points.length; ++x) {
			for (y = 0; y < points[x].length; ++y) {
				Color[] standardColors = {
					new Color(0xffffff),
					new Color(0x0000ff),
					new Color(0x00ff00),
					new Color(0xff0000),
					new Color(0x000000),
					new Color(0x444444),
					new Color(0xffffff)
				};
				Color[] rainColors = {
					new Color(0xffffff),
					new Color(0xF9ECFF),
					new Color(0xCDECFF),
					new Color(0xB2E1FB),
					new Color(0x8BD3FF),
					new Color(0x50BFFF),
					new Color(0x6EC5FD)
				};
//				Set proper colour palette

				if (points[x][y].getState() != 0) {
					if (Objects.equals(mode, "rain")){
						g.setColor(rainColors[points[x][y].getState()]);
					}else {
						g.setColor(standardColors[points[x][y].getState()]);
					}
					g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
				}
			}
		}

	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
			points[x][y].clicked();
			this.repaint();
		}
	}

	public void componentResized(ComponentEvent e) {
		int dlugosc = (this.getWidth() / size) + 1;
		int wysokosc = (this.getHeight() / size) + 1;
		initialize(dlugosc, wysokosc);
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
			points[x][y].setState(1);
			this.repaint();
		}
	}
//  Changes simulation mode
	public void setMode(String mode){
		if (!Objects.equals(mode, this.mode)){
			this.mode = mode;
			this.clear();
			this.initialize(points.length, points[0].length);
			this.setPointsMode(mode);
		}
	}
//	Sets rule and switches to standard simulation
	public void setRule(String rule){
		setMode("standard");
		setPointsRule(rule);
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}
