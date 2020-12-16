package client;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.table.*;

@SuppressWarnings("serial")
public class InviteFriend extends JFrame implements MouseListener {
	JTable jTable;
	DefaultTableModel model;
	HashSet<Integer> selectnum = new HashSet<Integer>();
    JLabel flist = new JLabel("친구를 선택하세요");
    JPanel listPanel = new JPanel();

	
//getSelectedRows ()
	public void mouseClicked(MouseEvent me) {
		int row = jTable.getSelectedRow();
		//jTable.
		if(selectnum.contains(row)) {//선택해제
			selectnum.remove(row);
		}
		else { //선택
			selectnum.add(row);
		}
		
		if(selectnum.isEmpty()) {
			flist.setText("친구를 선택하세요");	
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
		makeroom.setText("방만들기");
		makeroom.setFont(new Font("나눔바른펜", Font.BOLD, 10));
		flist.setFont(new Font("나눔바른펜", Font.PLAIN, 18));
		flist.setForeground(Color.white);

		//멀티 룸 만들기
		makeroom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!selectnum.isEmpty()) {
					String roomname = JOptionPane.showInputDialog(null, "room name", "방 이름을 설정하세요", JOptionPane.OK_CANCEL_OPTION);
					//방 이름 신청 받음
					int preck = JOptionPane.showConfirmDialog(null, "중간에 초대받은 사람도 처음부터 메세지를 보게 할까요?", "Set type", JOptionPane.YES_NO_OPTION);
					String showpre = null;

					if(preck == 0) showpre = "1";
					else showpre = "0";
					
					//ID를 굴비엮기
					String IDs = "";
					int first = 0;
					for(int i : selectnum) {
						if (first++ == 0) IDs = (String) model.getValueAt(i, 0);
						else IDs = IDs + "^" + model.getValueAt(i, 0);
					}
					
				   //멀티 채팅을 신청한다 (사람 리스트랑 방 이름을 보냄)
				   Client.makeMultiRoom(roomname, showpre, IDs);
				   frame.dispose();
				}
			}
		});

		JPanel title = new JPanel();
		title.setPreferredSize(new Dimension(250, 30));
		title.setBackground(new Color(74, 210, 149));
	    JLabel friend2 = new JLabel("친구초대");
	    friend2.setFont(new Font("나눔바른펜", Font.PLAIN, 15));
	    friend.setPreferredSize(new Dimension(430, 380));
	    
	    jTable = new JTable(model);
	    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 단일선택
	    jTable.addMouseListener(this);
	    jTable.getColumn("닉네임(이름)").setPreferredWidth(100);
	    jTable.getColumn("status").setPreferredWidth(50);
	    JScrollPane jScollPane = new JScrollPane(jTable);
	    jScollPane.setPreferredSize(new Dimension(180, 227));
	    
	    jTable.getColumn("한줄메시지").setWidth(0);
	    jTable.getColumn("한줄메시지").setMinWidth(0);
	    jTable.getColumn("한줄메시지").setMaxWidth(0);
	    
	    jTable.getTableHeader().setReorderingAllowed(false);
	    jTable.getTableHeader().setResizingAllowed(false);
	   
	    jTable.setRowSelectionAllowed(true);
	    jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    
	    DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	    dtcr.setHorizontalAlignment(SwingConstants.CENTER); 
	    TableColumnModel tcm = jTable.getColumnModel() ;
	    tcm.getColumn(0).setCellRenderer(dtcr);
	    tcm.getColumn(1).setCellRenderer(dtcr);
	    
	    jTable.setShowGrid(false);
	    jTable.setRowHeight(30);
	    

	    listPanel.setPreferredSize(new Dimension(230, 20));
	    listPanel.add(flist);
	    
	    title.add(friend2);
	    jScollPane.setPreferredSize(new Dimension(230, 230));
	    JPanel table = new JPanel();
	    table.setPreferredSize(new Dimension(250, 280));
	    table.setBackground(new Color(0, 54, 78));
	    table.add(jScollPane, "Left");
	    table.add(flist);
	    frame.add(title, BorderLayout.NORTH);
	    frame.add(table);
	    frame.add(makeroom, BorderLayout.SOUTH);
	    
	    frame.setTitle("Invite Friend");
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