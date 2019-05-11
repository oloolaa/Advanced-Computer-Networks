import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Socket{
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5566;
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;

    public Client() throws Exception{
        super(SERVER_IP, SERVER_PORT);
        client = this;
        out = new PrintWriter(this.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(System.in));

        new readLineThread();
        out.println("Joining the chat room...");

        while(true){
            String input = in.readLine();
            out.println(input);
        }
    }

    class readLineThread extends Thread{
        private BufferedReader buff;

        public readLineThread(){
            try {
                buff = new BufferedReader(new InputStreamReader(client.getInputStream()));
                start();
            }
            catch (Exception e) {}
        }

        @Override
        public void run() {
            try {
                while(true){
                    String result = buff.readLine();
                    if ("byeClient".equals(result)) {
                        break;
                    }
                    else {
                        System.out.println(result);
                    }
                }

                in.close();
                out.close();
                client.close();
            }
            catch (Exception e) {}
        }
    }

    public static void main(String[] args) {
        try {
            new Client();
        }
        catch (Exception e) {}
    }
}