package client;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import client.Client;

public class MainScreen extends JFrame {
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
	
	
	public MainScreen() {
      JFrame frame = new JFrame();
      JPanel panel = new JPanel();
      
      // ��ư �ִ� �κ�
      JPanel top = new JPanel();
      top.setPreferredSize(new Dimension(430, 40));
      top.setBorder(new TitledBorder(new LineBorder(Color.black,2)));
      // ��ư�� �̹��� �ְ� ��ư ũ�⿡ �°� �̹��� �ֱ�
      ImageIcon icon = new ImageIcon("image\\searching.png");
      Image searchImage = icon.getImage();
      Image searchChangingImg = searchImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
      ImageIcon searchChangeIcon = new ImageIcon(searchChangingImg);
      ImageIcon icon2 = new ImageIcon("image\\chatting.png");
      Image chatImage = icon2.getImage();
      Image chatChangingImage = chatImage.getScaledInstance(26, 26, Image.SCALE_SMOOTH);
      ImageIcon chatChangingIcon = new ImageIcon(chatChangingImage);
      ImageIcon icon3 = new ImageIcon("image\\setting.png");
      Image settingImage = icon3.getImage();
      Image settingChangeImg = settingImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
      ImageIcon settingChangeIcon = new ImageIcon(settingChangeImg);

      JButton searching = new JButton();
      searching.setPreferredSize(new Dimension(30, 30));
      searching.setBounds(10, 10, 30, 30);
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
      chatting.setBounds(350, 10, 30, 30);
      chatting.setIcon(chatChangingIcon);
      top.add(chatting);
      
      
      //��Ƽ�� �ʴ� ��ư �׼�
      chatting.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            InviteFriend invite = new InviteFriend();
         }
      });
      
      
      
      JButton setting = new JButton();
      setting.setPreferredSize(new Dimension(30, 30));
      setting.setBounds(390, 10, 30, 30);
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
      myInfo.setBorder(new TitledBorder(new LineBorder(Color.red,2)));
      
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
      friend.setPreferredSize(new Dimension(430, 380));
      friend.setBorder(new TitledBorder(new LineBorder(Color.green,2)));
      
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
      jTable.getColumn("�г���(�̸�)").setPreferredWidth(100);
      jTable.getColumn("���ٸ޽���").setPreferredWidth(250);
      jTable.getColumn("status").setPreferredWidth(50);
      JScrollPane jScollPane = new JScrollPane(jTable);
      jScollPane.setPreferredSize(new Dimension(425, 350));
      jTable.getTableHeader().setReorderingAllowed(false);
      jTable.getTableHeader().setResizingAllowed(false);
   
      
      
      jTable.setShowGrid(false);
      jTable.setRowHeight(30);
      
      // ���������� �κ�
      JPanel publicData = new JPanel();
      publicData.setPreferredSize(new Dimension(430, 60));
      publicData.setBorder(new TitledBorder(new LineBorder(Color.yellow,2)));
      
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
      frame.getContentPane().add(searching);
      frame.getContentPane().add(chatting);
      frame.getContentPane().add(setting);
      frame.getContentPane().add(panel);
      
      frame.setVisible(true);
      frame.setSize(450, 600);
      frame.setLocationRelativeTo(null);
      frame.setResizable(false);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      //��� ������ ������ socketǮ����
      Client.freeSocket();
   }
}