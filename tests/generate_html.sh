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
.sortable table {
    counter-reset: rowNumber;
}.sortable tr {
    counter-increment: rowNumber;
}.sortable tr td:first-child::before {
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
<br><table>
<tr bgcolor="$GREY">
<th></th>
<th>Recipe</th>
<th>Version</th>
<th>Build Status</th>
</tr>
EOF
  while read -r line; do
    recipe=`echo $line | awk '{print $1}'`
    version=`echo $line | awk '{print $2}'`
    status=`echo $line | awk '{print $3}'`

    if echo $status | grep -iq "PASS"; then
      color=$GREEN
    else
      color=$RED
    fi

    echo "<tr bgcolor=\"$color\"><td></td><td>$recipe</td><td>$version</td><td>$status</td></tr>" >> $index
  done < $LOGDIR/$distro/$machine/result.txt

  echo "</table></body></html>" >> $index
  done
done
