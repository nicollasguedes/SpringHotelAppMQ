package Hotel;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Maid {

    private static final String EXCHANGE_ALL = "all";
    private static Channel channel;

    public static void setupQueueConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        try {

            channel.exchangeDeclare(EXCHANGE_ALL, "fanout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int menu() {
        System.out.println("Beira Mar");
        System.out.println("1. Notificar Faxina!");
        System.out.println("4. EXIT");
        Scanner input = new Scanner(System.in);
        System.out.println("Digite a opção que vc quer realizar: ");
        return input.nextInt();
    }

    public static void main(String[] argv) throws Exception {
        setupQueueConnection();

        String faxinaMessage = "\nFAXINEiRA NA PORTA\n";


        int resposta = 0;
        while (resposta != 4) {
            resposta = menu();
            if (resposta == 1) {
                channel.basicPublish(EXCHANGE_ALL, "", null, faxinaMessage.getBytes("UTF-8"));
                System.out.println("FAXINA NOTIFICADA");
                System.exit(0);
            }

            if (resposta == 4) {
                System.out.println("EXIT");
                System.exit(0);
                channel.close();
            }
            if (resposta > 4 || resposta <= 0) {
                System.out.println("Escolha uma opção válida");

            }
        }

    }
}
