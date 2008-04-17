while true
do
echo `date` '     ' `git diff --ignore-space-change | wc -l` \(`git diff --ignore-space-change master\^ | wc -l` \(`git diff --ignore-space-change master\^\^ | wc -l` \) \)
sleep 600
done
