#
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree\
#	/meta-oe/recipes-support/eventlog/eventlog_0.2.13.bb?
# base branch: master
# base commit: 349507d36b14d1b83d2b27280ec19efe6d7229c6
#

SUMMARY = "Replacement syslog API"
DESCRIPTION = "The EventLog library aims to be a replacement of the 	\
              simple syslog() API provided on UNIX systems. The 	\
              major difference between EventLog and syslog is that 	\
              EventLog tries to add structure to messages. EventLog 	\
              provides an interface to build, format and output an 	\
              event record. The exact format and output method can 	\
              be customized by the administrator via a configuration 	\
              file. his package is the runtime part of the library. 	\
"

PR = "r1"
inherit debian-package
PV = "0.2.12"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b8ba8e77bcda9a53fac0fe39fe957767"

inherit autotools pkgconfig

#install follow Debian jessie
do_install_append () {
	LINKLIB=$(basename $(readlink ${D}${libdir}/libevtlog.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}
}
#correct the sub-package name
DEBIANNAME_${PN}-dbg = "libevtlog0-dbg"
