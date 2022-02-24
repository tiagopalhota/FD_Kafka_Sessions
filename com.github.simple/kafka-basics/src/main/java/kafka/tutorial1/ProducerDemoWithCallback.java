package kafka.tutorial1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback
{
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class);

        String bootstrapServers = "127.0.0.1:9092";
        String topic = "second_topic";
        String value = "Tiago the producer";
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


        for(int i=0; i < 10; i++){


        // create a ProducerRecord
        ProducerRecord<String,String> record = new ProducerRecord<String, String>(topic, value + "_" + Integer.toString(i));


        // send data
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                // executes everytime a record is successfully sent or an exception is thrown
                if( e == null){
                    //record successfully sent
                    logger.info("Received new metadata: \n" +
                            "Topic: " + recordMetadata.topic() + "\n" +
                            "Partition: " + recordMetadata.partition() + "\n" +
                            "Offset:" + recordMetadata.offset() + "\n" +
                            "Timestamp: " + recordMetadata.timestamp());

                }else{
                    // deal with the error
                    logger.error("Error while producing", e);
                }
            }
        });
        }
        // flush data
        producer.flush();

        // flush and close
        producer.close();
    }
}
