package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
	private static Socket clientSocket;
	private static PrintWriter out;
	private static Scanner in;
	private static String salt = null;
	private static String ID = null;
	private static AtomicInteger readSocket = new AtomicInteger(1);
	private static AtomicInteger writeSocket = new AtomicInteger(1);
	// socket�� ���� �ְ� ���°� ������ ģ��! �ʱⰪ�� 1 : 1�϶��� ��밡��, 0�϶��� ��� �Ұ���!

	// IP�ּҿ� port number�� ���ؼ� ������ ������ �����ϴ� method.
	public static void startConnection(String ip, int port) throws UnknownHostException, IOException {
		clientSocket = new Socket(ip, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
	}

	// �ұݸ����
	public static String makeSalt() {
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			byte[] bytes = new byte[16];
			random.nextBytes(bytes);
			String salt = new String(Base64.getEncoder().encode(bytes));

			return salt;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// ��ȣȭ �Լ� ¥��
	protected static String encryptionPW(String pw, String salt) {
		String raw = pw;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt.getBytes());
			md.update(raw.getBytes());
			String hex = String.format("%064x", new BigInteger(1, md.digest()));
			return hex;

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// loginüũ �Լ�
	protected static boolean logincheck(String id, char[] pw) {
		out.println("REQSALT" + "`|" + id); // �ұݿ�û
		salt = in.nextLine();

		String spw = String.valueOf(pw);
		spw = encryptionPW(spw, salt);
		// ���߿� ��й�ȣ�� ��ȣȭ�ؼ� �Ѱ��ֱ�

		out.println("LOGIN" + "`|" + id + "`|" + spw);
		String line = in.nextLine();

		if (line.startsWith("LOGIN")) {
			String info[] = line.split("\\`\\|");

			if (info[1].compareTo("SUCCESS") == 0) { // �α��� ���� �޼����� �޾Ҵٸ�
				ID = id;
				return true;
			}
		}
		return false;
	}

	// register �Լ�
	protected static int register(String git, String temp) {
		// 0 - ȸ������ ����
		// 1 - ���̵� �ߺ�
		// 2 - �г��� �ߺ�
		// 3 - �׳� ����

		String tp[] = temp.split("\\`\\|");

		// ��й�ȣ ��ȣȭ
		String salt = makeSalt();
		tp[2] = encryptionPW(tp[2], salt);

		temp = "`|" + salt;

		for (String k : tp) {
			temp = temp + "`|" + k;
		}

		// ������ ���� ������
		out.println("REGISTER`|" + git + temp);

		String line = in.nextLine();

		if (line.startsWith("REGISTER")) {
			String info[] = line.split("\\`\\|");

			if (info[1].compareTo("OK") == 0) { // �α��� ���� �޼����� �޾Ҵٸ�
				return 0;
			} else if (info[1].compareTo("ID") == 0) { // ���̵� �ߺ�
				return 1;
			} else if (info[1].compareTo("NN") == 0) { // �г���
				return 2;
			}
		}
		// �׳� �����ϸ� 3�� ����
		return 3;
	}

	// MainScreen���� ����Ѵ� - �� �Լ��� �ҷ��ͼ� ���� ������ �ٹ̰� �˴ϴ�.
	protected static String[] basicinfo() {
		String[] binfo = new String[3];

		for (int i = 0; i < 3; i++) {
			String line = in.nextLine();
			
			if (line.startsWith("BASICINFO")) {
				String info[] = line.split("\\`\\|");

				if (info[1].compareTo("name") == 0) {
					binfo[0] = line.substring(line.indexOf(info[2]));

				} else if (info[1].compareTo("nickname") == 0) {
					binfo[1] = line.substring(line.indexOf(info[2]));

				} else if (info[1].compareTo("state_message") == 0) {
					if (info[2].compareTo("null") == 0) {
						binfo[2] = null;
					} else
						binfo[2] = info[2];
				}
			}
		}
		return binfo;
	}

	public static String[][] friendList() {
		String[][] info = new String[20][4];
		// String[][name, nickname, last_connection, ���]

		String line = in.nextLine();

		if (line.compareTo("BASICINFO`|FRIENDLIST") != 0) {
			return null;
		}

		line = in.nextLine();
		int idx = 1;

		while (line.compareTo("BFEND") != 0) {
			String i[] = line.trim().split("\\`\\|");

			for (int k = 0; k < 4; k++) { // �̸�, �г���, ���ӿ���
				info[idx][k] = i[k];
			}
			idx++;
			line = in.nextLine().trim();
		}
		info[0][0] = Integer.toString(idx);

		return info; // ������ �����ִ� �������� ó�� []�� null�̸� �ű⼭ ���߰� �ؾ��ҵ�. �����ϰ���?
	}

	public static void freeSocket() {
		readSocket.set(1);
		writeSocket.set(1);
	}

	// ========================================���� ���� ���� �ǵ�������

	// thread��� �����ϱ� ���� ���� �κ�!!!

	
	static boolean PWck[] = {false, false}; //�ʱ����! {�� ������Ʈ Ȯ��, ���� ��}
	static boolean NNck[] = {false, false}; //�ʱ����! {�� ������Ʈ Ȯ��, ���� ��}
	static boolean settingInfock = false; //settingInfo�� �� ������Ʈ Ȯ��
	static String[] settingInfo = new String[8]; // [ID NICKNAME NAME PHONE EMAIL BIRTH GITHUB STATE_MESSAGE]


	// ���������Ҷ� pw�´��� Ȯ���ϴ� �Լ�
	protected static boolean pwcheck(char[] pw) {

		String spw = String.valueOf(pw);
		spw = encryptionPW(spw, salt); // �̹� �ұ��� �ִ�

		// ��й�ȣ üũ�ش޶�� ������,
		out.println("SETTING`|PWCK`|" + spw);


		// ��й�ȣ�� üũ ���θ� ���⼭ �����ϰ� �˴ϴ�. -> ���� üũ�� �� ���� ��ٸ���
		while(PWck[0] != true){
			System.out.println("waiting-pwcheck");
		}
		
		PWck[0] = false; //��Ȱ�� �����ϰ� �ٲ��ش�
		System.out.println(PWck[0] + " " + PWck[1]);

		
		if (PWck[1] == true) { //��й�ȣ�� �´ٸ� true, �ƴ϶�� false�� ����
			return true;
		}
		return false;
	}

	// ������ ���� �Լ�
	protected static int modifyInfo(String temp) {
		// 0 - ���� ����
		// 2 - �г��� �ߺ�

		// ������ ���� ������
		out.println("SETTING`|SAVE`|" + temp);

		//�г��� �ߺ����� Ȯ��
		while(NNck[0] != true){
			System.out.println("waiting-modityinfo");
		}
		
		NNck[0] = false; //��Ȱ�� �����ϰ� �ٲ��ش�
		System.out.println(NNck[0] + " " + NNck[1]);

		
		if (NNck[1] == true) { //��й�ȣ�� �´ٸ� true, �ƴ϶�� false�� ����
			return 0;
		}
		return 1;
	}
	
	//������ �����ִ� �Լ�
	protected static String[] settinginfo() {
		String[] binfo = new String[9];
		
		//�ϴ� ������ ������ ��û�մϴ�!
		out.println("SETTING`|REQ");
		
		//������ ���� ��ٸ�
		while(settingInfock != true){
			System.out.println("waiting-settinginfo");
		}
		settingInfock = false;
		
		return settingInfo;
	}

	
	
	
	public static void main(String[] args) {
		String file = "serverinfo.dat";
		String ip = null;
		int port = 0;

		// server�� ���� ȯ�� ������ ���� ���� �о����
		try {
			BufferedReader fileIn = new BufferedReader(new FileReader(file));
			ip = fileIn.readLine();
			String portString = fileIn.readLine();
			port = Integer.parseInt(portString);
			fileIn.close();
			startConnection(ip, port);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// stream�� ����Ǿ����ϴ�!

		// main screen�� �����ϴ� ����, �ٸ��������� socket��� ���ϰ� ���Ƴ���!
		// mainsScreen������� ���ư��� login���� ������ atomic�� ����� (������� ���� ����)
		readSocket.set(0);
		writeSocket.set(0);

		// �׷� �α��� or ȸ������ ȭ���� ���
		new LogIn();

		// main������ ������ MainScreen���� �Լ��� �ҷ��� socket�� ������ Ǯ���� ��.
		// => �̶����� thread���� ����°���

		// �������ʹ� ���� �̺�Ʈ ��! ��, server���� ������ ������, client�� ���� �۵��ϴ��� �� �� �ϳ���.

		// input�̶� Basic���� ��
		ExecutorService b_pool = Executors.newFixedThreadPool(2);

		b_pool.execute(new input());
		b_pool.execute(new basic());
//		
//		//ä�ù� ���� ��
//		ExecutorService chat_pool = Executors.newFixedThreadPool(100);
//
//

//		//�������� �������� ���� ��� �Է��� input thread�� ���ؼ� ó����

	}

	// ������ �Է¸� �޴� ������ (�Է� ������ �����ؼ� �ʿ��� ���� �־��ش�!!)
	public static class input implements Runnable {
		// chat�̸� ê,
		// �� ģ����û�̸� ģ����û ��� ó���ϰ�
		// ģ�� ��û ��û�� ���⼭ �ް� ����
		// ��, �Է��� ����� ���� ���� �����ؾ���!!!!!!!!!

		@Override
		public void run() {
			while (readSocket.get() == 0) {}; // 1�Ǹ� Ǯ����
			// ���� �޴°� ������ �� thread���� ó��!

			while (true) {
				String line = in.nextLine();

				// ģ�� ����
				if(line.startsWith("FRIEND")) {
					String info[] = line.split("\\`\\|");

					// ģ�� ��û�� ���Դ� => FRIEND REQ [ģ�� �̸�] [ģ�� ����]
					if(info[1].compareTo("REQ") == 0) {


					}
				}

				// �˻�
				else if (line.startsWith("SEARCH")) {
					String info[] = line.split("\\`\\|");

					// �˻� ����� ���ƿԴ� => SEARCH REQ [�˻��� ģ�� ��] [ģ�� ����Ʈ...  ��� �Ѱ��ٰ��ΰ�.... �̸�, ����, ��... �ٸ�������? �ϴ� �����̰� GUI����ź��� ��������]
					if(info[1].compareTo("REQ") == 0) {


					}
				}
				
				//����
				else if (line.startsWith("SETTING")) {
					String info[] = line.split("\\`\\|");

					// �� ������ �޴´� => SETTING INFO [ID NICKNAME NAME PHONE EMAIL BIRTH GITHUB STATE_MESSAGE]			
					if(info[1].compareTo("INFO") == 0) {
						int idx = 2;

						for(int i=0;i<8;i++, idx++) {
							settingInfo[i] = info[idx];
						}
					
						settingInfock = true;
					}

					// ��й�ȣ üũ ��û�Ѱ� ����=> SETTING PWCK OK/NO
					else if(info[1].compareTo("PWCK") == 0) {
						if(info[2].compareTo("OK") == 0) PWck[1] = true;
						else PWck[1] = false;
						
						PWck[0] = true;
						System.out.println("=>" + PWck[0] + " " + PWck[1]);

					}
					 
					// ���� ���忡�� �г��� ��ġ�� ��� üũ => SETTING NN
					else if(info[1].compareTo("NN") == 0) {
						if(info[2].compareTo("OK") == 0) NNck[1] = true;
						else NNck[1] = false;
						
						NNck[0] = true;
						System.out.println("=>" + NNck[0] + " " + NNck[1]);
					}
					 
					 
					
				}

				//ä�ù� ���� (1��)
				else if (line.startsWith("NMCHAT")) {
					String info[] = line.split("\\`\\|");


					if(info[1].compareTo("INFO") == 0) {

						
						

					}

					// ��й�ȣ üũ ��û�Ѱ� ����=> SETTING PWCK 1/0 (1�� true, 0�� false)
					else if(info[1].compareTo("PWCK") == 0) {


					}
				}

				//ä�ù� ���� (��Ƽ)
				else if (line.startsWith("MCHAT")) {
					String info[] = line.split("\\`\\|");


					if(info[1].compareTo("INFO") == 0) {

						
						

					}

					// ��й�ȣ üũ ��û�Ѱ� ����=> SETTING PWCK 1/0 (1�� true, 0�� false)
					else if(info[1].compareTo("PWCK") == 0) {


					}
				}
				
				
			}
		}
	}

	// ����ȭ���� ������ ģ��!
	public static class basic implements Runnable {

		@Override
		public void run() {

		}

	}

	// ä�ù� ���� ����� ������
	public static class chat implements Runnable {

		private int room_num;

		@Override
		public void run() {

		}

	}

}
