import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUI extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private Timer timer;
	private Board board;
	private JButton start;
	private JButton clear;
	private JButton moore;
	private JButton vonNeumann;
	private JButton antialias;
	private JButton wallRepulsion;
	private JComboBox<Integer> drawType;
	private JSlider pred;
	private JFrame frame;
	JSlider maxFieldSlider;
	private int iterNum = 0;
	private final int maxDelay = 500;
	private final int initDelay = 100;
	private boolean running = false;

	public GUI(JFrame jf) {
		frame = jf;
		timer = new Timer(initDelay, this);
		timer.stop();
	}

	public void initialize(Container container) {
		container.setLayout(new BorderLayout());
		container.setSize(new Dimension(1024, 768));

		JPanel buttonPanel = new JPanel();
		JPanel buttonSecondRow = new JPanel();
		JPanel buttonThirdRow = new JPanel();
		Container allButtons = new Container();
		allButtons.setLayout(new GridLayout(3,1));

		start = new JButton("Start");
		start.setActionCommand("Start");
		start.addActionListener(this);

		clear = new JButton("Calc Field");
		clear.setActionCommand("clear");
		clear.addActionListener(this);
		
		pred = new JSlider();
		pred.setMinimum(0);
		pred.setMaximum(maxDelay);
		pred.addChangeListener(this);
		pred.setValue(maxDelay - timer.getDelay());
		
		drawType = new JComboBox<Integer>(Point.types);
		drawType.addActionListener(this);
		drawType.setActionCommand("drawType");

		moore = new JButton("Set Moore Neigh");
		moore.setActionCommand("setMoore");
		moore.addActionListener(this);

		vonNeumann = new JButton("Set Neumann Neigh");
		vonNeumann.setActionCommand("setVonNeumann");
		vonNeumann.addActionListener(this);

		antialias = new JButton("Turn off antialiasing");
		antialias.setActionCommand("antialias");
		antialias.addActionListener(this);

		wallRepulsion = new JButton("Turn off wall repulsion");
		wallRepulsion.setActionCommand("wallRepulsion");
		wallRepulsion.addActionListener(this);

		clear = new JButton("Calc Field");
		clear.setActionCommand("clear");
		clear.addActionListener(this);

		maxFieldSlider = new JSlider();
		maxFieldSlider.setMinimum(100);
		maxFieldSlider.setMaximum(5000);
		maxFieldSlider.addChangeListener(this);
		maxFieldSlider.setValue(1000);
		maxFieldSlider.setSize(new Dimension(1000,100));

		buttonPanel.add(start);
		buttonPanel.add(clear);
		buttonSecondRow.add(antialias);
		buttonSecondRow.add(wallRepulsion);
		buttonSecondRow.add(moore);
		buttonSecondRow.add(vonNeumann);
		buttonThirdRow.add(new Label("Static Field display:   Closer"));
		buttonThirdRow.add(maxFieldSlider);
		buttonThirdRow.add(new Label("Further"));

		buttonPanel.add(drawType);
		buttonPanel.add(pred);


		board = new Board(1024, 768 - buttonPanel.getHeight());
		container.add(board, BorderLayout.CENTER);
		allButtons.add(buttonPanel);
		allButtons.add(buttonSecondRow);
		allButtons.add(buttonThirdRow);
		container.add(allButtons, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(timer)) {
			iterNum++;
			frame.setTitle("Pedestrian follow-up simulation (" + Integer.toString(iterNum) + " iteration)");
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

			} else if (command.equals("clear")) {
				iterNum = 0;
				start.setEnabled(true);
				board.clear();
				frame.setTitle("Cellular Automata Toolbox");
			}
			else if (command.equals("drawType")){
				int newType = (Integer)drawType.getSelectedItem();
				board.editType = newType;
			}else if (command.equals("setMoore")){
				board.setNeighbourhood(0);
			}
			else if (command.equals("setVonNeumann")){
				board.setNeighbourhood(1);
			}else if(command.equals("antialias")){
				if (antialias.getText().equals("Turn off antialiasing")){
					antialias.setText("Turn on antialiasing");
					board.setAntialiasing(false);
				}else{
					antialias.setText("Turn off antialiasing");
					board.setAntialiasing(true);
				}
			}else if(command.equals("wallRepulsion")){
				if (wallRepulsion.getText().equals("Turn off wall repulsion")){
					wallRepulsion.setText("Turn on wall repulsion");
					board.setWallRepulsion(false);
				}else{
					wallRepulsion.setText("Turn off wall repulsion");
					board.setWallRepulsion(true);
				}
			}
		}
	}

	public void stateChanged(ChangeEvent e) {

		timer.setDelay(maxDelay - pred.getValue());
		if (e.getSource().equals(maxFieldSlider) && board != null){
			board.setMaxStaticField(maxFieldSlider.getValue());
		}

	}
}
