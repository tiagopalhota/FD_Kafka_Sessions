# Connectors

For this demo, you need  a Twitter Developer account. Create one and fetch the credential details. ( accessToken,consumerSecret,consumerKeyAccessTokenSecret)

1. Download KAFKA connector from https://github.com/jcustenborder/kafka-connect-twitter/releases/download/0.2.26/kafka-connect-twitter-0.2.26.tar.gz
2. Create a root folder in your project called kafka-connect
3. Create a subfolder called connectors
4. Extract the .tar.gz file and copy all the .jar files into kafka-connect/connectors/kafka-connect-twitter.
5. Copy the connect-standalone.properties folder from the config folder within your KAFKA installation path into the kafka-connect folder we have created before
6. Uncomment the last line and make it as plugin.path=connectors
7. Create a run.sh shell script file within kafka-connect.
8. Paste the following code there ( this is only valid for macOS)
  ```bash
  #!/usr/bin/env bash
  # run the twitter connector

  connect-standalone.sh connect-standalone.properties twitter.properties
  ````
9. Create a twitter.properties file in the kafka-connect folder.
10. Paste the following code
````bash
name=connector1
tasks.max=1
connector.class=com.github.jcustenborder.kafka.connect.twitter.TwitterSourceConnector

# Set these required valuesn

process.deletes=false
filter.keywords=kafka
kafka.status.topic=twitter_status_connect
kafka.delete.topic=twitter_deletes_connect
twitter.oauth.accessTokenSecret=replaceByYourAccessToken
twitter.oauth.consumerSecret=replaceByYourconsumerSecret
twitter.oauth.accessToken=replaceByYourAccessToken
twitter.oauth.consumerKey=replaceByYourConsumerKey
````
11. Start ZooKeeper and Kafka
12. Ensure that you have a topic created for twitter_status_connect
````bash
kafka-topics.sh --bootstrap-server localhost:9092 --list
````

13. if not create one as such:
````bash
kafka-topics.sh --bootstrap-server localhost:9092 --topic twitter_deletes_connect --partitions 3 --replication-factor 1 --create
````
14. Start a consumer to see the data coming from twitter
  ````bash
  kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic twitter_status_connect --from-beginning
  ````
15. Start the twitter connect
````bash
sh run.sh
`````

This should now be pulling data from twitter and inserting it into Kafka twitter_status_connect topic