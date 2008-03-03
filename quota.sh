while true
do
echo -n `date` ; svn diff | wc -l
sleep 600
done
