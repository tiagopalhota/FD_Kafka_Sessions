package kafka.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoAssignSeek {
    public static void main(String[] args) {
        // returns com.github.kafka.tutorial1.ConsumerDemo
        Logger logger = LoggerFactory.getLogger(ConsumerDemoAssignSeek.class.getName());

        String bootstrapServers = "localhost:9092";
        String topic = "assign_seek";
        String groupID = "my-fourth-application";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // earliest -> read from the beginning of the topic
        // latest -> only new messages
        // none -> throws an error
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Create a consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

       // assign and seek are mostly used to replay data or fetch a specific message

        // assign
        TopicPartition partitionToReadFrom  = new TopicPartition(topic, 0   );
        long offsetToReadFrom = 4L; // L is for Long
        consumer.assign(Arrays.asList(partitionToReadFrom));


        // seek
        consumer.seek(partitionToReadFrom, offsetToReadFrom);

        int numberOfMessagesToRead = 2;
        int numberOfMessagesReadSoFar = 0;

        boolean keepOnReading = true;

        while(keepOnReading){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for(ConsumerRecord<String,String> record : records){

                numberOfMessagesReadSoFar += 1;

                logger.info("Key: " + record.key() + ", Value: " + record.value());
                logger.info("Partition: " + record.partition() + ", Offset:" + record.offset() );

                if( numberOfMessagesReadSoFar == numberOfMessagesToRead){
                    keepOnReading = false;
                    break;
                }

            }



        }


    }
}
