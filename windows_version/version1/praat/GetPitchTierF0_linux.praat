Text reading preferences: "UTF-8"
Text writing preferences: "UTF-8"

dirPath$="/home/APP/workspace_praat/GetPitchTierF0/data/"
Create Strings as file list... list 'dirPath$'/*.wav
fileNum= Get number of strings
for ifile to fileNum
select Strings list
fileName$ = Get string... ifile
newFileName$ = fileName$
Read from file... 'dirPath$'/'fileName$'
newdirPath$ = dirPath$
while index(newdirPath$, ":")> 0
a = index(newdirPath$, ":") - 1
lb$ = left$(newdirPath$, a)
b = length(newdirPath$) - a -1
rb$ = right$(newdirPath$, b)
newdirPath$ = lb$ + "_"+ rb$
endwhile

while index(newdirPath$, "/")> 0
a = index(newdirPath$, "/") - 1
lb$ = left$(newdirPath$, a)
b = length(newdirPath$) - a -1
rb$ = right$(newdirPath$, b)
newdirPath$ = lb$ + "_"+ rb$
endwhile

newFileName$ = newFileName$ - ".wav"
To Pitch... 0.01 50 500
Down to PitchTier
Write to text file... 'dirPath$'/'newFileName$'.PitchTier

endfor
select Strings list
Remove

form Get_Pitch_from
	positive the_Index_of_Referenced_Tier_in_TextGrid 3
	positive the_Number_of_Pitch_Points_in_an_Interval 10
endform

Create Strings as file list... list 'dirPath$'/*.textgrid
fileNum = Get number of strings
tierNum = 3
pointNum = 10
writeInfoLine: pointNum
writeInfoLine: tierNum
for ifile to fileNum
	select Strings list
	fileName$=Get string... ifile
	newFileName$=fileName$-".textgrid"
	textGridFileName$=newFileName$+".textgrid"
	textGridFileName$=dirPath$+textGridFileName$
	
	pitchTierFileName$=newFileName$+".PitchTier"
	writeInfoLine: pitchTierFileName$
	pitchTierFileName$=dirPath$+pitchTierFileName$
	saveFileName$=newFileName$+"_Pitch.txt"
	saveFileName$=dirPath$+saveFileName$
	
	filedelete 'saveFileName$'
	fileappend 'saveFileName$' 文件名
	fileappend 'saveFileName$' ,
	fileappend 'saveFileName$' 标注内容
	fileappend 'saveFileName$' ,
	fileappend 'saveFileName$' 起点时间
	fileappend 'saveFileName$' ,
	fileappend 'saveFileName$' 末点时间
	fileappend 'saveFileName$' ,
	fileappend 'saveFileName$' 时长
	fileappend 'saveFileName$' ,
	
	Read from file... 'pitchTierFileName$'
	select PitchTier 'newFileName$'
	pitchPointNum=Get number of points
	for pitchNum from 1 to pitchPointNum
		pitchTime'pitchNum'=Get time from index... 'pitchNum'
	endfor

	pitchNum=pitchPointNum+1
	pitchTime'pitchNum'=Get finishing time
	Read from file... 'textGridFileName$'
	select TextGrid 'newFileName$'

	writeInfoLine:newFileName$

	dd=Is interval tier... 'tierNum'
	if dd=1
		intervalNum=Get number of intervals... 'tierNum'
	endif
	if dd=0
		intervalNum=Get number of points... 'tierNum'
	endif
	beginTime0=0
	endTime0=0
	for interNum from 1 to intervalNum
		if dd=1
			labeName'interNum'$=Get label of interval... 'tierNum' 'interNum'
			beginTime'interNum'=Get starting point... 'tierNum' 'interNum'
			endTime'interNum'=Get end point... 'tierNum' 'interNum'
			selLength'interNum'=endTime'interNum'-beginTime'interNum'
		endif
		if dd=0
			labeName'interNum'$=Get label of point... 'tierNum' 'interNum'
			xuhao='interNum'-1
			beginTime'interNum'=endTime'xuhao'
			endTime'interNum'=Get time of point... 'tierNum' 'interNum'
			selLength'interNum'=endTime'interNum'-beginTime'interNum'
		endif
		startPoint=1
		pitchStartTime=0
		pitchEndTime=0
		startSel=0
		sT=beginTime'interNum'
		eT=endTime'interNum'
		pitchStartTime'interNum'=0
		pitchEndTime'interNum'=0
		for pitchNum from startPoint to pitchPointNum
			if pitchTime'pitchNum'>=sT and pitchTime'pitchNum'<=eT
				startSel=startSel+1
				if startSel=1
					pitchStartTime'interNum'=pitchTime'pitchNum'
				endif
				pitchNextNum=pitchNum+1
				pitchNextTime=pitchTime'pitchNextNum'
				if pitchNextTime>eT
					pitchEndTime'interNum'=pitchTime'pitchNum'
				endif
				startPoint=pitchNum-1
			endif
		endfor
	endfor

select PitchTier 'newFileName$'
dianNum=pointNum
for interNum from 1 to intervalNum
	if interNum=1
		fileappend 'saveFileName$' 基频起点
		fileappend 'saveFileName$' ,
		fileappend 'saveFileName$' 基频末点
		fileappend 'saveFileName$' ,
		for dian from 1 to dianNum
			fileappend 'saveFileName$' 点
			fileappend 'saveFileName$' 'dian'
			fileappend 'saveFileName$' 基频值
			fileappend 'saveFileName$' ,
		endfor
		fileappend 'saveFileName$' 'newline$'
	endif

	lN$=labeName'interNum'$
	fileappend 'saveFileName$' 'lN$'
	fileappend 'saveFileName$' ,
	pST=pitchStartTime'interNum'
	pET=pitchEndTime'interNum'
	printline 'pST''pET'
	if pST>0 and pET>0
		a=pST
		b=pET
		c=('b'-'a')/('dianNum'-1)
		select PitchTier 'newFileName$'
		tempposition='a'
		for pitchdata from 1 to 'dianNum'
			pitchTemp=Get value at time... 'tempposition'
			fileappend 'saveFileName$' 'pitchTemp'
			fileappend 'saveFileName$' ,
			tempposition='tempposition'+'c'
		endfor
	endif
	fileappend 'saveFileName$' 'newline$'
endfor
endif

select TextGrid 'newFileName$'
Remove
select PitchTier 'newFileName$'
Remove
endfor
select Strings list
Remove
exit
