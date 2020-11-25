package Variable;

import java.util.ArrayList;

public class RequestRoom {
	private String requester_ID;
	private String threadckID; //�ʹݺο� client�� thread�� ������ ID
	private int type; //0�̸� ����, 1�̸� ��ü
	private int participants_num; //���� ���� �� �ο� ��
	private ArrayList<String> participants_list = new ArrayList<String>();
	
	
	
	public RequestRoom(String rID, int type, int ptNum, String threadID, String[] name) {
		this.requester_ID = rID;
		this.participants_num = ptNum;
		this.type = type;
		threadckID = threadID;

		// name���� 3���� ID�� �������

		if (type == 0) {
			participants_list.add(name[3]);
		} else {
			int ck = 0;
			for (String i : name) {
				if (ck < 3) {
					ck++;
					continue;
				}
				participants_list.add(name[ck]);
			}
		}
	}
	
	
	public String getRequester_ID() {
		return requester_ID;
	}
	
	public int getType() {
		return type;
	}
	
	public int getParticipants_num() {
		return participants_num;
	}
	
	public ArrayList<String> getParticipants_list() {
		return participants_list;
	}
}
