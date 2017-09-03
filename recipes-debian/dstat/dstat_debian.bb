SUMMARY = "versatile resource statistics tool"
DESCRIPTION = "\
Dstat is a versatile replacement for vmstat, iostat and ifstat. Dstat\n\
overcomes some of the limitations of these programs and adds some\n\
extra features.\n\
.\n\
Dstat allows you to view all of your network resources instantly, you \n\
can for example, compare disk usage in combination with interrupts \n\
from your IDE controller, or compare the network bandwidth numbers\n\
directly with the disk throughput (in the same interval).\n\
.\n\
Dstat also cleverly gives you the most detailed information in columns\n\
and clearly indicates in what magnitude and unit the output is displayed.\n\
.\n\
Dstat is also unique in letting you aggregate block device throughput for\n\
a certain diskset or network bandwidth for a group of interfaces, i.e. you\n\
can see the throughput for all the block devices that make up a single\n\
filesystem or storage system.\n\
.\n\
Dstat's output, in its current form, is not suited for post-processing by\n\
other tools, it's mostly meant for humans to interpret real-time data\n\
as easy as possible."
HOMEPAGE = "http://dag.wieers.com/home-made/dstat/"
LICENSE = "GPL-2.0"
SECTION = "admin"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

RDEPENDS_${PN} = "python"

inherit debian-package allarch
PV = "0.7.2"

do_install() {
	oe_runmake install DESTDIR=${D}
	# Temporary fix for #558047
	rm ${D}${datadir}/${PN}/dstat_mysql5_conn.py
}
