package ua.java.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private Scanner scanner;
	private boolean streamsOpened;

	public ChatClient(int port, String ip) {
		scanner = new Scanner(System.in);
		try {
			socket = new Socket(InetAddress.getByName(ip), port);
			out = new ObjectOutputStream(socket.getOutputStream());

			System.out.println("Enter your nickname: ");
			String nickName = scanner.nextLine();
			Message m = new Message(nickName + " online");
			out.writeObject(m);
			out.flush();
			
			MessageReader reader = new MessageReader();
			reader.start();
			streamsOpened = true;
			String keyBoard = "";
			
			while (!keyBoard.equals("exit")) {
				keyBoard = scanner.nextLine();
				m = new Message(keyBoard);
				m.setIp(ip);
				m.setNickname(nickName);
				out.writeObject(m);
				out.flush();
			}
			reader.setStop();
		} catch (IOException e) {
			System.out.println("Server is not available at the moment");
		} finally {
			if (streamsOpened)
				close();
		}
	}

	private void close() {
		try {
			scanner.close();
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Streams were not closed");
		}
	}

	private static void usage() {
		System.err.println("Usage: ChatClient -p <port_number> -i <server_ip>");
		System.err.println("       -p[ort]  = enter port number");
		System.err.println("       -i[nternet protocol] = enter server IP Address");
		System.exit(1);
	}

	private class MessageReader extends Thread {

		private boolean stoped = false;

		public MessageReader() throws IOException {
			in = new ObjectInputStream(socket.getInputStream());
		}

		public void setStop() {
			stoped = true;
		}

		@Override
		public void run() {
			try {
				while (!stoped) {
					Message message = (Message) in.readObject();
					System.out.println(message);
				}
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
				System.out.println("Class not found");
			}
		}
	}

	public static void main(String[] args) throws IOException {
		int port = 0;
		String ip = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-p")) {
				port = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("-i")) {
				ip = args[i + 1];
			}
		}

		if ((port < 1023) ^ (ip == null)) {
			usage();
		} else
			new ChatClient(port, ip);
	}
}