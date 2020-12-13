package client;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.table.*;
import com.google.gson.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.*;
import java.text.*;

import client.Client;

public class MainScreen extends JFrame implements MouseListener, ActionListener {
	static JTable jTable;
	static String columnNames[] = { "ID", "�г���(�̸�)", "���ٸ޽���", "status" };
	static Object rowData[][] = {}; // ģ����� ���� �� �ڸ�!
	static DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
		public boolean isCellEditable(int i, int c) {
			return false;
		}

		public Class getColumnClass(int column) {
			try {
				return getValueAt(0, column).getClass();
			}
			catch (Exception e) {
				return null;
			}
		}
	};
	
	int flag = 0;
	private JMenuItem menuItemAdd;
	int searchingFlag = 0;
	
	//�ܺο��� �����ؼ� ������ �����ؾ� �� element��
	static JLabel myName;
	static JLabel message;
	static int friendnum;
	static ImageIcon onlineIcon;
	static ImageIcon offlineIcon;
	
	private static String date;
	private static String time;
	private static HashMap<String, String> SKYcode = new HashMap<String, String>();
	private static HashMap<String, String> PTYcode = new HashMap<String, String>();
	private static String data[] = new String[3]; 
	private static ImageIcon tempImgIcon;
	private static ImageIcon skyImgIcon;
	private static ImageIcon waterImgIcon;
	
	public static void setCurrentTime() {
	      Date date_now = new Date(System.currentTimeMillis()); // ����ð��� ������ Date������ �����Ѵ�
	      
	      SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd");
	      SimpleDateFormat time_format = new SimpleDateFormat("HH");

	      date = date_format.format(date_now).toString();
	      time = time_format.format(date_now).toString();
	      
	      int timeSet = Integer.parseInt(time) - 1;
	      time = Integer.toString(timeSet);
	      time = String.format("%02d", timeSet);
	      time = time + "00";
	   }
	
	//������ ����!!
	public static void changeMyInfo(String name, String nn, String state_M) { // �ۿ��� �θ��� �� ������ ����
		if (state_M.compareTo("null") == 0)
			state_M = null;

		// �̸��̶� ��޸� �ٲٸ� �ȴ�
		myName.setText("   " + nn + "(" + name + ")");
		message.setText(state_M);
	}
	
	//ģ�� ���� ���� - ID name nn state_m
	public static void changeFriendInfo(String[] info) { //�ۿ��� �θ��� ģ�� ���� ����
		int row = -1;

		//���� ģ���� ã���ϴ�
  	  	for(int i=0;i<friendnum;i++) {
  			Object line = model.getValueAt(i, 0);
  			if(line.toString().compareTo(info[0]) == 0) row = i;
  	  	}

  	  	if(row == -1) return;
  	  	
		if (info[3].compareTo("null") == 0) info[3]  = " ";;
  	  	
	  	model.setValueAt(info[2] + "(" + info[1] + ")", row, 1);
	  	model.setValueAt(info[3], row, 2);

	}
	
	//���ӻ��� ����
	public static void changeFriendstate(String ID, String state) { //�ۿ��� �θ��� ģ�� ���ӻ��� ����
		int row = -1;
		String columnNames[] = { "ID", "�г���(�̸�)", "���ٸ޽���", "status" };

  	  	for(int i=0;i<friendnum;i++) {
  			Object line = model.getValueAt(i, 0);
  			  			
  			if(line.toString().compareTo(ID) == 0) row = i;
  	  	}

  	  	if(row == -1) return;
  	  	
  	  	if(Integer.parseInt(state) == 0)
  	  		model.setValueAt(onlineIcon, row, 3);
  	  	else
  	  		model.setValueAt(offlineIcon, row, 3);
	}
	
	//ģ�� �߰��� ����Ʈ ������Ʈ
	public static void changeFriend(String[] flist) { // ģ���� �߰��� �� list update
		// ID, name, nickname, last_connection, ���
		String nn_name = flist[2] + "(" + flist[1] + ")";
		
		if (flist[4].compareTo("null") == 0) flist[4]  = " ";

		if (flist[3].compareTo("0") == 0) {
			Object inData[] = { flist[0], nn_name, flist[4], onlineIcon };
			model.addRow(inData);
		} else {
			Object inData[] = { flist[0], nn_name, flist[4], offlineIcon };
			model.addRow(inData);
		}
		friendnum++;
	}
	
	//ģ����û �˾� �����ֱ�
	public static int showFriendPlus(String nn, String name) {
		String[] buttons = {"Yes", "No"};
		String windowName = "ģ�� ��û";
		String showMessage = nn + "(" + name + ") ���� ģ����û�� �����Ͻðڽ��ϱ�?";
		
		
	      int result = JOptionPane.showOptionDialog(null, showMessage, windowName, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, "�ι�°��");
	      if (result == 0) {
	    	  return 1; //ģ������
	      } else if (result == 1) {
	    	  return 0; //ģ������
	      }
    	  return 0; //ģ������
	}

	//�ϴ�ä�ý�û �˾� �����ֱ�
	public static void showPCHAT(String fid, String nn, String name) {
		String windowName = "ä�� ��û";
		String showMessage = nn + "(" + name + ") ���� 1�� 1 ä���� �����Ͻðڽ��ϱ�?";
			
		int reply = JOptionPane.showConfirmDialog(null, showMessage, windowName, JOptionPane.YES_NO_OPTION);

		if (reply == JOptionPane.YES_OPTION) {
			System.out.println("1 => Yes");
			Client.CHATANSWER(fid, true);
			ChattingOne newchat = new ChattingOne(fid);
			newchat.setoppenInfo(nn, name);
			newchat.setTextFree();
			Client.addPCHAT(fid, newchat);
		} else {
			Client.CHATANSWER(fid, false);
		}
	}
	//��Ƽä�ý�û �˾� �����ֱ�
	public static void showMCHAT(int roomnumber, String roomname, String makerinfo) {
		String windowName = "ä�� ��û";
		String showMessage = roomname + "(by " + makerinfo + ")���� ��Ƽ ä���� �����Ͻðڽ��ϱ�?";
			
		int reply = JOptionPane.showConfirmDialog(null, showMessage, windowName, JOptionPane.YES_NO_OPTION);

		if (reply == JOptionPane.YES_OPTION) {
			Client.MCHATANSWER(roomnumber, roomname, true);
		} else {
			Client.MCHATANSWER(roomnumber, roomname, false);
		}
	}
		
	public static void showInviteInOriginRoom(int rn) {
		InviteFriendInOriginRoom IFO = new InviteFriendInOriginRoom(model, rn);
	}

	
	 public void mouseClicked(MouseEvent me) {
	      System.out.println(me);
	        if (me.getButton() == MouseEvent.BUTTON1) {
	           flag++;
	           System.out.println(flag);
	        }
	        if (me.getButton() == MouseEvent.BUTTON3) {
	           flag += 2;
	           System.out.println(flag);
	           if (flag == 3) {
	               JPopupMenu pm = new JPopupMenu();
	                JMenuItem pm_item1 = new JMenuItem("����");
	                pm_item1.addActionListener(this);
	                System.out.println(this);
	                JMenuItem pm_item2 = new JMenuItem("1:1 ä��");
	                pm_item2.addActionListener(this);
	                JMenuItem pm_item3 = new JMenuItem("��������");
	                pm_item3.addActionListener(this);
	                JMenuItem pm_item4 = new JMenuItem("�����ϱ�");
	                pm_item3.addActionListener(this);
	                pm.add(pm_item1);
	                pm.add(pm_item2);
	                pm.add(pm_item3);
	                pm.add(pm_item4);
	                pm.show(me.getComponent(), me.getX(), me.getY());
	           }
	           flag = 0;
	        }
	   }
	   
	   public void mouseEntered(MouseEvent e) {}
	   public void mouseExited(MouseEvent e) {}
	   public void mousePressed(MouseEvent e) {}
	   public void mouseReleased(MouseEvent e) {}
	
	   
	   
	public MainScreen() {
      JFrame frame = new JFrame();
      JPanel panel = new JPanel();
      panel.setBackground(new Color(0, 176, 80));

      // ��ư �ִ� �κ�
      JPanel top = new JPanel();
      top.setPreferredSize(new Dimension(430, 40));
      JLabel topTitle = new JLabel("TIC TAC _ TALK");
      topTitle.setForeground(new Color(0, 54, 78));
      topTitle.setFont(new Font("���", Font.BOLD, 24));
      top.add(topTitle);
      top.setBorder(null);
      top.setBackground(new Color(74, 210, 149));
      
      // ��ư�� �̹��� �ְ� ��ư ũ�⿡ �°� �̹��� �ֱ�
      ImageIcon icon = new ImageIcon("image/searching.png");
      Image searchImage = icon.getImage();
      Image searchChangingImg = searchImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
      ImageIcon searchChangeIcon = new ImageIcon(searchChangingImg);
      ImageIcon icon2 = new ImageIcon("image/chatting.png");
      Image chatImage = icon2.getImage();
      Image chatChangingImage = chatImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
      ImageIcon chatChangingIcon = new ImageIcon(chatChangingImage);
      ImageIcon icon3 = new ImageIcon("image/setting.png");
      Image settingImage = icon3.getImage();
      Image settingChangeImg = settingImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
      ImageIcon settingChangeIcon = new ImageIcon(settingChangeImg);

      JButton searching = new JButton();
      searching.setPreferredSize(new Dimension(30, 30));
      searching.setBounds(15, 10, 30, 30);
      searching.setIcon(searchChangeIcon);
      System.out.println(searching);
      top.add(searching);

      
      //searching ��ư -> ��! �ǵ��� ������
      searching.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent event) {
               if (event.getButton() == MouseEvent.BUTTON1) {
                   JPopupMenu pm = new JPopupMenu();
                   JMenuItem pm_item1 = new JMenuItem("�� ģ�� ã��");
                   JMenuItem pm_item2 = new JMenuItem("�� ģ�� ã��");
                   
                   pm.add(pm_item1);
                   pm.add(pm_item2);
                   pm.show(event.getComponent(), event.getX(), event.getY());


                   pm_item1.addActionListener(new ActionListener() {
                	   @Override
                	   public void actionPerformed(ActionEvent e) {
                		   //�� ģ�� ã��
                		   FindMyFriend finder = new FindMyFriend();
                	   }
                	   
                   });
                   
                   pm_item2.addActionListener(new ActionListener() {
                	   @Override
                	   public void actionPerformed(ActionEvent e) {
                		   //�� ģ�� ã��
                		   FindAllFriend finder = new FindAllFriend();
                	   }
                   });                   
                }
           }
      });
      
      
      JButton chatting = new JButton();
      chatting.setBounds(360, 10, 30, 30);
      chatting.setIcon(chatChangingIcon);
      top.add(chatting);
      
      
      //��Ƽ�� �ʴ� ��ư �׼�
      chatting.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            InviteFriend invite = new InviteFriend(model);
         }
      });
            
      JButton setting = new JButton();
      setting.setPreferredSize(new Dimension(30, 30));
      setting.setBounds(400, 10, 30, 30);
      setting.setIcon(settingChangeIcon);
      
      //������ ���� ��ư �׼� -> ��! �ǵ��� ������
      setting.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            PWCheck check = new PWCheck();
         }
      });
      
      
      //�⺻ ���� ��������!=========================================
      String[] binfo = Client.basicinfo();
      
      // �� ���� �κ�
      JPanel myInfo = new JPanel();
      GridLayout layout = new GridLayout(2, 2);
      JPanel blank = new JPanel();
      JPanel blank2 = new JPanel();
      myInfo.setLayout(layout);
      JLabel myInfo2 = new JLabel("   �� ����");
      myInfo2.setFont(new Font("�����ٸ���", Font.PLAIN, 11));
      myInfo2.setPreferredSize(new Dimension(430, 13));

      ImageIcon addIcon = new ImageIcon("image\\add.png");
      Image addImage = addIcon.getImage();
	  Image addChangingImg = addImage.getScaledInstance(27, 27, Image.SCALE_SMOOTH);
	  ImageIcon addChangeIcon = new ImageIcon(addChangingImg);

      
	  myInfo.setPreferredSize(new Dimension(430, 60));
      myInfo.setBorder(null);
      myInfo.setBackground(new Color(255, 229, 110));
      blank.setBackground(new Color(255, 229, 110));
      
	  myName = new JLabel("   " + binfo[0] + "(" + binfo[1] + ")");
      myName.setFont(new Font("�����ٸ���", Font.PLAIN, 13));
      message = new JLabel(binfo[2]);
      message.setFont(new Font("�����ٸ���", Font.PLAIN, 13));
      JLabel text1 = new JLabel("ģ�����");
      

      // ģ����� �κ�
      JPanel friend = new JPanel();
      JLabel friend2 = new JLabel("   ģ�����");
      friend2.setFont(new Font("�����ٸ���", Font.PLAIN, 11));
      friend2.setPreferredSize(new Dimension(430, 13));
      friend2.setForeground(Color.white);
      friend.setPreferredSize(new Dimension(430, 380));
      friend.setBorder(null);
      friend.setBackground(new Color(0, 54, 78));
      
      ImageIcon onlineImgIcon = new ImageIcon("image\\online.png");
      Image online = onlineImgIcon.getImage();
      Image onlineImg = online.getScaledInstance(10, 10, Image.SCALE_SMOOTH);
      onlineIcon = new ImageIcon(onlineImg);
      
      ImageIcon offlineImgIcon = new ImageIcon("image\\offline.png");
      Image offline = offlineImgIcon.getImage();
      Image offlineImg = offline.getScaledInstance(10, 10, Image.SCALE_SMOOTH);
      offlineIcon = new ImageIcon(offlineImg);

      String statusStr = ""; // online���� offline���� ���ϴ� �κ�
      Icon status = new ImageIcon();
      
      if (statusStr == "online") {
         status = new ImageIcon("image\\online.png");
      } else if (statusStr == "offline") {
         status = new ImageIcon("image\\offline.png");
      }

      
      //ģ����� �ҷ�����!=========================================
      String[][] friendlist = Client.friendList();
      // String[][ID, name, nickname, last_connection, ���]

      if(friendlist != null) {
    	  int idx = 0;
    	  int ck = Integer.parseInt(friendlist[0][0]);
    	  friendnum = ck - 1;

			for (String[] fl : friendlist) {
				if (idx == ck) break;
				if (idx == 0) {
					idx++;
					continue;
				}
				idx++;
				
				String line = fl[0] + "(" + fl[1] + ")";

				// ��ް� null�� ��� ó�����ֱ�
				if (fl[3].compareTo("null") == 0) fl[3] = " ";

				if (fl[2].compareTo("0") == 0) {
					Object inData[] = { fl[4], line, fl[3], onlineIcon };
					model.addRow(inData);
				} else {
					Object inData[] = { fl[4], line, fl[3], offlineIcon };
					model.addRow(inData);
				}
			}
		}
    
      
      //�� �̰� ������ �ȸ���?? ���߿������
      jTable = new JTable(model);
      jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// ���ϼ���
      //jTable.addMouseListener(this);
      jTable.setGridColor(new Color(0,128,0));
      jTable.setBackground(Color.white);
      jTable.setFillsViewportHeight(true);
      JTableHeader header = jTable.getTableHeader();
      jTable.getColumn("�г���(�̸�)").setPreferredWidth(100);
      jTable.getColumn("���ٸ޽���").setPreferredWidth(250);
      jTable.getColumn("status").setPreferredWidth(50);
      JScrollPane jScollPane = new JScrollPane(jTable);
      jScollPane.setPreferredSize(new Dimension(425, 350));
      jTable.getTableHeader().setReorderingAllowed(false);
      jTable.getTableHeader().setResizingAllowed(false);
   
      
      jTable.setRowHeight(30);

      jTable.setGridColor(new Color(0, 54, 78));
	  jTable.setShowVerticalLines(false);
      
      // ���������� �κ�
	  try {
	      String serviceKey = "fYdY4%2Byu3dmw0JWGdeMYIZwa2DYeLyJ7SKJINiF2j6%2BNLVvLQc11vBK0LS7k%2FNWyaTap78EXXRJd%2FZRUH6aLCA%3D%3D";
	      String dataType = "JSON";
	      
	      setCurrentTime();
	      
	       StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtFcst"); /*URL*/
	       urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
	       urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*��������ȣ*/
	       try {
			urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("50", "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	       urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8")); /*��û�ڷ�����(XML/JSON)Default: XML*/
	       urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8"));
	       urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(time, "UTF-8"));
	       urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("62", "UTF-8")); /*���������� X ��ǥ��*/
	       urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("124", "UTF-8")); /*�������� Y ��ǥ*/
	       //������ �������� ��ǥ (62, 124)
	      
	       
	       URL url = new URL(urlBuilder.toString());
	       HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	       conn.setRequestMethod("GET");
	       conn.setRequestProperty("Content-type", "application/json");
	       
	       BufferedReader rd;
	       if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
	           rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	       } else {
	           rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	       }
	       
	       StringBuilder sb = new StringBuilder();
	       String line;
	       
	       while ((line = rd.readLine()) != null) {
	           sb.append(line);
	       }
	       
	       rd.close();
	       conn.disconnect();
	       
	
	     String result= sb.toString();
	
	     JsonParser parser = new JsonParser(); 
	     JsonObject obj = (JsonObject) parser.parse(result); 
	     JsonObject parse_response = (JsonObject) obj.get("response"); 
	     JsonObject parse_body = (JsonObject) parse_response.get("body"); 
	     JsonObject parse_items = (JsonObject) parse_body.get("items");
	     JsonArray parse_item = (JsonArray) parse_items.get("item");     
	     
	     JsonObject weather;
	     String nextTime = null;
	        
	     for(int i = 0 ; i < parse_item.size(); i++) {
	        weather = (JsonObject) parse_item.get(i);
	        if(i == 0) {
	           nextTime = weather.get("fcstTime").getAsString();
	        }
	        
	        if(weather.get("fcstTime").getAsString().compareTo(nextTime) == 0) {
	           if(weather.get("category").getAsString().compareTo("T1H") == 0) {
	              data[0] = weather.get("fcstValue").getAsString();
	           }
	           else if(weather.get("category").getAsString().compareTo("SKY") == 0) {
	              data[1] = weather.get("fcstValue").getAsString();
	           }
	           else if(weather.get("category").getAsString().compareTo("PTY") == 0) {
	              data[2] = weather.get("fcstValue").getAsString();
	           }
	           
	        }
	
	     }
	
	     SKYcode.put("1", "����");
	     SKYcode.put("2", "��������"); //���� (2019.06.4)
	     SKYcode.put("3", "��������");
	     SKYcode.put("4", "�帲");
	
         PTYcode.put("0", "������ ����");
         PTYcode.put("1", "��");
         PTYcode.put("2", "��/��"); //���� (2019.06.4)
         PTYcode.put("3", "��");
         PTYcode.put("4", "�ҳ���");
         PTYcode.put("5", "�����");
         PTYcode.put("6", "�����/������");
         PTYcode.put("7", "������");
      } catch (Exception e) {
    	  System.out.println("Exception");
    	  e.printStackTrace();
      }

      JPanel publicData = new JPanel();
      //publicData.setBorder(null);
      publicData.setPreferredSize(new Dimension(430, 105));
      //publicData.setBorder(new TitledBorder(new LineBorder(Color.yellow,2)));
      publicData.setBackground(new Color(0, 54, 78));
      
      JPanel datePanel = new JPanel();
      //datePanel.setBorder(new TitledBorder(new LineBorder(Color.yellow,2)));
      JLabel yearLabel = new JLabel(date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6));
      yearLabel.setForeground(Color.white);
      yearLabel.setFont(new Font("���", Font.BOLD, 12));
      //yearLabel.setPreferredSize(new Dimension(60, 38));
      yearLabel.setHorizontalAlignment(JLabel.CENTER);
      yearLabel.setPreferredSize(new Dimension(70, 20));
      
      JLabel timeLabel = new JLabel(time.substring(0, 2) + ":" + time.substring(2));
      timeLabel.setForeground(Color.white);
      timeLabel.setFont(new Font("���", Font.BOLD, 27));
      //timeLabel.setPreferredSize(new Dimension(55, 35));
      timeLabel.setHorizontalAlignment(JLabel.CENTER);
      timeLabel.setPreferredSize(new Dimension(70, 38));
      datePanel.setPreferredSize(new Dimension(100, 80));
      datePanel.setBackground(new Color(0, 54, 78));
      
      //////////////////////////////////////////////////
      JPanel tempPanel = new JPanel() {};
      tempPanel.setBorder(null);
      tempPanel.setPreferredSize(new Dimension(100, 45));
      tempPanel.setBackground(new Color(0, 54, 78));
            
      JPanel temp = new JPanel();
      temp.setBackground(new Color(0, 54, 78));
      
      if (Double.parseDouble(data[0]) < 17) {
          tempImgIcon = new ImageIcon("image/cold.png");
      } else if (Double.parseDouble(data[0]) >= 17) {
          tempImgIcon = new ImageIcon("image/hot.png");
      }
      
      Image tempimg = tempImgIcon.getImage();
      Image tempimg2 = tempimg.getScaledInstance(40, 78, Image.SCALE_SMOOTH);
      ImageIcon tempIcon = new ImageIcon(tempimg2);
      JLabel tempImageLabel = new JLabel(tempIcon);
      temp.setBorder(null);
      //temp.setBorder(new TitledBorder(new LineBorder(Color.yellow,2)));
      temp.setPreferredSize(new Dimension(40, 93));
      
      //////////////// �̹��� //////////////////
      //temp.setBackground(Color.white);
      JLabel tempTextLabel = new JLabel(data[0] + "��");
      tempTextLabel.setForeground(Color.white);
      tempTextLabel.setFont(new Font("���", Font.BOLD, 25));
      tempTextLabel.setPreferredSize(new Dimension(80, 35));
      tempTextLabel.setHorizontalAlignment(JLabel.CENTER);
      
      JPanel skyPanel = new JPanel();
      skyPanel.setBorder(null);
      skyPanel.setPreferredSize(new Dimension(70, 100));
      skyPanel.setBackground(new Color(0, 54, 78));
      
      String SKYstr = SKYcode.get(data[1]);
      
      if (SKYstr.contentEquals("����")) {
    	  skyImgIcon = new ImageIcon("image/����.png");
      } else if (SKYstr.contentEquals("��������")) {
    	  skyImgIcon = new ImageIcon("image/��������.png");
      } else if (SKYstr.contentEquals("��������")) {
    	  skyImgIcon = new ImageIcon("image/��������.png");
      } else if (SKYstr.contentEquals("�帲")) {
    	  skyImgIcon = new ImageIcon("image/�帲.png");
      }
      
      Image skyimg = skyImgIcon.getImage();
      Image skyimg2 = skyimg.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
      ImageIcon skyIcon = new ImageIcon(skyimg2);
      JLabel skyImageLabel = new JLabel(skyIcon);
      //temp.setBorder(new TitledBorder(new LineBorder(Color.yellow,2)));
      //sky.setPreferredSize(new Dimension(40, 93));
      
      //////////////////////// �̹��� //////////////////////////
      JLabel skyTextLabel = new JLabel(SKYcode.get(data[1]));
      skyTextLabel.setForeground(Color.white);
      skyTextLabel.setFont(new Font("���", Font.BOLD, 13));
      skyTextLabel.setPreferredSize(new Dimension(55, 30));
      skyTextLabel.setHorizontalAlignment(JLabel.CENTER);
      
      JPanel waterPanel = new JPanel();
      waterPanel.setBorder(null);
      waterPanel.setPreferredSize(new Dimension(70, 100));
      waterPanel.setBackground(new Color(0, 54, 78));
      
      String PTYstr = PTYcode.get(data[1]);
      
      if (PTYstr.contentEquals("������ ����")) {
    	  waterImgIcon = new ImageIcon("image/����������.png");
      } else if (PTYstr.contentEquals("��")) {
    	  waterImgIcon = new ImageIcon("image/��.png");
      } else if (PTYstr.contentEquals("��/��")) {
    	  waterImgIcon = new ImageIcon("image/��.png");
      } else if (PTYstr.contentEquals("�ҳ���")) {
    	  waterImgIcon = new ImageIcon("image/�ҳ���.png");
      } else if (PTYstr.contentEquals("�����")) {
    	  waterImgIcon = new ImageIcon("image/�����.png");
      } else if (PTYstr.contentEquals("�����/������")) {
    	  waterImgIcon = new ImageIcon("image/����ﴫ����.png");
      } else if (PTYstr.contentEquals("������")) {
    	  waterImgIcon = new ImageIcon("image/������.png");
      }
      
      Image waterimg = waterImgIcon.getImage();
      Image waterimg2 = waterimg.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
      ImageIcon waterIcon = new ImageIcon(waterimg2);
      JLabel waterImageLabel = new JLabel(waterIcon);

      JLabel waterTextLabel = new JLabel(" " + PTYcode.get(data[1]));
      waterTextLabel.setForeground(Color.white);
      waterTextLabel.setFont(new Font("���", Font.BOLD, 13));
      waterTextLabel.setPreferredSize(new Dimension(55, 30));
      waterTextLabel.setHorizontalAlignment(JLabel.CENTER);

      datePanel.add(yearLabel);
      datePanel.add(timeLabel);
      temp.add(tempImageLabel);
      tempPanel.add(tempTextLabel);
      skyPanel.add(skyImageLabel);
      skyPanel.add(skyTextLabel);
      waterPanel.add(waterImageLabel);
      waterPanel.add(waterTextLabel);
      
      publicData.add(datePanel);
      publicData.add(temp);
      publicData.add(tempPanel);
      publicData.add(skyPanel);
      publicData.add(waterPanel);
      

      myInfo.add(myInfo2);
      myInfo.add(blank);
      myInfo.add(myName);
      myInfo.add(message);
      
      friend.add(friend2);
      friend.add(jScollPane, "Left"); //JScrooPane�� ���� JList�� ��Ÿ���� ���� ��ġ�Ѵ�.

      panel.add(top);
      panel.add(myInfo);
      panel.add(friend);
      panel.add(publicData);
      frame.setBackground(new Color(0, 176, 80));
      frame.getContentPane().add(searching);
      frame.getContentPane().add(chatting);
      frame.getContentPane().add(setting);
      frame.getContentPane().add(panel);
      
      frame.setVisible(true);
      frame.setSize(450, 600);
      frame.setLocationRelativeTo(null);
      frame.setResizable(false);
      frame.addWindowListener(new WindowListener() {
          public void windowOpened(WindowEvent e) {}
          public void windowIconified(WindowEvent e) {}
          public void windowDeiconified(WindowEvent e) {}
          public void windowDeactivated(WindowEvent e) {}
          public void windowClosing(WindowEvent e) {
        	  System.out.println(e);
          	String[] buttons = {"YES", "NO"};
    		int result = JOptionPane.showOptionDialog(null, "�����Ͻðڽ��ϱ�?", "����", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, "�ι�°��");
    		if (result == 0) {
    			System.exit(0);
    		} else if (result == 1) {
    		      frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    		}
          }
          public void windowClosed(WindowEvent e) {
        	  System.out.println("A");
          }
          public void windowActivated(WindowEvent e) {}
      });
      
      //��� ������ ������ socketǮ����
      Client.freeSocket();
   }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}