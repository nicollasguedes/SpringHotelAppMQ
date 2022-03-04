package Clientes;

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

public class Cliente_2 {
    private static final String EXCHANGE_ALL = "all";
    private static final String EXCHANGE_CLIENTE = "cliente";
    private String nome;
    private Room room;

    private static Channel channel;
    private static ConnectionFactory factory;


    private static Cliente_2 client;
    private static Map<Cliente_2, Room> clientes;
    private static Map<String, Room> rooms;

    public Cliente_2(String nome, Room room) {
        this.nome = nome;
        this.room = room;
    }

    public static void setFactory(ConnectionFactory factory) {
        Cliente_2.factory = factory;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Room getQuarto() {
        return room;
    }

    public void setQuarto(Room room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + nome + '\'' +
                ", room=" + room +
                '}';
    }


    public static void setupQueueConnection() throws IOException, TimeoutException {

        factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();


        channel.exchangeDeclare(EXCHANGE_ALL, "fanout");
        channel.exchangeDeclare(EXCHANGE_CLIENTE, "direct");


        String queueName = channel.queueDeclare().getQueue();
        channel.queueDeclare("checkinCliente2", false, false, false, null);

        channel.queueBind(queueName, EXCHANGE_ALL, "");
        channel.queueBind("checkinCliente2", EXCHANGE_CLIENTE, "checkinCliente2");


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });

        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(message.toUpperCase());
        };
        channel.basicConsume("checkinCliente2", true, deliverCallback2, consumerTag -> {
        });

    }

    public static int Menu() {
        System.out.println("Hotel Beira Mar");
        System.out.println("1. check-in no hotel");
        System.out.println("2. Encerrar estadia");
        System.out.println("3. Informações da estadia");
        System.out.println("4. Exit");

        Scanner input = new Scanner(System.in);
        System.out.println("Digite a opção que vc quer realizar: ");
        return input.nextInt();
    }

    public static void setupClientInfo() {
        client = new Cliente_2("Lael", null);
        clientes = new HashMap<>();
        clientes.put(client, null);
    }

    public static void roomsSetup() {
        rooms = new HashMap<>();
        rooms.put("1", new Room(1, "Quarto de Casal", 200));
        rooms.put("2", new Room(2, "Quarto de Solteiro", 100));
        rooms.put("3", new Room(3, "Quarto Duplo", 250));
    }

    public static void checkin() {
        try {
            Scanner input = new Scanner(System.in);
            channel.exchangeDeclare(EXCHANGE_CLIENTE, "direct");


            System.out.println("Quartos dispniveis: \n " + rooms.toString());
            System.out.println("Digite o ID do quarto ");
            int roomID = input.nextInt();
            if (roomID == 1) {
                System.out.println("Foi reservado um " + rooms.get("1").getRoomType() + ", boa estadia");
                String confirmacao = client.getNome() + " Alugou um  " + rooms.get("1").getRoomType() + "\n";
                clientes.put(client, rooms.get("1"));
                channel.basicPublish(EXCHANGE_CLIENTE, "checkin", null, confirmacao.getBytes("UTF-8"));

            }
            if (roomID == 2) {
                System.out.println("Foi reservado um " + rooms.get("2").getRoomType() + ", boa estadia");
                String confirmacao = client.getNome() + " Alugou um  " + rooms.get("2").getRoomType() + "\n";
                clientes.put(client, rooms.get("2"));
                channel.basicPublish(EXCHANGE_CLIENTE, "checkin", null, confirmacao.getBytes("UTF-8"));
            }
            if (roomID == 3) {
                System.out.println("Foi reservado um " + rooms.get("3").getRoomType() + ", boa estadia");
                String confirmacao = client.getNome() + " Alugou um  " + rooms.get("3").getRoomType() + "\n";
                clientes.put(client, rooms.get("3"));
                channel.basicPublish(EXCHANGE_CLIENTE, "checkin", null, confirmacao.getBytes("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void EncerrarEstadia() throws IOException {
        String confirmacao = "Estadia do cliente " + client.getNome() + " foi encerrada no " + clientes.get(client).getRoomType() + "\n";
        channel.basicPublish(EXCHANGE_CLIENTE, "checkin", null, confirmacao.getBytes("UTF-8"));
        clientes.remove(client);
        System.out.println("Estadia Encerrada volte sempre!");
    }

    public static void clientInfo() {
        System.out.println(client.getNome() + " as informações da sua estadia são: " + "\n");
        System.out.println(clientes.get(client));
    }

    public static void main(String[] args) throws Exception {
        setupQueueConnection();
        setupClientInfo();
        roomsSetup();


        int resposta = 0;
        while (resposta != 4) {
            resposta = Menu();

            if (resposta == 1) {
                checkin();
            }
            if (resposta == 2) {
                EncerrarEstadia();
                System.exit(0);
            }
            if (resposta == 3) {
                clientInfo();
            }
            if (resposta > 4||resposta <= 0){
                System.out.println("Escolha uma opção válida");

            }
        }
        System.out.println("EXIT");
        channel.close();
        System.exit(0);
    }
}
