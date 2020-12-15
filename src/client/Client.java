package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
	private static Socket clientSocket;
	private static String ip = null;
	private static PrintWriter out;
	private static Scanner in;
	private static String salt = null;
	private static String ID = null;
	private static boolean flag = true;
	private static AtomicInteger readSocket = new AtomicInteger(1);
	private static AtomicInteger writeSocket = new AtomicInteger(1);
	// socket�� ���� �ְ� ���°� ������ ģ��! �ʱⰪ�� 1 : 1�϶��� ��밡��, 0�϶��� ��� �Ұ���!
	private static HashMap<String, ChattingOne> PCHAT = new HashMap<String, ChattingOne>(); //������ �ϴ������� �����ϴ� ģ��. ģ���� ID�� �����.
	private static HashMap<Integer, ChattingMulti> MCHAT = new HashMap<Integer, ChattingMulti>(); //������ �ϴ������� �����ϴ� ģ��. ģ���� ID�� �����.
	private static HashMap<String, String> FileMatch = new HashMap<String, String>(); //�������� ������ ������ �����ص�
	private static HashMap<Integer, TTTGAME> TTTPOCKET = new HashMap<Integer, TTTGAME>(); //������ �ϴ������� �����ϴ� ģ��. ģ���� ID�� �����.

	private static ExecutorService filepool = Executors.newFixedThreadPool(50);
	// input�̶� Basic���� ��
	private static ExecutorService b_pool = Executors.newFixedThreadPool(2);

	
	public static String getCurrentTime() {
		Date date_now = new Date(System.currentTimeMillis()); // ����ð��� ������ Date������ �����Ѵ�
		
		//HHmmss
		SimpleDateFormat date_format = new SimpleDateFormat("yyMMddHHmmssSS");

		return date_format.format(date_now).toString();
	}
	
	
	// thread��� �����ϱ� ���� ���� �κ�!!!
	private static boolean PWck[] = {false, false}; //�ʱ����! {�� ������Ʈ Ȯ��, ���� ��}
	private static boolean NNck[] = {false, false}; //�ʱ����! {�� ������Ʈ Ȯ��, ���� ��}
	private static boolean settingInfock = false; //settingInfo�� �� ������Ʈ Ȯ��
	private static String[] settingInfo = new String[8]; // [ID NICKNAME NAME PHONE EMAIL BIRTH GITHUB STATE_MESSAGE]
	private static boolean fsl[] = {false, false}; //{ģ�����˻� ������Ʈ, �ܺ�ģ���˻� ������Ʈ)
	private static String[][] fslInfo = new String[21][4]; //ģ���˻��� �������Ʈ (ID, name, nickname, last_connection)
	private static boolean friendInfock = false; 
	private static String[] friendInfo = new String[7]; // [NICKNAME NAME STATE_MESSAGE EMAIL PHONE BIRTH GITHUB]
	private static boolean friend_dbck[] = {false, false}; //�������� �����Դ��� Ȯ���ϴ� �� (PCK, FCK)
	private static boolean friend_result[] = {true, true}; //���� �ִ��� ������ �˷���(PCK, FCK)
	private static String lefsfe;
	private static int roomNum = 0;
	private static boolean ckroomNum = false; 
	//boolean�������� True�� �س����� MainScreen���� ������ ������ false�� �������� ��.

	
	
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
		String[][] info = new String[20][5];
		// String[][ID, name, nickname, last_connection, ���]

		String line = in.nextLine();

		if (line.compareTo("BASICINFO`|FRIENDLIST") != 0) {
			return null;
		}

		line = in.nextLine();
		int idx = 1;

		while (line.compareTo("BFEND") != 0) {
			String i[] = line.trim().split("\\`\\|");

			for (int k = 0; k < 5; k++) { // ���̵�, �̸�, �г���, ���ӿ���, ��޴�?
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

	// ========================================���� ���� ���� �ǵ�������!
	//=================���� ���� �Լ�
	// ���������Ҷ� pw�´��� Ȯ���ϴ� �Լ�
	protected static boolean pwcheck(char[] pw) {

		String spw = String.valueOf(pw);
		spw = encryptionPW(spw, salt); // �̹� �ұ��� �ִ�

		// ��й�ȣ üũ�ش޶�� ������,
		out.println("SETTING`|PWCK`|" + spw);


		// ��й�ȣ�� üũ ���θ� ���⼭ �����ϰ� �˴ϴ�. -> ���� üũ�� �� ���� ��ٸ���
		while(PWck[0] != true){
			System.out.println(lefsfe);
		}
		
		PWck[0] = false; //��Ȱ�� �����ϰ� �ٲ��ش�
		System.out.println(PWck[0] + " " + PWck[1]);

		
		if (PWck[1] == true) { //��й�ȣ�� �´ٸ� true, �ƴ϶�� false�� ����
			PWck[1] = false;
			return true;
		}
		return false;
	}

	//������ ���� �Լ�
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
		//�ϴ� ������ ������ ��û�մϴ�!
		out.println("SETTING`|REQ");
		
		//������ ���� ��ٸ�
		while(settingInfock != true){
			System.out.println("waiting-settinginfo");
		}
		settingInfock = false;
		
		return settingInfo;
	}

	//�ܺ� ģ�� �˻� ����Ʈ�� �����ִ� �Լ�
	protected static String[][] NotfriendSearchList(String kw) {
		// String[][name, nickname, last_connection]

		//�ϴ� ������ ������ ��û�մϴ�! (with kw)
		out.println("SEARCH`|OF`|" + kw);
		
		//������ ���� ��ٸ�
		while(fsl[1] != true){
			System.out.println("waiting-NFSL");
		}
		fsl[1] = false;
		return fslInfo;
	}
		
	//ģ�� �� �˻� ����Ʈ�� �����ִ� �Լ�
	protected static String[][] FriendSearchList(String kw) {
		// String[][name, nickname, last_connection]

		//�ϴ� ������ ������ ��û�մϴ�! (with kw)
		out.println("SEARCH`|MF`|" + kw);
		
		//������ ���� ��ٸ�
		while(fsl[0] != true){
			System.out.println("waiting-FSL");
		}
		fsl[0] = false;
		
		return fslInfo;
	}
	
	//ģ�� ������ �޾ƿ��� �Լ�
	protected static String[] getFriendInfo(String FID) {
		
		//�ϴ� ������ ������ ��û�մϴ�!
		out.println("FRIEND`|INFO`|" + FID);
		
		//������ ���� ��ٸ�
		while(friendInfock != true){
			System.out.println("waiting-FINFO");
		}
		friendInfock = false;

		return friendInfo;
	}
		
	//ģ����û�ϴ� �Լ�
	protected static int requsetFriend(String fid) {
		//1 : ģ����û �Ϸ�, 0 : ģ����û ���� (�̹� �Ǿ��ִ°���)
		
		//�ϴ�, ģ����û ���̺� �����ϴ��� Ȯ���ϱ�
		out.println("FRIEND`|PCK`|" + fid);
		//�׸��� ģ�� ���̺��� �����ϴ��� Ȯ���ϱ�
		out.println("FRIEND`|FCK`|" + fid);

		//������ٸ��� (�� �� �ϳ��� ���� false�� �ѱ�� �ȵ�)
		while(friend_dbck[0] == false || friend_dbck[1] == false) {
			System.out.println("waiting-RF");
		}
		friend_dbck[0] = false;
		friend_dbck[1] = false;


		//�Ѵ� false���� ģ���� �ƴϰ� ģ����û ���̺��� ���� ���� �ȴ� => �׷� ��û�ص� �ȴٴ� ��!
		if(friend_result[0] == false && friend_result[1] == false) {
			//����Ѵٸ� ģ����û ���̺� �־��ֶ�� ��û!
			out.println("FRIEND`|APP`|" + fid);
			return 1;
		}
		return 0;
	}
	
	//ȸ��Ż��....�߰�....
	protected static void byebye() {
		out.println("SETTING`|BYE");
		MainScreen.clostMainScreen();
		flag = false;
	}
	
	
	//==================ä�� ��� ���� �Լ�	
	// <�ϴ��� ä��>
	//�����̶� �ϴ��� ä�������� Ȯ��
	protected static boolean ckINPCHAT(String FID) {
		if(PCHAT.containsKey(FID)) return true;
		else return false;
	}
	
	//�ϴ��� ä������ ����� ��Ƶδ� hashmap�� �ֱ�
	protected static void addPCHAT(String FID, ChattingOne chat) {
		PCHAT.put(FID, chat);
	}
	
	//�ϴ��� ä������ ����� ��Ƶδ� hashmap���� �����ϰ� ���濡�� �����ٰ� ����
	protected static void delPCHAT(String FID) {
		PCHAT.remove(FID);
		out.println("PCHAT`|outCHAT`|" + FID);
	}
	
	//���濡�� ä�� �ϰ� �ʹٰ� ��û
	protected static void ckANSWER(String FID) {
		//������ �� ��� ä���ϰ� �ʹٰ� ��û�ϱ�!
		out.println("PCHAT`|REQCHAT`|" + FID);
	}
	
	//���濡�� ä���� �����Ѵٰ� Y/N ������ (�ϴ����)
	protected static void CHATANSWER(String FID, boolean ans) {
		//PCHAT`|PESPONCHAT`|" + ä�ÿ�û��ID + Y/N : ä���Ұųİ� ����f���� ä�� �Ұ��� ������ �亯
		System.out.println("2 =>" + ans);
		
		//�׷� �� �ʶ� ä���Ұ�!
		if(ans) out.println("PCHAT`|PESPONCHAT`|" + FID + "`|Y");
		else out.println("PCHAT`|PESPONCHAT`|" + FID+ "`|N");
	}

	//����ڰ� ������ ä���� �޾Ƽ� ������ �����ϴ� ����. (�޴»���� ������ ����)
	//PCHAT`|sendCHAT`|" + ä�ù޴���ID + Content : ä�ó��� ���� (����������)
	protected static void sendPCHAT(String FID, String chat) {
		out.println("PCHAT`|sendCHAT`|" + FID + "`|" + chat);
	}
		
	
	// <��Ƽê>==========================================
	//�������� �븸��ٰ� ��û
	protected static void makeMultiRoom(String roomname, String showpre, String flist) {
		//"MCHAT`|REQROOM`|" + ���̸� + ���� ���� ���� +  �游��� ��û�� ID + flist      //�游��� ��û 
		out.println("MCHAT`|REQROOM`|" + roomname + "`|" + showpre + "`|" + ID + "`|" + flist);
		
		// room number�� �ޱ⸦ ��ٸ�
		while(ckroomNum != true){
			System.out.println("wait roomNum");
		}
		ckroomNum = false; //��Ȱ�� �����ϰ� �ٲ��ش�
		
		int rn = roomNum;
		//��Ƽ ê�� ���� ä��â�� ����ݴϴ�
		ChattingMulti nchat = new ChattingMulti(rn, roomname);
		
		//ä��â�� ���� hashMap�� �־��ݴϴ�
		MCHAT.put(rn, nchat);
	}

	//���濡�� ä���� �����Ѵٰ� Y/N ������ (��Ƽ����)
	protected static void MCHATANSWER(int roomid, String roomname, boolean ans) {
		//�׷� �� �ʶ� ä���Ұ�!
		if(ans) {
			out.println("MCHAT`|RESPONCHAT`|" + roomid + "`|" + ID + "`|Y");
			ChattingMulti nchat = new ChattingMulti(roomid, roomname);
			//ä��â�� ���� hashMap�� �־��ݴϴ�
			MCHAT.put(roomid, nchat);
			System.out.println(roomid + "�Ѵٱ�");
		}
		//�ƴϸ� �ƿ� ����! 
	}
	
	//����ڰ� ������ ä���� �޾Ƽ� ������ �����ϴ� ����. (�޴»���� ������ ����)
	//"MCHAT`|sendCHAT`|" + ���ȣ + ä�� ������ID + �ð� + content // ä�� ����
	protected static void sendMCHAT(int rn, String chat) {
		out.println("MCHAT`|sendCHAT`|"+ Integer.toString(rn) + "`|" + ID + "`|" + getCurrentTime() + "`|" + chat);
	}
	
	//�� ������
	//"MCHAT`|OUTCHAT`|" + ���ȣ + ������ID //ä�ÿ��� �����ϴ�
	protected static void delMCHAT(int rn) {
		MCHAT.remove(rn);
		out.println("MCHAT`|OUTCHAT`|" + rn + "`|" + ID);
	}
	
	//���� ��� ����Ʈ�� �ּ���
	//"MCHAT`|REQuLIST`|" + ���ȣ //ä�ÿ��� �����ϴ�
	protected static void reqULIST(int rn) {
		out.println("MCHAT`|REQuLIST`|" + rn);
	}
		
	//ģ�� �ʴ��Ұſ���!
	//"MCHAT`|InviteFriend`|" + ģ�� ���̵�(��)
	protected static void InviteFriend(int rn, String list) {
		out.println("MCHAT`|InviteFriend`|" + rn + "`|" + list);
	}
	

	//���� ����=========================
	//������ �� ���� �����ϰ� �ʹٰ� �˷��ִ� �κ�
	protected static void FileSendWant(String FID) {
		out.println("FILES`|ASK`|" + FID);
	}
	
	protected static void setFilematch(String FID, String path) {
		FileMatch.put(FID, path);
	}
	
	
	
	//ƽ���� ����==============================
	//���� ��û
	protected static void startTTT(String FID) {
		out.println("TTT`|ASK`|" + FID);
	}
	
	//���� ��û�� ���� ���� => FILES ANS ���ID, Y/N
	protected static void ANSTTT(String FID, String ANS) {
		//ANS => "Y" or "N"
		out.println("TTT`|ANS`|" + FID + "`|" + ANS);
	}
	
	
	//���� ���� ���� ������ �� �ѱ��=> FILES ANS ���ID, Y/N
	protected static void MYSELECTinTTT(int rn, int x, int y) {
		//(TTT ING RoonNumber X Y)
		out.println("TTT`|ING`|"+ rn + "`|" + x + "`|" + y);
	}
	
	protected static void stopclient() {
		flag = false;
	}

	
	public static void main(String[] args) {
		String file = "serverinfo.dat";
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
		// mainScreen��������� login���� ������ atomic�� ����� (������� ���� ����)
		readSocket.set(0);
		writeSocket.set(0);

		// �׷� �α��� or ȸ������ ȭ���� ���
		new LogIn();

		// main������ ������ MainScreen���� �Լ��� �ҷ��� socket�� ������ Ǯ���� ��.
		// => �̶����� thread���� ����°���
		// �������ʹ� ���� �̺�Ʈ ��! ��, server���� ������ ������, client�� ���� �۵��ϴ��� �� �� �ϳ���.



		b_pool.execute(new input());

		//�������� �������� ���� ��� �Է��� input thread�� ���ؼ� ó����
	}
	
	// ������ �Է¸� �޴� ������ (�Է� ������ �����ؼ� �ʿ��� ���� �־��ش�!!)
	public static class input implements Runnable {
		// ��, �Է��� ����� ���� ���� �����ؾ���!

		@Override
		public void run() {
			while (readSocket.get() == 0) {}; // 1�Ǹ� Ǯ����
			// ���� �޴°� ������ �� thread���� ó��!

			try {
			while (flag) {
				System.out.println("���");
				String line = in.nextLine();
				System.out.println(line);

				
				//���ο� ������ ������Ʈ�Ǵ� �κ�
				if(line.startsWith("UPDATE")) {
					String info[] = line.split("\\`\\|");
					
					//ģ����û�� ���Ծ�� => UPDATE FRIREQ NN name FID
					if(info[1].compareTo("FRIREQ") == 0) {
						int result = MainScreen.showFriendPlus(info[2], info[3]);
						
						if(result == 1) { //ģ���� �����޴ٸ�
							out.println("FRIEND`|OK`|" + info[4]);	
						}
						else if(result == 0) {//ģ���� �����ߴٸ�
							out.println("FRIEND`|NO`|" + info[4]);	
						}
					}

					//�� ������ ������Ʈ �϶��? ���� ; UPDATE MYINFO name nn state_m
					else if(info[1].compareTo("MYINFO") == 0) {
						MainScreen.changeMyInfo(info[2], info[3], info[4]);
					}
					
					//ģ�� ������ ������Ʈ ������! ���� ; UPDATE FINFO ID name nn state_m
					else if(info[1].compareTo("FINFO") == 0) {
						String[] finfo = {info[2], info[3], info[4], info[5]};
						MainScreen.changeFriendInfo(finfo);
					}
					
					
					//ģ���� ���Դ�/������? ���� ; UPDATE F_state F_ID ����(0�̸� �¶���)
					else if(info[1].compareTo("F_state") == 0) {
						MainScreen.changeFriendstate(info[2], info[3]);
					}
					
					
					//ģ���� Ż���޴�� ; UPDATE FBYE F_ID
					else if(info[1].compareTo("FBYE") == 0) {
						MainScreen.changeFriendOUT(info[2]);
					}
				}
				
				// ģ�� ����
				else if(line.startsWith("FRIEND")) {
					String info[] = line.split("\\`\\|");

					
					//ģ�� ������ �޾Ҵ� 	=> FRIEND INFO [NICKNAME NAME STATE_MESSAGE EMAIL PHONE BIRTH GITHUB]
					if(info[1].compareTo("INFO") == 0) {
						//������ �������ش� [NICKNAME NAME STATE_MESSAGE EMAIL PHONE BIRTH GITHUB]
						friendInfo[0] = info[2];
						friendInfo[1] = info[3];
						friendInfo[2] = info[4];
						friendInfo[3] = info[5];
						friendInfo[4] = info[6];
						friendInfo[5] = info[7];
						friendInfo[6] = info[8];
						
						friendInfock = true;
					}
					
					// ģ�� �߰� ���̺� ���� Ȯ���� ���� => FRIEND`|PCK`|" + T/F
					else if(info[1].compareTo("PCK") == 0) {
						if(info[2].compareTo("T") == 0) {
							friend_result[0] = true;
						}
						else friend_result[0] = false;

						friend_dbck[0] = true;
					}
					
					// ģ�� ���̺� ���� Ȯ���� ���� => FRIEND`|FCK`|" + T/F
					else if(info[1].compareTo("FCK") == 0) {
						if(info[2].compareTo("T") == 0) {
							friend_result[1] = true;
						}
						else friend_result[1] = false;

						friend_dbck[1] = true;
					}
					
					// �� ģ���� list�� ������Ʈ ���ּ���~~ => FRIEND`|APND`|" + ID, name, nickname, last_connection, ���
					else if(info[1].compareTo("APND") == 0) {
						String[] finfo = {info[2], info[3], info[4], info[5], info[6]};
						MainScreen.changeFriend(finfo);
					}
				}

				// �˻�
				else if (line.startsWith("SEARCH")) {
					String info[] = line.split("\\`\\|");

					// �˻� ����� ���ƿԴ� => SEARCH REQ MF/OF [�˻��� ģ�� ��] [ģ�� ����Ʈ...  ��� �Ѱ��ٰ��ΰ�.... �̸�, ����, ��... �ٸ�������? �ϴ� �����̰� GUI����ź��� ��������]
					if(info[1].compareTo("REQ") == 0) {

						int num = Integer.parseInt(info[3]);
						
						for(int i = 1 ; i<=num ; i++) {
							String ln[] = info[i + 3].split("\\^");
							
							for(int j = 0;j<4;j++) {
								fslInfo[i][j] = ln[j];
							}
						}
						fslInfo[0][0] = Integer.toString(num);
											
						if(info[2].equals("MF")) fsl[0] = true;
						else fsl[1] = true;
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
				else if (line.startsWith("PCHAT")) {
					String info[] = line.split("\\`\\|");

					//PCHAT`|QUSCHAT`|" + ä�ÿ�û��ID + ���� + �̸� : 
					//���� �˷��ָ鼭 ä���Ұųİ� �����   =>�޴��� : �̶� ����(�̸�), ID �����ϱ�
					if(info[1].compareTo("QUSCHAT") == 0) {
						//ä���Ұųİ� ����� -> ���� ���������� �˾� ����� �����!
						MainScreen.showPCHAT(info[2], info[3], info[4]);		
					}

					//������ ä���� �����޴��� �����޴��� üũ
					//"PCHAT`|ANSCHAT`|" + ID +"`|" + map.get("NICKNAME")+ "`|" + map.get("NAME")
					else if(info[1].compareTo("ANSCHAT") == 0) {
						//������ �������θ� üũ info[5]& �̸��� �г��� ����
						PCHAT.get(info[2]).checkAnswer(info[5], info[3], info[4]);
					}
					
					//PCHAT`|receivedCHAT`|" + ä�ú�����ID + Content : ä�ó��� ���� (���� ������)
					else if(info[1].compareTo("receivedCHAT") == 0) {
						//���� ä�� ���� �����ֱ�!
						PCHAT.get(info[2]).receiveChat(info[3]);
					}
					
					//PCHAT`|OUTCHAT`|" + ä�ú�����ID
					else if(info[1].compareTo("OUTCHAT") == 0) {
						//���� ä�� ���� �����ֱ�!
						if(PCHAT.containsKey(info[2]))
							PCHAT.get(info[2]).endchat();
					}
				}

				//ä�ù� ���� (��Ƽ)
				else if (line.startsWith("MCHAT")) {
					String info[] = line.split("\\`\\|");

					//���� �˷��ָ鼭 ä���Ұųİ� �����  
					//client.get(id).println("MCHAT`|INVCHAT`|" + room_num + "`|" + room_name + "`|" + makerInfo);
					if(info[1].compareTo("INVCHAT") == 0) {
						//ä���Ұųİ� ����� -> ���� ���������� �˾� ����� �����!
						MainScreen.showMCHAT(Integer.parseInt(info[2]), info[3], info[4]);		
					}
					
					//�� ��ȣ �˷��ִ� �κ�
					//"MCHAT`|RoomNumber`|" + ���ȣ
					else if(info[1].compareTo("RoomNumber") == 0) {
						roomNum = Integer.parseInt(info[2]);
						ckroomNum = true;
					}
					
					//client.get(id).println("MCHAT`|ANSCHAT`|" + room_num + "`|" + m.getSender_id());
					//���� ���Դٰ� �˸��� �κ�
					else if(info[1].compareTo("ANSCHAT") == 0) {
						System.out.println(MCHAT.keySet());
						MCHAT.get(Integer.parseInt(info[2])).notifyCome(info[3]);
					}
					
					//client.get(id).println("MCHAT`|receivedCHAT`|" + room_num 
					//		+ "`|" + m.getSender_id() + "`|" + senderInfo + "`|" + m.getMessage());
					//�޼����� �޾ҽ��ϴ�
					else if(info[1].compareTo("receivedCHAT") == 0) {
						if(info[3].equals(ID)) 	MCHAT.get(Integer.parseInt(info[2])).receiveChat("��", info[5]);
						else MCHAT.get(Integer.parseInt(info[2])).receiveChat(info[4], info[5]);

					}
					
					//client.get(id).println("MCHAT`|outCHAT`|" + room_num + "`|" + m.getSender_id() + "`|" + senderInfo);
					//���� �����ٰ� �˸��� �κ�
					else if(info[1].compareTo("outCHAT") == 0) {
						MCHAT.get(Integer.parseInt(info[2])).notifyOut(info[4]);
					}
					
					//"MCHAT`|ulist`|" + ���ȣ + ����Ʈ ä�� �� �ִ� ���� 
					//�濡 �ִ� ����� ����Ʈ
					else if(info[1].compareTo("ulist") == 0) {
						String userList[] = info[3].split("\\^");
						ChattingOnlinePeople people = new ChattingOnlinePeople(userList);
					}
					
					//"MCHAT`|PRECHAT`|" + room_num +  "`|" + cnum + "`|" + messagelist
					//������ ä�� �����ִ� ��!
					else if(info[1].compareTo("PRECHAT") == 0) {
						String ulist[][] = new String[100][2];
						
						for(int i = 4 ; i < 4 + Integer.parseInt(info[3]) ; i++) {
							String userList[] = info[i].split("\\^");

							ulist[i-4][0] = userList[0];
							ulist[i-4][1] = userList[1];
						}

						MCHAT.get(Integer.parseInt(info[2])).PrereceiveChat(Integer.parseInt(info[3]), ulist); ;
					}
				}

				//���� ���� ����
				else if (line.startsWith("FILES")) {
					String info[] = line.split("\\`\\|");

					//��밡 ������ ���� ������ �;� �ߴٰ� ��û => FILES`|ASK`|" + ID + "`|" + senderInfo
					if(info[1].compareTo("ASK") == 0) {
						int result = MainScreen.confirmFileSend(info[3]);	
						if(result == 1) out.println("FILES`|ANS`|" + info[2] + "`|" + "Y" );
						else out.println("FILES`|ANS`|" + info[2] + "`|" + "N" );
					}

					//�� socket����� portnum�ָ� �޾Ƽ� thread�� �ѱ�� => "FILES`|PNUM`|" + fileportnum )
					else if(info[1].compareTo("PNUM") == 0) {
						//���ο� thread�� �־�� �Ϸ���??
						filepool.execute(new filereceiverThread(Integer.parseInt(info[2]))); 
					}
					
					//����� ������ Ȯ���Ѵ�! => "FILES`|ANS`|" + ID + "`|" + "Y" + "`|" + fileportnum+1
					else if(info[1].compareTo("ANS") == 0) {
						if(info[3].equals("Y")) {
							filepool.execute(new filesenderThread(Integer.parseInt(info[4]), info[2])); 							
						}
					}
				}
				
				//TTT ���� ����
				else if (line.startsWith("TTT")) {
					String info[] = line.split("\\`\\|");

					//������ ���ӿ�û�� �����Դ�
					if(info[1].compareTo("ASK") == 0) {
						//(TTT ASK A���̵� �̸�(����)
						MainScreen.TTTrequset(info[2], info[3]);
					}
					
					//��밡 ���Ѵٰ� �ϸ�?
					else if(info[1].compareTo("ANS") == 0) {
						MainScreen.RejectTTT();
					}
					
					//���� �Ҳ��ϱ� �غ��϶�� ��ȣ => TTT INFO MNN FNN ROOMNUMBER ORDER)
					else if(info[1].compareTo("INFO") == 0) {
						TTTGAME t = new TTTGAME(Integer.parseInt(info[4]), Integer.parseInt(info[5]), info[2], info[3]);
						TTTPOCKET.put(Integer.parseInt(info[4]), t);
					}
					
					//��밡 �� ���� ǥ���϶�� ��ȣ => TTT NOTI rn x y
					else if(info[1].compareTo("NOTI") == 0) {
						int x = Integer.parseInt(info[3]);
						int y = Integer.parseInt(info[4]);
						TTTPOCKET.get(Integer.parseInt(info[2])).checkOPPNblock(x, y);
					}
					
					//����� Ȯ���ϴ� �κ� => TTT RESULT rn ���
					else if(info[1].compareTo("RESULT") == 0) {
						if(info[3].equals("WIN")) {
							TTTPOCKET.get(Integer.parseInt(info[2])).Winner(1);
						}
						else if(info[3].equals("LOSE")) {
							TTTPOCKET.get(Integer.parseInt(info[2])).Winner(0);
						}
						else {
							TTTPOCKET.get(Integer.parseInt(info[2])).Winner(-1);
						}
						
						//����� �� �ڿ��� ���̻��� ����� ����
						TTTPOCKET.remove(Integer.parseInt(info[2]));

					}
				}
			}

		} finally {
			// ����ä�õ� �� ������
			for (ChattingOne a : PCHAT.values()) {
				a.FexitChat();
			}
			PCHAT.clear();

			// ��Ƽä�õ鵵 �� ������
			for (ChattingMulti a : MCHAT.values()) {
				int rnum = a.roomnumber;
				MCHAT.remove(rnum);
				out.println("MCHAT`|OUTCHAT`|" + rnum + "`|" + ID);
			}
			MCHAT.clear();
		}
	}
}
	
	
	//file�� ó�����ִ� ������ (�����°� ����, �޴°� ����)
	public static class filesenderThread implements Runnable{

		int pnum;
		String FID = null;
		
		public filesenderThread(int portnum, String FID) {
			pnum = portnum;
			this.FID = FID;
		}
		
		@Override
		public void run() {
			try {
				Socket sendersoc = new Socket(ip, pnum);
				
	            OutputStream out =sendersoc.getOutputStream();   //������ ����Ʈ������ �����͸� ������ ��Ʈ���� �����մϴ�.
	            DataOutputStream dout = new DataOutputStream(out); //OutputStream�� �̿��� ������ ������ ������ ��Ʈ���� �����մϴ�.            
	            
	            /* ������ Client ������ �ڵ� */
            	String filename = FileMatch.get(FID);
            	FileMatch.remove(FID, filename);            	
            	
                FileInputStream fin = new FileInputStream(new File(filename)); //FileInputStream - ���Ͽ��� �Է¹޴� ��Ʈ���� �����մϴ�.
                byte[] buffer = new byte[1024];        //����Ʈ������ �ӽ������ϴ� ���۸� �����մϴ�.        
                int len;                               //������ �������� ���̸� �����ϴ� �����Դϴ�. 
                int data=0;                            //����Ƚ��, �뷮�� �����ϴ� �����Դϴ�.
                while((len = fin.read(buffer))>0){     //FileInputStream�� ���� ���Ͽ��� �Է¹��� �����͸� ���ۿ� �ӽ������ϰ� �� ���̸� �����մϴ�.
                   data++;                        //�������� ���� �����մϴ�.
                }        
                int datas = data;                      //�Ʒ� for���� ���� data�� 0�̵Ǳ⶧���� �ӽ������Ѵ�.
                fin.close();
                fin = new FileInputStream(filename);   //FileInputStream�� ����Ǿ����� ���Ӱ� �����մϴ�.
       
                dout.writeInt(data);                   //������ ����Ƚ���� ������ �����ϰ�, 
                dout.writeUTF("c_"+filename);               //"c_������ �̸�"�� ������ �����մϴ�.
        
                len = 0;      
                for(;data>0;data--){              //�����͸� �о�� Ƚ����ŭ FileInputStream���� ������ ������ �о�ɴϴ�.
                   len = fin.read(buffer);        //FileInputStream�� ���� ���Ͽ��� �Է¹��� �����͸� ���ۿ� �ӽ������ϰ� �� ���̸� �����մϴ�.
                   out.write(buffer,0,len);       //�������� ������ ����(1kbyte��ŭ������, �� ���̸� �����ϴ�.
                }
                
                
                System.out.println("������ �Ϸ�!");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	public static class filereceiverThread implements Runnable{

		int pnum;
		public filereceiverThread(int portnum) {
			pnum = portnum;
		}
		
		@Override
		public void run() {

			try {
				Socket sendersoc = new Socket(ip, pnum);

				InputStream in = null;
				FileOutputStream outt = null;
				in = sendersoc.getInputStream(); // Ŭ���̾�Ʈ�� ���� ����Ʈ ������ �Է��� �޴� InputStream�� ���� �����մϴ�.
				DataInputStream din = new DataInputStream(in); // InputStream�� �̿��� ������ ������ �Է��� �޴� DataInputStream��
																// �����մϴ�.

				int data = din.readInt(); // Int�� �����͸� ���۹޽��ϴ�.
				String filename = din.readUTF();            //String�� �����͸� ���۹޾� filename(������ �̸����� ����)�� �����մϴ�.      


				//���� ��θ� �����մϴ�.
				filename = MainScreen.returnPath() + filename;
				

				File file = new File(filename); // �Է¹��� File�� �̸����� �����Ͽ� �����մϴ�.
				outt = new FileOutputStream(file); // ������ ������ Ŭ���̾�Ʈ�κ��� ���۹޾� �ϼ���Ű�� FileOutputStream�� �����մϴ�.
				byte[] buffer = new byte[1024]; // ����Ʈ������ �ӽ������ϴ� ���۸� �����մϴ�.

				int len = 0; // ������ �������� ���̸� �����ϴ� �����Դϴ�.
				for (; data > 0; data--) { // ���۹��� data�� Ƚ����ŭ ���۹޾Ƽ� FileOutputStream�� �̿��Ͽ� File�� �ϼ���ŵ�ϴ�.
					len = in.read(buffer);
					outt.write(buffer, 0, len);
				}

				outt.close();
				sendersoc.close();
				MainScreen.successFileReceive();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
}
