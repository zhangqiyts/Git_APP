sub genconti { 
	my($data, $wav, $new_wav, $startTime_To_endTime) = @_;
	system("/usr/praat/praat --run /home/APP/workspace_praat/replaceF0.scp $data $wav $new_wav $startTime_To_endTime");
}

$path = $ARGV[0];
$mean = $ARGV[1];
$dev = $ARGV[2];
$Chinese_FileName = $ARGV[3];
$L2_FileName = $ARGV[4];

#$path = "/home/APP/PitchReplace/";
#$mean = "241.02";
#$dev = "46.31";
#$Chinese_FileName = "F2_ai1";
#$L2_FileName = "L2F_zhang_ai1_001";

@L2_Info = split(/\_/,$L2_FileName);
$L2_sex = shift(@L2_Info);
$L2_Name = shift(@L2_Info);
$L2_ID = $L2_sex."_".$L2_Name;

@Chinese_Info = split(/\_/,$Chinese_FileName);
$Chinese_ID = shift(@Chinese_Info);

$L2_path = $path."/".$L2_ID."/".$L2_FileName;

$file_data_dir = $L2_path."/data"; 
$file_Chinese_dir = $path."/".$Chinese_ID."/".$Chinese_FileName; 

$goaldir = $L2_path."/new_wav";
if(-d $goaldir){
	print "The folder 'new_wav' has already exists!\n";
}else{
	system("mkdir $goaldir");
}
$workspace_dir = "/opt/tomcat/webapps/PitchReplace_linux/data/";
$filedir_result = $workspace_dir.$L2_FileName;
if(-d $filedir_result){
	print "The folder $L2_FileName has already exists!\n";
}else{
	system("mkdir $filedir_result");
}

opendir(dir,"$file_data_dir")||die;
@file_data = readdir(dir);
close(dir);

my $start_time;
my $end_time;
my $point;
my $new_wav_name;
my @F0Zscore;
my @final_time;

for($i = 2; $i < @file_data; $i++) {
	if($file_data[$i] =~ /(.+)\_time.txt/){
		$new_wav_name = $L2_FileName;
		open(final_time, "$file_data_dir/$file_data[$i]")||die;
		@line = <final_time>;
			
		$Chinese_10F0Zscore = $Chinese_FileName."_10F0Zscore";
		print "$file_Chinese_dir/$Chinese_10F0Zscore";
		open(F0Zscore, "$file_Chinese_dir/$Chinese_10F0Zscore")||die;
		@F0Zscore = <F0Zscore>;

		$final_number_L2 = @line;
		$final_number_CH = @F0Zscore + 0;

		if($final_number_L2 == $final_number_CH){
			for($j = 0; $j < @F0Zscore; $j++){
				chomp($F0Zscore[$j]);
				@point = split(/\s+/, $F0Zscore[$j]);
				$data = "0";
				for($k = 0; $k < @point; $k++){
					$point[$k] = $point[$k]*$dev+$mean;
					$data = $data.",".$point[$k];
				}
				$old_wav_name = $new_wav_name;
				if($j == 0){
					$wav = $file_data_dir."/$old_wav_name.wav";
				}else {
					$wav = $goaldir."/$old_wav_name.wav";
				}
				$id = $j + 1;
				
				if ($old_wav_name =~ /(.+)_(.+)_(.+)_(.+)/) {
					$part1 = $1;
					$part2 = $2;
					$part3 = $3;
					$part4 = $4;
					if ($id == @F0Zscore) {
						$part4 =~ s/$part4/PitchReplace/;
					}else {		
						$part4 =~ s/$part4/$id/;
					}	
					$new_wav_name = $part1."_".$part2."_".$part3."_".$part4;
				}else {
					if (@F0Zscore == 1) {
						$new_wav_name = $new_wav_name."_PitchReplace";
					}else{
						$new_wav_name = $old_wav_name."_".$id;
					}			
				}
				
				$number = 2 * $j + 3;
				@final_time = split(/\s+/,$line[$j]);
				
				$start_time = $final_time[3];
				$end_time = $final_time[4];
				
				$new_wav = $goaldir."/$new_wav_name.wav";	
				$startTime_To_endTime = $start_time.",".$end_time;
				
				print "$data, $wav, $new_wav, $startTime_To_endTime\n";
				genconti($data, $wav, $new_wav, $startTime_To_endTime);
			}
		}else{
			print "please read the words on the screen!"
		}
		close(F0Zscore);
		close(line);
	}
	
	if($file_data[$i] =~ /(.+)\.wav/ || $file_data[$i] =~ /(.+)\.png/){
		system("cp $file_data_dir/$file_data[$i]  $filedir_result/$file_data[$i]");
	}
}
system("cp $file_Chinese_dir/$Chinese_FileName.wav  $filedir_result/$Chinese_FileName.wav");
opendir(Dir, $goaldir) || die;
@PitchReplaceWav = readdir(Dir);
for($i = 2; $i < @PitchReplaceWav; $i++){
	if($PitchReplaceWav[$i] =~ /(.+)\_PitchReplace.wav/){
		system("cp $goaldir/$PitchReplaceWav[$i]  $filedir_result/$PitchReplaceWav[$i]");
	}
}
closedir(Dir);
