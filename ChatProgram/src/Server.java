import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Server extends ServerSocket {
    private static final int SERVER_PORT = 5566;
    private static boolean isPrint = false;
    private static List<String> user_list = new ArrayList<>();
    private static List<ServerThread> thread_list = new ArrayList<>();
    private static LinkedList<Message> message_list = new LinkedList<>();

    public Server() throws IOException {
        super(SERVER_PORT);
        new PrintOutThread();
        System.out.println("Server is running.");

        try {
            while (true) {
                Socket socket = accept();
                new ServerThread(socket);
            }
        }
        catch (Exception e) {}
        finally {
            close();
        }
    }

    class PrintOutThread extends Thread {
        public PrintOutThread() {
            start();
        }

        @Override
        public void run() {
            while (true) {
                if (!isPrint) {
                    try {
                        Thread.sleep(500);
                        sleep(100);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    continue;
                }

                Message message = message_list.getFirst();
                for (int i = 0; i < thread_list.size(); i++) {
                    if (!message.getName().equals(user_list.get(i))) {
                        thread_list.get(i).sendMessage(message);
                    }
                }

                message_list.removeFirst();
                isPrint = message_list.size() > 0;
            }
        }
    }

    class ServerThread extends Thread {
        private Socket client;
        private PrintWriter out;
        private BufferedReader in;
        private String name;

        public ServerThread(Socket s) throws IOException {
            client = s;
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            in.readLine();
            start();
        }

        @Override
        public void run() {
            out.println("Connecting... Please input your name: ");
            System.out.println(getName());

            try {
                int flag = 0;

                while (true) {
                    String line = in.readLine();
                    if (line.equals("showuser")) {
                        out.println(this.listOnlineUsers());
                    } else if (line.equals("byeServer")) {
                        break;
                    } else {
                        if (flag == 0) {
                            name = line;
                            user_list.add(name);
                            thread_list.add(this);

                            out.println("Hi, " + name + "! You are in the chat room now!");
                            System.out.println(name + " connects to the server.");
                            pushMessage(name, "enters the chat room!");
                            flag = 1;
                        } else {
                            pushMessage(name, line);
                        }
                    }

                    System.out.println(name + ": " + line);
                }

                out.println("byeClient");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    client.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                thread_list.remove(this);
                user_list.remove(name);
                pushMessage(name, " exits the chat room.");
            }
        }

        public void pushMessage(String name, String msg) {
            Message message = new Message(name, msg);
            message_list.addLast(message);
            isPrint = true;
        }

        public void sendMessage(Message message) {
            out.println(message.getName() + ": " + message.getMessage());
        }

        private String listOnlineUsers() {
            String s = "---| Online User List |---\015\012";
            for (int i = 0; i < user_list.size(); i++) {
                s += "[" + user_list.get(i) + "]\015\012";
            }
            s += "--------------------";
            return s;
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}

class Message {
    String client;
    String message;

    public Message() {
        super();
    }

    public Message(String client, String message) {
        super();
        this.client = client;
        this.message = message;
    }

    public String getName() {
        return client;
    }

    public void setName(String name) {
        this.client = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message [client=" + client + ", message=" + message + "]";
    }
}