import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Analytica {
  def main(args: Array[String]): Unit = {

    val broker_id = "localhost:9092"
    val group_id = "users"
    val topics = "userData"

    val topicset = topics.split(",").toSet
    val kafkaparams = Map[String,Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> broker_id,
      ConsumerConfig.GROUP_ID_CONFIG -> group_id,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer]
    )

    val conf = new SparkConf().setMaster("local").setAppName("Analytica")
    val ssc = new StreamingContext(conf, Seconds(10))

    val sc = ssc.sparkContext
    sc.setLogLevel("OFF")

    val message = KafkaUtils.createDirectStream[String,String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String,String](topicset,kafkaparams)
    )

    // get data from Kafka Producer
    val input_data = message.map(_.value())
    val user_detail = input_data.map(line => {
      val arr = line.split(",")

      // bmi calculation
      val weightInKilos = arr(5).toDouble
      val height = arr(4).toDouble
      val heightInMeters = height/100

      val bmi:Double = weightInKilos/(heightInMeters*heightInMeters).round
      var category = ""

      //interpret BMI
      if (bmi < 18.5) category="Underweight"
      else if (bmi >= 18.5 && bmi < 25) category="Normal"
      else if (bmi >= 25 && bmi < 30) category="Overweight"
      else if (bmi >= 30) category="Obese"

      //return the rdd
      (arr(0),arr(1),arr(2).toInt,arr(3),height,weightInKilos,bmi,category)
    })

    user_detail.foreachRDD { rdd =>

      // Get the singleton instance of SparkSession
      val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
      import spark.implicits._

      // Convert RDD[String] to DataFrame
      val userdataDataFrame = rdd.toDF("fname","lname","age","gender","height","weight","bmi","category")

      //connect to mysql database
      val AnalyticaDB = spark.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/Analytica?sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false").option("driver","com.mysql.jdbc.Driver").option("dbtable", "user_data").option("user", "yourusername").option("password", "yourpassword").load()

      // creating temporary views
      userdataDataFrame.createOrReplaceTempView("user_data_DF")
      AnalyticaDB.createOrReplaceTempView("user_data")

      // append the data into mysql database
      spark.sql("INSERT INTO user_data SELECT * FROM user_data_DF")
    }

    ssc.start()
    ssc.awaitTermination()
  }
}