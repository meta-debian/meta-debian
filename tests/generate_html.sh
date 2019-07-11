#!/bin/sh

THISDIR=$(dirname $(readlink -f "$0"))
LOGDIR=$THISDIR/logs
HTMLDIR=$THISDIR/html

GREEN="#2ecc71"
GREY="#bdc3c7"
RED="#e74c3c"

TESTING_LOGS="https://raw.githubusercontent.com/tswcos/meta-debian-test-logs/master"

for i in $LOGDIR/*; do
	distro=`basename $i`
	for m in $LOGDIR/$distro/*; do
		machine=`basename $m`
		mkdir -p $HTMLDIR/$distro/$machine
		index=$HTMLDIR/$distro/$machine/index.html
		cat > $index << EOF
<html>
<head><title>meta-debian Status</title>
<style>
.main_table table {
    counter-reset: rowNumber;
}.main_table tr {
    counter-increment: rowNumber;
}.main_table tr td:first-child::before {
    content: counter(rowNumber);
}
</style>
</head>

<body>
<h1>meta-debian Status</h1>
<br>
<table>
<tr><td><b>Built distro</b></td><td>$distro</td></tr>
<tr><td><b>Built machine</b></td><td>$machine</td></tr>
</table>
<br>
<br><table class="main_table">
<thead>
<tr bgcolor="$GREY">
<th></th>
<th>Package</th>
<th>Version</th>
<th>Build Status</th>
<th>Ptest Status</th>
</tr></thead>
EOF
		if [ ! -f $LOGDIR/$distro/$machine/result.txt ]; then
			continue
		fi

		while read -r line; do
			recipe=`echo $line | awk '{print $1}'`
			version=`echo $line | awk '{print $2}'`
			build_status=`echo $line | awk '{print $3}'`
			ptest_status=`echo $line | awk '{print $4}'`

			if echo $build_status | grep -iq "PASS"; then
				bcolor=$GREEN
			elif echo $build_status | grep -iq "FAIL"; then
				bcolor=$RED
			else
				bcolor=$GREY
			fi

			if echo $ptest_status | grep -iq "PASS"; then
				pcolor=$GREEN
			elif echo $ptest_status | grep -iq "FAIL"; then
				pcolor=$RED
			else
				pcolor=$GREY
			fi

			echo "<tr><td></td><td>$recipe</td><td>$version</td><td bgcolor=\"$bcolor\"><a href=$TESTING_LOGS/$distro/$machine/$recipe.build.log>$build_status</a></td><td bgcolor=\"$pcolor\">$ptest_status</td></tr>" >> $index
		done < $LOGDIR/$distro/$machine/result.txt

		echo "</table></body></html>" >> $index
	done
done
