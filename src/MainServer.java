/**
MainServer.java

ä�� ���α׷��� main server.
room manage�� �����鼭 ä�ù��� �����Ѵ�. => Ŭ���̾�Ʈ -> ������ ä�� ������ �� ��, �濡 �ִ� ������� ������ �Ѱ��־�� �Ѵ�.

client�κ��� accept���� ��û�� ������ client�� �ٷ�� thread�� �����ؼ� socket�� �Ѱ��ش�. (�� thread�� EchoServer�� ����)


modifier: Kim Su hyeon.
E-mail Address: tpfbdpf@naver.com
Last Changed: Nov 13, 2020.
*/

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import Variable.RequestRoom;
import Variable.Message;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;


public class MainServer {

	// �������� client�� ������ �����Ѵ�. - �α��ο� �����ؾ� ���⿡ ���� �� ����
	public static HashMap<String, PrintWriter> client = new HashMap<>();

	// client thread�� room manage thread�� �̾��� ģ����
	public static Queue<RequestRoom> createRoomQueue = new LinkedList();
	public static Queue<Message> messageSet = new LinkedList(); //�޼����� ��� ���� ��������� ���ÿ� ������ �����ؾ��� -> hashMap! (room number�� �ۿ��� üũ �����ϵ���)
	
	final private static int portnum = 6789;
		
	public static String getCurrentTime() {
		Date date_now = new Date(System.currentTimeMillis()); // ����ð��� ������ Date������ �����Ѵ�
		
		//HHmmss
		SimpleDateFormat date_format = new SimpleDateFormat("yyMMddHHmmssSS");

		return date_format.format(date_now).toString();
	}

	public static void main(String[] args) throws Exception {
		// client�� �����ϴ� thread�� ������ pool���� (�ִ� 500����� ����)
		ExecutorService pool = Executors.newFixedThreadPool(500);
		
		// chatroom�� �����ϴ� roomManage ����
		pool.execute(new RoomManage());
		
		System.out.println("start server!");
		try (ServerSocket listener = new ServerSocket(portnum)) { // listener socket����
			while (true) {
				pool.execute(new Handler(listener.accept())); // socket�����û�� ���� accept�ϰ� thread�� �����ϸ� socket�� �Ѱ���
			}
		}
	}

	
	
	/** client thread �ڵ�. - ���� client�� ���õ� ���� ��� ���⼭ ó���Ѵ�. */
	public static class Handler implements Runnable {
		// ���� socket�� stream
		private Socket socket;
		private Scanner in;
		private PrintWriter out;

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

				
				while (true) { // ó���� �α��� ���� + ȸ������ => �α����� �ؾ��� while�� �Ѿ��
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
//						
						
						HashMap<String, String> temp = new HashMap<String, String>();
						temp.put("SALT", info[2]);
						temp.put("ID", info[3]);
						temp.put("NICKNAME", info[4]);
						temp.put("PASSWORD", info[5]);
						temp.put("NAME", info[6]);
						temp.put("PHONE", info[7]);
						temp.put("EMAIL", info[8]);
						temp.put("BIRTH", info[9]);

						if(info[1].equals("1")) {
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

							
			    HashMap<String,String> binfo = new HashMap<String,String>();
			    binfo = query.selectNAME_NICKNAME_STATE(ID);
				
				// �⺻ �������� client���� ���� �����ش� JDBC�� �̿��ؾ���
				out.println("BASICINFO`|name`|" + binfo.get("NAME"));
				out.println("BASICINFO`|nickname`|" + binfo.get("NICKNAME"));
				out.println("BASICINFO`|state_message`|" + binfo.get("STATE_MESSAGE"));
				
				//���ӻ��� ������Ʈ!!! -> ���� �������̶�� ��
				query.updateLAST_CONNECTION(ID, "0");
				
				//=====================================================================================================
				//===============================>> ���� ������ ���� �Ϸ� (�α��� & ȸ������) <<==================================
				//ģ����� �޾ƿ���� ���� client ���� �ȵ� + �׽�Ʈ ���غ�
				
				//ģ�����(ģ���� ���������� �ƴ�����)
				//ģ����� �޾ƿ���
				String[][] f_list = query.selectFRIEND(ID);

				out.println("BASICINFO`|FRIENDLIST"); //�������� ģ�� ����Ʈ�� �Ѿ�ٰ� ��ȣ�� �ش�
				int fnum = Integer.parseInt(f_list[0][1]) - 1;
				
				for(int i=1;i<=fnum;i++) {
					String[] l = f_list[i];
					String flist = "";

					for(String t : l) {
						flist = flist + "`|" + t;
					}
					out.println(flist);
//					System.out.println("=> " + flist);
				}
				out.println("BFEND"); //���� �ѱ�°� ������ ��!

				
				//�̷��� �⺻ �������� �� �Ѱ��ְ� �Ǹ� ���� thread�󿡼� ��� ���鼭 �ൿ�ϴ� �ϸ� ���� �Ȥ�..... �ٵ� �����ϸ� ��û �ѵ��ϰ���??
				
								
				//main update�� ��� ���� ���� �����غ��� -> ģ�� ��û�̶����, �ٸ� ������ Ȱ�� ���� ���

				System.out.println("��������մϴ�!");

				//���α׷��� ���ư��� ���� ������ �̷������ �κ� (Ŭ���̾�Ʈ�κ��� �Է��� �޴´�! / client -> server)
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
						}
						
						//ģ�� ���� => FRIEND NO [FID]
						else if(info[1].compareTo("NO") == 0) {
							//ģ�� ��û ���̺��� �������ְ�, ��.
							query.deleteFRIEND_PLUS(ID, info[2]);
						}
						
						//ģ�� ������ Ȯ�� => FRIEND INFO [FID]
						else if(info[1].compareTo("INFO") == 0) {
							//ģ���� �� ������ �����ش�. �ϴ� �����°͸� ������. �޴°� �ű⼭ �˾Ƽ� �ϰ���!
							HashMap<String, String> map = query.bringINFO(info[2]);
							out.println("FRIEND INFO`|" + map.get("ID") + "`|" + map.get("LAST_CONNECTION") + "`|" + map.get("PHONE")  + "`|" 
									+ map.get("EMAIL")  + "`|" + map.get("BIRTH")  + "`|" + map.get("GITHUB")  + "`|" + map.get("STATE_MESSAGE"));
							//������ ���� : FRIEND INFO [FID LAST_CONNECTION phone email birth github state_message]
							//github�� ��޴� ���ٸ� null�� ��.
						}
					}
					
					/**�˻� ���� ó��========================================*/
					else if (line.startsWith("SEARCH")) {
						String info[] = line.split("\\`\\|");
						
						//�� ģ�� �˻� (my friend) => SEARCH MF [�˻���]
						if(info[1].compareTo("MF") == 0) {
							String[] idlist = query.searchMYFRIEND(ID, info[2]);
							
							String list = null;
							for(String s : idlist) {
								list = list + "`|" + s;
							}
							
							//���� ���������
							out.println("SEARCH`|MFRT" + list);
						}
						
						//�ܺ� ģ�� �˻� (other friend) => SEARCH OF [�˻���]
						else if(info[1].compareTo("OF") == 0) {
							String[] idlist = query.searchOTHERFRIEND(ID, info[2]);
							
							String list = null;
							for(String s : idlist) {
								list = list + "`|" + s;
							}

							//���� ���������
							out.println("SEARCH`|OFRT" + list);
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
							else
								out.println("SETTING`|PWCK`|NO");
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
							if(info[8 + ck].equals(""))
								query.updateGITHUB(ID, null);
							else
								query.updateGITHUB(ID, info[8 + ck]);
							
							if(info[9 + ck].equals(""))
								query.updateSTATE_MESSAGE(ID, null);
							else
								query.updateSTATE_MESSAGE(ID, info[9 + ck]);

							
							//��й�ȣ ������Ʈ
							if(ck == 2) query.updatePASSWORD(ID, info[3], info[3]);
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
						
	
					}

					
					
					/**ä�ù� ���� (1:1) chat not multi ========================================*/
					else if (line.startsWith("NMCHAT")) {
						String info[] = line.split("\\`\\|");
						
						//1:1 ä�� ��û => CHATNM APP [thread �ĺ���ȣ] [���ID]
						if(info[1].compareTo("APP") == 0) {
							//���� ��û�ߴ�! <--- ���⼭�� �̰ű���

							//�� ���� ��û�ϴ� ������ ¥��
							RequestRoom r = new RequestRoom(ID, 0, 2, info[2], info);
							//�游��� ��� queue�� �־��ش�
							createRoomQueue.add(r);
						}

					}
						
					/**ä�ù� ���� (��Ƽ) chat multi ========================================*/
					else if (line.startsWith("MCHAT")) {
						String info[] = line.split("\\`\\|");
						
						//1:1 ä�� ��û => CHATM APP [thread �ĺ���ȣ] [�ʴ��ο� ��] [���ID]
						if(info[1].compareTo("APP") == 0) {
							//���� ��û�ߴ�! <--- ���⼭�� �̰ű���

							//�� ���� ��û�ϴ� ������ ¥��
							RequestRoom r = new RequestRoom(ID, 1, Integer.parseInt(info[1]), info[2], info);

							//�游��� ��� queue�� �־��ش�
							createRoomQueue.add(r);
						}
					}
					
					/**ä�� ���� ���� ========================================*/
					else if (line.startsWith("CHAT")) { 
						String info[] = line.split("\\`\\|");
						
						//1:1 ä�� ���� => CHAT OK [roomID]
						if(info[1].compareTo("OK") == 0) {
							//������ ���� ��û�� ���� ä�ù濡 ���� ���� �����޴�.
							//�̷��� �ǻ縦 ǥ���ϴ� queue�� �ϳ� �� �ѱ�????????????????????????????????????????
						}
						
						
						//1:1 ä�� ���� => CHAT NO [roomID]
						else if(info[1].compareTo("NO") == 0) {
							//������ ���� ��û�� ���� ä�ù濡 ���� ���� �����ߴ�.
							//�̷��� �ǻ縦 ǥ���ϴ� queue�� �ϳ� �� �ѱ�????????????????????????????????????????
						}
					}
					
					/**ä�� ========================================*/
					else if (line.startsWith("CHAT")) { //CHAT room_id sender_id time message ������ => message�� �� �ڷ� ������!!
						String info[] = line.split("\\`\\|");
						
						int room_id = Integer.parseInt(info[1]);
						Message m = new Message(room_id, info[2], info[3], line.substring(line.indexOf(info[4])));
						//�� ���̵�, ������, �ð�, ����
						
						messageSet.add(m);
						//�̷��� �߰��ϸ� ���� chat thread���� ó���� ����
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// Client�� �����ϸ�, �������� �� �������ش�.

				if(ID != null) {
					// �α��� �� ���¶��
					//������ ���ӽð��� ������Ʈ�Ǿ����
					query.updateLAST_CONNECTION(ID, getCurrentTime());
					

					// ä�ù濡���� �� ���������Ѵ�!!!
				}
				//��α��� ���¿��� ���°� ��� �� �����̼��ϸ� ������ ��
			
			}
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	
	// roomManage thread �ڵ�
	public static class RoomManage implements Runnable {

		ExecutorService chat_pool = Executors.newFixedThreadPool(500);


		@Override
		public void run() {
			
			Random random = new Random(); //���� ��ü ����(����Ʈ �õ尪 : ����ð�)
	        random.setSeed(System.currentTimeMillis()); //�õ尪 ������ ���� �Ҽ��� ����
			
			while(true) {
				
				//�� �����޶�� ��û�� �մٸ�
				if(!createRoomQueue.isEmpty()) {
					//��û ��������
					RequestRoom temp = createRoomQueue.poll();
					
					//���ȣ �������� �����ϱ�
					
					
					
					
					
					int num = random.nextInt(10000);
					//�����ͺ��̽� room_num�� Ȥ�� �̹� �ִ¼����� üũ, ���� �ִٸ� �ٽ� ���� ����
					
					
					
					//���ο� ä�ÿ� ���� ������ �۵�!
					//�� chat pool�� client�� ���� �޼����� �����ְ� �Ǵµ�, ���� client hashmap�� �̿��ϰ� ��.
					chat_pool.execute(new chat(num, temp.getRequester_ID(), temp.getType(), temp.getParticipants_num(), temp.getParticipants_list()));
				}
					
				
				
				
			}
		}
	}
	
	
	// ä�ù� thread �ڵ�
	public static class chat implements Runnable {
		int room_num;
		private String requester_ID;
		private int type; //0�̸� ����, 1�̸� ��ü
		private int participants_num; //���� ����
		private ArrayList<String> participants_list = new ArrayList<String>();

		
		public chat(int room_num, String requester_ID, int type, int participants_num, ArrayList<String> participants_list) {
			this.room_num = room_num;
			this.requester_ID = requester_ID;
			this.type = type;
			this.participants_num = participants_num;
			this.participants_list = participants_list;
			
			//�׸��� DB�� �߰��ϴ� ����! => chatTable�� �߰�
		}

		//���� ���� ���������!
		/**�� thread�� �ؾ��� �ൿ���� �����
		 * 
		 * ���� ��������ٸ� �濡 �ʴ��ϴ� �ൿ�� ���ؾ� �Ѵ�.
		 * (����ó���� ���߿� ������ ���� �ϴ� ��ɺ��� �����!!!!!)
		 * 
		 * - �ϴ��ΰ��
		 * 
		 * 1. ���� ����ڿ��� ���� ��������ٰ� �˸� => �׷� �ϴ� client�������� ä�ù��� �ϳ� ����, ä�� �Է��� ���ϴ� ���·� �������.
		 * 2. �ٸ� ��뿡�Ե� ä�� ��û�� �Դٰ� �˸� => ��뿡�� ������ �� client�������� �˾��� ���� �����Ͻðڽ��ϱ�? �� ��
		 * 3. �ϴ� �� �ٸ� ��뿡�Լ� ������ �� ���� (Y/N)
		 * 
		 * 4-1. ��밡 �����Ѵ�? -> �游�� ����� client�� ������ �޼����� ���� : �׷� �游�� ����� client�� ������ ä���� �����߽��ϴ�! �޼��� �߰� ä�ù� �ݱ�
		 *  				=> �׸��� DB���� �� ä�ù��� �����ϰ�, thread�� ����ȴ�.
		 * 
		 * 4-2. ��밡 �����Ѵ�? -> �游�� ����� client�� ������ �޼����� ���� : �׷� �游�� ����� client�� ä�ù��� Ȱ��ȭ�ȴ�
		 * 					 -> ������ ����ʿ����� ������ ���� ä�ù��� �߸鼭 Ȱ��ȭ �Ǿ�� ��. (�̰� ���� Ŭ���̾�Ʈ���� �� ��)
		 * 
		 * (��밡 �����Ѵٴ� ���� �Ͽ�)
		 * 5. while�� ���鼭 client�κ��� �Է��� �ִ��� Ȯ����. => peek�� ���� queue�� ���� ���鼭 �� �޼����� �ƴ��� Ȯ�� => queue�ϱ� �ð�������� ���ü��ۿ� ����!
		 * ���� �츮 ���� �޼�����?
		 * 
		 * �޼����� DB�� �����ϰ�, �濡 �ִ� ��� ����鿡�� message�� �Ѹ���. (�׷��f�� 1���� �θ�ۿ� ������...)
		 * 
		 * 
		 * ���� �ص� ������ �ܼ��� â�� ������ �������� �ƴϰ�, ä�ù� ������ ������ ��ư�� �����ų� �α׾ƿ��� �ؾ� ������ ���������� �����Ѵ�!!!
		 * �̰� ��F�� �Ǵ�����? => �� �̰� �ʹ� ������ФФ� => �����غ���...�����Ұ���...�ФФФ�
		 * 
		 *  
		 * 6. �̸� �ݺ��ϴٰ� ���� �Ѹ��� �����Ѵٸ�? -> ~~���� �����̽��ϴ�. ä���� ����Ǿ����ϴ�. 
		 * 									=> �׸��� ���� ���� ���� text area�� Ȱ��ȭ�� ä���� ��ġ�� ��. 
		 * 
		 *
		 *
		 * - ��Ƽ�ΰ��
		 * 
		 * 1. ���� ����ڿ��� ���� ��������ٰ� �˸� => �׷� �ϴ� client�������� ä�ù��� �ϳ� ���
		 * 2. �ٸ� ��뿡�Ե� ä�� ��û�� �Դٰ� �˸� => ��뿡�� ������ �� client�������� �˾��� ���� �����Ͻðڽ��ϱ�? �� ��
		 * 3. �ϴ� �� �ٸ� ��뿡�Լ� ������ �� ���� (Y/N)
		 * 
		 * 4-1. ��밡 �����Ѵ�? -> �׳� �׻���� �ȵ����� �Ǵ� ��. ��Ͽ��� �����Ѵ�.(DB������...) �� �̰ř� ���δ�...
		 * 
		 * 4-2. ��밡 �����Ѵ�? -> ������ ��� client�� ä�ù��� �� + �ٸ� ���鿡�� �������� ���Խ��ϴ�~ �߰��ϱ�.
		 * 
		 * (��밡 �����Ѵٴ� ���� �Ͽ�)
		 * 5. while�� ���鼭 client�κ��� �Է��� �ִ��� Ȯ����. => peek�� ���� queue�� ���� ���鼭 �� �޼����� �ƴ��� Ȯ�� => queue�ϱ� �ð�������� ���ü��ۿ� ����!
		 * ���� �츮 ���� �޼�����?
		 * �޼����� DB�� �����ϰ�, �濡 �ִ� ��� ����鿡�� message�� �Ѹ���. (�׷��f�� 1���� �θ�ۿ� ������...)
		 * 
		 * 
		 * ���� �ص� ������ �ܼ��� â�� ������ �������� �ƴϰ�, ä�ù� ������ ������ ��ư�� �����ų� �α׾ƿ��� �ؾ� ������ ���������� �����Ѵ�!!!
		 * �̰� ��F�� �Ǵ�����? => �� �̰� �ʹ� ������ФФ� => �����غ���...�����Ұ���...�ФФФ�
		 * 
		 * 
		 * 
		 * 6. �̸� �ݺ��ϴٰ� ���������Լ� �����ٴ� ��ȣ�� �Դٸ�? => ��밡 �����ٰ� �ٸ� ����ڵ鿡�� �˸���.
		 * 
		 * 
		 * 
		 * 
		 * 
		 * */
		
		
		@Override
		public void run() {
			
			while(true) {				
				//�޼����¿� �޼����� ����ִµ�
				if(!messageSet.isEmpty()) {
					if(messageSet.peek().getRoom_id() == room_num) {
						//�� �޼����� �츮�ų�?
						
						
					}
				}
			}

			
			
			
			//�ο����� 1���� �Ǹ� ê�� �����
			//������ �����ϱ����� chat���� ��ü ����, room���� ���� ���� �ؾ���
			
			
		}

	}

}
