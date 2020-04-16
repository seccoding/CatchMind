/**
 * 
 */
var sock = undefined;
var roomId = undefined;
var member = undefined;

var send = undefined;
var sendDraw = undefined;
var clearCanvas = undefined;
var forceClose = undefined;

$().ready(function() {
	// WS 연결
	sock = new SockJS("/myHandler");

	// WS 연결 성공!
	sock.onopen = function() {
		
		// send : connection으로 message를 전달
		// connection이 맺어진 후 가입(JOIN) 메시지를 전달
		send("JOIN", "");

		// onmessage : message를 받았을 때의 callback
		sock.onmessage = function(e) {
			var content = JSON.parse(e.data);

			if (content.messageType == "CHAT"
					|| content.messageType == "JOIN") {
				
				if ( content.messageType == "JOIN" ) {
					joinMember(content);
				}
				
				receiveChat(content);
			} else if (content.messageType == "START-DRAW") {
				receiveStartDraw(content.point);
			} else if (content.messageType == "DRAW") {
				receiveDraw(content.point);
			} else if (content.messageType == "CLEAR") {
				receiveClearCanvas();
			} else if ( content.messageType == "GAME" ) {
				execCommand(content);
			} else if ( content.messageType == "PASS" ) {
				receiveChat(content);
				execPass(content);
				receiveClearCanvas();
			} else if ( content.messageType == "QUIT" ) {
				quitMember(content);
			}
		}
	}

	/**
	 * 일반 메시지 전송
	 */
	send = function(type, message) {

		var msg = {
			chatRoomId : roomId,
			messageType : type,
			writer : member,
			message : message
		};

		sock.send(JSON.stringify(msg));
	}

	/**
	 * 캔버스 그림 좌표 전송
	 */
	sendDraw = function(type, left, top, color, size) {

		var point = {
			left : left,
			top : top,
			color : color,
			size : size
		};

		var msg = {
			chatRoomId : roomId,
			messageType : type,
			writer : member,
			point : point
		};

		sock.send(JSON.stringify(msg));
	}

	/**
	 * 캔버스 전체 지우기 전송
	 */
	sendClearCanvas = function() {
		var msg = {
			chatRoomId : roomId,
			messageType : "CLEAR"
		};

		sock.send(JSON.stringify(msg));
	}
	
});

window.onbeforeunload = function() {
	if ( sock != undefined && sock != null ) {
		sock.close();
	}
}
