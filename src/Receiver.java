import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//receives message form the exchange
public class Receiver  extends Application{


    private final String EXCHANGE_NAME = "logs";
    private ListView listView;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Receiver");

        listView = new ListView();
        VBox vBox = new VBox(listView);

        Scene scene = new Scene(vBox, 300, 120);
        primaryStage.setScene(scene);
        primaryStage.show();


        try{
            Receive();
        }catch (Exception e){System.out.print("Sorry, something is wrong!" + e);}


    }
    public static void main(String[] args) {
        launch(args);
    }


    public void Receive() throws Exception
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            String time =  LocalTime.now().format(dtf);
            listView.getItems().add(time +" : " +message);

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}





