package client;


import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.*;

@SuppressWarnings("serial")
public class MyFriendList extends JFrame implements MouseListener {
	JFrame frame = new JFrame();
	JPanel friend = new JPanel();
	JTable jTable;
	DefaultTableModel model;
	HashMap<String, String> idmatch = new HashMap<String, String>();
	HashMap<String, String> statematch = new HashMap<String, String>();

	
	@SuppressWarnings("unused")
	public void mouseClicked(MouseEvent me) {
		int row = jTable.getSelectedRow();
		Object line = model.getValueAt(row, 0);
		String FID = idmatch.get(line.toString());
		String state = statematch.get(line.toString());

		String[] buttons = {"1:1채팅", "정보보기"};
		int result = JOptionPane.showOptionDialog(null, "옵션을 선택하세요", "옵션", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, "두번째값");
		if (result == 0) {
	
			// 일대일채팅 스타또! - 채팅중이면 이미 채팅중입니다
			if(Client.ckINPCHAT(FID) || !state.equals("0")) {
				JOptionPane.showMessageDialog(null, "이미 채팅중이거나 상대가 오프라인입니다.");
			}
			else {
				frame.dispose();
				ChattingOne chatting = new ChattingOne(FID); //채팅방 키고
				Client.addPCHAT(FID, chatting); //채팅중인 상대에 상대방을 더해주고
		        Client.ckANSWER(FID);
			}
		} else if (result == 1) {
			FriendInfo info = new FriendInfo(FID.toString(), 1);
			//정보확인할땐 굳이 창 안닫아도됨
		}
	}
	
	
	public MyFriendList(String keyword) {
		JPanel friend = new JPanel();
		friend.setPreferredSize(new Dimension());
	    JLabel friend2 = new JLabel("친구리스트");
	    friend2.setFont(new Font("나눔바른펜", Font.PLAIN, 15));
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
	    
	    //친구 검색한 목록 불러오기!!!
	    String[][] friendlist = Client.FriendSearchList(keyword);
	    //친구검색한 결과리스트 (ID, name, nickname, last_connection)
	    
	    String columnNames[] = { "닉네임(이름)", "status" };
	    Object rowData[][] =  { };// 친구목록 들어가야 될 자리!
	      
	    model = new DefaultTableModel(rowData, columnNames) {
			public boolean isCellEditable(int i, int c) {
				return false;
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
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
	    		  idmatch.put(line, fl[0]); //line - 아이디 저장
	    		  statematch.put(line, fl[3]);
	    		  
	        	  if(fl[3].compareTo("0") == 0) {
	        		  //ID는 숨겨서 저장
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
	    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 단일선택
	    jTable.setBackground(Color.white);
	    jTable.setFillsViewportHeight(true);
	    jTable.addMouseListener(this);
	    jTable.setGridColor(new Color(0,128,0));
	    jTable.getColumn("닉네임(이름)").setPreferredWidth(100);
	    jTable.getColumn("status").setPreferredWidth(50);
	    JScrollPane jScollPane = new JScrollPane(jTable);
	    jScollPane.setPreferredSize(new Dimension(180, 210));
	    jTable.getTableHeader().setReorderingAllowed(false);
	    jTable.getTableHeader().setResizingAllowed(false);
	    
	    DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	    dtcr.setHorizontalAlignment(SwingConstants.CENTER); 
	    TableColumnModel tcm = jTable.getColumnModel() ;
	    for(int i = 0; i < tcm.getColumnCount() - 1; i++){
	    	tcm.getColumn(i).setCellRenderer(dtcr);
	    }
	      
	    jTable.setRowHeight(30);
	    jTable.setShowGrid(false);
		jTable.setShowVerticalLines(false);
	    
		JPanel panel = new JPanel();
	    friend.add(panel);
	    friend.setBackground(new Color(0, 54, 78));
	    
	    panel.add(friend2);
	    panel.setBackground(new Color(74, 210, 149));
	    panel.setPreferredSize(new Dimension(200, 35));
	    friend2.setFont(new Font("나눔바른펜", Font.PLAIN, 15));
	    friend.add(jScollPane, "Left"); //JScrooPane에 담은 JList를 나타내기 위해 배치한다.
	    
	    frame.add(friend);
	    
	    frame.setTitle("List");
	    frame.setVisible(true);
        frame.setSize(200, 300);
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