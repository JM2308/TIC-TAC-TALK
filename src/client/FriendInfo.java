package client;
import java.awt.*;
import javax.swing.*;


/**
 * �Ϸ�
 * */


public class FriendInfo extends JFrame{

	//type : 0 - NF, 1 - F
	public FriendInfo(String ID, int type) {
        super.setLayout(new GridLayout(8, 1));
		//JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JLabel name = new JLabel("�г���(�̸�)");
		name.setFont(new Font("�������(����)", Font.BOLD, 30));
		JLabel message = new JLabel("���ٸ޽���");
		message.setFont(new Font("�������(����)", Font.BOLD, 15));
		JLabel email = new JLabel("�̸���");
		email.setFont(new Font("�������(����)", Font.BOLD, 15));
		

		JLabel phone = new JLabel("��ȭ��ȣ");
		phone.setFont(new Font("�������(����)", Font.BOLD, 15));
		JLabel birth = new JLabel("����");
		birth.setFont(new Font("�������(����)", Font.BOLD, 15));
		JLabel github = new JLabel("����");
		github.setFont(new Font("�������(����)", Font.BOLD, 15));
		
		
		JPanel namePanel = new JPanel();
		namePanel.add(name);
		JPanel messagePanel = new JPanel();
		messagePanel.add(message);
		JPanel emailPanel = new JPanel();
		emailPanel.add(email);
		
		JPanel phonePanel = new JPanel();
		phonePanel.add(phone);
		JPanel birthPanel = new JPanel();
		birthPanel.add(birth);
		JPanel githubPanel = new JPanel();
		githubPanel.add(github);
		
		add(namePanel);
		add(messagePanel);
		add(emailPanel);
		
		//���� �޾ƿ���
		String info[] = Client.getFriendInfo(ID);
		// [NICKNAME NAME STATE_MESSAGE EMAIL PHONE BIRTH GITHUB]
		
		for(String q : info) {
			System.out.println(q);
		}
		
		
		String line1 = info[0] + "(" + info[1] + ")";
		name.setText(line1);
		message.setText(info[2]);
		email.setText(info[3]);
		phone.setText(info[4]);
		birth.setText(info[5]);
		github.setText(info[6]);

		
		//ģ����� ���� �� �� ������
		if(type == 1) {
			add(phonePanel);
			add(birthPanel);
			add(githubPanel);
		}

		setVisible(true);
		setSize(500, 400);
		setLocationRelativeTo(null);
		setResizable(false);
	}
}
