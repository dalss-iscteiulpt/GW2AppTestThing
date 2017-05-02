import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import me.nithanim.gw2api.v2.GuildWars2Api;

public class BuildNotifier extends JFrame {

	private JPanel contentPane;
	private final JTextField pathField = new JTextField();
	private final JLabel lblPath = new JLabel("Path to File");
	private final JTextArea informationField = new JTextArea();
	private final JSpinner timeSpinner = new JSpinner();
	private final JLabel lblTimeBetweenChecks = new JLabel("Loop (minutes)");
	private final JLabel lblCurrentBuild = new JLabel();
	private final JButton btnStart = new JButton("Start");
	private final JButton btnStop = new JButton("Stop");
	private final JLabel lblInformationDetails = new JLabel("Information Details");
	private final JLabel status = new JLabel("Running");
	private final JLabel newBuildlabel = new JLabel("New Build: None");

	private GuildWars2Api gw2api;
	private int currentBuild;
	private int minuteLoop;
	private int MINUTE_TO_MILLIS=60000;
	private String path;
	private BuildNotifierThread activeThread;
	



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BuildNotifier frame = new BuildNotifier();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public BuildNotifier() {
		setResizable(false);
		gw2api = new GuildWars2Api();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 292, 216);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		pathField.setBounds(0, 118, 265, 20);
		pathField.setColumns(10);
		pathField.setText("H:\\Gaming\\Guild Wars 2\\Gw2-64.exe");
		contentPane.add(pathField);

		lblPath.setBackground(Color.WHITE);
		lblPath.setBounds(0, 104, 72, 14);
		contentPane.add(lblPath);

		informationField.setBounds(0, 164, 286, 23);
		contentPane.add(informationField);
		timeSpinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));

		timeSpinner.setBounds(0, 78, 62, 20);
		contentPane.add(timeSpinner);

		lblTimeBetweenChecks.setBounds(0, 64, 104, 14);
		contentPane.add(lblTimeBetweenChecks);

		lblCurrentBuild.setBounds(0, 13, 143, 14);
		contentPane.add(lblCurrentBuild);
		currentBuild = gw2api.build().get().getId();
		lblCurrentBuild.setText("Current Build: "+currentBuild);
		btnStart.setBackground(Color.WHITE);

		btnStart.setBounds(187, 9, 89, 23);
		btnStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					startButtonAction();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});
		contentPane.add(btnStart);
		btnStop.setBackground(Color.WHITE);
		btnStop.setBounds(187, 34, 89, 23);
		btnStop.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stopButtonAction();

			}
		});
		contentPane.add(btnStop);

		lblInformationDetails.setBounds(0, 149, 143, 14);

		contentPane.add(lblInformationDetails);
		status.setForeground(Color.BLACK);
		status.setBackground(Color.LIGHT_GRAY);
		status.setHorizontalAlignment(SwingConstants.CENTER);
		status.setBounds(187, 63, 89, 18);
		status.setOpaque(true);

		contentPane.add(status);
		newBuildlabel.setBounds(0, 38, 143, 14);
		
		contentPane.add(newBuildlabel);
	}

	public void startButtonAction() throws InterruptedException, IOException{
		informationField.setText("");
		minuteLoop = (Integer) timeSpinner.getValue();
		path = pathField.getText();
		if(minuteLoop != 0 && !path.equals("")){
			activeThread = new BuildNotifierThread();
			activeThread.start();
			status.setBackground(new Color(44,172,70));
			btnStart.setEnabled(false);
		}
		if(minuteLoop == 0){
			informationField.append("Loop time can't be 0 minutes \n");
		}
		if(path.equals("")){
			informationField.append("Path can't be empty \n");
		}

	}

	public void stopButtonAction() {
		activeThread.interrupt();
		status.setBackground(new Color(229,52,52));
		btnStart.setEnabled(true);

	}

	public void cleanLine(int n){
		int end;
		try {
			end = informationField.getLineEndOffset(n);
			informationField.replaceRange("", 0, end);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public class BuildNotifierThread extends Thread{
		private RunningThread running= new RunningThread();

		@Override
		public void run() {
			running.start();
			while(true){
				int newBuild = gw2api.build().get().getId();
				if(currentBuild != newBuild){
					running.interrupt();
					newBuildlabel.setText("New Build: "+newBuild);
					informationField.setText("");
					informationField.append("New Build Available: "+newBuild);
					path=path.replace("\0", "\\");
					status.setBackground(new Color(229,52,52));
					try {
						Process process = Runtime.getRuntime().exec(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				} else {
					try {
						Thread.sleep(minuteLoop*MINUTE_TO_MILLIS);
					} catch (InterruptedException e) {
						informationField.setText("");
						informationField.append("Thread Stopped \n");
						running.interrupt();
						break;
					}
				}
			}
		}

		public class RunningThread extends Thread{
			@Override
			public void run() {
				try {
					while(true){
						informationField.setText("");
						informationField.append(". ");
						Thread.sleep(500);
						informationField.append(". ");
						Thread.sleep(500);
						informationField.append(". ");
						Thread.sleep(500);
					}
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
