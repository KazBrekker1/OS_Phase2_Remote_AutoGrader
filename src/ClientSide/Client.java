package ClientSide;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Socket server;
	DataOutputStream dos;
	DataInputStream dis;

	private static Client currentClient;
	private String userName = "";
	private String password = "";
	private String sbj = "";
	private String teamNo = "";
	private String prjNo = "";
	private String trailNo = "0";

	public Client() {

	}

	public String isValidUser(int choice, String user, String pass) {
		String serverResponse = "";
		setUserName(user);
		setPassword(pass);
		try {
			server = new Socket("localhost", 4000);
			dis = new DataInputStream(server.getInputStream());
			dos = new DataOutputStream(server.getOutputStream());
			System.out.println("in Client " + user + " : " + pass);
			// send to server
			dos.writeInt(choice);
			dos.writeUTF(user);
			dos.writeUTF(pass);
			dos.flush();
			serverResponse = dis.readUTF();

		} catch (IOException e) {
			e.printStackTrace();
			serverResponse = "Server Error";
		}
		return serverResponse;
	}

	public void sendFile(int choice, String path) throws IOException {
		server = new Socket("localhost", 4000);
		dis = new DataInputStream(server.getInputStream());
		dos = new DataOutputStream(server.getOutputStream());
		FileInputStream fis = new FileInputStream(path);

		dos.writeInt(choice);
		dos.writeUTF(sbj);
		dos.writeUTF(teamNo);
		dos.writeUTF(prjNo);

		byte[] buffer = new byte[8192];
		int count;
		while ((count = fis.read(buffer)) > -1) {
			dos.write(buffer, 0, count);
		}
		fis.close();
		dos.flush();
		dos.close();
	}

	public void intialize(int choice, String sbj, String teamNo, String prjNo, String dueDate, String pwd) {
		try {
			server = new Socket("localhost", 4000);
			dis = new DataInputStream(server.getInputStream());
			dos = new DataOutputStream(server.getOutputStream());

			dos.writeInt(choice);
			dos.writeUTF(sbj);
			dos.writeUTF(teamNo);
			dos.writeUTF(prjNo);
			dos.writeUTF(dueDate);
			dos.writeUTF(pwd);
			dos.flush();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void recieve(int choice, String selection, String sbj, String teamNo, String prjNo) throws IOException {
		server = new Socket("localhost", 4000);
		dis = new DataInputStream(server.getInputStream());
		dos = new DataOutputStream(server.getOutputStream());

		dos.writeInt(choice);
		dos.writeUTF(selection);
		dos.writeUTF(sbj);
		dos.writeUTF(teamNo);
		dos.writeUTF(prjNo);
		dos.flush();
		FileOutputStream fos = new FileOutputStream(String.format("%s_Team%s_%s", sbj, teamNo, selection));
		byte[] buffer = new byte[4096];
		int filesize = 10000; // Send file size in separate msg
		int read = 0;
		int remaining = filesize;
		while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > -1) {
			remaining -= read;
			fos.write(buffer, 0, read);
		}

		fos.flush();
		dos.close();
		fos.close();
		dis.close();
	}

	public String signOut(String userName) {
		String serverResponse = "";
		try {
			server = new Socket("localhost", 4000);
			dis = new DataInputStream(server.getInputStream());
			dos = new DataOutputStream(server.getOutputStream());
			// send to server
			dos.writeInt(6);
			dos.writeUTF(userName);
			dos.flush();
			serverResponse = dis.readUTF();
			dos.close();

		} catch (IOException e) {
			e.printStackTrace();
			serverResponse = "Server Error";
		}

		return serverResponse;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSbj() {
		return sbj;
	}

	public void setSbj(String sbj) {
		this.sbj = sbj;
	}

	public String getTeamNo() {
		return teamNo;
	}

	public void setTeamNo(String teamNo) {
		this.teamNo = teamNo;
	}

	public String getPrjNo() {
		return prjNo;
	}

	public void setPrjNo(String prjNo) {
		this.prjNo = prjNo;
	}

	public String getTrailNo() {
		return trailNo;
	}

	public void setTrailNo(String trailNo) {
		this.trailNo = trailNo;
	}

	public static Client getInstance() {
		if (currentClient == null)
			currentClient = new Client();
		return currentClient;
	}
}
