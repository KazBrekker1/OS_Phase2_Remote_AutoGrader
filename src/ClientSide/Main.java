package ClientSide;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Window.Type;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Cursor;

public class Main extends JFrame {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		frame = new JFrame();
		frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		JButton sLogin = new JButton("STUDENT");
		sLogin.setFont(new Font("Abyssinica SIL", Font.ITALIC, 20));
		sLogin.setBounds(146, 89, 156, 62);
		frame.getContentPane().add(sLogin);
		sLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				Interface st = new Interface();
				st.getFrame().setVisible(true);
			}
		});
		JButton fLogin = new JButton("FACULTY");
		fLogin.setFont(new Font("Abyssinica SIL", Font.ITALIC, 20));
		fLogin.setBounds(146, 179, 156, 62);
		frame.getContentPane().add(fLogin);
		
		JLabel lblLoginAs = new JLabel("LOGIN AS");
		lblLoginAs.setFont(new Font("Abyssinica SIL", Font.BOLD | Font.ITALIC, 20));
		lblLoginAs.setBounds(173, 29, 110, 48);
		frame.getContentPane().add(lblLoginAs);
		fLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				Faculty ft = new Faculty();
				ft.getFrame().setVisible(true);
			}
		});
	}
}
