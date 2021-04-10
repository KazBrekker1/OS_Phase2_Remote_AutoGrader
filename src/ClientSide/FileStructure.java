package ClientSide;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.Font;

public class FileStructure extends JFrame {
	private JTextField prjNo;
	private JTextField teamNo;

	private JFrame frame;
	private JLabel lblDueDate;
	private JTextField dueDate;
	private JTextField pwdTextField;
	private JLabel lblPleaseEnterThe;

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileStructure window = new FileStructure();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FileStructure() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		prjNo = new JTextField();
		prjNo.setBounds(216, 113, 147, 19);
		frame.getContentPane().add(prjNo);
		prjNo.setColumns(10);

		JLabel lblTeamNumber = new JLabel("project number");
		lblTeamNumber.setFont(new Font("Abyssinica SIL", Font.ITALIC, 20));
		lblTeamNumber.setBounds(47, 108, 157, 31);
		frame.getContentPane().add(lblTeamNumber);

		JLabel lblTeamNumber_1 = new JLabel("team number");
		lblTeamNumber_1.setFont(new Font("Abyssinica SIL", Font.ITALIC, 20));
		lblTeamNumber_1.setBounds(57, 151, 147, 27);
		frame.getContentPane().add(lblTeamNumber_1);

		teamNo = new JTextField();
		teamNo.setBounds(216, 154, 147, 19);
		frame.getContentPane().add(teamNo);
		teamNo.setColumns(10);

		JButton creat = new JButton("create file structure");
		creat.setBounds(140, 227, 181, 31);
		frame.getContentPane().add(creat);

		lblDueDate = new JLabel("due date");
		lblDueDate.setFont(new Font("Abyssinica SIL", Font.ITALIC, 20));
		lblDueDate.setBounds(72, 71, 104, 25);
		frame.getContentPane().add(lblDueDate);

		dueDate = new JTextField();
		dueDate.setBounds(216, 73, 147, 19);
		frame.getContentPane().add(dueDate);
		dueDate.setColumns(10);

		JLabel lblTeamPassword = new JLabel("team password");
		lblTeamPassword.setFont(new Font("Abyssinica SIL", Font.ITALIC, 20));
		lblTeamPassword.setBounds(47, 187, 165, 28);
		frame.getContentPane().add(lblTeamPassword);

		pwdTextField = new JTextField();
		pwdTextField.setColumns(10);
		pwdTextField.setBounds(216, 191, 147, 19);
		frame.getContentPane().add(pwdTextField);

		lblPleaseEnterThe = new JLabel("PLEASE FILL THE FOLLOWING FILEDS");
		lblPleaseEnterThe.setFont(new Font("Abyssinica SIL", Font.BOLD | Font.ITALIC, 20));
		lblPleaseEnterThe.setBounds(26, 12, 412, 34);
		frame.getContentPane().add(lblPleaseEnterThe);
		creat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client client = Client.getInstance();
				client.intialize(5, client.getSbj(), teamNo.getText(), prjNo.getText(), dueDate.getText(),
						getMd5(pwdTextField.getText()));
				JOptionPane.showMessageDialog(null, "Successfully Created File Structure");
				dueDate.setText(null);
				prjNo.setText(null);
				teamNo.setText(null);
				pwdTextField.setText(null);
			}
		});

	}

	public static String getMd5(String input) {
		try {
			// Static getInstance method is called with hashing MD5
			MessageDigest md = MessageDigest.getInstance("MD5");

			// digest() method is called to calculate message digest
			// of an input digest() return array of byte
			byte[] messageDigest = md.digest(input.getBytes());

			// Convert byte array into sig num representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			StringBuilder hashtext = new StringBuilder(no.toString(16));
			while (hashtext.length() < 32) {
				hashtext.insert(0, "0");
			}
			return hashtext.toString();
		}
		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
