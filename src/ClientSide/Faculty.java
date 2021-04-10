package ClientSide;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Faculty extends JFrame {

	private JPanel contentPane;
	private JFrame frame;

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
					Faculty window = new Faculty();
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
	public Faculty() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JTextField UserName = new JTextField();
		UserName.setBounds(189, 80, 191, 27);
		frame.getContentPane().add(UserName);
		UserName.setColumns(10);

		JPasswordField Password = new JPasswordField();
		Password.setColumns(10);
		Password.setBounds(189, 138, 191, 27);
		frame.getContentPane().add(Password);

		JLabel lblPleaseEnterYour = new JLabel("Username");
		lblPleaseEnterYour.setFont(new Font("DejaVu Serif", Font.BOLD, 20));
		lblPleaseEnterYour.setBounds(57, 76, 157, 31);
		frame.getContentPane().add(lblPleaseEnterYour);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("DejaVu Serif", Font.BOLD, 20));
		lblPassword.setBounds(57, 134, 157, 31);
		frame.getContentPane().add(lblPassword);

		JLabel lblSignIn = new JLabel("Faculty Sign In");
		lblSignIn.setFont(new Font("Abyssinica SIL", Font.BOLD | Font.ITALIC, 22));
		lblSignIn.setBounds(137, 30, 191, 34);
		frame.getContentPane().add(lblSignIn);

		JButton btnLogIn = new JButton("Log In");
		btnLogIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Call client Class
				Client client = Client.getInstance();
				String authResponse = client.isValidUser(4, UserName.getText().toLowerCase(),
						Password.getText().toLowerCase());

				if (authResponse.equals("Auth Success")) {
					String str = UserName.getText();
					client.setUserName(str);
					List<String> List = Arrays.asList(str.split("_"));
					client.setSbj(List.get(0));
					JOptionPane.showMessageDialog(null, "SUCCESS!!");
					frame.setVisible(false);
					FileStructure fs = new FileStructure();
					fs.getFrame().setVisible(true);
				} else {
					JOptionPane.showMessageDialog(null, "FAIL!!");
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
