<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta content="width=device-width, user-scalable=no" name="viewport" />
    <title>检测结果</title>
	<style type="text/css">
	    body {
        	margin:0px;
            padding:0px;
			overflow-x:hidden;
			overflow-y:auto;
            font-size:16px;
            background:white;
	    }   
    </style>
</head>
<body>
	<center>
	<div id="container" style="width:400px">
		<div id="footer" style="text-align:center;color:#00FF00;font-size:50px;">${PinYin}</div>
		<div><img src="http://172.26.50.134:8080/PitchReplace/data/${L2_ID}/${L2_ID}.png" width="400" height="100"/></div>
		<canvas id="Stasrt_Canvas" width="400" height="300" style="border:1px solid #d3d3d3;"></canvas>
		<audio id = "player" src="http://172.26.50.134:8080/PitchReplace/data/${L2_ID}/${L2_ID}.wav"></audio>
		<br/>
	    <br/>
		<button onclick="playWav()" style="width:130px;height:60px;font-size:20px;">my pronunciation</button>
	</div>
	</center> 
</body>
<script>
	function playWav() {    
		document.getElementById('player').play();
	}
	
	function drawStar(cx, cy, strokeStyle, fileStyle) {
		spikes = 5;
		outerRadius = 25;
		innerRadius = 10;
	    var rot = Math.PI / 2 * 3;
	    var x = cx;
	    var y = cy;
	    var step = Math.PI / spikes;
	    var canvas = document.getElementById("Stasrt_Canvas");
	    var ctx = canvas.getContext("2d");
	    ctx.strokeSyle = "#000";
	    ctx.beginPath();
	    ctx.moveTo(cx, cy - outerRadius)
	    for (i = 0; i < spikes; i++) {
	        x = cx + Math.cos(rot) * outerRadius;
	        y = cy + Math.sin(rot) * outerRadius;
	        ctx.lineTo(x, y)
	        rot += step

	        x = cx + Math.cos(rot) * innerRadius;
	        y = cy + Math.sin(rot) * innerRadius;
	        ctx.lineTo(x, y)
	        rot += step
	    }
	    ctx.lineTo(cx, cy - outerRadius)
	    ctx.closePath();
	    ctx.lineWidth=5;
	    ctx.strokeStyle=strokeStyle;
	    ctx.stroke();
	    ctx.fillStyle=fileStyle;
	    ctx.fill();

	}

	var StarStyle = {
			  Empty: 1,
			  Half: 2,
			  Full: 3,
			};
	function drawPositionStar(pos, starStyle)
	{
		var fill_style = "white";
		var stroke_style = "white";
		switch(starStyle){
		case StarStyle.Empty:
			fill_style = "white";
			stroke_style = "#F6F6F4";
			break;
		case StarStyle.Half:
			fill_style = "white";
			stroke_style = "skyblue";
			break;
		case StarStyle.Full:
			fill_style = "yellow";
			stroke_style = "skyblue";
			break;
		default:
			alert("error!!!");
		}
			
		switch(pos) {
		case 1 :
			drawStar(60, 100, stroke_style, fill_style);
			break;
		case 2:
			drawStar(130, 100, stroke_style, fill_style);
			break;
		case 3:
			drawStar(200, 100, stroke_style, fill_style);
			break;
		case 4:
			drawStar(270, 100, stroke_style, fill_style);
			break;
		case 5:
			drawStar(340, 100, stroke_style, fill_style);
			break;
		default:
			alert(pos);	
		
		}
	}

	function drawScore(score)
	{
		full_star = parseInt(score / 2);
	  	half_star = score % 2;
	  	empty_star = parseInt((10 - score ) /2);
	  
	  	var pos = 1;
	  	for (; pos <= full_star; ++pos) {
		  	drawPositionStar(pos, StarStyle.Full);
	  	}
	  
	  	for(; pos <= full_star + half_star; pos++){
		  	drawPositionStar(pos, StarStyle.Half);
	  	}
	  
	  	for(; pos <= full_star + half_star + empty_star; pos++){
		  	drawPositionStar(pos, StarStyle.Empty);
	  	}
	}
	drawScore(${Score});
</script>
</html>