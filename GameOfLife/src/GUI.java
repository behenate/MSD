import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class containing GUI: board + buttons
 */
public class GUI extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private Timer timer;
	private Board board;
	private JButton start;
	private JButton clear;
	private JSlider pred;
	private JFrame frame;
	private int iterNum = 0;
	private final int maxDelay = 500;
	private final int initDelay = 100;
	private boolean running = false;

	public GUI(JFrame jf) {
		frame = jf;
		timer = new Timer(initDelay, this);
		timer.stop();
	}

	/**
	 * @param container to which GUI and board is added
	 */
	public void initialize(Container container) {
		container.setLayout(new BorderLayout());
		container.setSize(new Dimension(1024, 768));

		JPanel buttonPanel = new JPanel();
		JPanel modesPanel = new JPanel();

		start = new JButton("Start");
		start.setActionCommand("Start");
		start.setToolTipText("Starts clock");
		start.addActionListener(this);

		clear = new JButton("Clear");
		clear.setActionCommand("clear");
		clear.setToolTipText("Clears the board");
		clear.addActionListener(this);

		pred = new JSlider();
		pred.setMinimum(0);
		pred.setMaximum(maxDelay);
		pred.setToolTipText("Time speed");
		pred.addChangeListener(this);
		pred.setValue(maxDelay - timer.getDelay());

		buttonPanel.add(start);
		buttonPanel.add(clear);
		buttonPanel.add(pred);

//		Mode and rule buttons
		JButton standard = new JButton("Standard Mode");
		standard.setActionCommand("StandardMode");
		standard.addActionListener(this);
		modesPanel.add(standard);

		JButton cities = new JButton("Cities Mode");
		cities.setActionCommand("cities");
		cities.addActionListener(this);
		modesPanel.add(cities);

		JButton coral = new JButton("Coral Mode");
		coral.setActionCommand("coral");
		coral.addActionListener(this);
		modesPanel.add(coral);

		JButton rain = new JButton("Rain Mode");
		rain.setActionCommand("RainMode");
		rain.addActionListener(this);
		modesPanel.add(rain);


		board = new Board(1024, 768 - buttonPanel.getHeight());
		container.add(board, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
		container.add(modesPanel, BorderLayout.NORTH);
	}

	/**
	 * handles clicking on each button
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(timer)) {
			iterNum++;
			frame.setTitle("Game of Life (" + Integer.toString(iterNum) + " iteration)");
			board.iteration();
		} else {
			String command = e.getActionCommand();
			if (command.equals("Start")) {
				if (!running) {
					timer.start();
					start.setText("Pause");
				} else {
					timer.stop();
					start.setText("Start");
				}
				running = !running;
				clear.setEnabled(true);

			}
			else if (command.equals("clear")) {
				iterNum = 0;
				timer.stop();
				start.setEnabled(true);
				board.clear();
				frame.setTitle("Cellular Automata Toolbox");
			}
			else if(command.equals("StandardMode")){
				frame.setTitle("Game of Life (" + Integer.toString(iterNum) + " iteration)");
				board.setMode("standard");
				board.setRule("standard");
			}else if(command.equals("RainMode")){
				frame.setTitle("Rain (" + Integer.toString(iterNum) + " iteration)");
				board.setMode("rain");
			}else if (command.equals("cities")){
				frame.setTitle("Game of Life City(" + Integer.toString(iterNum) + " iteration)");
				board.setRule("cities");
			}else if (command.equals("coral")){
				frame.setTitle("Game of Life Coral(" + Integer.toString(iterNum) + " iteration)");
				board.setRule("coral");
			}

		}
	}

	/**
	 * slider to control simulation speed
	 * @see ChangeListener#stateChanged(ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		timer.setDelay(maxDelay - pred.getValue());
	}
}
