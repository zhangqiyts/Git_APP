use POSIX qw(log10);
$path = $ARGV[0];
#$path = "/home/APP/PitchReplace/L2F_1_an1";
$dir = "/home/APP/workspace_perl/PitchReplace";

$filedir = $path."/data";
$goaldir = $path."/Get10F0";

if(-d $goaldir){
	print "The folder 'Get10F0' already exists!\n";
}else{
	system("mkdir $goaldir");
}

open(in,"$dir/finalset.list")||die;
while(<in>){
	chomp;
	$hash{$_} = 1;
}
close(in);

opendir(dir,"$filedir")||die;
@Items = readdir(dir);
close(dir);
$flag = 0;
for($i = 2; $i < @Items; $i++){
	if($Items[$i] =~ /(.+)\_Pitch.txt/){
		open(in,"$filedir/$Items[$i]")||die;
		open(out,">$goaldir/$1.txt")||die;			
			
		@arr = <in>;
		for($m = 2; $m < @arr-1; $m++){
			chomp($arr[$m]);
			$count = 0;
			@line = split(",", $arr[$m]);
			$initial_final = $line[0];
			if (defined $hash{$initial_final}) {
				
				print out"$initial_final";
				for($k = 1; $k < @line; $k++){
					# $value = log10($line[$k]);
					$value = $line[$k];
					print out" $value";
				}
				print out"\n";
			}
		}
		if ($flag == 1){	
			next;
		}
		close(in);
		close(out);
	}	
}
