var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);

server.listen(process.env.PORT || 3000);
var room = Array();
var room_queue = Array();
var id = 0;

function room_infor(_name, _size) {
    this.name = _name;
    this.size = _size;
}
room_queue.push(new room_infor(id,1));

io.sockets.on('connection', function (socket) {
    console.log('connecting');
    
   // io.emit("serverSend_list_room",{list : room});
   socket.on('get_room',function(){
    socket.emit("serverSend_list_room",{list : room});
   });
    
    socket.on('create_room', function () {
        if (room_queue.length > 0) {
            var inf = room_queue[0];
            inf.size = 1;
            room_queue.shift();
            room.push(inf);
            socket.join(inf.name);
            console.log("create " + inf.name);
            socket.emit('come_room_ans', { val: true, name : inf.name});
        }
        else {
            id++;
            var inf = new room_infor(id, 1);
            room.push(inf);
            socket.join(inf.name);
            console.log("create " + inf.name);
            socket.emit('come_room_ans', { val: true, name : inf.name});
        }
        console.log(room.length);
        io.sockets.emit("serverSend_list_room",{list : room});

    });

    socket.on('come_room', function (_name) {
        console.log(_name);
        const index = room.findIndex(val => val.name == _name);

        console.log(index+ "__" +room[index]);
        if (index != -1 && room[index].size < 2) {
            
            socket.emit('come_room_ans', { val: true ,name : _name});
            room[index].size += 1;
            socket.join(_name);
            console.log(_name + " joined");

            io.sockets.emit("serverSend_list_room",{list : room});
        }else {
            socket.emit('come_room_ans', { val: false, name : _name});
            console.log(_name + " full");
        }


    });//come room
    socket.on("out_room",function(_name){
        console.log("device out room");
        const index = room.findIndex(val => val.name == _name);
        if(index!=-1){
            room[index].size-=1;
            socket.leave(_name);
            if(room[index].size==0){
                room_queue.push(room[index]);
                room.splice(index,1);
            }
            else{
                socket.to(_name).emit("other_user_out");
            }
            io.sockets.emit("serverSend_list_room",{list : room});
        }

    });
});