package client;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class ChattingOnlinePeople extends JFrame {
	JTable jTable;
	DefaultTableModel model;
	
	public ChattingOnlinePeople(String[] ulist) {
		JFrame frame = new JFrame();
		JPanel friend = new JPanel();
		
	    JLabel friend2 = new JLabel("�����ڸ��");
	    friend2.setFont(new Font("�����ٸ���", Font.PLAIN, 15));
	    //friend2.setPreferredSize(new Dimension(430, 13));
	    friend.setPreferredSize(new Dimension(430, 380));

	    String columnNames[] = { "�г���(�̸�)" };
	    Object rowData[][] = { };    
	    model = new DefaultTableModel(rowData, columnNames) {
	       public boolean isCellEditable(int i, int c) {
	          return false;
	       }
	    };
	
	    for(String ul : ulist) {
			Object inData[] = {ul};
			model.addRow(inData);
	    }
	    
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
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
	}
}
