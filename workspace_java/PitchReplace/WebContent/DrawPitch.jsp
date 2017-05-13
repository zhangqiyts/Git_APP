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
            
        .buttons {
            font-size:16px;
            margin-top:16px;
            width:60px;
            height:40px;
            line-height:30px;
            margin-left:10px;
        } 
    </style>
</head>
<body>
<!-- <form action="Upload" method = "post" enctype = "multipart/form-data"> -->
<center>
	<table><tr>      
    	<td><img src="data/${L2_ID}/${Chinese_ID}.png" width="350" height="250" /><br/></td>
    </tr></table>
    
 	<canvas id="myCanvas" width="350" height="400" style="border:1px solid #d3d3d3;"></canvas>
 	<br/>
	<audio id = "player1" src="data/${L2_ID}/${Chinese_ID}.wav"></audio>
	<audio id = "player2" src="data/${L2_ID}/${L2_ID}.wav"></audio>
	<audio id = "player3" src="data/${L2_ID}/${L2_ID}_PitchReplace.wav"></audio>
	<button onclick="execute_mother_tongue()">母语</button> 
	<button onclick="execute_second_language()">二语</button> 
	<button onclick="exeute_together()">合成</button> 
</center> 
<!-- </form> -->
</body>
	<script>     
	     function draw_line(Time_Array, F0_Array, canvas_name, is_clear, color)
	     {
	    	 var canvas = document.getElementById(canvas_name);
	    	 var hei = canvas.height;
	         var context = canvas.getContext('2d');
	         var scale = 320;
	         
	         if (is_clear){
	             context.clearRect(0, 0, canvas.width, canvas.height);
	         }
	         context.lineWidth =3;
	         context.beginPath();      
	         var time_s = Time_Array[0] * scale;
	         var f0_s = hei - F0_Array[0];
	         context.moveTo(time_s, f0_s);
	         
	     	 for(var i = 1; i < Time_Array.length; i++){
	     		time = Time_Array[i]* scale;
	     		f0 = hei - F0_Array[i];
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
	         
	         for(var i = 0; i < CH_Time_Syllable.length && CH_Time_Syllable[i].length != 0; i++){
	        	 var CH_Time_Array = new Array();
	        	 CH_Time_Array = CH_Time_Syllable[i].split(" ");
	        	 var CH_F0_Array = new Array();
	        	 CH_F0_Array = CH_F0_Syllable[i].split(" ");
	        	 var is_clear = true;
	        	 draw_line(CH_Time_Array, CH_F0_Array, "myCanvas", is_clear, '#FF0000');
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
	         
	         for(var i = 0; i < L2_Time_Syllable.length && L2_Time_Syllable[i].length != 0; i++){
	        	 var L2_Time_Array = new Array();
	        	 L2_Time_Array = L2_Time_Syllable[i].split(" ");
	        	 var L2_F0_Array = new Array();
	        	 L2_F0_Array = L2_F0_Syllable[i].split(" ");
	        	 var is_clear = true;
	        	 draw_line(L2_Time_Array, L2_F0_Array, "myCanvas", is_clear, '#0000CD');
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
	        	 is_clear = true;
	        	 draw_line(CH_Time_Array, CH_F0_Array, "myCanvas", is_clear, '#FF0000');
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
	        	 draw_line(L2_Time_Array, L2_F0_Array, "myCanvas", is_clear, '#0000CD');
	         }
	         document.getElementById('player3').play();
	        
	     }
	     
	     function exeute_together()
	     {
	    	 draw_togethor();
	         document.getElementById('player3').play();
	     }   
	     draw_togethor();
    </script>
</html>