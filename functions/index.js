const functions = require('firebase-functions');

const admin=require('firebase-admin');

admin.initializeApp({
	credential:admin.credential.applicationDefault(),
  	databaseURL:'ws://chatapp-6ff85.firebaseio.com/'
});
console.log("jjiiiiiii");
exports.notify=functions.database.ref('Chats/{id}')
    .onCreate((docSnapshot,context)=>{
            console.log("starting");

        var senderId=docSnapshot.val().sender;
        var msg=docSnapshot.val().message;
        var Id=docSnapshot.key;
          var receiverId=docSnapshot.val().receiver;

         console.log("snapshot "+docSnapshot.val());
        console.log("receiverID "+receiverId);
        console.log("senderID "+senderId);
        console.log("msg "+msg);
        console.log("Id "+Id);

        admin.database().ref('Users/'+senderId).once('value').then(function (snapshot){
        var  sender=snapshot.val().username;
        console.log("sender "+sender);

                    admin.database().ref('Users/'+receiverId).once('value').then(function (snapshot){
                     var receiver=snapshot.val().username;
                    console.log("receiver "+receiver);

  		admin.database().ref('Tokens/'+receiverId).orderByKey().on('child_added', function (snapshot) {
		console.log("snapshot.val=tokn =  "+snapshot.val());

        if(snapshot.val()!==null ){
          console.log(" exist");
          var tokenId=snapshot.val();
          console.log("token "+snapshot.val());
          console.log("sender "+sender);

                        const notif={
                            token: tokenId,
                            notification:{
                                title:" Message de "+sender,
                                body:msg,
                               // icon: 'ic_notification',
                                //color: '#18d821',
                               //sound: 'default',

                            }
                            /*,
                            android:{
                                  priority: "high"
                                }*/

                        };
                       admin.messaging().send(notif)
                         .then((response) => {

                          return console.log('Successfully sent message:', response);
                         })
                         .catch((error) => {
                          return  console.log('Error sending message:', error);
                         });

        }else
         	console.log("doesnt exist");
      });

                    });
        });






    });