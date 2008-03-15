while true
do
echo -n `date` ; git diff | wc -l
sleep 600
done
