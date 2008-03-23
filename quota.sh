while true
do
echo `date` '     ' `git diff | wc -l` \(`git diff master\^ | wc -l` \(`git diff master\^\^ | wc -l` \) \)
sleep 600
done
