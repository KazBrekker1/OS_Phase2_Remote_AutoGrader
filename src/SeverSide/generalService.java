package SeverSide;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class generalService extends Thread {
	private Socket client;
	private ArrayList<String[]> Ids = new ArrayList<>();
	private ArrayList<String[]> fIds = new ArrayList<>();
	DataInputStream dis;
	DataOutputStream dos;
	private static ArrayList<String> currCients = new ArrayList<>();

	public generalService(Socket client) {
		this.client = client;
		this.start();
	}

	public void readTeamInfo() {
		try {
			File myObj = new File("src/teams.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				String[] teamInfo = data.split("_");
				String sbjName = teamInfo[0];
				String projectNumber = teamInfo[2];
				String teamPassword = teamInfo[3];
				String[] finalInfo = { String.format("%s_%s_%s", sbjName, teamInfo[1], projectNumber), teamPassword };
				this.Ids.add(finalInfo);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void readFacultyInfo() {
		try {
			File myObj = new File("src/faculty.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				String[] fInfo = data.split("_");
				String sbjName = fInfo[0];
				String fPass = fInfo[2];
				String[] finalInfo = { String.format("%s_%s", sbjName, fInfo[1]), fPass };
				this.fIds.add(finalInfo);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void logAction(String action, String user) throws IOException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		synchronized (this) {
			Files.write(Paths.get("src/serverLogFile.txt"),
					String.format("User: %s, Did This: %s, On: %s\n", user, action, formatter.format(date)).getBytes(),
					StandardOpenOption.APPEND);
		}
	}

	public void run() {// any code here is threaded
		try {
			readTeamInfo();
			readFacultyInfo();
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			int choice = 0;
			try {
				choice = dis.readInt();
			} catch (Exception e) {
				e.printStackTrace();
			}
			switch (choice) {
			case 1:
				String userName = dis.readUTF();
				String password = dis.readUTF();
				boolean login = true;
				int i = 0;
				for (i = 0; i < currCients.size(); i++) {
					System.out.println("user: " + userName + " curr: " + currCients.get(i));
					if (currCients.get(i).equalsIgnoreCase(userName)) {
						login = false;
					}
				}
				if (login) {
					Auth(userName, password);
					logAction("Team Logged In", userName);
				} else {
					String response;
					response = "Already Logged in";
					dos.writeUTF(response.toString());
					dos.flush();
				}
				break;
			case 2:
				String sbj = dis.readUTF();
				String teamNo = dis.readUTF();
				String projectNo = dis.readUTF();
				saveFile(sbj, teamNo, projectNo);
				logAction("Submitted File", String.format("%s_team%s_%s", sbj, teamNo, projectNo));
				break;
			case 3:
				String selection = dis.readUTF();
				sbj = dis.readUTF();
				teamNo = dis.readUTF();
				projectNo = dis.readUTF();
				sendFile(selection, sbj, teamNo, projectNo);
				logAction(String.format("Downloaded %s", selection),
						String.format("%s_team%s_%s", sbj, teamNo, projectNo));
				break;
			case 4:
				String fName = dis.readUTF();
				String fPass = dis.readUTF();
				fAuth(fName, fPass);
				logAction("Instructor Logged In", fName);
				break;
			case 5:
				sbj = dis.readUTF();
				teamNo = dis.readUTF();
				projectNo = dis.readUTF();
				String dueDate = dis.readUTF();
				String pwd = dis.readUTF();
				initFileStructure(sbj, teamNo, projectNo, dueDate, pwd);
				logAction(String.format("File Structure for %s_Team%s_Project%s was created", sbj, teamNo, projectNo),
						String.format("Instructor of %s", sbj));
				break;
			case 6:
				userName = dis.readUTF();
				logAction("Logged Out", userName);
				System.out.println(currCients.toString());
				for (i = 0; i < currCients.size(); i++) {
					System.out.println("user: " + userName + " curr: " + currCients.get(i));
					if (currCients.get(i).equalsIgnoreCase(userName)) {
						currCients.remove(i);
					}
				}
				String response;
				response = "Logged Out";
				dos.writeUTF(response.toString());
				dos.flush();
				System.out.println(currCients.toString());
				break;
			default:
				System.out.println("This is the Default Case");
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Auth(String userName, String password) {
		try {
			StringBuilder response = new StringBuilder();
			boolean found = false;
			for (int i = 0; i < Ids.size(); i++) {
				if (userName.equalsIgnoreCase(Ids.get(i)[0]) && password.equalsIgnoreCase(Ids.get(i)[1])) {
					currCients.add(userName);
					found = true;
					List<String> List = Arrays.asList(Ids.get(i)[0].split("_"));
					String sbj = List.get(0);
					String teamNumber = List.get(1).split("m")[1];
					String prjNumber = List.get(2);
					ProcessBuilder processBuilder = new ProcessBuilder();
					processBuilder.command("bash", "-c",
							String.format("ls src/../Courses/%s/Projects/Project%s/Teams/Team%s/Trials | wc -l", sbj,
									prjNumber, teamNumber));
					try {
						Process process = processBuilder.start();
						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String trialNumber = reader.readLine();
						response.append(String.format("Auth Success_%s", trialNumber));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (!found) {
				response.append("Invalid Username or Password_0");
			}
			dos.writeUTF(response.toString());
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fAuth(String userName, String password) {
		try {
			StringBuilder response = new StringBuilder();
			boolean found = false;
			for (int i = 0; i < fIds.size(); i++) {
				if (userName.equalsIgnoreCase(fIds.get(i)[0]) && password.equalsIgnoreCase(fIds.get(i)[1])) {
					found = true;
					response.append("Auth Success");
				}
			}
			if (!found) {
				response.append("Invalid Username or Password");
			}
			dos.writeUTF(response.toString());
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveFile(String sbj, String teamNo, String projectNo) throws IOException {

		String fileName = String.format("%s_team%s_%s.c", sbj, teamNo, projectNo);
		FileOutputStream fos = new FileOutputStream(fileName);
		byte[] buffer = new byte[4096];
		int filesize = 10000; // Send file size in separate msg
		int read = 0;
		int remaining = filesize;
		while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > -1) {
			remaining -= read;
			fos.write(buffer, 0, read);
		}
		fos.flush();
		fos.close();
		dis.close();
		submitFile(fileName, sbj, teamNo, projectNo);
		// TODO: This if for Navigation
	}

	private void submitFile(String fileName, String sbj, String teamNo, String projectNo) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("src/Menu2.sh", "1", fileName, sbj, teamNo, projectNo);
		try {
			processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initFileStructure(String sbj, String teamNo, String projectNo, String dueDate, String pwd) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command("src/Menu2.sh", "2", sbj, projectNo, teamNo, dueDate);
			try {
				processBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				Files.write(Paths.get("src/teams.txt"),
						String.format("%s_team%s_%s_%s\n", sbj, teamNo, projectNo, pwd).getBytes(),
						StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				readTeamInfo();
			}
		}
	}

	private void sendFile(String selection, String sbj, String teamNo, String prjNo) throws IOException {
		System.out.printf("Selection from backend: %s, %s, %s, %s\n", selection, sbj, teamNo, prjNo);
		String fileName;
		if (selection.equals("Team Info")) {
			fileName = String.format("src/../Courses/%s/Projects/Project%s/Teams/Team%s/TeamInfo", sbj, prjNo, teamNo);
			FileInputStream fis = new FileInputStream(fileName);
			byte[] buffer = new byte[8192];
			int count;
			while ((count = fis.read(buffer)) > -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();
			dos.flush();
			dos.close();
		} else if (selection.equals("Final Grade")) {
			fileName = String.format("src/../Courses/%s/Projects/Project%s/Teams/Team%s/FinalResult", sbj, prjNo,
					teamNo);
			FileInputStream fis = new FileInputStream(fileName);
			byte[] buffer = new byte[8192];
			int count;
			while ((count = fis.read(buffer)) > -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();
			dos.flush();
			dos.close();
		} else {
			String fileName2;
			String fileName3;
			String trialNumber = selection.split(" ")[1];
			fileName = String.format("src/../Courses/%s/Projects/Project%s/Teams/Team%s/Trials/Trial%s/Log", sbj, prjNo,
					teamNo, trialNumber);
			fileName2 = String.format("src/../Courses/%s/Projects/Project%s/Teams/Team%s/Trials/Trial%s/RunTimeOutput",
					sbj, prjNo, teamNo, trialNumber);
			fileName3 = String.format("src/../Courses/%s/Projects/Project%s/Teams/Team%s/Trials/Trial%s/ErrorLog", sbj,
					prjNo, teamNo, trialNumber);
			byte[] buffer = new byte[8192];
			FileInputStream fis = new FileInputStream(fileName);
			FileInputStream fis2 = new FileInputStream(fileName2);
			FileInputStream fis3 = new FileInputStream(fileName3);
			int count;
			while ((count = fis.read(buffer)) > -1) {
				dos.write(buffer, 0, count);
			}
			int count2;
			while ((count2 = fis2.read(buffer)) > -1) {
				dos.write(buffer, 0, count2);
			}
			int count3;
			while ((count3 = fis3.read(buffer)) > -1) {
				dos.write(buffer, 0, count3);
			}
			fis.close();
			fis2.close();
			fis3.close();
			dos.flush();
			dos.close();

		}
	}
}
