package client;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class InviteFriend extends JFrame implements MouseListener {
	JTable jTable;
	DefaultTableModel model;
	HashSet<Integer> selectnum = new HashSet<Integer>();
    JLabel flist = new JLabel("ģ���� �����ϼ���");
    JPanel listPanel = new JPanel();

	
//getSelectedRows ()
	public void mouseClicked(MouseEvent me) {
		int row = jTable.getSelectedRow();
		//jTable.
		if(selectnum.contains(row)) {//��������
			selectnum.remove(row);
		}
		else { //����
			selectnum.add(row);
		}
		
		if(selectnum.isEmpty()) {
			flist.setText("ģ���� �����ϼ���");	
		}
		else {
			flist.setText(selectnum.toString());
		}
	}
	
	
	public InviteFriend(DefaultTableModel m) {
		model = m;
		JFrame frame = new JFrame();
		frame.setBackground(Color.yellow);
		JPanel friend = new JPanel();
		friend.setPreferredSize(new Dimension(250, 100));
	    JButton makeroom = new JButton();
	    makeroom.setBackground(new Color(74, 210, 149));
	    makeroom.setPreferredSize(new Dimension(180, 25));
		makeroom.setText("�游���");
		makeroom.setFont(new Font("�����ٸ���", Font.BOLD, 10));
		flist.setFont(new Font("�����ٸ���", Font.PLAIN, 18));
		flist.setForeground(Color.white);

		//��Ƽ �� �����
		makeroom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!selectnum.isEmpty()) {
					String roomname = JOptionPane.showInputDialog(null, "room name", "�� �̸��� �����ϼ���", JOptionPane.OK_CANCEL_OPTION);
					//�� �̸� ��û ����
					int preck = JOptionPane.showConfirmDialog(null, "�߰��� �ʴ���� ����� ó������ �޼����� ���� �ұ��?", "Set type", JOptionPane.YES_NO_OPTION);
					
					String showpre = null;

					if(preck == 0) showpre = "1";
					else showpre = "0";
					
					//ID�� ���񿫱�
					String IDs = "";
					int first = 0;
					for(int i : selectnum) {
						if (first++ == 0) IDs = (String) model.getValueAt(i, 0);
						else IDs = IDs + "^" + model.getValueAt(i, 0);
					}
					
				   //��Ƽ ä���� ��û�Ѵ� (��� ����Ʈ�� �� �̸��� ����)
				   Client.makeMultiRoom(roomname, showpre, IDs);
				   frame.dispose();
				}
			}
		});

		JPanel title = new JPanel();
		title.setPreferredSize(new Dimension(250, 30));
		title.setBackground(new Color(74, 210, 149));
	    JLabel friend2 = new JLabel("ģ���ʴ�");
	    friend2.setFont(new Font("�����ٸ���", Font.PLAIN, 15));
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
	    
	    
	    jTable = new JTable(model);
	    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// ���ϼ���
	    jTable.addMouseListener(this);
	    jTable.getColumn("�г���(�̸�)").setPreferredWidth(100);
	    jTable.getColumn("status").setPreferredWidth(50);
	    JScrollPane jScollPane = new JScrollPane(jTable);
	    jScollPane.setPreferredSize(new Dimension(180, 227));
	
	    
	    jTable.getColumn("���ٸ޽���").setWidth(0);
	    jTable.getColumn("���ٸ޽���").setMinWidth(0);
	    jTable.getColumn("���ٸ޽���").setMaxWidth(0);
	    
	    jTable.getTableHeader().setReorderingAllowed(false);
	    jTable.getTableHeader().setResizingAllowed(false);
	   
	    jTable.setRowSelectionAllowed(true);
	    jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    
	    jTable.setShowGrid(false);
	    jTable.setRowHeight(30);
	    

	    listPanel.setPreferredSize(new Dimension(230, 20));
	    listPanel.add(flist);
	    
	    title.add(friend2);
	    //friend.add(friend2);
	    //friend.add(jScollPane, "Left"); //JScrooPane�� ���� JList�� ��Ÿ���� ���� ��ġ�Ѵ�.
	    jScollPane.setPreferredSize(new Dimension(230, 230));
	    JPanel table = new JPanel();
	    table.setPreferredSize(new Dimension(250, 280));
	    table.setBackground(new Color(0, 54, 78));
	    
	    table.add(jScollPane, "Left");
	    table.add(flist);
	    //friend.add(flist);
	    //friend.add(makeroom);
	    
	    frame.add(title, BorderLayout.NORTH);
	    //frame.add(jScollPane);
	    frame.add(table);
	    //frame.add(listPanel);
	    //frame.add(friend);
	    //frame.add(flist);
	    frame.add(makeroom, BorderLayout.SOUTH);
	    //frame.add(friend, BorderLayout.SOUTH);

	    
	    frame.setVisible(true);
        frame.setSize(250, 350);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
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