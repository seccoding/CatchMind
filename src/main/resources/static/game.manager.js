var isControlMe = false;

var startGame = function() {
	var msg = {
		chatRoomId: roomId,
		messageType: "GAME",
		command: "START"
	};

	sock.send(JSON.stringify(msg));
};

var endGame = function() {
	var msg = {
		chatRoomId: roomId,
		messageType: "GAME",
		command: "END"
	};

	sock.send(JSON.stringify(msg));
};

var nextTurn = function() {
	var msg = {
		chatRoomId: roomId,
		messageType: "GAME",
		command: "NEXT_TURN"
	};

	sock.send(JSON.stringify(msg));
};

var execCommand = function(content) {
	if ( content.command == "START" ) {
		alert("게임이 시작되었습니다!");
	}
	else if ( content.command == "END" ) {
		alert("게임이 종료되었습니다!");
	}
	else if ( content.command == "NEXT_TURN" ) {
		isControlMe = true;
	}
	else if ( content.command == "NOT_MY_TURN" ) {
		isControlMe = false;
	}
}