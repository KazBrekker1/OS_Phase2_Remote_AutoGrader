package ClientSide;

import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class Uploading extends JFrame {

	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Uploading frame = new Uploading();
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
	public Uploading() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		JComboBox<String> comboBox = new JComboBox<>();
		comboBox.setBounds(126, 145, 176, 24);
		contentPane.add(comboBox);
		contentPane.setLayout(null);

		JLabel lblThisIsUploading = new JLabel("Choose The Service");
		lblThisIsUploading.setBounds(155, 12, 181, 42);
		contentPane.add(lblThisIsUploading);

		JButton btnGetClient = new JButton("Submit File");
		btnGetClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client client = Client.getInstance();
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("."));
				chooser.setDialogTitle("Browse the folder to process");
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					try {
						client.sendFile(2, chooser.getSelectedFile().getPath());
						JOptionPane.showMessageDialog(null, "Submitted Successfully");
						int trialNumber = Integer.parseInt(client.getTrailNo());
						int nextTrialNumber = ++trialNumber;
						client.setTrailNo(String.valueOf(nextTrialNumber));
						comboBox.addItem("Trial " + nextTrialNumber);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else {
					System.out.println("No Selection ");
				}
			}
		});
		btnGetClient.setBounds(126, 66, 176, 71);
		contentPane.add(btnGetClient);

		comboBox.addItem("Team Info");
		comboBox.addItem("Final Grade");
		Client client = Client.getInstance();
		for (int i = 1; i <= Integer.parseInt(client.getTrailNo()); i++) {
			comboBox.addItem("Trial " + i);
		}

		JButton btnDownload = new JButton("Download");
		btnDownload.setBounds(157, 180, 117, 25);
		contentPane.add(btnDownload);

		JButton btnSignOut = new JButton("SIGN OUT");
		btnSignOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.err.println(client.signOut(client.getUserName()));
				System.exit(0);

			}
		});
		btnSignOut.setBounds(155, 217, 117, 25);
		contentPane.add(btnSignOut);
		btnDownload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					client.recieve(3, comboBox.getSelectedItem().toString(), client.getSbj(), client.getTeamNo(),
							client.getPrjNo());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
	}
}
