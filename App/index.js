const express = require("express")
const bodyParser = require("body-parser")
const { Kafka } = require('kafkajs')

const app = express()
app.use(bodyParser.urlencoded({extended:true}))

app.get("/", function(req,res){
  res.sendFile(__dirname + "/index.html")
})

app.post("/", function(req,res){
  const fname = req.body.fname;
  const lname = req.body.lname;
  const age = req.body.age;
  const gender = req.body.gender;
  const height = req.body.height;
  const weight = req.body.weight;

  console.log(fname+lname);

  run(fname,lname,age,gender,height,weight)
  res.send("<h1>Details Submitted Successfully!!!</h1>")
})

app.listen(3000, function(){
  console.log("Server Running on Port 3000");
})

// kafka code
async function run(fname,lname,age,gender,height,weight){
  try{
    const kafka = new Kafka({
      "clientId": "Analytica",
      "brokers" :["localhost:9092"]
    })

    const producer = kafka.producer();
    await producer.connect()
    
    const result =  await producer.send({
      "topic": "userData",
      "messages": [
        {value: fname+","+lname+","+age+","+gender+","+height+","+weight}
      ]
    })

    console.log(`Send Successfully : ${fname+","+lname+","+age+","+gender+","+height+","+weight}`)
    await producer.disconnect();
  }
  catch(ex)
  {
    console.error(`Something bad happened ${ex}`)
  }
}