$dir = $ARGV[0];
$L2_FileName = $ARGV[1];
$Save_GetF0Path = $ARGV[2];

# $dir = "D:/home/APP/PitchReplace/";
# $L2_FileName = "L2F_zhang_ai1";
# $Save_GetF0Path = "L2F_zhang";

@L2_Info = split(/\_/,$L2_FileName);
$L2_sex = shift(@L2_Info);
$L2_Name = shift(@L2_Info);
$L2_ID = $L2_sex."_".$L2_Name;

$path = "";
@file = split(/\//,$dir);
for($i = 0; $i < @file-1; $i++){
	$path = $path.$file[$i]."\\";
}
$path = $path.$file[@file-1];

$L2_path = $path."\\".$L2_ID."\\".$L2_FileName;

$filedir = $L2_path."\\data"; 
$goaldir = $L2_path."\\data";

open(in,"D:\\home\\APP\\workspace_perl\\PitchReplace\\finalset.list")||die;
while(<in>){
	chomp;
	$hash_final{$_} = 1;
}
close(in);

opendir(dir,"$filedir")||die;
@file = readdir(dir);
close(dir);

for($i = 2; $i < @file; $i++){
	if($file[$i] =~ /(.+)\.textgrid/){
		$nameTime = $1."_time.txt";
		open(out,">$goaldir\\$nameTime")||die; #获取的时间文件
		open(in,"$filedir\\$file[$i]")||die;
		my @timeInfo_syllable = ();
	    my @timeInfo_final = ();
		my $syllable_time;
		$flag_item2 = 0;
		$flag_item3 = 0;	
		$flag_item4 = 1;	
		while(<in>){
			chomp;
			if($_ =~ /^xmax = (.+)/){
				$syllable_time = $1;
			}
			if($_ =~ /(\s+)item \[2\]/){
				$flag_item2 = 1;
			}
			if($_ =~ /(\s+)item \[3\]/){
				$flag_item3 = 1;
			}	
			if($_ =~ /(\s+)item \[4\]/){
				$flag_item4 = 0;
			}
			if($flag_item2 eq 1 && $flag_item3 eq 0){
				if($_ =~ /(\s+)xmin = (.+)/){
					$min = $2;
					# print "$min  ";
					push(@timeInfo_syllable,$min);
				}
				if($_ =~ /(\s+)xmax = (.+)/){
					$max = $2;
					# print "$max \n";
					push(@timeInfo_syllable,$max);
 				}
				if($_ =~ /(\s+)text = \"sil\"/ || $_ =~ /(\s+)text = \"\"/){
					pop(@timeInfo_syllable);
 					pop(@timeInfo_syllable);
 				}		
			}
			if($flag_item3 eq 1 && $flag_item4 eq 1){
				if($_ =~ /(\s+)xmin = (.+)/){
					$min = $2;
#					print "$2\n";
					push(@timeInfo_final,$min);
				}
				if($_ =~ /(\s+)xmax = (.+)/){
					$max = $2;
#					print "$_\n";
					push(@timeInfo_final,$max);
 				}
				if($_ =~ /(\s+)text = \"(.+)\"/){
 					unless(defined($hash_final{$2})){
 						pop(@timeInfo_final);
 						pop(@timeInfo_final);
 					}
 				}        
			}
		}
		for($k = 2; $k < @timeInfo_syllable; $k = $k + 2){
			#整个时长；音节时长起点、终点；韵母时长起点、终点；韵母取点弯头降尾时长的起点、终点
			print out "$syllable_time $timeInfo_syllable[$k] $timeInfo_syllable[$k+1] $timeInfo_final[$k] $timeInfo_final[$k+1]\n";
			$timeDeleteOnset = $timeInfo_final[$k]+$timeInfo_final[$k]*0.03; #弯头0.03
			$timeDeleteCoda = $timeInfo_final[$k+1]-$timeInfo_final[$k+1]*0.05; #降尾0.05
		}	
		close(out);	
		close(in);		
	}		
}
$praat_filedir = $filedir."\\"; 
print "$praat_filedir\n";
system("d:\\usr\\praat\\Praat.exe --run d:\\home\\APP\\workspace_praat\\GetPitchTierF0.scp $praat_filedir");
open(in,"D:\\home\\APP\\workspace_perl\\PitchReplace\\finalset.list")||die;
while(<in>){
	chomp;
	$hash{$_} = 1;
}
close(in);

$flag = 0;
opendir(dir,"$filedir")||die;
@file_data = readdir(dir);
for($i = 2; $i < @file_data; $i++) {
	if($file_data[$i] =~ /(.+).PitchTier/){
		$PitchTierToF0 = $1."_TimeF0.txt";
		open(PitchTier, "$filedir\\$file_data[$i]")||die;
		open(PitchTierToF0, ">$filedir\\$PitchTierToF0")||die;
		open(GetF0,">$Save_GetF0Path/$1_F0")||die;
		while(<PitchTier>){
			chomp;
			my $size;
			my $time;
			my $f0;
			if($_ =~ /(\s+)number = (.+)/){
				$time = sprintf("%.3f", $2);
				print PitchTierToF0 "time=$time ";
			}
			if($_ =~ /(\s+)value = (.+)/){
				$f0 = $2;
				print PitchTierToF0 "F0=$f0\n";
				print GetF0 "$f0\n";
			}
		}
		close(PitchTier);
		close(PitchTierToF0);
		close(GetF0);	
	}
	if($file_data[$i] =~ /(.+)\_Pitch.txt/){
		open(Pitch,"$filedir/$file_data[$i]")||die;	
		@arr = <Pitch>;
		for($m = 2; $m < @arr-1; $m++){
			chomp($arr[$m]);
			$count = 0;
			print " $arr[$m]\n";
			@line = split(",", $arr[$m]);
			$initial_final = $line[0];
			if (defined $hash{$initial_final}) {
				
				print out"$initial_final";
				for($k = 1; $k < @line; $k++){
					# $value = log10($line[$k]);
					$value = $line[$k];
				}
			}
		}
		if ($flag == 1){	
			next;
		}
		close(Pitch);
	}
}
close(dir);