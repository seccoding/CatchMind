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
		isControlMe = false;
		alert("게임이 종료되었습니다!");
	}
	else if ( content.command == "NEXT_TURN" ) {
		isControlMe = true;
		$("#quiz").val(content.quiz);
		$("#quiz").show();
		$("#clear").click();
		gameTimer(content.timer);
	}
	else if ( content.command == "NOT_MY_TURN" ) {
		isControlMe = false;
		$("#quiz").hide();
		gameTimer(content.timer);
	}
}

var gameTimer = function(seconds) {
	$("#timer").show();
	
	var seconds = seconds;

    var min = 0;
    var sec = 0;
    var interval = setInterval(function() {
        seconds -= 1;
        min = parseInt(seconds / 60);
        sec = seconds % 60;

        $("#min").text(min);
        $("#sec").text(sec);

        if ( min == 0 && seconds == 0 ) {
            if ( isControlMe == true ) {
            	isControlMe = false;
            	nextTurn();
            }
            clearInterval(interval);
        }

    }, 1000);
}