import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	boolean started = false;
	ServerSocket ss = null;
	
	List<Client> clients = new ArrayList<Client> ();
	
	public static void main(String[] args) {
		new ChatServer().start();//这里另外定义一个start方法的原因是，静态的main方法里面不可以直接new一个内部类对象
	}
	

	public void start() {
		try {
			ss = new ServerSocket(8888);
			started = true;
		} catch (BindException e1) {
			System.out.println("端口使用中...");
			System.out.println("请关闭相关程序，重新运行服务器");
			System.exit(0);// 注意的是，这里的try中如果发生异常，进入了catch
		} catch (IOException e) {// 中执行，当catch执行完之后，下面的try还是会继续执行，
			e.printStackTrace();// 如果想让程序运行完catch中语句后退出，可以再catch里面
								// 写上System.exit(0);我们可以把try-catch理解成if-else的感觉。
		}

		try {
			while (started) {
				Socket s = ss.accept();//这样写的缺点是，服务器连接上一个Client之后，就会不断执行下面的那个while死循环，这里的accept就不会再得到执行的机会，这样的话，如果启动第二个Client端，是无法连接到Server端的。
				Client c = new Client(s);
				new Thread(c).start();  //一个Server可以连多个Client，但是这个Client必须要通过accept连接到这个server
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
	}								// 这个属于阻塞式，当Client端关闭之后，这里会发生EOFException，其中EOF的意思是END
										// OF FILE
	

	class Client implements Runnable {
		private Socket s = null;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean bconnected = false;
		
		public Client(Socket s) { //构造函数可以指定成public，也可以直接不写默认是default
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
				System.out.println("对方退出了，我从List里清除了");
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
							s = null; //加上这步的原因是更加严谨，这样java垃圾回收期就知道了内存当中的这块对象没人引用了，就把它给回收了
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
				
			}
			
		
	}

}
