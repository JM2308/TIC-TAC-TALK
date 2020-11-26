package client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Setting extends JFrame {
	public Setting() {
		super.setLayout(new GridLayout(16, 2));
		JPanel panel = new JPanel();
		JPanel panel1 = new JPanel();
		JPanel IDPanel = new JPanel();
		JPanel NickNamePanel = new JPanel();
		JPanel PWPanel = new JPanel();
		JPanel NamePanel = new JPanel();
		JPanel PNPanel = new JPanel();
		JPanel EmailPanel = new JPanel();
		JPanel BirthPanel = new JPanel();
		JPanel GithubPanel = new JPanel();
		JPanel messagePanel = new JPanel();
		JLabel label = new JLabel("Please modify your information!");
		JLabel label1 = new JLabel("��й�ȣ�� �������� �ʴ´ٸ� ��й�ȣ ĭ�� ����μ���");

		JLabel labelID = new JLabel("ID                           : ");
		JLabel labelNickName = new JLabel("NickName           : ");
		JLabel labelPW = new JLabel("Password           : ");
		JLabel labelName = new JLabel("Name                   : ");
		JLabel labelPN = new JLabel("Phone Number  : ");
		JLabel labelEmail = new JLabel("Email                    : ");
		JLabel labelBirth = new JLabel("Birth                     : ");
		JLabel labelGit = new JLabel("GitHub                  : ");
		JLabel labelMessage = new JLabel("Message             : ");
		
		JTextField ID = new JTextField(15);
		ID.setEditable(false);
		JTextField NickName = new JTextField(15);
		JPasswordField txtPass = new JPasswordField(15);
		JTextField name = new JTextField(15);
		JTextField phoneNumber = new JTextField(15);
		JTextField email = new JTextField(15);
		JTextField github = new JTextField(15);
		JTextField smessage = new JTextField(15);
		
		
		String[] year = new String[71];
		String yearSelect = "";
		int yearStart = 1950;
		for (int i = 0; i <= 70; i++) {
			year[i] = "  " + Integer.toString(yearStart);
			yearStart++;
		}
		
		JComboBox yearCombo = new JComboBox(year); 
		add(yearCombo);

		String[] month = { "  1", "  2", "  3", "  4", "  5", "  6", "  7", "  8", "  9", "  10", "  11", "  12" };
		JComboBox monthCombo = new JComboBox(month); 
		add(monthCombo);
		
		String[] day = new String[31];
		int dayStart = 1;
		for (int i = 0; i < 31; i++) {
			day[i] = "  " + Integer.toString(dayStart);
			dayStart++;
		}
		
		JComboBox dayCombo = new JComboBox(day); 
		add(dayCombo);	
		
		JPanel panelBtn = new JPanel();
		JButton SettingBtn = new JButton("SAVE");
		
		panel.add(label);
		panel1.add(label1);
		IDPanel.add(labelID);
		IDPanel.add(ID);
		NickNamePanel.add(labelNickName);
		NickNamePanel.add(NickName);
		PWPanel.add(labelPW);
		PWPanel.add(txtPass);
		NamePanel.add(labelName);
		NamePanel.add(name);
		PNPanel.add(labelPN);
		PNPanel.add(phoneNumber);
		EmailPanel.add(labelEmail);
		EmailPanel.add(email);
		BirthPanel.add(labelBirth);
		BirthPanel.add(yearCombo);
		BirthPanel.add(monthCombo);
		BirthPanel.add(dayCombo);
		GithubPanel.add(labelGit);
		GithubPanel.add(github);
		messagePanel.add(labelMessage);
		messagePanel.add(smessage);
		panelBtn.add(SettingBtn, BorderLayout.SOUTH);
		
		
		//���� �ҷ�����!![ID NICKNAME NAME PHONE EMAIL BIRTH GITHUB STATE_MESSAGE]
		String[] infoo = Client.settinginfo();
		for(String k : infoo) {
			System.out.println(k);
		}
		
		
		ID.setText(infoo[0]);
		NickName.setText(infoo[1]);
		name.setText(infoo[2]);
		phoneNumber.setText(infoo[3]);
		email.setText(infoo[4]);
		
		String Y = infoo[5].substring(0, 2);
		int M = Integer.parseInt(infoo[5].substring(2, 4));
		int D = Integer.parseInt(infoo[5].substring(4, 6));

		
		if(Integer.parseInt(Y) >= 50) Y = "19" + Y;
		else Y = "20" + Y;
		
		int Yy = Integer.parseInt(Y) - 1950;

		yearCombo.setSelectedIndex(Yy);
		monthCombo.setSelectedIndex(M - 1);;
		dayCombo.setSelectedIndex(D - 1);
		
		
		if(infoo[6].compareTo("null") != 0) 
			github.setText(infoo[6]);
		if(infoo[7].compareTo("null") != 0) 
			smessage.setText(infoo[7]);

		
		
		
		//setting save ��ư �׼�!
		SettingBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String git = "0";
				
				if(NickName.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "NickName�� �Է��ϼ���");
					return;
				}

				if(name.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "�̸��� �Է��ϼ���");
					return;
				}
				
				if(phoneNumber.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "��ȭ��ȣ�� �Է��ϼ���");
					return;
				}
				
				if(email.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "email�� �Է��ϼ���");
					return;
				}

			
				//��� ������ ����ϸ� ������ �ش�
				String message = NickName.getText() + "`|" + name.getText() 
				                 + "`|" + phoneNumber.getText() + "`|"
								+ email.getText() + "`|";
				
				String year = yearCombo.getSelectedItem().toString().substring(4);
				int month = Integer.parseInt(monthCombo.getSelectedItem().toString().trim());
				int day = Integer.parseInt(dayCombo.getSelectedItem().toString().trim());
				String m = null;
				String d = null;
				
				if(month < 10) m = "0" + month;	
				else m = Integer.toString(month);
				if(day < 10) d = "0" + day;
				else d = Integer.toString(day);

				String Git = null;
				String state_m = null;
				//����, ����, ��� �����ֱ�
				if (github.getText().equals("")) {
					Git = "";
				}
				else Git = github.getText();
				
				if (smessage.getText().equals("")) {
					state_m = "";
				}
				else state_m = smessage.getText();
				
				message = message + year + m + d + "`|" + Git + "`|" + smessage.getText();


				//pw�Է���� Ȯ��
				//+ " " + String.valueOf(txtPass.getPassword())
				if(String.valueOf(txtPass.getPassword()).equals("")) {
					System.out.println("pw�� ���� null�Դϴ�.");
					message = "0`|" + message;
				}
				else {
					System.out.println("pw�� ���� ���� �ֽ��ϴ�.");
					message = "1`|" + String.valueOf(txtPass.getPassword()) + "`|" + message;
				}
				
				//���� client���� update!!
				int tf = Client.modifyInfo(message);
				

				if(tf == 0) {
					JOptionPane.showMessageDialog(null, "SUCCESS!!");
				}
				else if(tf == 1) {
					//�� ���̵� �ߺ��ȴ�, �׷��� �˷���� �ϳ�???
					JOptionPane.showMessageDialog(null, "���̵� �ߺ�!!");
				}			
			}
		});
		
		JButton back = new JButton("BACK");
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}			
		});
			
		
		add(panel);
		add(panel1);
		add(IDPanel);
		add(NickNamePanel);
		add(PWPanel);
		add(NamePanel);
		add(PNPanel);
		add(EmailPanel);
		add(BirthPanel);
		add(GithubPanel);
		add(messagePanel);
		add(panelBtn);
		
		setVisible(true);
		setSize(450, 600);
		setLocationRelativeTo(null);
		setResizable(false);
	}
	
}
