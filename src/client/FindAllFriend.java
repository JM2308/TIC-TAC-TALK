package client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


//�����غ��� �긦 ���� �ؾ��ϴ°� �ƴұ�????


public class FindAllFriend extends JFrame {
	String searchFriend = "";
	
	public FindAllFriend() {
		super.setLayout(new GridLayout(6, 1));
		//JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JPanel blank = new JPanel();
		JLabel name = new JLabel("�� ģ�� ã��");
		JPanel namePanel = new JPanel();
		name.setFont(new Font("�������(����)", Font.BOLD, 15));
		JTextField find = new JTextField(15);
		JPanel findPanel = new JPanel();
		JButton search = new JButton("SEARCH");
		JPanel btnPanel = new JPanel();
		search.setPreferredSize(new Dimension(90, 20));
		
		search.addActionListener(new ActionListener() {
	         @Override
		     public void actionPerformed(ActionEvent e) {
	        	 searchFriend = find.getText(); // ã�� ģ���� �̸��� string���� ����
	        	 MyFriendList list = new MyFriendList();
	        	 
	        	 
	        	 
	        	 
	         }
	    });
		
		namePanel.add(name);
		findPanel.add(find);
		btnPanel.add(search);
		
		add(blank);
		add(namePanel);
		add(findPanel);
		add(btnPanel);
		
		setVisible(true);
		setSize(300, 200);
		setLocationRelativeTo(null);
		setResizable(false);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FindAllFriend frame = new FindAllFriend();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
