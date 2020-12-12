package client;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class ChattingOne{
	private String FID;
    private String NN;
    private String name;
    private String sender;
    
    JFrame frame = new JFrame(FID);
    JPanel panel = new JPanel();
    JTextField textField = new JTextField(25);
    JTextArea messageArea = new JTextArea(16, 50);
    JButton button = new JButton("SEND");
    JPanel leftLine = new JPanel();
    JPanel rightLine = new JPanel();
    JPanel top = new JPanel();
    JLabel label = new JLabel("CHATTING ROOM");


    //������ ������ ���� ����
    public void checkAnswer(String YN, String nn, String name) {
    	if(YN.equals("N")) {
			JOptionPane.showMessageDialog(null, "������ ä���� �����ϼ̽��ϴ�.");
			Client.delPCHAT(FID);
	        frame.dispose();
    	}
    	else {
    		setoppenInfo(nn, name);
    		messageArea.append("������ �����ϼ̽��ϴ�. \n");

    		//ä�� Ȱ��ȭ
            textField.setEditable(true);
    	}
    }
    
    public void setoppenInfo(String nn, String name) {
		NN = nn;
		this.name = name;
		sender = nn + "(" + name + ")";
    }
    
    public void setTextFree() {
        textField.setEditable(true);
    }

    public void endchat() {
		messageArea.append("������ �����̽��ϴ�. \n");
        textField.setEditable(false);
		button.setEnabled(false);

    }
    
	//�޼��� �߰�
    public void receiveChat(String content) {
    	messageArea.append(sender + ": "+ content + "\n");
    }
    
    //�޼��� ������
    public void sendChat() {
		String getTxt = textField.getText();
		if(getTxt.equals("")) return;
		Client.sendPCHAT(FID, getTxt); //�̰� �ٽ�!
		
		messageArea.append("�� : " + getTxt + "\n");
		textField.setText("");
    }
     
    public void exitChat() { //ä�� �����ϱ�
    	//�����Ұųİ� �� �� �� �����
		int reply = JOptionPane.showConfirmDialog(null, "ä���� �����Ͻðڽ��ϱ�?", "ä�þ˸�", JOptionPane.YES_NO_OPTION);

		if (reply == JOptionPane.YES_OPTION) {
			Client.delPCHAT(FID);
	        frame.dispose();
		}	
    }
    
    public ChattingOne(String FID) {
    	this.FID = FID;
    	
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
    	
        label.setFont(new Font("���", Font.BOLD, 30));
        label.setForeground(Color.WHITE);
	  
        top.add(label);
        leftLine.setBackground(new Color(0, 78, 150));
        rightLine.setBackground(new Color(0, 78, 150));
        textField.setEditable(false);
        Font font = new Font("���", Font.PLAIN, 14);
        messageArea.setFont(font);
        messageArea.setEditable(false); // ������ �Է��� ���ڸ� ������ �� ������ �Ѵ�.
        messageArea.setBackground(new Color(143, 226, 255));
        button.setPreferredSize(new Dimension(58, 22));
        button.setFont(new Font("���", Font.BOLD, 13));
        button.setBackground(Color.yellow);
        button.setBorder(null);
        top.setBackground(new Color(0, 78, 150));
        top.setPreferredSize(new Dimension(400, 50));
        panel.add(textField, BorderLayout.SOUTH);
        panel.add(button, BorderLayout.EAST);
        panel.setBackground(Color.WHITE);
        panel.setBackground(new Color(0, 78, 150));
        frame.add(leftLine, BorderLayout.EAST);
        frame.add(rightLine, BorderLayout.WEST);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.add(top, BorderLayout.NORTH);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.pack();
        
        
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
        frame.setSize(400, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE );

    }
 
}