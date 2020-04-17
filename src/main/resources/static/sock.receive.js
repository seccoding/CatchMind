/**
 * 
 */
var receiveChat = function(content) {
	var chatBox = $(".chat_box");
	
	var message = "<div>" + content.message + "</div>";
	message = message.replaceAll("↵", "<br/>").replaceAll("\n", "<br/>");
	
	var isMe = content.fromMe == "me";
	if ( !isMe ) {
		message = '<div style="width:50px; margin-right: 3px;">' + content.writer + '</div>' + message;
	}
	
	chatBox.append('<div class=' + content.fromMe + '>' + message + '</div>');
	chatBox.scrollTop(99999999999999999999999);
}

var canvas = undefined;
var ctx = undefined;

// 지우기
var receiveClearCanvas = function() {
	if ( ctx == undefined ) {
		canvas = $("#canvas");
		ctx = canvas[0].getContext("2d");
	}
	
	ctx.fillStyle = "#FFFFFF";
	ctx.fillRect(0, 0, 500, 500);
}

// 그리기 시작 (수신)
var receiveStartDraw = function(point) {
	canvas = $("#canvas");
	ctx = canvas[0].getContext("2d");

	ctx.strokeStyle = point.color;
	ctx.lineWidth = point.size;
	ctx.beginPath();

	ctx.moveTo(point.left, point.top);
}

// 그리기 (수신)
var receiveDraw = function(point) {
	ctx.lineTo(point.left, point.top);
	ctx.stroke();
}