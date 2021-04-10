package ClientSide;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class Interface extends JFrame {

	private JFrame frame;
	private JTextField UserName;
	private JTextField Password;

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
					Interface window = new Interface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Interface() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		UserName = new JTextField();
		UserName.setBounds(189, 89, 191, 27);
		frame.getContentPane().add(UserName);
		UserName.setColumns(10);

		Password = new JPasswordField();
		Password.setColumns(10);
		Password.setBounds(189, 147, 191, 27);
		frame.getContentPane().add(Password);

		JLabel lblPleaseEnterYour = new JLabel("Username");
		lblPleaseEnterYour.setFont(new Font("DejaVu Serif", Font.BOLD, 20));
		lblPleaseEnterYour.setBounds(57, 85, 157, 31);
		frame.getContentPane().add(lblPleaseEnterYour);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("DejaVu Serif", Font.BOLD, 20));
		lblPassword.setBounds(57, 143, 157, 31);
		frame.getContentPane().add(lblPassword);

		JLabel lblSignIn = new JLabel("Team Sign In");
		lblSignIn.setFont(new Font("Abyssinica SIL", Font.BOLD | Font.ITALIC, 22));
		lblSignIn.setBounds(148, 27, 165, 34);
		frame.getContentPane().add(lblSignIn);

		JButton btnLogIn = new JButton("Log In");
		btnLogIn.setToolTipText("");
		btnLogIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Call client Class
				Client client = Client.getInstance();
				String[] authResponse = client
						.isValidUser(1, UserName.getText().toLowerCase(), getMd5(Password.getText())).split("_");

				if (authResponse[0].equals("Auth Success")) {
					String str = UserName.getText();
					client.setUserName(str);
					List<String> List = Arrays.asList(str.split("_"));
					client.setSbj(List.get(0));
					client.setTeamNo(List.get(1).split("m")[1]);
					client.setPrjNo(List.get(2));
					client.setTrailNo(authResponse[1]);
					JOptionPane.showMessageDialog(null, "SUCCESS!!");
					frame.setVisible(false);
					Uploading upload = new Uploading();
					upload.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(null, "Invalid UserName OR Password");
				}
			}
		});
		btnLogIn.setBackground(UIManager.getColor("Button.select"));
		btnLogIn.setBounds(47, 208, 117, 25);
		frame.getContentPane().add(btnLogIn);

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserName.setText(null);
				Password.setText(null);
			}
		});
		btnClear.setBounds(176, 208, 117, 25);
		frame.getContentPane().add(btnClear);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnCancel.setBounds(305, 208, 117, 25);
		frame.getContentPane().add(btnCancel);
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
