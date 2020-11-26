package client;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import client.Client;

public class MainScreen extends JFrame{
	JTable jTable;
	DefaultTableModel model;
	int flag = 0;
	private JMenuItem menuItemAdd;
	int searchingFlag = 0;

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
      
	  JLabel myName = new JLabel("   " + binfo[0] + "(" + binfo[1] + ")");
      myName.setFont(new Font("�����ٸ���", Font.PLAIN, 13));
	  JLabel message = new JLabel(binfo[2]);
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
      ImageIcon onlineIcon = new ImageIcon(onlineImg);
      
      ImageIcon offlineImgIcon = new ImageIcon("image\\offline.png");
      Image offline = offlineImgIcon.getImage();
      Image offlineImg = offline.getScaledInstance(10, 10, Image.SCALE_SMOOTH);
      ImageIcon offlineIcon = new ImageIcon(offlineImg);

      String statusStr = ""; // online���� offline���� ���ϴ� �κ�
      Icon status = new ImageIcon();
      
      if (statusStr == "online") {
         status = new ImageIcon("image\\online.png");
      } else if (statusStr == "offline") {
         status = new ImageIcon("image\\offline.png");
      }
      
      
      
      //ģ����� �ҷ�����!=========================================
      String[][] friendlist = Client.friendList();
		// String[][name, nickname, last_connection, ���]


      String columnNames[] = { "�г���(�̸�)", "���ٸ޽���", "status" };
      Object rowData[][] = { }; // ģ����� ���� �� �ڸ�!

      model = new DefaultTableModel(rowData, columnNames) {
         public boolean isCellEditable(int i, int c) {
            return false;
         }
         
         public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
         }
      };

      if(friendlist != null) {
    	  int idx = 0;
    	  int ck = Integer.parseInt(friendlist[0][0]);

          for(String[] fl : friendlist) {
        	  if(idx == ck) break;
        	  if(idx == 0) {idx++; continue;}
    		  idx++;
    		  
        	  String line = fl[0] + "(" + fl[1] + ")";
        	  
        	  if(fl[2].compareTo("0") == 0) {
            	  Object inData[] = {line, fl[3], onlineIcon};
        		  model.addRow(inData);	
        	  }
        	  else {
            	  Object inData[] = {line, fl[3], offlineIcon};

        		  model.addRow(inData);
        	  }
        	  
          } 
      }
      //���߿� ģ����� ������ model�� �ǵ帮�� ���� �ൿ���� ����...
      
      
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
      
      
      String[] buttons = {"����", "����"};
      
//      while(true) {
//    	  
//    	  //ģ����û�� �ִٴ� ��ȣ
//    	  if(Client.fslInfoPlusREQTOCLIENT == true) {
//    		  String[][] FirendRequset = Client.getFriendPlusInfo();
//    		  
//    		  /*
//    	      int result = JOptionPane.showOptionDialog(null, "�Է��ϰ���� �޽���", "�˸�â �̸�", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, �迭�ִºκ�!!!!!!, "�ι�°��");
//    	      if (result == 0) {
//    	         //ù��°��ư ������ ��
//    	      } else if (result == 1) {
//    	         //�ι�°��ư ������ ��
//    	      }
//    		  */
//    		  
//    	  }
//    	  
//      }
   }

}