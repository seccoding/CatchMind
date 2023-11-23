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
		receiveChat({
			message: "게임이 시작되었습니다!"
			, writer: "System"
			, fromMe: "other"
		});
	}
	else if ( content.command == "END" ) {
		isControlMe = false;
		receiveChat({
			message: "게임이 종료되었습니다!"
			, writer: "System"
			, fromMe: "other"
		});
	}
	else if ( content.command == "NEXT_TURN" ) {
		isControlMe = true;
		
		$("#message").attr('disabled', true);
		$("#quiz").val(content.quiz);
		$("#quiz").show();
		$("#clear").click();
		$("#control-panel").show();
		
		var gamer = getMember(content.playerSessionId);
		$("#gamer").text("[" + gamer.name + "님이 문제 제출 중]");
		
		gameTimer(content.timer);
	}
	else if ( content.command == "NOT_MY_TURN" ) {
		isControlMe = false;
		
		$("#message").attr('disabled', false);
		$("#quiz").hide();
        $("#control-panel").hide();
        
		var gamer = getMember(content.playerSessionId);
		$("#gamer").text("[" + gamer.name + "님이 문제 제출 중]");
		
		gameTimer(content.timer);
	}
	else if ( content.command == "SHOW_START_BTN" ) {
		$("#startGame").show();
	}
	else if ( content.command == "HIDE_START_BTN" ) {
		$("#startGame").hide();
	}
}

var execPass = function(content) {
	 
	if ( timer != undefined ) {
		clearInterval(timer);
		timer = undefined;
	}
	
	receiveChat({
		message: content.writer + "님 정답! ["+content.message+"]"
		, writer: "System"
		, fromMe: "other"
	});
}

var quitMember = function(content) {
	
	var gamer = getMember(content.sessionId);
	
	receiveChat({
		message: gamer.name + content.message
		, writer: gamer.name
		, fromMe: "other"
	});
}

var getMember = function(sessionId) {
	for ( var i in members ) {
		if ( members[i].sessionId == sessionId ) {
			return members[i];
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

        sec = sec < 10 ? "0"+sec : sec;
        
        $("#min").text(min);
        $("#sec").text(sec);

        if ( min == 0 && parseInt(seconds) == 0 ) {
            if ( isControlMe == true ) {
            	isControlMe = false;
            	nextTurn();
            }
            clearInterval(timer);
        }

    }, 1000);
}