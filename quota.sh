while true
do
echo `date` '     ' `git diff -M --ignore-space-change master | wc -l` \(`git diff -M --ignore-space-change master\^ | wc -l` \(`git diff -M --ignore-space-change master\^\^ | wc -l` \) \)
sleep 600
done
