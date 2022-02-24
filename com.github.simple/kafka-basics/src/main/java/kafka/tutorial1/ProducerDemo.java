package kafka.tutorial1;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerDemo
{
    public static void main(String[] args) {


        String bootstrapServers = "127.0.0.1:9092";
        String topic = "hello_world";
        String value = "Hello from Java";
        // create Producer properties
        Properties properties = new Properties();


        // create Producer properties
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName() );
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName() );

     /*
        ------------------------------------------------------------------------
        --  old way of specifying the properties
        ------------------------------------------------------------------------
        help what type of values/keys we are sending, helps to convert to bytes
         org.apache.kafka.common.serialization.StringSerializer
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName() );*/




        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties) ;

        // create a ProducerRecord
        ProducerRecord<String,String> record = new ProducerRecord<String, String>(topic, value);


        // send data
        producer.send(record);

        // flush data
        producer.flush();

        // flush and close
        producer.close();
    }
}
