import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JPanel implements ActionListener, ChangeListener {
    private static final long serialVersionUID = 1L;
    private Timer timer;
    private Board board;
    private JButton start;
    private JButton clear;
    private JButton const_mode;
    private JButton nagel_mode;
    private JComboBox<Integer> drawType;
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

    public void initialize(Container container) {
        container.setLayout(new BorderLayout());
        container.setSize(new Dimension(1024, 768));

        JPanel buttonPanel = new JPanel();
        JPanel modePanel = new JPanel();

        start = new JButton("Start");
        start.setActionCommand("Start");
        start.addActionListener(this);

        clear = new JButton("Clear");
        clear.setActionCommand("clear");
        clear.addActionListener(this);

        const_mode = new JButton("Const Speed");
        const_mode.setActionCommand("const");
        const_mode.addActionListener(this);

        nagel_mode = new JButton("Nagel-Schreckenberg");
        nagel_mode.setActionCommand("nagel");
        nagel_mode.addActionListener(this);

        pred = new JSlider();
        pred.setMinimum(0);
        pred.setMaximum(maxDelay);
        pred.addChangeListener(this);
        pred.setValue(maxDelay - timer.getDelay());

        buttonPanel.add(start);
        buttonPanel.add(clear);
        buttonPanel.add(pred);

        modePanel.add(const_mode);
        modePanel.add(nagel_mode);

        board = new Board(1024, 768 - buttonPanel.getHeight());
        container.add(board, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        container.add(modePanel, BorderLayout.NORTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(timer)) {
            iterNum++;
            frame.setTitle("Cars simulation (" + Integer.toString(iterNum) + " iteration)");
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
                timer.stop();
                start.setEnabled(true);
                board.clear();
                frame.setTitle("Cars Toolbox");
            } else if (command.equals("drawType")) {
                int newType = (Integer) drawType.getSelectedItem();
                board.editType = newType;
            }else if (command.equals("const")){
                board.setMode(0);
            }else if (command.equals("nagel")){
                board.setMode(1);
            }

        }
    }

    public void stateChanged(ChangeEvent e) {
        timer.setDelay(maxDelay - pred.getValue());
    }
}
