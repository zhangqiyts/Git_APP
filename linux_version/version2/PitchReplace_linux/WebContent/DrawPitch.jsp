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
	<canvas id="pinyinCanvas" width="400" height="90" style="border:1px solid #ffffff;"></canvas>
	<div><img src="http://172.26.51.149:8080/PitchReplace/data/${L2_ID}/${L2_ID}.png" width="400" height="100"/></div>
 	<canvas id="myCanvas" width="400" height="300" style="border:1px solid #d3d3d3;"></canvas>
 	<br/>
    <br/>
	<audio id = "player1" src="http://172.26.51.149:8080/PitchReplace/data/${L2_ID}/${Chinese_ID}.wav"></audio> 
	<audio id = "player2" src="http://172.26.51.149:8080/PitchReplace/data/${L2_ID}/${L2_ID}.wav"></audio>
	<audio id = "player3" src="http://172.26.51.149:8080/PitchReplace/data/${L2_ID}/${L2_ID}_PitchReplace.wav"></audio>
	<button onclick="execute_mother_tongue()" style="width:130px;height:60px;margin-right:16px;font-size:20px;">standard pronunciation</button> 
	&nbsp;&nbsp;<button onclick="execute_second_language()" style="width:130px;height:60px;margin-right:16px;font-size:20px;">my pronunciation</button> 
	&nbsp;&nbsp;<button onclick="exeute_together()" style="width:130px;height:60px;margin-right:16px;font-size:20px;">pitch processing</button>
</center> 
</body>
	<script>   
	    showPinYin();
		function showPinYin(){
			var pinyinCanvas = document.getElementById("pinyinCanvas");
			var pinyinCtx = pinyinCanvas.getContext("2d");
			pinyinCtx.font="50px Georgia";
			
			var PinYin = '${PinYin}';
			var ToneResultFlag = '${ToneResultFlag}';
	        var PinYin_array = new Array();
	        PinYin_array = PinYin.split(" ");
	    	var ToneResultFlag_Array = new Array();
	    	ToneResultFlag_Array = ToneResultFlag.split(" ");
	    	
	    	if(PinYin_array.length == ToneResultFlag_Array.length){
	    		
	    		if(ToneResultFlag_Array.length == 1){
	    			if (ToneResultFlag_Array[0] == "0"){
						pinyinCtx.fillStyle= "#FF0000"
						pinyinCtx.fillText(PinYin_array[0],145,60);

					}else {
						pinyinCtx.fillStyle="#00FF00";
						pinyinCtx.fillText(PinYin_array[0],145,60)
					}
	    		}else{
	    			var width_start = 75;
					for(var i = 0; i < ToneResultFlag_Array.length && ToneResultFlag_Array[i].length != 0; i++){
						width_start = width_start + 125 * i;
						if (ToneResultFlag_Array[i] == "0"){
							pinyinCtx.fillStyle= "#FF0000"
							pinyinCtx.fillText(PinYin_array[i],width_start,60);

						}else {
							pinyinCtx.fillStyle="#00FF00";
							pinyinCtx.fillText(PinYin_array[i],width_start,60)
						}
					}
	    		}
	    		
			}
		}
    	var myCanvas;
        var pitchCtx;
        var convasWidth=399;
        var convasHeight=299; 
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
	     
	     function execute_mother_tongue(){
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
	        	 draw_line(CH_Time_Array, CH_F0_Array, "myCanvas", is_clear, '#00FF00');
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
	        	 draw_line(L2_Time_Array, L2_F0_Array, "myCanvas", is_clear, '#0000ff');
	        	 is_clear = false;
	         }
	         document.getElementById('player2').play();
	         
	     }
	     
	     function draw_togethor()
	     {
			var ToneResultFlag = '${ToneResultFlag}';
		    var ToneResultFlag_Array = new Array();
		    ToneResultFlag_Array = ToneResultFlag.split(" ");
		    
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
	        	draw_line(CH_Time_Array, CH_F0_Array, "myCanvas", is_clear, '#00FF00');
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
	        	if(ToneResultFlag_Array[i] == "0"){
	        		draw_line(L2_Time_Array, L2_F0_Array, "myCanvas", is_clear, '#FF0000');
	        	}else {
	        		draw_line(L2_Time_Array, L2_F0_Array, "myCanvas", is_clear, '#ffffff'); //正确就不画
	        	}
	        }          
	    }
	     
	    function exeute_together(){
	    	draw_togethor();
	    	document.getElementById('player3').play();
	    } 
	    draw_togethor();
    </script>
</html>