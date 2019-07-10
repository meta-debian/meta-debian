#!/bin/sh

THISDIR=$(dirname $(readlink -f "$0"))
LOGDIR=$THISDIR/logs
HTMLDIR=$THISDIR/html

GREEN="#2ecc71"
GREY="#bdc3c7"
RED="#e74c3c"

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
} tr {
    counter-increment: rowNumber;
} tr td:first-child::before {
    content: counter(rowNumber);
}
</style>
</head>

<body>
<h1>meta-debian Status</h1>
<br>
Built distro : $distro<br>
Built machine: $machine<br>
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
		while read -r line; do
			recipe=`echo $line | awk '{print $1}'`
			version=`echo $line | awk '{print $2}'`
			build_status=`echo $line | awk '{print $3}'`
			ptest_status=`echo $line | awk '{print $4}'`

			if echo $build_status | grep -iq "PASS"; then
				color=$GREEN
			else
				color=$RED
			fi

			echo "<tr bgcolor=\"$color\"><td></td><td>$recipe</td><td>$version</td><td>$build_status</td><td>$ptest_status</td></tr>" >> $index
		done < $LOGDIR/$distro/$machine/result.txt

		echo "</table></body></html>" >> $index
	done
done
