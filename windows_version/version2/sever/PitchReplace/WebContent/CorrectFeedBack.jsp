<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
<meta charset="utf-8"> 
<title></title> 
</head>
<body>

<div id="container" style="width:500px">
<div id="footer" style="text-align:center;color:#009933;font-size:50px;">
nǐ hǎo</div>
<div><img src="/images/pulpit.jpg" width="500" height="100" />
</div>
<audio id="music2" src="mov_bbb.mp4" >你的浏览器不支持audio标签。</audio>    
<a href="javascript:playWav();"><img src="/images/pulpit.jpg" style="text-align:center;" width="48" height="50" id="music_btn2" border="0"></a>
</div>
</body>
<script>
	function playWav() {    
		var music = document.getElementById('music2');    
		var music_btn = document.getElementById('music_btn2');    	 
		music.play(); 
		if (music_btn.src.indexOf('/images/pulpit.jpg')>0) {
       music_btn.src= "mov_bbb.ogg";
		}
	} 
</script>
</html>