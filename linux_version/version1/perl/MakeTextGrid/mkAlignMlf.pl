#make .align.mlf
$path = $ARGV[0];
#$path = "/home/APP/PitchReplace/L2F_1_an1";
$dir = "/home/APP/workspace_perl/MakeTextGrid";
opendir(dir,"$path/mlf");
@mlfArray=readdir(dir);
close(dir);

opendir(dir,"$path/scp");
@scpArray=readdir(dir);
close(dir);

if(-d "$path/alignMlf"){
	print "The folder 'alignMlf' already exists!\n";
}else{
	system("mkdir $path/alignMlf");
}

for($i=2;$i<@mlfArray;$i++)
{
	system("/usr/local/bin/HVite -A -T 1 -l '*' -o N -b  sil -C $dir/wav2mfcc_xword.winwav.cfg -m -H $dir/MODELS -i $path/alignMlf/$mlfArray[$i] -t 800.0 -y lab -I $path/mlf/$mlfArray[$i] -S $path/scp/$scpArray[$i] $dir/htklexnospnosil $dir/IF-notone.sp.list");
}
