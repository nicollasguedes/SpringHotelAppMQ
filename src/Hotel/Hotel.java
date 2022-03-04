package Hotel;

import Hotel.Room.Room;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Hotel {
    private static final String EXCHANGE_ALL = "all";
    private static final String EXCHANGE_CLIENTE = "cliente";

    private static Channel channel;

    public static String[] messages = new String[3];


    public static void setupQueueConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        try {

            channel.exchangeDeclare(EXCHANGE_ALL, "fanout");
            channel.exchangeDeclare(EXCHANGE_CLIENTE, "direct");


            String queueName = channel.queueDeclare().getQueue();
            channel.queueDeclare("checkin", false, false, false, null);
            channel.queueBind("checkin", EXCHANGE_CLIENTE, "checkin");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(message);
            };

            channel.basicConsume("checkin", true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setupRooms() {
        Map<String, Room> rooms = new HashMap<String, Room>();
        rooms.put("1", new Room(1, "Quarto Solteiro", 100));
        rooms.put("2", new Room(2, "Quarto Duplo Solteiro", 190));
        rooms.put("3", new Room(3, "Dormitorio", 80));
    }

    public static void setupMessages() {
        String breakfast = "Café da manhã é: Pão com ovo, cuscuz e café \n";
        String welcomemessage = "Tenha uma Boa Estadia" + "\n";
        String messagePromo = "\nPROMOÇÕES:\n" +
                "50% de desconto no Natal!\n" +
                "20% de desconto para o quarto duplo!\n";
        messages[0] = breakfast;
        messages[1] = welcomemessage;
        messages[2] = messagePromo;
    }

    public static int Menu() {
        System.out.println("Beira Mar");

        System.out.println("1. Mandar mensagem de boas vindas");
        System.out.println("2. Notificar café da manhã");
        System.out.println("3. Notificar promos");
        System.out.println("4. EXIT");

        Scanner input = new Scanner(System.in);
        System.out.println("Digite a opção que vc quer realizar: ");
        return input.nextInt();
    }

    public static void welcomeMessage() throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("Para qual cliente você deseja mandar a mensagem?");
        int cliente = input.nextInt();
        if (cliente == 1) {
            channel.basicPublish(EXCHANGE_CLIENTE, "checkinCliente", null, messages[1].getBytes("UTF-8"));
            System.out.println("Mensagem enviada para o cliente 1");
        }
        if (cliente == 2) {
            channel.basicPublish(EXCHANGE_CLIENTE, "checkinCliente2", null, messages[1].getBytes("UTF-8"));
            System.out.println("Mensagem enviada para o cliente 2");
        }
        if (cliente > 2 || cliente < 0) {
            System.out.println("Cliente não encontrado");
        }
        System.out.println("Digite a opção que vc quer realizar: ");
    }

    public static void notifyBreakfast() throws IOException {
        channel.basicPublish(EXCHANGE_ALL, "", null, messages[0].getBytes("UTF-8"));

    }

    public static void sendPromo() throws IOException {
        channel.basicPublish(EXCHANGE_ALL, "", null, messages[2].getBytes("UTF-8"));
        System.out.println("PROMO ENVIADA PARA TODOS OS CLIENTES");
    }

    public static void main(String[] argv) throws Exception {
        setupQueueConnection();
        setupRooms();
        setupMessages();

        int resposta = 0;
        while (resposta != 4) {
            resposta = Menu();

            if (resposta == 1) {
                welcomeMessage();
            }

            if (resposta == 2) {
                notifyBreakfast();
            }

            if (resposta == 3) {
                sendPromo();
            }
            if (resposta == 4) {
                System.out.println("EXIT!!!");
                System.exit(0);
            }
            if (resposta > 4||resposta <= 0){
                System.out.println("Escolha uma opção válida");
            }
        }
        channel.close();
    }

}

