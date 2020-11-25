package client;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class ChattingOnlinePeople extends JFrame {
	JTable jTable;
	DefaultTableModel model;
	
	public ChattingOnlinePeople() {
		JFrame frame = new JFrame();
		JPanel friend = new JPanel();
		
	    JLabel friend2 = new JLabel("�����ڸ��");
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
	    
	    String columnNames[] = { "�г���(�̸�)" };
	    Object rowData[][] = // ģ����� ���� �� �ڸ�!
	       {
	       { "�г���1(�̸�)"},
	       { "�г���2(�̸�)"},
	       { "�г���3(�̸�)"},
	       };
	    
	    model = new DefaultTableModel(rowData, columnNames) {
	       public boolean isCellEditable(int i, int c) {
	          return false;
	       }
	    };
	
	    jTable = new JTable(model);
	    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// ���ϼ���
	    jTable.getColumn("�г���(�̸�)").setPreferredWidth(100);
	    JScrollPane jScollPane = new JScrollPane(jTable);
	    jScollPane.setPreferredSize(new Dimension(180, 227));
	    
	    jTable.setShowGrid(false);
	    jTable.setRowHeight(30);
	    
	    friend.add(friend2);
	    friend.add(jScollPane, "Left"); //JScrooPane�� ���� JList�� ��Ÿ���� ���� ��ġ�Ѵ�.
	    frame.add(friend);
	    
	    frame.setVisible(true);
        frame.setSize(200, 300);
        //frame.setResizable(false);
	}

	public static void main(String[] args) {
	      ChattingOnlinePeople main = new ChattingOnlinePeople();
	}
}


