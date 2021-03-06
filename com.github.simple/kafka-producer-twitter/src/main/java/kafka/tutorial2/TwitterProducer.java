
package kafka.tutorial2;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterProducer {
    public TwitterProducer() {
    }

    Logger logger = LoggerFactory.getLogger(TwitterProducer.class.getName());
    String consumerKey = "consumerKey";
    String consumerSecret = "consumerSecret";
    String token = "token";
    String secret = "secret";

    public static void main(String[] args) {
        // create twitter client
        new TwitterProducer().run();

        // create a kafka producer
    }

    public void run() {

        logger.info("Setup");
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100);

        Client client = createTwitterClient(msgQueue);
        client.connect();


        // create a kafka producer
        KafkaProducer<String, String> producer = createKafkaProducer();

        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread( ()-> {
            logger.info("stopping application...");
            logger.info("Shutting down client from twitter...");
            client.stop();
            logger.info("closing producer...");
            // sends all data that is still in memory to kafka before shutdown
            producer.close();
            logger.info("done");
        }));

        // loop to send tweets to kafka
        // on a different thread, or multiple different threads....
        while (!client.isDone()) {

            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }

            if (msg != null) {
                logger.info(msg);
                producer.send(new ProducerRecord<>("twitter_demo", null, msg), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if( e != null){
                            logger.error("Something bad happened", e);
                        }
                    }
                });
            }


        }
        logger.info("End of application");

    }

    public Client createTwitterClient(BlockingQueue<String> msgQueue) {

        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        // Optional: set up some followings and track terms

        List<String> terms = Lists.newArrayList( "bitcoin", "FDTechnologies");
        hosebirdEndpoint.trackTerms(terms);

        logger.info("Consumer Key -> " + consumerKey);
        logger.info("Consumer Secret ->" + consumerSecret);
        logger.info("Token --> " + token);
        logger.info("Secret --> " + secret);

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);


        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));
        // optional: use this if you want to process client events

        Client hosebirdClient = builder.build();

        return hosebirdClient;


    }


   public KafkaProducer<String, String> createKafkaProducer(){
       String bootstrapServers = "127.0.0.1:9092";

       // create Producer properties
       Properties properties = new Properties();


       // create Producer properties
       properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
       properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName() );
       properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName() );


       // create safe producer
       properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
       properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
       properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
       // kafka 2.0 >= 1.1 so 5, otherwise 1.
       properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");


       // high throughput producer ( at the expense of a bit of latency and CPU usage)
       properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
       properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
       properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024));  // 32KB

       // create the producer
       KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties) ;


       return producer;
   }



}

