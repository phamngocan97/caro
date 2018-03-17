var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);

server.listen(process.env.PORT || 4000);
var room = Array();
var room_queue = Array();
var id = 0;

function room_infor(_name, _size) {
    this.name = _name;
    this.size = _size;
}
room_queue.push(new room_infor(id, 1));




io.sockets.on("connection", function (socket) {
    console.log("connecting");
    socket.un = -1;
    // io.emit("serverSend_list_room",{list : room});
    socket.on("get_room", function () {
        socket.emit("serverSend_list_room", { list: room });
    });

    socket.on("create_room", function () {
        if (room_queue.length > 0) {
            var inf = room_queue[0];
            inf.size = 1;
            room_queue.shift();
            room.push(inf);
            socket.join(inf.name);
            console.log("create " + inf.name);
            socket.emit("come_room_ans", { val: true, name: inf.name, id: 1 });

            socket.un = inf.name;
        }
        else {
            id++;
            var inf = new room_infor(id, 1);
            room.push(inf);
            socket.join(inf.name);
            console.log("create " + inf.name);
            socket.emit("come_room_ans", { val: true, name: inf.name, id: 1 });

            socket.un = id;
        }


        io.sockets.emit("serverSend_list_room", { list: room });


    });

    socket.on("come_room", function (_name) {

        const index = room.findIndex(val => val.name == _name);

        if (index != -1 && room[index].size < 2) {

            room[index].size += 1;
            socket.join(_name);
            console.log(_name + " joined");

            socket.emit("come_room_ans", { val: true, name: _name, id: 2 });
            io.sockets.emit("serverSend_list_room", { list: room });

            socket.un = _name;
        } else {
            socket.emit("come_room_ans", { val: false, name: _name, id: -1 });
            console.log(_name + " full");
        }


    });//come room

    socket.on("send_turn", function (turn, _name, _x, _y) {
        io.sockets.in(_name).emit("sever_send_turn", { val: turn, x: _x, y: _y });
        console.log("sent turn " + turn);
        // if (turn == 1) {
        //     io.sockets.in(_name).emit("sever_send_turn", { val: 2, x: _x, y: _y });
        //     console.log("sent turn " + 2);
        // } else {
        //     io.sockets.in(_name).emit("sever_send_turn", { val: 1, x: _x, y: _y });
        //     console.log("sent turn " + 1);
        // }
    });

    socket.on("clientSend_ready", function (_name, id, isReady) {
        socket.broadcast.to(_name).emit("serverSend_state", { val: isReady });
        console.log(id + " ready");
    });

    socket.on("clientSend_enough", function (_name) {
        const index = room.findIndex(val => val.name == _name);
        if (index != -1) {
            io.sockets.in(_name).emit("sever_send_enough", { valo: 1 });
            console.log("sent ennough " + _name);
            console.log("size: " + io.nsps['/'].adapter.rooms[_name].length);
        }
    });
    socket.on("clientSend_playing",function(_name){
        io.sockets.in(_name).emit("severSend_playing");
    });

    socket.on("client_winner",function(_name,_id){
        io.sockets.in(_name).emit("serverSend_winner",{id : _id});
    });

    socket.on("out_room", function (_name) {
        console.log("device out room");
        const index = room.findIndex(val => val.name == _name);
        if (index != -1) {
            room[index].size -= 1;
            socket.leave(_name);
            if (room[index].size == 0) {
                room_queue.push(room[index]);
                room.splice(index, 1);
            }
            else {
                socket.to(_name).emit("other_user_out");
                io.sockets.in(_name).emit("sever_send_enough", { valo: -1 });
                //socket.to(_name).emit("sever_send_id", { id: 1 });
            }
            io.sockets.emit("serverSend_list_room", { list: room });
        }
        socket.un = -1;

    });
    socket.on("disconnect",function(){
        console.log("device is disconnected " + socket.un);
        if(socket.un != -1){
            const index = room.findIndex(val => val.name == socket.un);
            console.log("index: " + index);
            
            room[index].size -= 1;
            socket.leave(socket.un);
            if (room[index].size == 0) {
                room_queue.push(room[index]);
                room.splice(index, 1);
            }
            else {
                socket.to(socket.un).emit("other_user_out");
                io.sockets.in(socket.un).emit("sever_send_enough", { valo: -1 });
                //socket.to(_name).emit("sever_send_id", { id: 1 });
            }
            io.sockets.emit("serverSend_list_room", { list: room });
        }
    });
});                                                                                                                                                                                                                                                                                 