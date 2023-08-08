import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.concurrent.*;

public class SimpleChatClientA {
  private JTextArea incoming;
  private JTextField outgoing;
  private PrintWriter writer;
  private BufferedReader reader;

  public void go() {

    setUpNetworking();

    outgoing = new JTextField(20);

    JScrollPane scroller = createScrollPane();


    JButton sendButton = new JButton("Send");
    sendButton.addActionListener(e -> sendMessage());

    JPanel mainPanel = new JPanel();
    mainPanel.add(scroller);
    mainPanel.add(outgoing);
    mainPanel.add(sendButton);

    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(new IncomingReader());

    JFrame frame = new JFrame("Ludicrously Simple Chat Client");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
    frame.setSize(500,600);
    frame.setVisible(true);


  }

  private void setUpNetworking() {
    try {
      InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 5000);
      SocketChannel socketChannel = SocketChannel.open(serverAddress);

      writer = new PrintWriter(Channels.newWriter(socketChannel, UTF_8));
      reader = new BufferedReader(Channels.newReader(socketChannel, UTF_8));
      System.out.println("Networking established");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private JScrollPane createScrollPane() {
    incoming = new JTextArea(15,30);
    incoming.setLineWrap(true);
    incoming.setWrapStyleWord(true);
    incoming.setEditable(false);
    JScrollPane scroll = new JScrollPane(incoming);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    return scroll;
  }

  private void sendMessage() {
    writer.println(outgoing.getText());
    writer.flush();
    outgoing.setText("");
    outgoing.requestFocus();
  }

  public class IncomingReader implements Runnable {
    public void run() {
      String message;
      try {
        while ((message = reader.readLine()) != null) {
          System.out.println("read " + message);
          incoming.append(message + "\n");
        }


      } catch (IOException e) {
        e.printStackTrace();
      }



    }
  }

  public static void main(String[] args) {
    new SimpleChatClientA().go();
  }
}
