package client;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class ChattingMulti {
	public int roomnumber;
	private String room_name;


    JFrame frame = new JFrame("Chatter");
    JPanel panel = new JPanel();
    JTextField textField = new JTextField(23);
    JTextArea messageArea = new JTextArea(16, 50);
    JButton button = new JButton("SEND");
    JPanel topLine = new JPanel();
    JPanel leftLine = new JPanel();
    JPanel rightLine = new JPanel();
    JPanel bottomLine = new JPanel();
    JPanel top = new JPanel();
    JPanel bottom = new JPanel();
    JPanel name = new JPanel();
    JLabel label = new JLabel("CHATTING ROOM");

    
	//������ ���Ӿ�� �˸�
    public void notifyCome(String sender) {
    	messageArea.append(sender + "���� �����ϼ̽��ϴ�.\n");
    }
    
	//������ ������ �˸�
    public void notifyOut(String sender) {
    	messageArea.append(sender + "���� �����ϼ̽��ϴ�.\n");
    }
    
	//�޼��� �߰�
    public void receiveChat(String sender, String content) {
    	messageArea.append(sender + ": "+ content + "\n");
    }
    
    //�޼��� ������
    public void sendChat() {
		String getTxt = textField.getText();
		if(getTxt.equals("")) return;
		Client.sendMCHAT(roomnumber, getTxt); //�̰� �ٽ�!
		textField.setText("");
    }
    
    //ä�� �����ϱ�
    public void exitChat() { 
    	//�����Ұųİ� �� �� �� �����
		int reply = JOptionPane.showConfirmDialog(null, "ä���� �����Ͻðڽ��ϱ�?", "ä�þ˸�", JOptionPane.YES_NO_OPTION);

		if (reply == JOptionPane.YES_OPTION) {
			Client.delMCHAT(roomnumber);
	        frame.dispose();
		}	
    }
    
    //ä�� �����������ϱ�
    public void FexitChat() { 
    	//�����Ұųİ� �� �� �� �����
    	Client.delMCHAT(roomnumber);
        frame.dispose();
    }
    
	//premessage �߰�
    public void PrereceiveChat(int num, String[][] clist) {
    	for(int i=0;i<num-1;i++) {
        	messageArea.append(clist[i][0] + ": "+ clist[i][1] + "\n");
    	}
    	messageArea.append("---------------------------------\n");

    }
    
 
    public ChattingMulti(int roomnumber, String roomname) {
    	this.roomnumber = roomnumber;
    	this.room_name = roomname;
    	frame.setTitle(roomname);
    	
    	frame.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {
            	exitChat();
            }
            public void windowClosed(WindowEvent e) {
            	
            }
            public void windowActivated(WindowEvent e) {}
        }); 
    	
    	
    	name.setPreferredSize(new Dimension(300, 30));
    	name.add(label);
    	label.setHorizontalAlignment(JLabel.CENTER);
    	label.setPreferredSize(new Dimension(220, 30));
        label.setFont(new Font("���", Font.BOLD, 23));
        label.setForeground(Color.black);
        
    	ImageIcon icon = new ImageIcon("image/add.png");
    	Image addImage = icon.getImage();
	    Image addChangingImg = addImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
	    ImageIcon addChangeIcon = new ImageIcon(addChangingImg);

	    ImageIcon icon3 = new ImageIcon("image/user.png");
	    Image userImage = icon3.getImage();
	    Image userChangeImg = userImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
	    ImageIcon userChangeIcon = new ImageIcon(userChangeImg);

	    JButton add = new JButton();
	    add.setBounds(10, 6, 28, 28);
	    add.setIcon(addChangeIcon);
	      
	    //ģ���ʴ���!! ���� �����ұ�...
	    add.addActionListener(new ActionListener() {
		       @Override
		       public void actionPerformed(ActionEvent e) {
		    	   MainScreen.showInviteInOriginRoom(roomnumber);
		       }
		});
	    
	    JButton user = new JButton();
	    user.setBounds(304, 6, 28, 28);
	    user.setIcon(userChangeIcon);
	    
	    //���� ���� ���� Ȯ�� ���!
	    user.addActionListener(new ActionListener() {
		       @Override
		       public void actionPerformed(ActionEvent e) {
		    	   Client.reqULIST(roomnumber);
		       }
		});
	    
	    
        frame.getContentPane().add(add);
        frame.getContentPane().add(user);
        topLine.setPreferredSize(new Dimension(600, 10));
        topLine.setBackground(new Color(255, 229, 110));
        leftLine.setBackground(new Color(255, 229, 110));
        rightLine.setBackground(new Color(255, 229, 110));
        bottomLine.setPreferredSize(new Dimension(600, 1));
        bottomLine.setBackground(new Color(255, 229, 110));
        textField.setEditable(true);
        Font font = new Font("���", Font.PLAIN, 14);
        messageArea.setFont(font);
        messageArea.setLineWrap(true);
        messageArea.setBorder(null);
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(0, 54, 78));
        button.setPreferredSize(new Dimension(58, 22));
        button.setFont(new Font("���", Font.BOLD, 13));
        button.setBackground(new Color(255, 229, 110));
        button.setBorder(null);
        top.setBackground(new Color(74, 210, 149));
        top.setPreferredSize(new Dimension(400, 50));
        top.add(label);
        top.add(topLine);
        bottom.setPreferredSize(new Dimension(400, 60));
        bottom.setBackground(new Color(74, 210, 149));
        bottom.add(textField);
        bottom.add(button);
        panel.setBackground(new Color(74, 210, 149));
        panel.setPreferredSize(new Dimension(400, 50));
        panel.add(bottomLine);
        panel.add(bottom);
        panel.setBackground(new Color(255, 229, 110));
        panel.setPreferredSize(new Dimension(350, 45));
        frame.getContentPane().add(leftLine, BorderLayout.EAST);
        frame.getContentPane().add(rightLine, BorderLayout.WEST);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(top, BorderLayout.NORTH);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.pack();
        messageArea.setForeground(Color.white);

        
        //ä�ú�����!!!
        //��ư ������, ���͸� �ĵ� ���� ����!
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	sendChat();
            }
        });
       
        button.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(textField.isEnabled())
        		sendChat();
	        }
        });


        frame.setVisible(true);
        frame.setSize(350, 550);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE );
    }
	
}
