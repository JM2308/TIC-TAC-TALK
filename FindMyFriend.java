package client;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FindMyFriend extends JFrame {
	String searchFriend = "";
	
	public FindMyFriend() {
		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout(6, 1));
		
		JPanel panel = new JPanel();
		JPanel blank = new JPanel();
		blank.setBackground(new Color(74, 210, 149));
		blank.setBorder(null);
		JPanel blank1 = new JPanel();
		blank1.setBackground(new Color(74, 210, 149));
		blank1.setBorder(null);
		JPanel blank2 = new JPanel();
		blank2.setBackground(new Color(74, 210, 149));
		blank2.setBorder(null);
		
		ImageIcon icon = new ImageIcon("image/findMyFriend.png");
	    Image titleImage = icon.getImage();
	    Image titleChangeImg = titleImage.getScaledInstance(350, 35, Image.SCALE_SMOOTH);
	    ImageIcon titleChangeIcon = new ImageIcon(titleChangeImg);
	    JButton name = new JButton();
	    name.setPreferredSize(new Dimension(100, 30));
	    name.setBounds(5, 5, 500, 15);
	    name.setIcon(titleChangeIcon);
	    //setting.setForeground(Color.red);
	    name.setBorder(null);
	    name.addActionListener(new ActionListener() {
	     @Override
	     	public void actionPerformed(ActionEvent e) {
	          // ���� ��й�ȣ�� ������ Setting setting = new Setting();
	          // Ʋ���� JOptionPane.showMessageDialog(null,  "Wrong!!");
	        }
	    });
		
		
		JTextField find = new JTextField(15);
		JPanel findPanel = new JPanel();
		findPanel.setBackground(new Color(74, 210, 149));
		
		JButton search = new JButton("SEARCH");
		search.setBackground(new Color(0, 54, 78));
		search.setForeground(Color.white);
		JPanel btnPanel = new JPanel();
		btnPanel.setBackground(new Color(74, 210, 149));
		search.setPreferredSize(new Dimension(90, 20));
		
		search.addActionListener(new ActionListener() {
	         @Override
		     public void actionPerformed(ActionEvent e) {
	        	 searchFriend = find.getText(); // ã�� ģ���� �̸��� string���� ����
	        	 MyFriendList list = new MyFriendList(searchFriend);
	        	 dispose();
		     }
	    });
		
		//namePanel.add(name);
		findPanel.add(find);
		btnPanel.add(search);
		
		frame.add(blank);
		frame.add(name);
		frame.add(findPanel);
		frame.add(btnPanel);
		frame.add(blank1);
		frame.add(blank2);
		
		frame.setVisible(true);
		frame.setSize(300, 200);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
	}
	
}
