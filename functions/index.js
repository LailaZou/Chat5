const functions = require('firebase-functions');

const admin=require('firebase-admin');

admin.initializeApp({
	credential:admin.credential.applicationDefault(),
  	databaseURL:'ws://chatapp-6ff85.firebaseio.com/'
});
console.log("jjiiiiiii");
exports.notify=functions.database.ref('Chats/{id}')
    .onWrite((change, context) =>{
           // console.log("starting "+docSnapshot);
        var senderId= change.after.child('sender').val();;
        var msg=change.after.child('message').val();
        var Id=change.after.key;
          var receiverId=change.after.child('receiver').val();
          var sender=null;
          var receiver=null;
      var tokenId=null;


        // console.log("snapshot "+docSnapshot.val());
         console.log("snapshot "+change.after);
        console.log("receiverID "+receiverId);

        console.log("senderID "+senderId);
        console.log("msg "+msg);
        console.log("Id "+Id);

        admin.database().ref('Tokens/'+receiverId).orderByKey().on('child_added', function (snapshot) {
                		console.log("snapshot.val=tokn =  "+snapshot.val());
                           tokenId=snapshot.val();
                          console.log("token "+tokenId);


                      });

        admin.database().ref('Users/'+senderId).once('value').then(function (snapshot){
        sender=snapshot.val().username;
        console.log("sender "+sender);

             admin.database().ref('Users/'+receiverId).once('value').then(function (snapshot){
                 if(snapshot.val()){
                         receiver=snapshot.val().username;
                          console.log("receiver "+receiver);

         if(tokenId!==null && sender!==null && receiver!==null && msg!==null){
                  console.log(" exist");
                  console.log("token "+tokenId);
                  console.log("sender "+sender);
                  console.log("receiver "+receiver);
                  console.log("receiver "+msg);




                                    var payload = {
                                      notification: {
                                        title: "Message de "+sender,
                                        body: ""+msg,
                                       icon: 'ic_logo',
                                       color: '#4568dc',
                                      sound: 'default',
                                      }
                                    };
                                     var options = {
                                      priority: "high",
                                      timeToLive: 60 * 60 *24
                                    };
                                    admin.messaging().sendToDevice(tokenId, payload, options)
                                      .then(function(response) {
                                        console.log("Successfully sent message:", response);
                                      })
                                      .catch(function(error) {
                                        console.log("Error sending message:", error);
                                      });
                                     console.log("before breaking");

                                        return;
                 	                console.log("after breaking");



                }else
                 	console.log("doesnt exist");

                 }
                 });


        });











    });