#!/bin/sh
#
# Generate HTML output from test result.

THISDIR=$(dirname $(readlink -f "$0"))
LOGDIR=$THISDIR/logs
HTMLDIR=$THISDIR/html

GREEN="#2ecc71"
GREY="#bdc3c7"
RED="#e74c3c"

TESTING_LOGS="https://raw.githubusercontent.com/tswcos/meta-debian-test-logs/master"

deby_machines="''"
deby_tiny_machines="''"

for d in $LOGDIR/*; do
	test -d $d || continue

	distro=`basename $d`
	echo "DISTRO: $distro"

	for m in $LOGDIR/$distro/*; do
		test -d $m || continue

		machine=`basename $m`

		if [ ! -f $LOGDIR/$distro/$machine/result.txt ]; then
			continue
		fi

		if [ "$distro" = "deby" ]; then
			deby_machines="$deby_machines,'$machine'"
		else
			deby_tiny_machines="$deby_tiny_machines,'$machine'"
		fi

		echo "Generating html for $machine..."

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
<script>
function reload() {
	var distro = document.getElementById("distro").value;
	var machine = document.getElementById("machine").value;

	current_url = window.location.href.replace("/index.html","");
	new_url = current_url + "/../../" + distro + "/" + machine + "/index.html";
	new_url = new_url.replace("//..","/..");
	window.location = new_url;
}

function select_distro() {
	var deby_tiny_machines = [##DEBY_TINY_MACHINES##];
	var deby_machines = [##DEBY_MACHINES##];
	var machines = [];

	if (document.getElementById('distro').value == "deby") {
		machines = deby_machines;
	} else {
		machines = deby_tiny_machines;
	}

	var new_opts = [];
	for (m of machines) {
		new_opts.push("<option value='" + m + "'>" + m + "</option>");
	}
	document.getElementById("machine").innerHTML=new_opts.join()
}

window.onload = function(){
	var distro = "";
	var machine = "";
	var dirs = window.location.href.split("/");
	if (dirs[dirs.length - 1] == "index.html") {
		machine = dirs[dirs.length - 2]
		distro = dirs[dirs.length - 3]
	} else {
		machine = dirs[dirs.length - 1]
		distro = dirs[dirs.length - 2]
	}

	document.getElementById('distro').value = distro;

	select_distro();

	document.getElementById('machine').value = machine;
};
</script>
</head>

<body>
<h1>meta-debian Status</h1>
<br>
<h3>$distro - $machine</h3>
<table>
<tr>
	<td><b>Built distro</b></td>
	<td><select id="distro" name="distro" onchange="select_distro()">
		<option value='deby-tiny'>deby-tiny</option>
		<option value='deby'>deby</option>
</select></td>
</tr>
<tr>
	<td><b>Built machine</b></td>
	<td><select id="machine" name="machine">
	</select></td>
</tr>
</table>
<button onclick="reload()">Go</button>
<br>
<br><table class="main_table">
<thead>
<tr bgcolor="$GREY">
<th></th>
<th>Package</th>
<th>Build Status</th>
<th>Ptest Status<br/>(PASS/SKIP/FAIL)</th>
<th>Build Version</th>
<th>Ptest Version</th>
</tr></thead>
EOF

		while read -r line; do
			recipe=`echo $line | awk '{print $1}'`
			build_status=`echo $line | awk '{print $2}'`
			ptest_status=`echo $line | awk '{print $3}'`
			build_version=`echo $line | awk '{print $4}'`
			ptest_version=`echo $line | awk '{print $5}'`

			build_log="$TESTING_LOGS/$distro/$machine/$recipe.build.log"
			ptest_log="$TESTING_LOGS/$distro/$machine/$recipe.ptest.log"

			if echo $build_status | grep -iq "PASS"; then
				bcolor=$GREEN
			elif echo $build_status | grep -iq "FAIL"; then
				bcolor=$RED
			else
				bcolor=$GREY
			fi

			html_ptest_status=$ptest_status
			if echo $ptest_status | grep -iq "NA"; then
				pcolor=$GREY
			else
				fail=`echo $ptest_status | cut -d/ -f3`

				pcolor=$RED
				test "$fail" = "0" && pcolor=$GREEN

				html_ptest_status="<a href=$ptest_log>$ptest_status</a>"
			fi

			html_build_status="<td bgcolor=\"$bcolor\"><a href=$build_log>$build_status</a></td>"
			html_ptest_status="<td bgcolor=\"$pcolor\">$html_ptest_status</td>"
			html_build_version="<td>`echo $build_version | sed "s#,#<br/>#g"`</td>"
			html_ptest_version="<td>`echo $ptest_version | sed "s#,#<br/>#g"`</td>"
			echo "<tr><td></td><td>$recipe</td> \
			        ${html_build_status}${html_ptest_status} \
			        ${html_build_version}${html_ptest_version} \
			      </tr>" >> $index
		done < $LOGDIR/$distro/$machine/result.txt

		echo "</table></body></html>" >> $index
	done
done

sed -i -e "s@##DEBY_MACHINES##@${deby_machines}@g" \
       -e "s@##DEBY_TINY_MACHINES##@${deby_tiny_machines}@g" \
       ${HTMLDIR}/*/*/index.html