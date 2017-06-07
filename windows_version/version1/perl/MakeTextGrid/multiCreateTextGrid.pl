# create textgrid file 2013/12/8
use Encode;
$dir = $ARGV[0];
$path = "";
@file = split(/\//,$dir);
for($i = 0; $i < @file-1; $i++){
	$path = $path.$file[$i]."\\";
}
$path = $path.$file[@file-1];

open(htklexi,"D:\\home\\APP\\workspace_perl\\MakeTextGrid\\htklexnospnosil")||die;
while(<htklexi>){
	chomp;
	@htkline=split(/\s+/,$_);
	shift @htkline;
	shift @htkline;
	$keyshtk=join(" ",@htkline);
	#print "$keyshtk\n";
	$htkhash{$keyshtk}=1;
}
close(htklexi);
opendir(dir,"$path\\alignMlf");
@alignMlfArray=readdir(dir);
close(dir);

for($alignMlfOrder=2;$alignMlfOrder<@alignMlfArray;$alignMlfOrder++)
{
	my @totalArray=();
	my $count=0;
	open(in,"$path\\alignMlf\\$alignMlfArray[$alignMlfOrder]");
	while($line=<in>)
	{
		chomp($line);
		if($line=~/^\"\'\*\'\/(\S+)\.lab/)
		{
			$fileName=$1;
			$fileName=$fileName.".textgrid";
			$fileNum++;#统计需处理的音频个数
		}
		if($line=~/([\d]+)\s([\d]+)\s([a-zA-Z]+)/)#3个一组为一个phone
		{
			push(@phoneLineArray,$1/1e7);
			push(@phoneLineArray,$2/1e7);
			push(@phoneLineArray,$3);	
		}
		if($line=~/^\./)
		{
			my %hash=();#key为fileName,value为上边的@phoneLineArray的引用
			my @tmpPLA=@phoneLineArray;
			my @wordPLA;
			for($wordI=2;$wordI<@tmpPLA;)
			{
				$w1=$tmpPLA[$wordI];
				$w2=$tmpPLA[$wordI+3];
				$w3=$tmpPLA[$wordI+6];
				$isword=$w1." ".$w2." ".$w3;
				# print "$isword\n";
				$isword1=$w1." ".$w2;
				#print "$isword1\n";
				if(defined $htkhash{$isword})
				{
					$WORD=$w1.$w2.$w3;
					#print "3 $WORD\n";
					$bd1=$tmpPLA[$wordI-2];
					$bd2=$tmpPLA[$wordI+5];
					
					push(@wordPLA,$bd1);
					push(@wordPLA,$bd2);
					push(@wordPLA,$WORD);
					$wordI=$wordI+9;
				}elsif(defined $htkhash{$isword1})
				{
					$WORD=$w1.$w2;
					#print "2 $WORD\n";
					$bd1=$tmpPLA[$wordI-2];
					$bd2=$tmpPLA[$wordI+2];
					
					push(@wordPLA,$bd1);
					push(@wordPLA,$bd2);
					push(@wordPLA,$WORD);
					$wordI=$wordI+6;
				}else
				{
					$WORD=$w1;
					#print "1 $WORD\n";
					$bd1=$tmpPLA[$wordI-2];
					$bd2=$tmpPLA[$wordI-1];
					
					push(@wordPLA,$bd1);
					push(@wordPLA,$bd2);
					push(@wordPLA,$WORD);
					$wordI=$wordI+3;
				}
			}
			$hash{$fileName}=\@tmpPLA;
			$wordhash{$fileName}=\@wordPLA;
			push(@totalArray,\%hash);
			@phoneLineArray=();#做完后@phoneLineArray置空
		}
	}
	close(in);
	$totalArrayLen=@totalArray;
	# print "\t$totalArrayLen\n";
		
	for ($fileOrder=0;$fileOrder<@totalArray;$fileOrder++)
	{
		my %fileHash=%{$totalArray[$fileOrder]};
			
		foreach $waveName(sort keys %fileHash)#fileHash只有一个元素（文件名和对应的phone数组）
		{
			#print "waveName:".$waveName."\n";
			my @phoneLineArray=@{$fileHash{$waveName}};
			my @wordbdarray=@{$wordhash{$waveName}};
			my $arrayLen=@phoneLineArray;#数组长度，3个为一组表示一个phone
			$xmin=$phoneLineArray[0];
			$xmax=$phoneLineArray[$arrayLen-2];
			$pywavname=$waveName;
			$pywavname=~s/\.text//;
			$firstSilEnd=$phoneLineArray[1];
			$seconfSilBegin=$phoneLineArray[$arrayLen-3];
	#################文件头			
			open(out,">$path\\data\\$waveName");
			print out "File type = \"ooTextFile\"\n";
			print out "Object class = \"TextGrid\"\n";
			print out "\n";
			print out "xmin = $xmin\n";
			print out "xmax = $xmax\n";
			print out "tiers? <exists>\n";
			print out "size = 3 \n";	#总层数设置处
			print out "item []:\n";

	#######第I层：TPY层#####################################
			
			$l=@wordbdarray/3;
			# print "@wordbdarray\n";
			# $tpyStart=$phoneLineArray[3];
			# $tpyEnd=$phoneLineArray[$arrayLen-5];
			print out "    item [1]:\n";
			print out "        class = \"TextTier\"\n";
			print out "        name = \"PT\"\n";
			print out "        xmin = $xmin\n";
			print out "        xmax = $xmax\n";
			print out "        points: size = 0\n";
			
			print out "    item [2]:\n";
			print out "        class = \"IntervalTier\"\n";
			print out "        name = \"PY\"\n";
			print out "        xmin = $xmin\n";
			print out "        xmax = $xmax\n";
			print out "        intervals: size = $l\n";#不同音节需更改部分

			for($i=0;$i<$l;$i++)
			{
				$num=$i+1;
				$index=$num*3;
				print out "        intervals [$num]:\n";
				print out "            xmin = $wordbdarray[$index-3]\n";
				print out "            xmax = $wordbdarray[$index-2]\n";
				print out "            text = \"$wordbdarray[$index-1]\"\n";
			}
				
	#########第2层：SY层#####################
			my $phoneNum=$arrayLen/3;
			print out "    item [3]:\n";
			print out "        class = \"IntervalTier\"\n";
			print out "        name = \"SY\"\n";
			print out "        xmin = $xmin\n";
			print out "        xmax = $xmax\n";
			print out "        intervals: size = $phoneNum\n";
			for($j=0;$j<$phoneNum;$j++)
			{
				my $phoneGroup=$j*3;
				my $intervalsNum=$j+1;
				my $xmin=$phoneLineArray[$phoneGroup];
				my $xmax=$phoneLineArray[$phoneGroup+1];
				my $text=$phoneLineArray[$phoneGroup+2];
				print out "        intervals [$intervalsNum]:\n";
				print out "            xmin = $xmin\n";
				print out "            xmax = $xmax\n";
				print out "            text = \"$text\"\n";
			}
		}
	}
}