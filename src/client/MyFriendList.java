package client;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.table.*;

public class MyFriendList extends JFrame implements MouseListener {
	JTable jTable;
	DefaultTableModel model;
	HashMap<String, String> idmatch = new HashMap<String, String>();

	
	public void mouseClicked(MouseEvent me) {
		int row = jTable.getSelectedRow();
		Object line = model.getValueAt(row, 0);
		String FID = idmatch.get(line.toString());

		String[] buttons = {"1:1ä��", "��������"};
		int result = JOptionPane.showOptionDialog(null, "�ɼ��� �����ϼ���", "�ɼ�", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, "�ι�°��");
		if (result == 0) {
			// �ϴ���ä�� ��Ÿ��! - ä�����̸� �̹� ä�����Դϴ�
			dispose();
			ChattingOne chatting = new ChattingOne();
			
			
		} else if (result == 1) {
			System.out.println(FID);
			FriendInfo info = new FriendInfo(FID.toString(), 1);
			//����Ȯ���Ҷ� ���� â �ȴݾƵ���
		}
	}
	
	
	public MyFriendList(String keyword) {
		JFrame frame = new JFrame();
		JPanel friend = new JPanel();
		
	    JLabel friend2 = new JLabel("ģ������Ʈ");
	    friend2.setFont(new Font("�����ٸ���", Font.PLAIN, 15));
	    //friend2.setPreferredSize(new Dimension(430, 13));
	    friend.setPreferredSize(new Dimension(430, 380));
	    
	    ImageIcon onlineImgIcon = new ImageIcon("image/online.png");
	    Image online = onlineImgIcon.getImage();
	    Image onlineImg = online.getScaledInstance(10, 10, Image.SCALE_SMOOTH);
	    ImageIcon onlineIcon = new ImageIcon(onlineImg);
	    
	    ImageIcon offlineImgIcon = new ImageIcon("image/offline.png");
	    Image offline = offlineImgIcon.getImage();
	    Image offlineImg = offline.getScaledInstance(10, 10, Image.SCALE_SMOOTH);
	    ImageIcon offlineIcon = new ImageIcon(offlineImg);
	
	    String statusStr = ""; // online���� offline���� ���ϴ� �κ�
	    Icon status = new ImageIcon();
	    
	    if (statusStr == "online") {
	       status = new ImageIcon("image/online.png");
	    } else if (statusStr == "offline") {
	       status = new ImageIcon("image/offline.png");
	    }
	    
	    
	    //ģ�� �˻��� ��� �ҷ�����!!!
	    String[][] friendlist = Client.FriendSearchList(keyword);
	    //ģ���˻��� �������Ʈ (ID, name, nickname, last_connection)
	    
	    String columnNames[] = { "�г���(�̸�)", "status" };
	    Object rowData[][] = // ģ����� ���� �� �ڸ�!
	       { };
	    
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
	    	  int ck = Integer.parseInt(friendlist[0][0]) + 1;
	    	  System.out.println("ck => " + ck);

	          for(String[] fl : friendlist) {
	        	  if(idx == ck) break;
	        	  if(idx == 0) {idx++; continue;}
	    		  idx++;
	    		  
	        	  String line = fl[2] + "(" + fl[1] + ")";
	        	  
	    		  idmatch.put(line, fl[0]); //line - ���̵� ����
	    		  
	        	  if(fl[3].compareTo("0") == 0) {
	        		  //ID�� ���ܼ� ����
	            	  Object inData[] = {line, onlineIcon};
	        		  model.addRow(inData);	
	        	  }
	        	  else {
	            	  Object inData[] = {line, offlineIcon};
	        		  model.addRow(inData);
	        	  }
	          } 
	    }
	     
	    
	    jTable = new JTable(model);
	    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// ���ϼ���
	    jTable.addMouseListener(this);
	    jTable.getColumn("�г���(�̸�)").setPreferredWidth(100);
	    jTable.getColumn("status").setPreferredWidth(50);
	    JScrollPane jScollPane = new JScrollPane(jTable);
	    jScollPane.setPreferredSize(new Dimension(180, 227));
	    jTable.getTableHeader().setReorderingAllowed(false);
	    jTable.getTableHeader().setResizingAllowed(false);
	    
	    jTable.setShowGrid(false);
	    jTable.setRowHeight(30);
	    
	    friend.add(friend2);
	    friend.add(jScollPane, "Left"); //JScrooPane�� ���� JList�� ��Ÿ���� ���� ��ġ�Ѵ�.
	    frame.add(friend);
	    
	    frame.setVisible(true);
        frame.setSize(200, 300);
        frame.setLocationRelativeTo(null);
		

        //frame.setResizable(false);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}


}
