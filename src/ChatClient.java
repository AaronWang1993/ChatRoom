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
			String str = tf.getText().trim(); // trim()������java.lang.String��ķ�����������
			// ta.setText("Client:" + " " + str);// ȥ���ַ�����ͷ�ͽ�β�Ŀո�
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
					ta.setText(ta.getText() + str + '\n');// ����д�Ļ�ÿ��֮ǰ�����ݾ��ܱ������������������ݸ��ǵ�
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
