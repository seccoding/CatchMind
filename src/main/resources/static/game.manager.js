var isControlMe = false;

var timer = undefined;

var members = undefined;

var replaceMember = function(content) {
	
	$(".members").remove();
	
	var memberList = $(".members_score");
	members = content.gamer;
	
	for ( var i in members ) {
		memberList.append('<tr class="members"><td>' + members[i].name + '</td><td>' + members[i].score + '</td></tr>');
	}
}

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

var execPass = function(content) {
	 
	if ( timer != undefined ) {
		clearInterval(timer);
		timer = undefined;
	}
	
	alert(content.writer + "님 정답!");
}

var quitMember = function(content) {
	
	for ( var i in members ) {
		if ( members[i].sessionId == content.sessionId ) {
			alert(members[i].name + content.message);
			return;
		}
	}
}

var gameTimer = function(time) {
	
	if ( timer != undefined ) {
		clearInterval(timer);
		timer = undefined;
	}
	
	$("#timer").show();
	
	var seconds = time;

    var min = parseInt(seconds / 60);
    var sec = seconds % 60;
    
    timer = setInterval(function() {
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
            clearInterval(timer);
        }

    }, 1000);
}