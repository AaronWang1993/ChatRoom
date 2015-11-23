import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	boolean started = false;
	ServerSocket ss = null;
	
	List<Client> clients = new ArrayList<Client> ();
	
	public static void main(String[] args) {
		new ChatServer().start();//�������ⶨ��һ��start������ԭ���ǣ���̬��main�������治����ֱ��newһ���ڲ������
	}
	

	public void start() {
		try {
			ss = new ServerSocket(8888);
			started = true;
		} catch (BindException e1) {
			System.out.println("�˿�ʹ����...");
			System.out.println("��ر���س����������з�����");
			System.exit(0);// ע����ǣ������try����������쳣��������catch
		} catch (IOException e) {// ��ִ�У���catchִ����֮�������try���ǻ����ִ�У�
			e.printStackTrace();// ������ó���������catch�������˳���������catch����
								// д��System.exit(0);���ǿ��԰�try-catch����if-else�ĸо���
		}

		try {
			while (started) {
				Socket s = ss.accept();//����д��ȱ���ǣ�������������һ��Client֮�󣬾ͻ᲻��ִ��������Ǹ�while��ѭ���������accept�Ͳ����ٵõ�ִ�еĻ��ᣬ�����Ļ�����������ڶ���Client�ˣ����޷����ӵ�Server�˵ġ�
				Client c = new Client(s);
				new Thread(c).start();  //һ��Server���������Client���������Client����Ҫͨ��accept���ӵ����server
				clients.add(c);
			}
		} catch(IOException e) {
			e.printStackTrace();		//dis = new DataInputStream(s.getInputStream());
		} finally{
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}								// �����������ʽ����Client�˹ر�֮������ᷢ��EOFException������EOF����˼��END
										// OF FILE
	

	class Client implements Runnable {
		private Socket s = null;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean bconnected = false;
		
		public Client(Socket s) { //���캯������ָ����public��Ҳ����ֱ�Ӳ�дĬ����default
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bconnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch(IOException e) {
				clients.remove(this);
				System.out.println("�Է��˳��ˣ��Ҵ�List�������");
			}
		}
		
		public void run() {
			
				try {
					while(bconnected) {
						String str = dis.readUTF();
						for(int i = 0; i < clients.size(); i ++) {
							Client c = clients.get(i);
							c.send(str);
						}
					}
				} catch (EOFException e) {
					System.out.println("Client was disconnected");
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						if(dos != null) {
							dos.close();
						}
					} catch(IOException e) {
						e.printStackTrace();
					}
					if (dis != null) {
						try {
							dis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (s != null)
						try {
							s.close();
							s = null; //�����ⲽ��ԭ���Ǹ����Ͻ�������java���������ھ�֪�����ڴ浱�е�������û�������ˣ��Ͱ�����������
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
				
			}
			
		
	}

}
