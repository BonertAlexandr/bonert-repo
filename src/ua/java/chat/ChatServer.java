package ua.java.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ChatServer {
	private ServerSocket ss;
	private List<ConnectedClient> clients = new ArrayList<ConnectedClient>();
	private CircularQueue<Message> historyOfMessage = new CircularQueue<Message>(
			new LinkedList<Message>(), 10);

	public ChatServer(int port, int history) {
		try {
			ss = new ServerSocket(port);
			historyOfMessage = new CircularQueue<Message>(
					new LinkedList<Message>(), history);

			while (true) {
				Socket socket = ss.accept();
				ConnectedClient client = new ConnectedClient(socket);
				clients.add(client);
				client.start();
			}
		} catch (IOException e) {
		} finally {
			closeAll();
		}
	}

	private void closeAll() {
		try {
			ss.close();
			synchronized (clients) {
				Iterator<ConnectedClient> iterator = clients.iterator();
				while (iterator.hasNext()) {
					iterator.next().close();
				}
			}
		} catch (IOException e) {
			System.out.println("Server socket was not close");
		}
	}

	private class ConnectedClient extends Thread {
		private ObjectInputStream in;
		private ObjectOutputStream out;
		private Socket socket;

		private ConnectedClient(Socket socket) {
			this.socket = socket;
			try {
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Socket initialize streams exception");
			}
		}

		private void sendHistoryOfMessage() throws IOException {
			synchronized (clients) {
				Iterator<Message> iterator = historyOfMessage.iterator();
				while (iterator.hasNext()) {
					Message m = iterator.next();
					out.writeObject(m);
					out.flush();
				}
			}
		}

		private void sendMessage(Message message) throws IOException {
			synchronized (clients) {
				Iterator<ConnectedClient> iterator = clients.iterator();
				while (iterator.hasNext()) {
					ConnectedClient cc = iterator.next();
					cc.out.writeObject(message);
					out.flush();
				}
			}
		}

		@Override
		public void run() {
			try {
				if (historyOfMessage.size() > 0) {
					sendHistoryOfMessage();
				}

				while (true) {
					Message message = (Message) in.readObject();

					if (message.getText().equals("exit")) {
						synchronized (clients) {
							Iterator<ConnectedClient> iterator = clients
									.iterator();
							Message lastMessage = new Message(
									message.getNickName() + " offline");
							while (iterator.hasNext()) {
								ConnectedClient cc = iterator.next();
								if (cc == this)
									continue;
								cc.out.writeObject(lastMessage);
								out.flush();
								historyOfMessage.offer(lastMessage);
							}
						}
						break;
					}
					sendMessage(message);
					historyOfMessage.offer(message);
				}
			} catch (IOException e) {
				System.out.println("IO exception");
			} catch (ClassNotFoundException e) {
				System.out.println("Server Class not found");
			} finally {
				close();
			}
		}

		private void close() {
			try {
				in.close();
				out.close();
				socket.close();
				clients.remove(this);
				if (clients.size() == 0) {
					ChatServer.this.closeAll();
					System.exit(0);
				}
			} catch (IOException e) {
				System.out.println("Streams was not close");
			}
		}

	}

	public static void main(String[] args) {
		try {
			int port = 0, history = 0;

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db
					.parse("src\\ua\\java\\chat\\conf.xml");

			doc.normalize();

			NodeList nList = doc.getElementsByTagName("conf");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					port = Integer.parseInt(eElement
							.getElementsByTagName("port").item(0)
							.getTextContent());
					history = Integer.parseInt(eElement
							.getElementsByTagName("history").item(0)
							.getTextContent());
				}
			}
			new ChatServer(port, history);
		} catch (ParserConfigurationException e) {
			System.out
					.println("DocumentBuilder cannot be created by your configuration");
		} catch (SAXException e) {
			System.out.println("Parse error");
		} catch (IOException e) {
			System.out.println("IO exception");
		}
	}
}