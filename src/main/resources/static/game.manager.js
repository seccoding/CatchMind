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
		gameTimer(10);
	}
	else if ( content.command == "END" ) {
		alert("게임이 종료되었습니다!");
	}
	else if ( content.command == "NEXT_TURN" ) {
		isControlMe = true;
		$("#startGame").show();
		$("#quiz").val(content.quiz);
		$("#quiz").show();
		$("#clear").click();
		
	}
	else if ( content.command == "NOT_MY_TURN" ) {
		isControlMe = false;
		$("#startGame").hide();
		$("#quiz").hide();
		
//		gameTimer(content.timer);
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

        if ( seconds == 0 ) {
            clearInterval(interval);
            endGame();
        }

    }, 1000);
}