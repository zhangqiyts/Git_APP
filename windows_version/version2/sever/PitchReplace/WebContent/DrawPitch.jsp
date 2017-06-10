<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta content="width=device-width, user-scalable=no" name="viewport" />
    <title>音高替换</title>
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
<body onload="initPitch();" style="background:rgb(240,240,220);">
<center>
	<table>
		<tr>      
    		<td><img src="http://192.168.1.103:8080/PitchReplace/data/${L2_ID}/${L2_ID}.png" width="458" height="150" /><br/></td>
    	</tr>
    </table>
    
 	<canvas id="myCanvas" width="400" height="250" style="border:2px solid #000000;"></canvas>
 	<br/>
    <br/>
    <br/>
	<audio id = "player1" src="http://192.168.1.103:8080/PitchReplace/data/${L2_ID}/${Chinese_ID}.wav"></audio> 
	<audio id = "player2" src="http://192.168.1.103:8080/PitchReplace/data/${L2_ID}/${L2_ID}.wav"></audio>
	<audio id = "player3" src="http://192.168.1.103:8080/PitchReplace/data/${L2_ID}/${L2_ID}_PitchReplace.wav"></audio>
	<button onclick="execute_mother_tongue()" style="width:170px;height:80px;margin-right:16px;font-size:25px;">standard pronunciation</button> 
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button onclick="execute_second_language()" style="width:170px;height:80px;margin-right:16px;font-size:25px;">my pronunciation</button> 
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button onclick="exeute_together()" style="width:170px;height:80px;margin-right:16px;font-size:25px;">pitch processing</button>
</center> 
</body>
	<script>          		
        var myCanvas;
        var pitchCtx;
        var convasWidth=399;
        var convasHeight=249;            

        function initPitch() {
            var curLeft=document.getElementById("myCanvas").offsetLeft;
            var curTop=document.getElementById("myCanvas").offsetTop;
            var touchzone = document.getElementById("myCanvas");
            pitchCtx = touchzone.getContext("2d");
            PitchKongJian();
        }
        function PitchKongJian() {
            pitchCtx.strokeStyle ='rgb(200,200,200)';
            pitchCtx.beginPath();
            
            for (var k=0; k<=5;k++) {
                pitchCtx.moveTo(20,k*convasHeight/5);
                pitchCtx.lineTo(395,k*convasHeight/5);
				pitchCtx.lineWidth = 0.5;
				if (k==0) {
					pitchCtx.fillText(k,5,convasHeight-k*convasHeight/5);
                }else if(k<5) {
					pitchCtx.fillText(k,5,convasHeight-k*convasHeight/5+4);
				}else{
					pitchCtx.fillText(k,5,convasHeight-k*convasHeight/5+8);
				}
                pitchCtx.stroke();
            }
            pitchCtx.closePath();
        }
        
	    function draw_line(Time_Array, F0_Array, canvas_name, is_clear, color) {
	    	initPitch();
	    	var canvas = document.getElementById(canvas_name);
	    	var hei = canvas.height;
	        var context = canvas.getContext('2d');
	        var scale_time = 300;
	        var scale_f0 = 50;
	        if (is_clear){
	        	context.clearRect(0, 0, canvas.width, canvas.height);
	        }
	        context.lineWidth =5;
	        context.beginPath();      
	        var time_s = Time_Array[0] * scale_time;
	        var f0_s = hei - F0_Array[0]*scale_f0;
	        context.moveTo(time_s, f0_s);
	         
	     	for(var i = 1; i < Time_Array.length; i++){
	     		time = Time_Array[i]* scale_time;
	     		f0 = hei - F0_Array[i]*scale_f0;
		        context.lineTo(time, f0);
	     	}
	        context.strokeStyle = color;
	        context.stroke();    
	     }
	     
	     function execute_mother_tongue()
	     {
	    	 var CH_Time = '${CH_Time}';
	         var CH_F0 = '${CH_F0}';
	         var CH_Time_Syllable = new Array();
	         CH_Time_Syllable = CH_Time.split(";");
	         var CH_F0_Syllable = new Array();
	         CH_F0_Syllable = CH_F0.split(";");
	         var is_clear = true;
	         for(var i = 0; i < CH_Time_Syllable.length && CH_Time_Syllable[i].length != 0; i++){
	        	 var CH_Time_Array = new Array();
	        	 CH_Time_Array = CH_Time_Syllable[i].split(" ");
	        	 var CH_F0_Array = new Array();
	        	 CH_F0_Array = CH_F0_Syllable[i].split(" ");
	        	 draw_line(CH_Time_Array, CH_F0_Array, "myCanvas", is_clear, '#32CD32');
	        	 is_clear = false;
	         }
	         document.getElementById('player1').play();
	     }
	     
	
	     function execute_second_language()
	     {
	         var L2_Time = '${L2_Time}';
	         var L2_F0 = '${L2_F0}';
	         var L2_Time_Syllable = new Array();
	         L2_Time_Syllable = L2_Time.split(";");
	         var L2_F0_Syllable = new Array();
	         L2_F0_Syllable = L2_F0.split(";");
	         var is_clear = true;
	         for(var i = 0; i < L2_Time_Syllable.length && L2_Time_Syllable[i].length != 0; i++){
	        	 var L2_Time_Array = new Array();
	        	 L2_Time_Array = L2_Time_Syllable[i].split(" ");
	        	 var L2_F0_Array = new Array();
	        	 L2_F0_Array = L2_F0_Syllable[i].split(" "); 
	        	 draw_line(L2_Time_Array, L2_F0_Array, "myCanvas", is_clear, '#FF0000');
	        	 is_clear = false;
	         }
	         document.getElementById('player2').play();
	         
	     }
	     
	     function draw_togethor()
	     {
	    	 var is_clear;
	         var CH_Time = '${CH_Time}';
	         var CH_F0 = '${CH_F0}';
	         var CH_Time_Syllable=new Array();
	         CH_Time_Syllable = CH_Time.split(";");
	         var CH_F0_Syllable = new Array();
	         CH_F0_Syllable = CH_F0.split(";");
	         
	         for(var i = 0; i < CH_Time_Syllable.length && CH_Time_Syllable[i].length != 0; i++){
	        	 var CH_Time_Array = new Array();
	        	 CH_Time_Array = CH_Time_Syllable[i].split(" ");
	        	 var CH_F0_Array = new Array();
	        	 CH_F0_Array = CH_F0_Syllable[i].split(" ");
	        	 is_clear = false;
	        	 draw_line(CH_Time_Array, CH_F0_Array, "myCanvas", is_clear, '#32CD32');
	         }
	         
	         var L2_Time = '${L2_Time}';
	         var L2_F0 = '${L2_F0}';
	         var L2_Time_Syllable = new Array();
	         L2_Time_Syllable = L2_Time.split(";");
	         var L2_F0_Syllable = new Array();
	         L2_F0_Syllable = L2_F0.split(";");
	         
	         for(var i = 0; i < L2_Time_Syllable.length && L2_Time_Syllable[i].length != 0; i++){
	        	 var L2_Time_Array = new Array();
	        	 L2_Time_Array = L2_Time_Syllable[i].split(" ");
	        	 var L2_F0_Array = new Array();
	        	 L2_F0_Array = L2_F0_Syllable[i].split(" ");
	        	 is_clear = false;
	        	 draw_line(L2_Time_Array, L2_F0_Array, "myCanvas", is_clear, '#FF0000');
	         }  
	        
	     }
	     
	     function exeute_together(){
	    	 draw_togethor();
	    	 document.getElementById('player3').play();
	     }   
	     draw_togethor();
    </script>
</html>