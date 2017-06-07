$path = $ARGV[0];
$mean = $ARGV[1];
$dev = $ARGV[2];
$Chinese_FileName = $ARGV[3];
$L2_FileName = $ARGV[4];
$pitchrange_min = $ARGV[5];
$pitchrange_max = $ARGV[6];

# $path = "/home/APP/PitchReplace/";
# $mean = "241.02";
# $dev = "46.31";
# $Chinese_FileName = "F2_ai1";
# $L2_FileName = "L2F_zhang_ai1_001";
# $pitchrange_min = "215.221";
# $pitchrange_max = "343.063";

print "path:$path\n";
print "mean:$mean\n";
print "dev:$dev\n";
print "Chinese_Filename:$Chinese_FileName\n";
print "L2_FileName:$L2_FileName\n";
print "Pitchmin:$pitchrange_min\n";
print "PitchMax:$pitchrange_max\n";

@L2_Info = split(/\_/,$L2_FileName);
$L2_sex = shift(@L2_Info);
$L2_Name = shift(@L2_Info);
$L2_syllableTone = shift(@L2_Info);
$L2_ID = $L2_sex."_".$L2_Name;

@Chinese_Info = split(/\_/,$Chinese_FileName);
$Chinese_ID = shift(@Chinese_Info);

$L2_path = $path."/".$L2_ID."/".$L2_FileName;

$file_data_dir = $L2_path."/data"; 
$file_Chinese_10f0_dir = $path."/".$Chinese_ID."/".$Chinese_FileName; 

$filedir_result = $L2_path."/Regression"; 
if(-d "$filedir_result"){
	print "The folder '$filedir_result' has already exists!\n";
}else{
	system("mkdir $filedir_result");
}

my @mean_dev;
my $timeDeleteOnset;
my $timeDeleteCoda;
my @final_time;

$file_Chinese_dir = $path."/".$Chinese_ID."/".$Chinese_FileName; 
opendir(Dir, $file_Chinese_dir) || die;
@Chinese_file = readdir(Dir);
for($i = 2; $i < @Chinese_file; $i++){
	if($Chinese_file[$i] =~ /(.+)\_TimeF0$/){
		open(Chinese_TimeF0, "$file_Chinese_dir/$Chinese_file[$i]")||die;
		open(Chinese_Regression_TimeF0, ">$filedir_result/$Chinese_file[$i]");
		while(<Chinese_TimeF0>){
			chomp;
			@chinese_timef0 = split(/\s+/,$_);
			if($chinese_timef0[1] ne "***"){
				$Chinese_Regression_F0 = sprintf("%.3f", $chinese_timef0[1]);
			}else{
				$Chinese_Regression_F0 = "***";
			}	
			print Chinese_Regression_TimeF0 "$chinese_timef0[0] $Chinese_Regression_F0\n";		
		}
		close(Chinese_Regression_TimeF0);
	}
	if($Chinese_file[$i] =~ /(.+)\_FiveDegree$/){
		system("cp $file_Chinese_dir/$Chinese_file[$i]  $filedir_result/$Chinese_file[$i]");
	}
	if($Chinese_file[$i] =~ /(.+)\_time.txt/){
		open(Chinese_Time, "$file_Chinese_dir/$Chinese_file[$i]")||die;
		$Chinese_Regression_Time = $1."_regression_time";
		open(Chinese_Regression_Time, ">$filedir_result/$Chinese_Regression_Time");
		while(<Chinese_Time>){
			chomp;
			@chinese_time = split(/\s+/,$_);
			foreach(@chinese_time){
				$regressiom_time = sprintf("%.3f", $_);
				print Chinese_Regression_Time "$regressiom_time ";
			}
			print Chinese_Regression_Time "\n";
		}
		close(Chinese_Time);
		close(Chinese_Regression_Time);
	}
}
	
opendir(dir,"$file_data_dir")||die;
@file_data = readdir(dir);
close(dir);
for($i = 2; $i < @file_data; $i++) {
	if($file_data[$i] =~ /(.+)\_time.txt/){
		$L2_Regression_Time = $1."_regression_time";
		open(L2_Regression_Time, ">$filedir_result/$L2_Regression_Time");
		$new_wav_name = $L2_FileName;
		$Final_DelOnsetCoda_TimeF0 = $L2_FileName."_TimeF0";
		$Final_DelOnsetCoda_TimeF0Zscore = $L2_FileName."_TimeF0Zscore";
		$Final_DelOnsetCoda_FiveDegree = $L2_FileName."_FiveDegree";
		open(Final_DelOnsetCoda_TimeF0, ">$file_data_dir/$Final_DelOnsetCoda_TimeF0");
		open(Final_DelOnsetCoda_TimeF0Zscore, ">$file_data_dir/$Final_DelOnsetCoda_TimeF0Zscore");
		open(L2_Regression_TimeF0, ">$filedir_result/$Final_DelOnsetCoda_TimeF0");
		open(L2_Regression_FiveDegree, ">$filedir_result/$Final_DelOnsetCoda_FiveDegree");
		open(L2_Regression_FiveDegree, ">$filedir_result/$Final_DelOnsetCoda_FiveDegree");
		open(final_time, "$file_data_dir/$file_data[$i]")||die;
		my $timeDeleteOnset;
		my $timeDeleteCoda;
		my $TimeF0File = $L2_FileName."_TimeF0.txt";
		@syllableTone = split(/X/,$L2_syllableTone);
		$row = 0;
		while(<final_time>){
			chomp;
			@final_time = split(/\s+/,$_);
			foreach(@final_time){
				$regressiom_time = sprintf("%.3f", $_);
				print L2_Regression_Time "$regressiom_time ";
			}
			print L2_Regression_Time "\n";
			
			if($syllableTone[$row] =~ /(.+)(\d+)/){
				$tone = $2;
			}
			
			if($tone eq '3'){
				$timeDeleteOnset = $final_time[3]+($final_time[4]-$final_time[3])*0.05; #弯头0.03
				$timeDeleteCoda = $final_time[4]-($final_time[4]-$final_time[3])*0.1; #降尾0.05
			}else{
				$timeDeleteOnset = $final_time[3]+($final_time[4]-$final_time[3])*0.05; #弯头0.03
				$timeDeleteCoda = $final_time[4]-($final_time[4]-$final_time[3])*0.1; #降尾0.05
			}
			
			print "$timeDeleteOnset $timeDeleteCoda\n";
			open(time_F0, "$file_data_dir/$TimeF0File");		
			@time_F0 = <time_F0>;
			my @time_f0_first = split(/\s+/, $time_F0[0]); #第一行
			if($time_f0_first[0] =~ /time=(.+)/){
				$time_first = $1;
			}
			my @time_f0_last = split(/\s+/, $time_F0[@time_F0-1]); #第一行
			if($time_f0_last[0] =~ /time=(.+)/){
				$time_last = $1;
			}
			
			if(($timeDeleteOnset <= $time_first)&&($timeDeleteCoda >= $time_last)){ #整个文件
				$timeDeleteOnset = $time_first;
				$timeDeleteCoda = $time_last;
				
			}elsif(($timeDeleteOnset <= $time_first)&&($timeDeleteCoda < $time_last)){
				$timeDeleteOnset = $time_first;
				for($k = @time_F0-1; $k >= 0; $k--){
					@time_f0_now = split(/\s+/, $time_F0[$k]); #当前行
					if($time_f0_now[0] =~ /time=(.+)/){
						$time_now = $1;
					}
					
					@time_f0_before = split(/\s+/, $time_F0[$k-1]); #下一行
					if($time_f0_before[0] =~ /time=(.+)/){
						$time_before = $1;
					}
					if(($timeDeleteCoda > $time_before) && ($timeDeleteCoda < $time_now)){
						$timeDeleteCoda = $time_now;
						last;
					}
					if(($timeDeleteCoda eq $time_before)){
						$timeDeleteCoda = $time_before;
						last;
					}
					if(($timeDeleteCoda eq $time_now)){
						$timeDeleteCoda = $time_now;
						last;
					}
				}
			}elsif(($timeDeleteOnset > $time_first)&&($timeDeleteCoda >= $time_last)){
				$timeDeleteCoda = $time_last;
				for($k = 0; $k < @time_F0; $k++){
					@time_f0_now = split(/\s+/, $time_F0[$k]); #当前行
					if($time_f0_now[0] =~ /time=(.+)/){
						$time_now = $1;
					}
					
					@time_f0_after = split(/\s+/, $time_F0[$k+1]); #下一行
					if($time_f0_after[0] =~ /time=(.+)/){
						$time_after = $1;
					}
					if(($timeDeleteOnset < $time_after) && ($timeDeleteOnset > $time_now)){
						$timeDeleteOnset = $time_now;
						last;
					}
					if(($timeDeleteOnset eq $time_after)){
						$timeDeleteOnset = $time_after;
						last;
					}
					if(($timeDeleteOnset eq $time_now)){
						$timeDeleteOnset = $time_now;
						last;
					}
				}
			}else{
				for($k = 0; $k < @time_F0; $k++){
					@time_f0_now = split(/\s+/, $time_F0[$k]); #当前行
					if($time_f0_now[0] =~ /time=(.+)/){
						$time_now = $1;
					}
					
					@time_f0_after = split(/\s+/, $time_F0[$k+1]); #下一行
					if($time_f0_after[0] =~ /time=(.+)/){
						$time_after = $1;
					}
					if(($timeDeleteOnset < $time_after) && ($timeDeleteOnset > $time_now)){
						$timeDeleteOnset = $time_now;
						last;
					}
					if(($timeDeleteOnset eq $time_after)){
						$timeDeleteOnset = $time_after;
						last;
					}
					if(($timeDeleteOnset eq $time_now)){
						$timeDeleteOnset = $time_now;
						last;
					}
				}
				for($k = @time_F0-1; $k >= 0; $k--){
					@time_f0_now = split(/\s+/, $time_F0[$k]); #当前行
					if($time_f0_now[0] =~ /time=(.+)/){
						$time_now = $1;
					}
					
					@time_f0_before = split(/\s+/, $time_F0[$k-1]); #下一行
					if($time_f0_before[0] =~ /time=(.+)/){
						$time_before = $1;
					}
					
					if(($timeDeleteCoda > $time_before) && ($timeDeleteCoda < $time_now)){
						$timeDeleteCoda = $time_now;
						last;
					}
					if(($timeDeleteCoda eq $time_before)){
						$timeDeleteCoda = $time_before;
						last;
					}
					if(($timeDeleteCoda eq $time_now)){
						$timeDeleteCoda = $time_now;
						last;
					}
				}
			}	
			foreach(@time_F0){
				chomp;		
				@time_F0_each = split(/\s+/, $_);
				if($time_F0_each[0] =~ /time=(.+)/){
					$each_time = $1;
				}
				if($time_F0_each[1] =~ /F0=(.+)/){
					$each_F0 = $1;
				}
				if($each_time >= $timeDeleteOnset && $each_time <= $timeDeleteCoda){
					$each_F0 = sprintf("%.3f", $each_F0);
					my $F0_Zscore = sprintf("%.3f",($each_F0-$mean)/$dev);
					#my $L2_Regression_TimeF0 = sprintf("%.3f",$F0_Zscore*$mean_dev[1]+$mean_dev[0]);
					my $L2_Regression_TimeF0 = $each_F0;
					my $L2_Regression_FiveDegree = ($each_F0-$pitchrange_min)/($pitchrange_max-$pitchrange_min)*5;
					print Final_DelOnsetCoda_TimeF0 "$each_time $each_F0\n"; 
					print Final_DelOnsetCoda_TimeF0Zscore "$each_time $F0_Zscore\n";	
					print L2_Regression_TimeF0 "$each_time $L2_Regression_TimeF0\n";
					print L2_Regression_FiveDegree "$each_time $L2_Regression_FiveDegree\n";
				}
			}
			close(time_F0);
			print Final_DelOnsetCoda_TimeF0Zscore "*** ***\n";
			print Final_DelOnsetCoda_TimeF0 "*** ***\n";
			print L2_Regression_TimeF0 "*** ***\n";	
			print L2_Regression_FiveDegree "*** ***\n";				
			$row++;
		}
		close(final_time);
		close(L2_Regression_Time);
		close(Final_DelOnsetCoda_TimeF0Zscore);	
		close(Final_DelOnsetCoda_TimeF0);	
		close(L2_Regression_TimeF0);	
		close(L2_Regression_FiveDegree);	
	}
}

