import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

public class ChatClient extends Frame {
	TextField tf = new TextField();
	TextArea ta = new TextArea();
	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;

	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String str = tf.getText().trim(); // trim()是属于java.lang.String里的方法，作用是
			// ta.setText("Client:" + " " + str);// 去掉字符串开头和结尾的空格
			tf.setText("");
			try {
				dos.writeUTF(str);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}

	public void launchFrame() {
		setBounds(400, 300, 800, 800);
		add(tf, BorderLayout.SOUTH);
		add(ta, BorderLayout.NORTH);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				disconnect();
				System.exit(-1);
			}
		});
		tf.addActionListener(new MyActionListener());
		pack();
		setVisible(true);
		connect();
		Server ser = new Server();
		new Thread(ser).start();
	}

	public void connect() {
		try {
			s = new Socket("127.0.0.1", 8888);
			dos = new DataOutputStream(s.getOutputStream());
			System.out.println("connected");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			dos.close();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class Server implements Runnable {
		private boolean bconnected = false;

		public void run() {
			try {
				dis = new DataInputStream(s.getInputStream());
				bconnected = true;
				while (bconnected) {
					String str = dis.readUTF();
					ta.setText(ta.getText() + str + '\n');// 这样写的话每次之前的内容就能保存下来，不被新内容覆盖掉
				}
			} catch (SocketException e1) {
				System.out.println("Bye!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new ChatClient().launchFrame();

	}

}
