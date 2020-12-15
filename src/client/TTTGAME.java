package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.*;


public class TTTGAME extends JFrame implements ActionListener {
	public int rn;
	public int order = 1; // ������
	public boolean end = false;
	public ImageIcon myturn = new ImageIcon("image/myturn.png");
	public ImageIcon yourturn = new ImageIcon("image/yourturn.png");
	public ImageIcon notturn = new ImageIcon("image/notMyTurn.png");
	
    Image myTurnImage = myturn.getImage();
    Image myTurnChangeImg = myTurnImage.getScaledInstance(80, 60, Image.SCALE_SMOOTH);
    ImageIcon myTurnChangeIcon = new ImageIcon(myTurnChangeImg);
    
    Image yourTurnImage = yourturn.getImage();
    Image yourTurnChangeImg = yourTurnImage.getScaledInstance(80, 60, Image.SCALE_SMOOTH);
    ImageIcon yourTurnChangeIcon = new ImageIcon(yourTurnChangeImg);
    
    Image notTurnImage = notturn.getImage();
    Image notTurnChangeImg = notTurnImage.getScaledInstance(80, 60, Image.SCALE_SMOOTH);
    ImageIcon notTurnChangeIcon = new ImageIcon(notTurnChangeImg);

	
	private ImageIcon myTurnImgIcon;
	private ImageIcon yourTurnImgIcon;
    private JButton[][] boardBtn = new JButton[3][3];
    
    private JButton myTurnBtn = new JButton();
    private JButton yourTurnBtn = new JButton();

	
	JTable table;
	
	private int doubleClick = 0;
	@Override
	public void actionPerformed(ActionEvent arg0) {}
	private Integer[][] checkedBlock = new Integer[3][3];
	

	
	//���� �ٲ��ֱ� (order�� 0�̸� �ƹ��� ������ ��ư �ȹٲ�
	public void changeOrder() {
		if(!end) {
			if(order == 1) order = 0;
			else order = 1;
		
			if (order == 1) { // �� �����϶�
			    myTurnBtn.setIcon(myTurnChangeIcon);
			    yourTurnBtn.setIcon(notTurnChangeIcon);
			} else if (order == 0) { // ��� �����϶�
			    myTurnBtn.setIcon(notTurnChangeIcon);
			    yourTurnBtn.setIcon(yourTurnChangeIcon);
			}
		}
	}
	
	//��밡 �� �����ߴ��� üũ
	public void checkOPPNblock(int x, int y) {
		checkedBlock[x][y] = 1;
		boardBtn[x][y].setIcon(yourTurnImgIcon);
		changeOrder();
	}
	
	// ���� ������ �ϸ� ���� �Ѿ��, �� �����ߴ��� ������� �Ѵ�.
	public void selectBlock(int x, int y) {
		if (!end) {

			if (order == 1) {
				changeOrder();
				// Ŭ���̾�Ʈ���� �ѱ�� �κ�
				Client.MYSELECTinTTT(rn, x, y);
			}
		}
	}
	
	//1�� ������ ���� �̱��, 0�� ������ ��밡 �̱�� -1�� ������ ����
	public void Winner(int order) {
		
		String windowName = "���� ���� ";
		String showMessage = "���ڰ� ������";
		
		if(order == 1) showMessage = "�̰���ϴ�!";
		else if(order == 0) showMessage = "�й��߽��ϴ�!";
		else showMessage = "�����ϴ�!";

		
		//���� ������ �ƹ��͵� ���� �Ұ�
		if(order == 1) order = 0;
		end = true;
		
	    myTurnBtn.setIcon(notTurnChangeIcon);
	    yourTurnBtn.setIcon(notTurnChangeIcon);
		
		JOptionPane.showMessageDialog(null, showMessage, windowName, JOptionPane.INFORMATION_MESSAGE);		
	}
	
	

	
	//������ ���´�!!!
	public TTTGAME(int roomnumber, int od, String MNN, String FNN) {
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				checkedBlock[i][j] = 0;
			}
		}
		this.rn = roomnumber;
		order = od; //���� ������ �޴´�
		
		
		JFrame frame = new JFrame();
		
		JPanel back = new JPanel();
		back.setPreferredSize(new Dimension(450, 150));
		back.setBackground(new Color(242, 242, 242));
		
		JButton titleBtn = new JButton();
		titleBtn.setBounds(0, 0, 450, 50);
		titleBtn.setBorder(null);
		ImageIcon icon1 = new ImageIcon("image/tictactoeTitle.png");
	    Image titleImage = icon1.getImage();
	    Image titleChangeImg = titleImage.getScaledInstance(450, 50, Image.SCALE_SMOOTH);
	    ImageIcon titleChangeIcon = new ImageIcon(titleChangeImg);
	    titleBtn.setIcon(titleChangeIcon);
	    
	    
		JPanel titleIcon = new JPanel();
		titleIcon.setPreferredSize(new Dimension(150, 0));
		titleIcon.setBackground(new Color(74, 210, 149));
		
		JButton fightBtn = new JButton();
		fightBtn.setBounds(181, 51, 88, 88);
		fightBtn.setBorder(null);
		ImageIcon icon2 = new ImageIcon("image/fight.png");
	    Image fightImage = icon2.getImage();
	    Image fightChangeImg = fightImage.getScaledInstance(88, 88, Image.SCALE_SMOOTH);
	    ImageIcon fightChangeIcon = new ImageIcon(fightChangeImg);
	    fightBtn.setIcon(fightChangeIcon);
	    


		myTurnBtn.setBounds(45, 50, 80, 60);
		myTurnBtn.setBorder(null);
		
		yourTurnBtn.setBounds(325, 50, 80, 60);
		yourTurnBtn.setBorder(null);

		
		if (order == 1) { // �� �����϶�
		    myTurnBtn.setIcon(myTurnChangeIcon);
		    yourTurnBtn.setIcon(notTurnChangeIcon);
		} else if (order == 0) { // ��� �����϶�
		    myTurnBtn.setIcon(notTurnChangeIcon);
		    yourTurnBtn.setIcon(yourTurnChangeIcon);
		}


		JLabel myIDLabel = new JLabel(MNN);
		myIDLabel.setFont(new Font("�����ٸ���", Font.BOLD, 15));
		myIDLabel.setBackground(Color.yellow);
		
		JLabel yourIDLabel = new JLabel(FNN);
		yourIDLabel.setFont(new Font("�����ٸ���", Font.BOLD, 15));
		yourIDLabel.setBackground(Color.yellow);
		
		JPanel myIDPanel = new JPanel();
		myIDPanel.setBackground(new Color(255, 229, 110));
		myIDPanel.setBounds(35, 110, 100, 28);
		myIDPanel.add(myIDLabel);
		
		JPanel yourIDPanel = new JPanel();
		yourIDPanel.setBackground(new Color(255, 229, 110));
		yourIDPanel.setBounds(315, 110, 100, 28);
		yourIDPanel.add(yourIDLabel);
		
		myTurnImgIcon = new ImageIcon("image/myCircle.png");
	    Image myTurn = myTurnImgIcon.getImage();
	    Image myTurnImg = myTurn.getScaledInstance(130, 76, Image.SCALE_SMOOTH);
	    myTurnImgIcon = new ImageIcon(myTurnImg);

		yourTurnImgIcon = new ImageIcon("image/yourCircle.png");
	    Image yourTurn = yourTurnImgIcon.getImage();
	    Image yourTurnImg = yourTurn.getScaledInstance(55, 55, Image.SCALE_SMOOTH);
	    yourTurnImgIcon = new ImageIcon(yourTurnImg);

	    JButton boardTitleBtn = new JButton();
	    boardTitleBtn.setBounds(30, 156, 390, 20);
	    boardTitleBtn.setBorder(null);
		ImageIcon icon5 = new ImageIcon("image/boardtitle.png");
	    Image boardTitleImage = icon5.getImage();
	    Image boardTitleChangeImg = boardTitleImage.getScaledInstance(390, 32, Image.SCALE_SMOOTH);
	    ImageIcon boardTitleChangeIcon = new ImageIcon(boardTitleChangeImg);
	    boardTitleBtn.setIcon(boardTitleChangeIcon);
		
	    
	    
	    boardBtn[0][0] = new JButton();
	    boardBtn[0][0].setBounds(30, 176, 130, 76);
		ImageIcon icon6 = new ImageIcon("image/board.png");
	    Image boardImage = icon6.getImage();
	    Image boardChangeImg = boardImage.getScaledInstance(130, 76, Image.SCALE_SMOOTH);
	    ImageIcon boardChangeIcon = new ImageIcon(boardChangeImg);
	    boardBtn[0][0].setIcon(boardChangeIcon);
	    boardBtn[0][0].addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (order == 1) { // �������϶��� �۵��ǵ���
					doubleClick++;
					if (doubleClick == 2 && checkedBlock[0][0] != 1) {
						boardBtn[0][0].setIcon(myTurnImgIcon);
						selectBlock(0, 0);
					} else if (doubleClick > 2) {
						doubleClick = 1;
					}
				}

			}
		});

		boardBtn[0][1] = new JButton();
		boardBtn[0][1].setBounds(160, 176, 130, 76);
		boardBtn[0][1].setIcon(boardChangeIcon);
		boardBtn[0][1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (order == 1) {
					doubleClick++;
					if (doubleClick == 2 && checkedBlock[0][1] != 1) {
						boardBtn[0][1].setIcon(myTurnImgIcon);
						selectBlock(0, 1);
					} else if (doubleClick > 2) {
						doubleClick = 1;
					}
				}
			}
		});

		boardBtn[0][2] = new JButton();
		boardBtn[0][2].setBounds(290, 176, 130, 76);
		boardBtn[0][2].setIcon(boardChangeIcon);
		boardBtn[0][2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (order == 1) {
					doubleClick++;
					if (doubleClick == 2 && checkedBlock[0][2] != 1) {
						boardBtn[0][2].setIcon(myTurnImgIcon);
						selectBlock(0, 2);
					} else if (doubleClick > 2) {
						doubleClick = 1;
					}
				}
			}
		});

		boardBtn[1][0] = new JButton();
		boardBtn[1][0].setBounds(30, 252, 130, 76);
		boardBtn[1][0].setIcon(boardChangeIcon);
		boardBtn[1][0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (order == 1) {
					doubleClick++;
					if (doubleClick == 2 && checkedBlock[1][0] != 1) {
						boardBtn[1][0].setIcon(myTurnImgIcon);
						selectBlock(1, 0);
					} else if (doubleClick > 2) {
						doubleClick = 1;
					}
				}
			}
		});

		boardBtn[1][1] = new JButton();
		boardBtn[1][1].setBounds(160, 252, 130, 76);
		boardBtn[1][1].setIcon(boardChangeIcon);
		boardBtn[1][1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (order == 1) {
					doubleClick++;
					if (doubleClick == 2 && checkedBlock[1][1] != 1) {
						boardBtn[1][1].setIcon(myTurnImgIcon);
						selectBlock(1, 1);
					} else if (doubleClick > 2) {
						doubleClick = 1;
					}
				}
			}
		});

		boardBtn[1][2] = new JButton();
		boardBtn[1][2].setBounds(290, 252, 130, 76);
		boardBtn[1][2].setIcon(boardChangeIcon);
		boardBtn[1][2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (order == 1) {
					doubleClick++;
					if (doubleClick == 2 && checkedBlock[1][2] != 1) {
						boardBtn[1][2].setIcon(myTurnImgIcon);
						selectBlock(1, 2);
					} else if (doubleClick > 2) {
						doubleClick = 1;
					}
				}
			}
		});

		boardBtn[2][0] = new JButton();
		boardBtn[2][0].setBounds(30, 328, 130, 76);
		boardBtn[2][0].setIcon(boardChangeIcon);
		boardBtn[2][0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (order == 1) {
					doubleClick++;
					if (doubleClick == 2 && checkedBlock[2][0] != 1) {
						boardBtn[2][0].setIcon(myTurnImgIcon);
						selectBlock(2, 0);
					} else if (doubleClick > 2) {
						doubleClick = 1;
					}
				}
			}
		});

		boardBtn[2][1] = new JButton();
		boardBtn[2][1].setBounds(160, 328, 130, 76);
		boardBtn[2][1].setIcon(boardChangeIcon);
		boardBtn[2][1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (order == 1) {
					doubleClick++;
					if (doubleClick == 2 && checkedBlock[2][1] != 1) {
						boardBtn[2][1].setIcon(myTurnImgIcon);
						selectBlock(2, 1);
					} else if (doubleClick > 2) {
						doubleClick = 1;
					}
				}
			}
		});

		boardBtn[2][2] = new JButton();
		boardBtn[2][2].setBounds(290, 328, 130, 76);
		boardBtn[2][2].setIcon(boardChangeIcon);
		boardBtn[2][2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (order == 1) {
					doubleClick++;
					if (doubleClick == 2 && checkedBlock[2][2] != 1) {
						boardBtn[2][2].setIcon(myTurnImgIcon);
						selectBlock(2, 2);
					} else if (doubleClick > 2) {
						doubleClick = 1;
					}
				}
			}
		});

		frame.add(titleBtn);
		frame.add(fightBtn);
		frame.add(myIDPanel);
		frame.add(yourIDPanel);
		frame.add(myTurnBtn);
		frame.add(yourTurnBtn);
		frame.add(boardTitleBtn);
		frame.add(boardBtn[0][0]);
		frame.add(boardBtn[0][1]);
		frame.add(boardBtn[0][2]);
		frame.add(boardBtn[1][0]);
		frame.add(boardBtn[1][1]);
		frame.add(boardBtn[1][2]);
		frame.add(boardBtn[2][0]);
		frame.add(boardBtn[2][1]);
		frame.add(boardBtn[2][2]);
		
		frame.add(back);
		
		
		frame.setVisible(true);
		frame.setSize(450, 450);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
	}
	
}