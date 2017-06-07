$path = $ARGV[0];
$L2_ID = $ARGV[1];
$Save_GetF0Path = $ARGV[2];
# $dir = "/home/APP/ToneTest/";
# $L2_ID = "L2F_zhang";

$filedir = $path."/".$L2_ID."/wav/";
$praat_filedir = $filedir; 

system("/usr/praat/praat --run /home/APP/workspace_praat/GetPitchTier.scp $praat_filedir");
opendir(dir,"$filedir")||die;
@file_data = readdir(dir);
for($i = 2; $i < @file_data; $i++) {
	if($file_data[$i] =~ /(.+).PitchTier/){
		open(PitchTier, "$filedir/$file_data[$i]")||die;
		open(GetF0,">$Save_GetF0Path/$1_F0")||die;
		while(<PitchTier>){
			chomp;
			my $f0;
			if($_ =~ /(\s+)value = (.+)/){
				$f0 = $2;
				print GetF0 "$f0\n";
			}
		}
		close(PitchTier);
		close(GetF0);	
	}
}
close(dir);
