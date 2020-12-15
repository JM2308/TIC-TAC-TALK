/**
MainServer.java

ä�� ���α׷��� main server.
room manage�� �����鼭 ä�ù��� �����Ѵ�. => Ŭ���̾�Ʈ -> ������ ä�� ������ �� ��, �濡 �ִ� ������� ������ �Ѱ��־�� �Ѵ�.

client�κ��� accept���� ��û�� ������ client�� �ٷ�� thread�� �����ؼ� socket�� �Ѱ��ش�. (�� thread�� EchoServer�� ����)

modifier: Kim Su hyeon.
E-mail Address: tpfbdpf@naver.com
*/

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import Variable.Message;
import Variable.TMessage;
import java.text.SimpleDateFormat;


public class MainServer {

	public static HashMap<String, PrintWriter> client = new HashMap<>(); //�������� client����
	public static HashMap<Integer, Queue<Message>> messageSet = new HashMap<>(); //chat thread���� �����޸�
	public static HashMap<Integer, Queue<TMessage>> TTTSet = new HashMap<>(); //TTT thread���� �����޸�

	//�����޸𸮸� ����� �� ���ڼ� ����
	private static AtomicInteger messageCK = new AtomicInteger(1); 
	private static AtomicInteger TTTCK = new AtomicInteger(1);

	//thread���� �����ϴ� thread pool
	private static ExecutorService messagepool = Executors.newFixedThreadPool(500);
	private static ExecutorService filepool = Executors.newFixedThreadPool(50);
	private static ExecutorService TTTpool = Executors.newFixedThreadPool(50);

	final private static int portnum = 6789;
	private static int forroomnumber = 1;
	private static int TTTnumber = 1;
	private static int fileportnum = 33333;
	
	//���� �ð��� ������
	public static String getCurrentTime() {
		Date date_now = new Date(System.currentTimeMillis()); // ����ð��� ������ Date������ �����Ѵ�
		
		//HHmmss
		SimpleDateFormat date_format = new SimpleDateFormat("yyMMddHHmmssSS");

		return date_format.format(date_now).toString();
	}

	
	public static void main(String[] args) throws Exception {
		// client�� �����ϴ� thread�� ������ pool���� (�ִ� 500����� ����)
		ExecutorService pool = Executors.newFixedThreadPool(500);
		
		System.out.println("start server!");
		try (ServerSocket listener = new ServerSocket(portnum)) { // listener socket����
			while (true) {
				pool.execute(new Handler(listener.accept())); // socket�����û�� ���� accept�ϰ� thread�� �����ϸ� socket�� �Ѱ���
			}
		}
	}
	
	/** client thread �ڵ�. - ���� client�� ���õ� ���� ��� ���⼭ ó���Ѵ�. 
	 * client�� ���� �� �� ���� Handler thread�� �ϳ� �� ���ܳ��� client������ �Է��� ���� ��ũ�Ѵ�.
	 * Handler thread�� client hashmap���� �����ȴ�.
	 * */
	public static class Handler implements Runnable {
		// ���� socket�� stream
		private Socket socket;
		private Scanner in;
		private PrintWriter out;
		Thread thread;
		
		// ����� ���� ����
		private String ID = null;

		// constructor -> stream �����۾�
		public Handler(Socket socket) throws IOException {
			this.socket = socket; // ������ �� socket ����
		}

		@Override
		public void run() {
			try {
				in = new Scanner(socket.getInputStream());
			    out = new PrintWriter(socket.getOutputStream(), true);

			    //���� ȭ�� �� �� ���� ���ư��� ��				
				while (true) { // ó���� �α��� ���� + ȸ������ => �α����� �ؾ��� �� while�� �Ѿ��
					String line = in.nextLine();
					System.out.println(line);

					// �α��� : LOGIN ID PASSWORD
					if(line.startsWith("REQSALT")) {
						String info[] = line.split("\\`\\|");

						System.out.println(info[1]);

						
						out.println(query.bringSALT(info[1]));

					}
					else if (line.startsWith("LOGIN")) {
						String info[] = line.split("\\`\\|");
						// ���̵� ����� �´��� �����ͺ��̽��� �����ϸ� üũ
						
						int rt = query.selectLOGIN(info[1], info[2]);

						if (rt == 1) { // ���� �´ٸ�, ID���� ����
							this.ID = info[1];
							client.put(ID, out); // clinet hashmap�� ����ڵ��
							out.println("LOGIN`|SUCCESS");
							break;
						} else { // �ƴϸ� �ٽ� �α����϶�� ����
							out.println("LOGIN`|FAIL");
						}

					}

					// ȸ������ : 
					else if (line.startsWith("REGISTER")) {
						String info[] = line.split("\\`\\|");
						
						for(String t : info) {
							System.out.println(t);
						}
						
						HashMap<String, String> temp = new HashMap<String, String>();
						temp.put("SALT", info[2]);
						temp.put("ID", info[3]);
						temp.put("NICKNAME", info[4]);
						temp.put("PASSWORD", info[5]);
						temp.put("NAME", info[6]);
						temp.put("PHONE", info[7]);
						temp.put("EMAIL", info[8]);
						temp.put("BIRTH", info[9]);

						if(info[1].compareTo("1") == 0) {
							temp.put("GITHUB", info[10]);
						}
						
						
						//�ϴ� id, nickname�ߺ� üũ�� �Ѵ�.
						//���� �ߺ�üũ�� �ϴµ� ��ģ�ٸ�, ��� �ȵǴ��� ����
						if(query.selectID(info[3]) == -1) {
							out.println("REGISTER`|ID");
						}
						else if(query.selectNICKNAME(info[4]) == -1) {
							out.println("REGISTER`|NN");
						}
						else {
							query.insertUSER(temp);
							out.println("REGISTER`|OK");
						}
						query.updateLAST_CONNECTION(info[3], getCurrentTime()); //���� �ð� �־��ֱ�
					}
				}
				//�α��� ������ ���⼭ ����
							
				// ù ���۽� �⺻ �������� client���� ���� �����ش� JDBC�� �̿��ؾ���
			    HashMap<String,String> binfo = new HashMap<String,String>();
			    binfo = query.selectNAME_NICKNAME_STATE(ID);
				
				out.println("BASICINFO`|name`|" + binfo.get("NAME"));
				out.println("BASICINFO`|nickname`|" + binfo.get("NICKNAME"));
				out.println("BASICINFO`|state_message`|" + binfo.get("STATE_MESSAGE"));
				
				//���ӻ��� ������Ʈ!!! -> ���� �������̶�� ��
				query.updateLAST_CONNECTION(ID, "0");
								
				//ģ�����(ģ���� ���������� �ƴ�����)
				//ģ����� �޾ƿ���
				String[][] f_list = query.selectFRIEND(ID);

				out.println("BASICINFO`|FRIENDLIST"); //�������� ģ�� ����Ʈ�� �Ѿ�ٰ� ��ȣ�� �ش�
				int fnum = Integer.parseInt(f_list[0][1]) - 1;
				
				for(int i=1;i<=fnum;i++) {
					String[] l = f_list[i];
					String flist = "";
					int ck = 1;

					for(String t : l) {
						if(ck==1) flist = t;
						else flist = flist + "`|" + t;
						ck++;
					}
					out.println(flist);
				}
				out.println("BFEND"); //���� �ѱ�°� ������ ��!

				
				//�ٸ� ����鿡�� ���� ���Դٰ� �˸���! (���ӻ��� ����)
				for (PrintWriter output : client.values()) {
					if(output.equals(out)) continue;
					//��ο��� ������ client���� �˾Ƽ� �ɷ��� ��������
					//���� ; UPDATE F_state F_ID ����(1�̸� ��������)
					output.println("UPDATE`|F_state`|" + ID + "`|" + 0);
				}
				
				
				//��������� (�α��� & ȸ������ & �⺻ ���� �����ֱ�) <<==================================

				//���� ģ����û�� �����ϴ� Thread�� �Ʒ��� while�� ���ÿ� ���ư��� �˴ϴ�
				RealTimeUpdater runnable = new RealTimeUpdater(ID, out);
				thread = new Thread(runnable);

				thread.start();	//ģ����û Ȯ�� thread �۵� ����
				System.out.println("��������մϴ�!");

//���α׷��� ���ư��� ���� ������ �̷������ �κ� (Ŭ���̾�Ʈ�κ��� �Է��� �޴ºκ�! / client -> server)
				while (true) {
					System.out.println("���۹��!");
					
					String line = in.nextLine();
					System.out.println(line);
					
/**ģ�� ���� ó��========================================*/
					if (line.startsWith("FRIEND")) {
						String info[] = line.split("\\`\\|");
						
						//ģ�� ��û (apply) => FRIEND APP [FID]
						if(info[1].compareTo("APP") == 0) {
							//ID, FID�� ģ�� ��û ��� table�� ���� �ȴ�.
							query.insertFRIEND_PLUS(ID, info[2]);
						}
						
						//ģ�� ���� => FRIEND OK [FID]
						else if(info[1].compareTo("OK") == 0) {
							//ģ����û ���̺��� �������ְ�, ģ�� ���̺� �߰����ش�.
							query.deleteFRIEND_PLUS(ID, info[2]);
							query.insertFRIEND(ID, info[2]);
							
							//ģ���� �����ؼ� Friend�� �Ǿ��ٸ�, client���� �߰��� ����߰���?
							//�ϴ� ���� ������Ʈ�ϱ� => ģ�� ���̺��� �籸���϶�� �˷��ִ°���
							//ID, name, nickname, last_connection, ���
							HashMap<String, String> mapmy = query.bringINFO(info[2]);	
							out.println("FRIEND`|APND`|" + mapmy.get("ID") + "`|" + mapmy.get("NAME") + "`|" + mapmy.get("NICKNAME") + "`|"
									+ mapmy.get("LAST_CONNECTION") + "`|"+ mapmy.get("STATE_MESSAGE"));
							
							//���� ģ���� �������̶��, ģ�� list�� ������Ʈ �����ֶ�� ���� �����ֱ�!
							if(client.containsKey(info[2])) {
								HashMap<String, String> mapp = query.bringINFO(ID);	
								client.get(info[2]).println("FRIEND`|APND`|" + mapp.get("ID") + "`|" + mapp.get("NAME") + "`|" + mapp.get("NICKNAME") + "`|"
										+ mapp.get("LAST_CONNECTION") + "`|"+ mapp.get("STATE_MESSAGE"));
							}
						}
						
						//ģ�� ���� => FRIEND NO [FID]
						else if(info[1].compareTo("NO") == 0) {
							//ģ�� ��û ���̺��� �������ְ�, ��.
							query.deleteFRIEND_PLUS(ID, info[2]);
						}
						
						//ģ�� ������ Ȯ�� => FRIEND INFO [NICKNAME NAME STATE_MESSAGE EMAIL PHONE BIRTH GITHUB]
						else if(info[1].compareTo("INFO") == 0) {
							HashMap<String, String> map = query.bringINFO(info[2]);
							out.println("FRIEND`|INFO`|" + map.get("NICKNAME") + "`|" + map.get("NAME") + "`|" + map.get("STATE_MESSAGE")  + "`|" 
									+ map.get("EMAIL")  + "`|" + map.get("PHONE") + "`|" + map.get("BIRTH")  + "`|" + map.get("GITHUB")  + "`|");
						}
						
						//ģ�� ��û ���̺� �ִ��� Ȯ�� => FRIEND PCK [FID]
						//������ false�� �����Ѵ�.
						else if(info[1].compareTo("PCK") == 0) {

							String ck = query.checkFRIEND_PLUS(ID, info[2]);
							System.out.println("ww" + ck);

							if(ck.compareTo("true") == 0) {
								out.println("FRIEND`|PCK`|T");
							}
							else out.println("FRIEND`|PCK`|F");
						}
						
						//ģ�� ���̺� �ִ��� Ȯ�� => FRIEND FCK [FID]
						else if(info[1].compareTo("FCK") == 0) {

							String ck = query.checkFRIEND(ID, info[2]);
							System.out.println(ck);

							if(ck.compareTo("true") == 0) {
								out.println("FRIEND`|FCK`|T");
							}
							else out.println("FRIEND`|FCK`|F");	
						}
					}
					
/**�˻� ���� ó��========================================*/
					else if (line.startsWith("SEARCH")) {
						String info[] = line.split("\\`\\|");
						
						//�� ģ�� �˻� (my friend) => SEARCH MF [�˻���]
						if(info[1].compareTo("MF") == 0) {
							String[][] idlist = query.searchMYFRIEND(ID, info[2]);
							
							int num = 0;
							String list = null;
							
							
							if(idlist == null) {
								out.println("SEARCH`|REQ`|MF`|" + 0);
								continue;
							}
							
							for(String[] s : idlist) {
								if(s[0] == null) break;
								num++;
								int ck = 1;
								
								for(String k : s) {
									if(num == 1 && ck == 1) list =  k;
									else if(ck == 1) list = list + "`|" + k;
									else list = list + "^" + k;
									ck++;
								}
								System.out.println(list);

							}
							
							//���� ���������
							out.println("SEARCH`|REQ`|MF`|" + num + "`|" + list);
						}
						
						//�ܺ� ģ�� �˻� (other friend) => SEARCH OF [�˻���]
						else if(info[1].compareTo("OF") == 0) {
							//	// return String[][id, nickname, name, last_connection]
							
							String[][] idlist = query.searchOTHERFRIEND(ID, info[2]);
							int num = 0;
							String list = null;
							
							if(idlist == null) {
								out.println("SEARCH`|REQ`|OF`|" + 0);
								continue;
							}

							
							for(String[] s : idlist) {
								if(s[0] == null) break;
								num++;
								int ck = 1;
								
								for(String k : s) {
									if(num == 1 && ck == 1) list = "`|" + k;
									else if(ck == 1) list = list + "`|" + k;
									else list = list + "^" + k;
									ck++;
								}
								System.out.println(list);
							}

							//���� ���������
							out.println("SEARCH`|REQ`|OF`|" + num + list);
							
							System.out.println(num);
							System.out.println("SEARCH`|REQ`|OF`|" + num + list);

						}
					}

								
/**���� ���� ó��========================================*/
					else if (line.startsWith("SETTING")) {
						String info[] = line.split("\\`\\|");

						
						//������ �����ϱ� ���� ��� check => SETTING PWCK [��ȣȭ�� PW]
						if(info[1].compareTo("PWCK") == 0) {
							int ck = query.checkPASSWORD(ID, info[2]);

							//��� ����
							if(ck == 1) {
								out.println("SETTING`|PWCK`|OK");
							}
							else {
								out.println("SETTING`|PWCK`|NO");
							}

						}
						
						//������ ���� => SETTING SAVE [0 NICKNAME NAME PHONE EMAIL BIRTH GITHUB STATE_MESSAGE]
						//           SETTING SAVE [1 PW SALT NICKNAME NAME PHONE EMAIL BIRTH GITHUB STATE_MESSAGE] => ������ ���̵�� ���ٲ�
						else if(info[1].compareTo("SAVE") == 0) {
							
							int ck = Integer.parseInt(info[2]);
							if(ck == 1) ck++;

							//�ٲ�͸� update������!
							HashMap<String, String> map = query.bringINFO(ID); 

							if(map.get("NICKNAME").compareTo(info[3 + ck]) != 0) {
								//�г��� �ߺ�üũ!!!
								if(query.selectNICKNAME(info[3 + ck]) == 1) {
									query.updateNICKNAME(ID, info[3 + ck]);
									out.println("SETTING`|NN`|OK");				
									}
								else {
									out.println("SETTING`|NN`|NO");	
									continue;
								}
							}
							else {
								out.println("SETTING`|NN`|OK");									
							}
							
							
							if(map.get("NAME").compareTo(info[4 + ck]) != 0) {
								query.updateNAME(ID, info[4 + ck]);
							}
							
							if(map.get("PHONE").compareTo(info[5 + ck]) != 0) {
								query.updatePHONE(ID, info[5 + ck]);
							}
							
							if(map.get("EMAIL").compareTo(info[6 + ck]) != 0) {
								query.updateEMAIL(ID, info[6 + ck]);
							}
							
							if(map.get("BIRTH").compareTo(info[7 + ck]) != 0) {
								query.updateBIRTH(ID, info[7 + ck]);
							}
							
							//�̰� �ΰ��� �� ������Ʈ ���� => GITHUB STATE_MESSAGE
							try {
								if(info[8 + ck].compareTo("") == 0)
									query.updateGITHUB(ID, null);
								else
									query.updateGITHUB(ID, info[8 + ck]);
							}
							catch(Exception e) {
								query.updateGITHUB(ID, null);
								info[8 + ck] = null;
							}

							
							try {
								if(info[9 + ck].compareTo("") == 0)
									query.updateSTATE_MESSAGE(ID, null);
								else
									query.updateSTATE_MESSAGE(ID, info[9 + ck]);
							}
							catch(Exception e) {
								query.updateSTATE_MESSAGE(ID, null);
								info[9 + ck] = null;

							}

							//��й�ȣ ������Ʈ
							if(ck == 2) query.updatePASSWORD(ID, info[3], info[3]);
							
							//��� �Է��� ������ �� ������ �������� ������Ʈ!
							out.println("UPDATE`|MYINFO`|" + info[4 + ck] + "`|" + info[3 + ck] + "`|" + info[9 + ck]);
							
							//ģ���鿡�Ե� �ٲ� �� ������ �ڶ��ؾ���
							//�ϴ� �� ģ�� ���� �޾ƿ���
							String[][] f_list2 = query.selectFRIEND(ID);
							
							//ģ������ ID�� �������̶�� �����ּ���~
							int plag = 1;
							for(String[] l : f_list2) {
								if(plag == 1) {
									plag++;
									continue;
								}
								if(client.containsKey(l[4])) {
									client.get(l[4]).println("UPDATE`|FINFO`|" + ID + "`|" + info[4 + ck] + "`|" + info[3 + ck] + "`|" + info[9 + ck]);
								}
							}
						}
						
						//�� ���� ��û => SETTING REQ (GUI�� ä������ �� ������ ��û�ϴ� ��)
						else if(info[1].compareTo("REQ") == 0) {
							//���� �� ������ �����ش�.
							HashMap<String, String> map = query.bringINFO(ID);
							
							//������ �����ش�
							out.println("SETTING`|INFO`|" + map.get("ID") + "`|" + map.get("NICKNAME") + "`|" + map.get("NAME") + "`|" 
										+ map.get("PHONE") + "`|" + map.get("EMAIL")  + "`|" + map.get("BIRTH")  + "`|" + map.get("GITHUB")  
										+ "`|" + map.get("STATE_MESSAGE"));
							//������ ���� : FRIEND INFO [ID NICKNAME NAME PHONE EMAIL BIRTH GITHUB STATE_MESSAGE]
							//github�� ��޴� ���ٸ� null�� ��.			
						}
						
	
						//ȸ��Ż��=> SETTING BYE
						else if(info[1].compareTo("BYE") == 0) {
							
							//ģ������ ID�� �������̶�� Ż���޴ٰ� �˷��ֱ�
							String[][] f_list2 = query.selectFRIEND(ID);
							int plag = 1;
							for(String[] l : f_list2) {
								if(plag == 1) {
									plag++;
									continue;
								}
								if(client.containsKey(l[4])) {
									client.get(l[4]).println("UPDATE`|FBYE`|" + ID );
								}
							}
							query.deleteEVERYWHERE(ID);	

						}
					}
					
					
/**ä�ù� ���� (1:1) personal chat ========================================*/
					else if (line.startsWith("PCHAT")) {
						String info[] = line.split("\\`\\|");
						
						//PCHAT`|REQCHAT`|" + FID : ��� ä���ϰ� �ʹٰ� ��ȣ�ֱ�
						if(info[1].compareTo("REQCHAT") == 0) {
							//���濡�� ä�ù濡 �����Ұ��� ���������
							HashMap<String, String> map = query.bringINFO(ID);
							//���⼭ id�� A. (b�� A�� ������ �޴� ��Ȳ) (���� ����� A��, B���� ������ �մϴ�!)
							
							//PCHAT`|QUSCHAT`|" + ä�ÿ�û��ID + ���� + �̸� : ���� �˷��ָ鼭 ä���Ұųİ� �����   =>�޴��� : �̶� ����(�̸�), ID �����ϱ�
							client.get(info[2]).println("PCHAT`|QUSCHAT`|" + ID+ "`|" + map.get("NICKNAME")+ "`|" + map.get("NAME"));
						}
						
						
						//PCHAT`|PESPONCHAT`|" + ä�ÿ�û��ID + Y/N : ä���Ұųİ� ����f���� ä�� �Ұ��� ������ �亯
						else if(info[1].compareTo("PESPONCHAT") == 0) {
							//PCHAT`|ANSCHAT`|" + ä�ÿ�û��ID + ����(�̸�) : ������ ä�� �����ߴٰ� �˷��ֱ� + ä�� ��� Ǯ�� //������ : �̶� ����(�̸�), ID �����ϱ�
							HashMap<String, String> map = query.bringINFO(ID);
							//���⼭ id�� B (A�� B�� ���� ���ο� ������ �޴� ��Ȳ) => a���� ������ �����ؾ� �ϴ� ��Ȳ (���� ����� B��)

							if(info[3].equals("Y")) { //ä���� �����Ѵٸ� �����Ѵٰ� �˷���
								client.get(info[2]).println("PCHAT`|ANSCHAT`|" + ID +"`|" + map.get("NICKNAME")+ "`|" + map.get("NAME") + "`|" + "Y");
							}
							else { //�����Ѵٸ� �����Ѵٰ� �˸�
								client.get(info[2]).println("PCHAT`|ANSCHAT`|" + ID + "`|" + map.get("NICKNAME")+ "`|" + map.get("NAME") + "`|" + "N");
							}
						}
						
						
						//A�� �� ä���� B���� �����ִ� ��Ȳ (���� ����� A�̰�, ���� b�� ��Ŭ���̾�Ʈ�� �ٷ� ä�� ������! (������ �ƴ�))
						//PCHAT`|sendCHAT`|" + ä�ù޴���ID + Content : ä�ó��� ���� (����������)
						else if(info[1].compareTo("sendCHAT") == 0) {
							//PCHAT`|receivedCHAT`|" + ä�ú�����ID + Content : ä�ó��� ���� (���� ������)
							client.get(info[2]).println("PCHAT`|receivedCHAT`|" + ID +"`|" + info[3]);
						}
						
						
						//A�� B���� ������ �����ٰ� �˷��ִ� �κ�
						//PCHAT`|outCHAT`|" + ä�ù޴���ID
						else if(info[1].compareTo("outCHAT") == 0) {
							//PCHAT`|outCHAT`|" + ä�ú�����ID
							client.get(info[2]).println("PCHAT`|OUTCHAT`|" + ID);
						}
					}
				
					
/**ä�ù� ���� (��Ƽ) chat multi ========================================*/
					else if (line.startsWith("MCHAT")) {
						while (messageCK.get() == 0) {};
						messageCK.set(0);
						
						String info[] = line.split("\\`\\|");
						
						//"MCHAT`|REQROOM`|" + ���̸� + ���� ���� ���� +  �游��� ��û�� ID + flist      
						//�游��� ��û
						if(info[1].compareTo("REQROOM") == 0) {
							//�� ���ڸ� �ο��޴´�
							int rn = forroomnumber;
							forroomnumber++;
							
							//flist�� ������ ������ ����Ʈ �����?
							String requset_flist[] = info[5].split("\\^");
														
							messagepool.execute(new Chat(rn, info[2], info[3], info[4], requset_flist)); // socket�����û�� ���� accept�ϰ� thread�� �����ϸ� socket�� �Ѱ���

							Queue<Message> m = new LinkedList();
							messageSet.put(rn, m);
							
							//"MCHAT`|RoomNumber`|" + ���ȣ    //�� ��ȣ �����ֱ� - �̰� ���ӵ� �������� �����ҵ�??? ��, ���̸� ���⼭ ��ٷ��� �ϴ� �κ���.
							out.println("MCHAT`|RoomNumber`|" + rn);
						}
						
						//"MCHAT`|RESPONCHAT`|" + ���ȣ+ �� ID?? + Y // ä���Ұųİ� ����f���� ä�� �Ұ��� ������ �亯
						else if(info[1].compareTo("RESPONCHAT") == 0) {
							//�̰� ������ �ٷ� ä�ÿ� �����ϰڴٴ� �ǹ̿� �����ϴ�.
							Message m = new Message(Integer.parseInt(info[2]), 0, ID, "0", "0");
							messageSet.get(Integer.parseInt(info[2])).add(m);
						}
						
						//out.println("MCHAT`|sendCHAT`|"+ Integer.toString(rn) + "`|" + ID + "`|" + getCurrentTime() + "`|" + chat);
						//�޼�������
						else if(info[1].compareTo("sendCHAT") == 0) {
							Message m = new Message(Integer.parseInt(info[2]), 1, ID, info[4], info[5]);
							messageSet.get(Integer.parseInt(info[2])).add(m);
							}
						
						//"MCHAT`|OUTCHAT`|" + ���ȣ + ������ID //ä�ÿ��� �����ϴ�
						//�����ٰ� ���ϱ�
						else if(info[1].compareTo("OUTCHAT") == 0) {
							//������ - rn, 3, �������� ID, 0, 0
							Message m = new Message(Integer.parseInt(info[2]), 3, ID, "0", "0");
							messageSet.get(Integer.parseInt(info[2])).add(m);
						}
						
						//"MCHAT`|REQuLIST`|" + ���ȣ  //ä�ÿ��� �����ϴ�
						//�����ٰ� ���ϱ�
						else if(info[1].compareTo("REQuLIST") == 0) {
							Message m = new Message(Integer.parseInt(info[2]), 4, ID, "0", "0");
							messageSet.get(Integer.parseInt(info[2])).add(m);
						}
						
						//"MCHAT`|InviteFriend`|" + ���ȣ + ģ�� ���̵�(��)
						else if(info[1].compareTo("InviteFriend") == 0) {
							Message m = new Message(Integer.parseInt(info[2]), 5, ID, info[3], "0");
							messageSet.get(Integer.parseInt(info[2])).add(m);
						}
						messageCK.set(1);
					}
					
/**file ���� ���� (A - sender, B - receiver) ========================================*/
					else if (line.startsWith("FILES")) {
						String info[] = line.split("\\`\\|");
						// >> ���⼭�� ������ �ְ������ �����ϴ� �������� ������, ������ �ְ� �޴°� ���ο� thread���� ���� socket�� ��� ����
						
						//A�� B���� ������ ������ �ʹٰ� ������ �Ծ�� => FILES ASK ���ID
						if(info[1].compareTo("ASK") == 0) {
							//��븦 ã�Ƽ� ������ API�� ���缭 ������ (FILES ASK A���̵� �̸�(����)
							HashMap<String, String> map = query.bringINFO(ID);
							String senderInfo = map.get("NICKNAME") + "(" + map.get("NAME") + ")";

							client.get(info[2]).println("FILES`|ASK`|" + ID + "`|" + senderInfo);
						}
												
						//B���� A�� ������ ������ ������ ���� ���θ� �����ϴ� ������ �Ծ�� => FILES ANS ���ID, Y/N
						else if(info[1].compareTo("ANS") == 0) {
							
							//���� ������ �޴´ٰ� �Ѵٸ�?
							if(info[3].equals("Y")) {
								//�� ���̸� �̾��� thread�� ����� �ݴϴ�.
								filepool.execute(new filemanage(fileportnum)); // socket�����û�� ���� accept�ϰ� thread�� �����ϸ� socket�� �Ѱ���

								//������ ����� �޴� ������� 1 �� ū portnum�� ������.
								out.println("FILES`|PNUM`|" + fileportnum );
								fileportnum+=1;
								client.get(info[2]).println("FILES`|ANS`|" + ID + "`|" + "Y" + "`|" + fileportnum);
								fileportnum+=1;
							}
							else { //�ȹ޴´ٰ� �ϸ�? => A���� ���൵ �ȴٰ� �˸���
								client.get(info[2]).println("FILES`|ANS`|" + ID + "`|" + "N" );
							}
						}						
					}
					
/**TTT ����  ========================================*/
					else if (line.startsWith("TTT")) {
						String info[] = line.split("\\`\\|");
						
						while (TTTCK.get() == 0) {};
						TTTCK.set(0);
						
						//A�� B���� ����� ��û (TTT ASK ���ID)
						if(info[1].compareTo("ASK") == 0) {
							// ��븦 ã�Ƽ� ������ API�� ���缭 ������ (TTT ASK A���̵� �̸�(����)
							HashMap<String, String> map = query.bringINFO(ID);
							String senderInfo = map.get("NICKNAME") + "(" + map.get("NAME") + ")";

							client.get(info[2]).println("TTT`|ASK`|" + ID + "`|" + senderInfo);
						}
												
						//B���� A�� ������ ������ ������ ���� ���θ� �����ϴ� ������ �Ծ�� => FILES ANS ���ID, Y/N
						else if(info[1].compareTo("ANS") == 0) {
							//������ �Ѵٰ� �ϸ�?
							if(info[3].equals("Y")) {
								int rn = TTTnumber++;
								
								//�� ���ϱ�
								double dValue = Math.random();
								int order = ((int) (dValue * 10))%2;
								int Aorder = 0;
								int Border = 0;
								
								//0�̸� A��, 1�̸� B�� ��.
								if(order == 0) Aorder = 1;
								else Border = 1;
								
								//TTT������ ������ thread�� ����� �ݴϴ�.
								TTTpool.execute(new TTT(rn, info[2] ,ID)); //������ �� ��밡 A
								Queue<TMessage> m = new LinkedList();
								TTTSet.put(rn, m);
								
								HashMap<String, String> map = query.bringINFO(info[2]);
								String senderInfo = map.get("NICKNAME") + "(" + map.get("NAME") + ")";
								
								HashMap<String, String> map2 = query.bringINFO(ID);
								String senderInfo2 = map2.get("NICKNAME") + "(" + map2.get("NAME") + ")";

								//�޴� ������ �ٽ� ������ �����ش� (TTT INFO MNN FNN ROOMNUMBER ORDER) => �̰� �ް� GUI����
								out.println("TTT`|INFO`|" + senderInfo2 + "`|" + senderInfo + "`|" + rn + "`|" + Border);

								//���� �� ������Ե� ������ ������ (TTT INFO MNN FNN ROOMNUMBER ORDER) => �̰� �ް� GUI ����
								client.get(info[2]).println("TTT`|INFO`|" + senderInfo + "`|" + senderInfo2 + "`|" + rn + "`|" + Aorder);
								
							}
							else { //�ȹ޴´ٰ� �ϸ�? => A���� �����ߴٰ� �˸���
								client.get(info[2]).println("TTT`|ANS`|" + ID + "`|" + "N" );
							}
						}		
						
						//���� ���� �ְ�޴� ������ (TTT ING RoonNumber X Y)
						else if(info[1].compareTo("ING") == 0) {
							// Tmessage queue�� �־��ش�. => �׷� �� thread���� ��뿡�� �� ������ �˷��ִ��� �� ��.
							TMessage m = new TMessage(Integer.parseInt(info[3]), Integer.parseInt(info[4]), ID);
							TTTSet.get(Integer.parseInt(info[2])).add(m);
						}
						TTTCK.set(1);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// Client�� �����ϸ�, �������� �� �������ش�.

				if(ID != null) {
					// �α��� �� ���¶��
					//������ ���ӽð��� ������Ʈ�Ǿ����
					query.updateLAST_CONNECTION(ID, getCurrentTime());
					
					//ģ���鿡�Ե� �� �����Ѵٰ� ���׹�� �ҹ�����
					for (PrintWriter output : client.values()) {
						if(output.equals(out)) continue;
						//��ο��� ������ client���� �˾Ƽ� �ɷ��� ��������
						//���� ; UPDATE F_state F_ID ����(1�̸� ��������)
						output.println("UPDATE`|F_state`|" + ID + "`|" + 1);
					}
	
					// ä�ù濡���� �� ���������Ѵ�!!! => client���� ó��

					//client���� ����
					client.remove(ID);
				}
				//��α��� ���¿��� ���°� ��� �� �����̼��ϸ� ������ ��
			}
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	/** file ���� thread �ڵ�
	 * : file�� ������ ��, �� client�� ���ο� socket�� ��� ���� Socket���� ������ ���������� �����Ѵ�.
	 * */
	public static class filemanage implements Runnable{

		private ServerSocket soc;
		private ServerSocket soc1;
		static Socket sender = new Socket(); 
		static Socket receiver = new Socket(); 


		public filemanage (int pnum) throws IOException {
	    	soc = new ServerSocket(pnum);  //�޴� ��� ����
	    	soc1 = new ServerSocket(pnum + 1);  //������ ��� ����.
		}
		
		@Override
		public void run() {

			try {
				sender = soc1.accept();
				receiver = soc.accept();

				// ������ ������κ��� ������ �ޱ�!
				InputStream in = null; // A�� ���� �о��������
				FileOutputStream out = null; // ���������� ���ϻ����� ���� ����
				in = sender.getInputStream(); // Ŭ���̾�Ʈ�� ���� ����Ʈ ������ �Է��� �޴� InputStream�� ���� �����մϴ�.
				DataInputStream din = new DataInputStream(in); // InputStream�� �̿��� ������ ������ �Է��� �޴� DataInputStream ����.
			
				
				/* sender -> receiver*/
				int data = din.readInt(); // (Int�� ������)���� ������ byte �о����
				String filename = din.readUTF(); // String�� �����͸� ���۹޾� filename(������ �̸����� ����)�� �����մϴ�.
				String[] flist = filename.split("\\\\");
				filename = flist[flist.length-1];

				File file = new File(filename); // �Է¹��� File�� �̸����� �����Ͽ� �����մϴ�.

				out = new FileOutputStream(file); // ������ ������ Ŭ���̾�Ʈ�κ��� ���۹޾� �ϼ���Ű�� FileOutputStream�� �����մϴ�.
				int datas = data; // ����Ƚ��, �뷮�� �����ϴ� �����Դϴ�.
				byte[] buffer = new byte[1024]; // ����Ʈ������ �ӽ������ϴ� ���۸� �����մϴ�.

				int len; // ������ �������� ���̸� �����ϴ� �����Դϴ�.
				for (; data > 0; data--) { // ���۹��� data�� Ƚ����ŭ ���۹޾Ƽ� FileOutputStream�� �̿��Ͽ� File�� �ϼ���ŵ�ϴ�.
					len = in.read(buffer);
					out.write(buffer, 0, len);
				}

				System.out.println("���� �ޱ� �Ϸ�");

				
				/* server -> receiver */
				FileInputStream fin = new FileInputStream(new File(filename)); // FileInputStream - ���Ͽ��� �Է¹޴� ��Ʈ��
				OutputStream outt = receiver.getOutputStream(); // Ŭ���̾�Ʈ���� ������ ����
				DataOutputStream dout = new DataOutputStream(outt); // OutputStream�� �̿��� ������ ������ ������ ��Ʈ���� �����մϴ�
				buffer = new byte[1024]; //�ӽ����� ����
				len = 0; //����
				data = 0; // ����Ƚ��
				
				// FileInputStream�� ���� ���Ͽ��� �Է¹��� �����͸� ���ۿ� �ӽ������ϰ� �� ���̸� �����մϴ�.
				while ((len = fin.read(buffer)) > 0) { 
					data++;
				}
				datas = data; // �Ʒ� for���� ���� data�� 0�̵Ǳ⶧���� �ӽ������Ѵ�.
				fin.close();

				fin = new FileInputStream(filename); // FileInputStream�� ����Ǿ����� ���Ӱ� �����մϴ�.

				dout.writeInt(data); // ������ ����Ƚ���� ������ �����ϰ�,
				dout.writeUTF(filename); // ������ �̸��� ������ �����մϴ�.

				len = 0;
				for (; data > 0; data--) { // �����͸� �о�� Ƚ����ŭ FileInputStream���� ������ ������ �о�ɴϴ�.
					len = fin.read(buffer); // FileInputStream�� ���� ���Ͽ��� �Է¹��� �����͸� ���ۿ� �ӽ������ϰ� �� ���̸� �����մϴ�.
					outt.write(buffer, 0, len); // �������� ������ ����(1kbyte��ŭ������, �� ���̸� �����ϴ�.
				}
				System.out.println("���� ������ �Ϸ�");

				out.close(); // client���� ���� �� ������ ����� ���ؼ� �ʼ�!!
				fin.close(); // client���� ���� �� ������ ����� ���ؼ� �ʼ�!!

				if (file.exists()) { // ���� ���� ����
					if (file.delete()) {
						System.out.println("���ϻ��� ����");
					} else {
						System.out.println("���ϻ��� ����");
					}
				} else {
					System.out.println("������ �������� �ʽ��ϴ�.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} //�� ������ ����
		}
	}
	
	/** multi-Chat thread �ڵ�
	 * ���� client���� ��ü ä���� �� ��, �� ä���� ������ ����Ѵ�.
	 * */
	public static class Chat implements Runnable {
		int room_num;
		String room_name;
		private String type; //�������� �������� ���� => N�� �Ⱥ����ְ� Y�� ������
		private int participants_num = 0; //���� ����
		private HashSet<String> participants_list = new HashSet<String>(); //list�� ��Ƶ� ��
		
		public Chat(int room_num, String room_name, String type, String requester_ID, String[] flist) {
			this.room_num = room_num;
			this.room_name = room_name;
			this.type = type; //1�� ������ ������ �ٺ��������.
			
			HashMap<String, String> map = query.bringINFO(requester_ID);
			String makerInfo = map.get("NICKNAME")+ "(" + map.get("NAME") + ")";
						
			//flist���鼭 ����鿡�� ä�� �ʴ� �޼��� ������
			for(String id : flist) {
				//"MCHAT`|INVCHAT`|" + ���ȣ + ���̸� + �ʴ��� �̸�    //���濡�� �� �ʴ���޴ٰ� �˷��ֱ�
				if(client.containsKey(id))
					client.get(id).println("MCHAT`|INVCHAT`|" + room_num + "`|" + room_name + "`|" + makerInfo);
			}	
			participants_list.add(requester_ID);
			participants_num++;
		}
		
		@Override
		public void run() {
			boolean flag = true;
			
			while (flag) {
				// �޼����¿� �޼����� ����ִµ�

				System.out.println(Integer.toString(room_num) + messageSet.get(room_num).isEmpty());
				
				if (!messageSet.get(room_num).isEmpty()){
					Message m = messageSet.get(room_num).poll();
					
					if (m.equals(null))
						continue;

					int k = m.getType();

					if (k == 0) {
						// �����Ѵ� - rn, 0, ������ ID, 0, 0
						// �ٸ� �����ڵ鿡�� �̻���� ���Դٰ� �˷��ִ� �κ� + ���� ������Դ� Typeüũ�ؼ� 
						// "MCHAT`|ANSCHAT`|" + ���ȣ + ���ID
						HashMap<String, String> map = query.bringINFO(m.getSender_id());
						String senderInfo = map.get("NICKNAME") + "(" + map.get("NAME") + ")";

						for (String id : participants_list) {
							client.get(id).println("MCHAT`|ANSCHAT`|" + room_num + "`|" + senderInfo);
						}

						if(type.equals("1")) { //1�̶�� �ϸ� ������ ���� �޼����� �� �����ش�
							// return array[][time, sender, content]
							String[][] chatlist = query.bringCHATTING(Integer.toString(room_num));
							
							int cnum = Integer.parseInt(chatlist[0][0]);
							
							String messagelist = "";
							
							for(int i=1;i<=cnum;i++) {
								HashMap<String, String>  map2 = query.bringINFO(chatlist[i][1]);
								String senderInfo2 = map2.get("NICKNAME") + "(" + map2.get("NAME") + ")";
								client.get(m.getSender_id()).println("=====>" + chatlist[i][2]);

								messagelist =  messagelist + "`|" + senderInfo2 + "^" + chatlist[i][2];
							}
							
							//���� ê���� �����ֱ�
							client.get(m.getSender_id()).println("MCHAT`|PRECHAT`|" + room_num +  "`|" + cnum + messagelist);
					
						}

						participants_list.add(m.getSender_id());
						participants_num++;
					}

					else if (k == 1) {// �޼��������� - rn, 1, sender ID, time, message
						// "MCHAT`|receivedCHAT`|" + ���ȣ + ä�ú�����ID + ����(name) + content //ä�ó��� ���� (�Ѹ���)
						HashMap<String, String> map = query.bringINFO(m.getSender_id());
						String senderInfo = map.get("NICKNAME") + "(" + map.get("NAME") + ")";

						for (String id : participants_list) {
							client.get(id).println("MCHAT`|receivedCHAT`|" + room_num + "`|" + m.getSender_id() + "`|"
									+ senderInfo + "`|" + m.getMessage());
						}
						query.insertCHATTING(Integer.toString(room_num), m.getTime(), m.getSender_id(), m.getMessage());
					}

					else if (k == 2) { // �ʴ��Ѵ�- rn, 2, �ʴ��� ID, �ʴ�޴��� ID, 0
						client.get(m.getTime())
								.println("MCHAT`|INVCHAT`|" + room_num + "`|" + room_name + "`|" + m.getSender_id());
					} 
					
					else if (k == 3) { // ������ - rn, 3, �������� ID, 0, 0
						participants_list.remove(m.getSender_id());
						participants_num--;
						// "MCHAT`|outCHAT`|" + ���ȣ + ������ID + ����(name) //ä�ÿ��� �����ϴ� (�Ѹ���)
						HashMap<String, String> map = query.bringINFO(m.getSender_id());
						String senderInfo = map.get("NICKNAME") + "(" + map.get("NAME") + ")";

						for (String id : participants_list) {
							client.get(id).println(
									"MCHAT`|outCHAT`|" + room_num + "`|" + m.getSender_id() + "`|" + senderInfo);
						}
					} 
					
					else if (k == 4) {
						// "MCHAT`|ulist`|" + ���ȣ + ����Ʈ ä�� �� �ִ� ���� //�������� ��������Ʈ�� ��û�ڿ��� ����
						String userlist = null; // �̰� ä���ֱ�!!!!
						int a = 0;
						
						for (String id : participants_list) {
							HashMap<String, String> map = query.bringINFO(id);
							String senderInfo = map.get("NICKNAME") + "(" + map.get("NAME") + ")";
							
							if(a == 0) {
								a++;
								userlist = senderInfo;
							}
							else {
								userlist = userlist + "^" + senderInfo;
							}
						}
	
						client.get(m.getSender_id()).println("MCHAT`|ulist`|" + room_num + "`|" + userlist);
					}
					
					else if (k == 5) { //ģ������ �ʴ��ϱ�

						String ulist[] = m.getTime().split("\\^");
						
						HashMap<String, String> map = query.bringINFO(m.getSender_id());
						String senderInfo = map.get("NICKNAME") + "(" + map.get("NAME") + ")";
						
						//flist���鼭 ����鿡�� ä�� �ʴ� �޼��� ������
						for(String id : ulist) {
							//"MCHAT`|INVCHAT`|" + ���ȣ + ���̸� + �ʴ��� �̸�    //���濡�� �� �ʴ���޴ٰ� �˷��ֱ�
							if(client.containsKey(id) && !participants_list.contains(id))
								client.get(id).println("MCHAT`|INVCHAT`|" + room_num + "`|" + room_name + "`|" + senderInfo);
						}
					}

					messageCK.set(1);
				} else {
					System.out.println("+");
				}

				// �ο����� 0���� �Ǹ� ê�� ����� => �̰͵� �����
				if (participants_num < 1) {
					flag = false;
					messageSet.remove(room_num);
				} else {
					System.out.println(".");
				}
			}
			
			//������ �����ϱ����� chat���� ��ü ����, room���� ���� ���� �ؾ���	
			query.deleteCHATTING(Integer.toString(room_num));

		}
	}

	/** ƽ���� thread �ڵ�
	 * �� client���� ƽ���� ������ �� ��, ������ ������ ���!
	 * */
	public static class TTT implements Runnable{
		private int room_num;
		private String AID;
		private String BID;
		private int gameboard[][]= {{0,0,0},{0,0,0},{0,0,0}};
		private int count=0;
		
		public TTT(int room_num, String A, String B) {
			//order�� 1�̸� A�� ��, 0�̸� B�� ��.
			
			this.room_num = room_num;
			this.AID = A; //O����
			this.BID = B; //X����
			
		}

		//���а� ������ Ȯ���ϴ� �ڵ�. (���� �������� Ȯ���ϴ� �ڵ� (1�̸� A, 2�̸� B)), -1�̸� ���º�, 0�̸� �� �Ҽ� �ִٴ� �� 
		public int checkIfWinner() {
			for(int i=0;i<gameboard.length;i++) {
				if (((gameboard[i][0]==1)||(gameboard[i][0]==2)) && (gameboard[i][0] == gameboard[i][1]) && (gameboard[i][0] == gameboard[i][2])) {
					if(gameboard[i][0]==1) return 1;
					else return 2;
				}
				else if (((gameboard[0][i]==1)||(gameboard[0][i]==2)) && (gameboard[0][i] == gameboard[1][i]) && (gameboard[0][i] == gameboard[2][i])) {
					if(gameboard[0][i]==1) return 1;
					else return 2;
				}			
			}
			
			if (((gameboard[0][0]==1)||(gameboard[0][0]==2)) && (gameboard[0][0] == gameboard[1][1]) && (gameboard[0][0] == gameboard[2][2])) {
				if(gameboard[0][0]==1) return 1;
				else return 2;
			}	
			
			else if (((gameboard[0][2]==1)||(gameboard[0][2]==2)) && (gameboard[0][2] == gameboard[1][1]) && (gameboard[0][2] == gameboard[2][0])) {
				if(gameboard[0][2]==1) return 1;
				else return 2;
			}	
			
			if(count==9) {//���º�1
				return -1;
			}
			return 0;
		}
		
		@Override
		public void run() {
			boolean flag = true;

			while (flag) {
				if (!TTTSet.get(room_num).isEmpty()){
					TMessage m = TTTSet.get(room_num).poll();
					
					if (m.equals(null))
						continue;
					
					int ck;
					if(m.getSender_id().equals(AID)) ck = 1;
					else ck = 2;
					
					gameboard[m.getx()][m.gety()] = ck;
					client.get(BID).println(gameboard[0][0]+ "|" + gameboard[0][1]+ "|" + gameboard[0][2]+ "\n" + gameboard[1][0] + "|" +  gameboard[1][1]+ "|" +  gameboard[1][2]
							+ "\n" + gameboard[2][0]+ "|" + gameboard[2][1] + "|" + gameboard[2][2]);
					client.get(BID).println(m.getx()+ "|" +m.gety());

					
					//��밡 ��� ���� �״��� �˸���
					if(m.getSender_id().equals(AID)) 
						client.get(BID).println("TTT`|NOTI`|" + room_num + "`|" + m.getx() + "`|" + m.gety());
					else
						client.get(AID).println("TTT`|NOTI`|" + room_num + "`|" + m.getx() + "`|" + m.gety());
					
					int ckwinner = checkIfWinner();
					
					if(ckwinner == 1) { //���ڰ� A���~
						client.get(AID).println("TTT`|RESULT`|" + room_num + "`|" + "WIN");
						client.get(BID).println("TTT`|RESULT`|" + room_num + "`|" + "LOSE");
						flag = false;
						
					} else if (ckwinner == 2) { // ���ڰ� B���~~
						client.get(AID).println("TTT`|RESULT`|" + room_num + "`|" + "LOSE");
						client.get(BID).println("TTT`|RESULT`|" + room_num + "`|" + "WIN");
						flag = false;

					} else if (checkIfWinner() == -1) {// ���º�!
						client.get(AID).println("TTT`|RESULT`|" + room_num + "`|" + "SAME");
						client.get(BID).println("TTT`|RESULT`|" + room_num + "`|" + "SAME");

						flag = false;
					}
					
				} else {
					System.out.println("+");
				}
			}
			TTTSet.remove(room_num);
			for(int i=0;i<3;i++) {
				for(int j=0;j<3;j++) {
					System.out.print(gameboard[i][j]);
				}
				System.out.println("");
			}
		}
	}
	
	/** ģ����û ���� thread �ڵ�
	 * �ϳ��� handler thread�� ����� ��, ���� �پ ����Ǹ� �ǽð����� ģ����û�� �����Ѵ�.
	 * */
	public static class RealTimeUpdater implements Runnable {
		// ����� ���� ����
		private String ID = null;
		private PrintWriter out;

		public RealTimeUpdater(String id, PrintWriter out) throws IOException {
			ID = id;
			this.out = out;
		}

		@Override
		public void run() {
			System.out.println("realtime!");

			while (client.containsKey(ID)) {

				if (query.checkPLUS(ID) == 1) { // ���� ģ����û ����Ʈ�� ���� �ִٸ�?

					String[][] FriendPlusList = query.bringFRIEND_PLUS(ID);
					int i = 0;

					// �� �ִ°� �� ����~~ // return String[][name, nickname, last_connection, ��� ,id]
					for (String[] list : FriendPlusList) {
						try {
							if (list[0].compareTo("null") == 0)
								continue;
						} catch (Exception e) {
							break;
						}

						out.println("UPDATE`|FRIREQ`|" + list[1] + "`|" + list[0] + "`|" + list[4]);

						System.out.println("!");

						// client���� �����ؼ� ���� �ٲ𶧱��� ��ٸ�
						while (query.checkFRIEND_PLUS(list[4], ID).compareTo("false") != 0) {
							;
						}
						System.out.println("change!");

						// �ٲ� DB�� ����Ǹ� �������� �������� �Ѿ�ϴ�
					}
				}
				try {
					Thread.sleep(1000);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
