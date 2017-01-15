SUMMARY = "collection of more utilities from FreeBSD"
DESCRIPTION = "This package contains lots of small programs many people expect to find when\n\
they use a BSD-style Unix system.\n\
.\n\
It provides banner (as printerbanner), calendar, col, colcrt, colrm, column,\n\
from (as bsd-from), hexdump (or hd), look, lorder, ncal (or cal), ul, and\n\
write (as bsd-write).\n\
.\n\
This package used to contain whois and vacation, which are now distributed in\n\
their own packages. Also here was tsort, which is now in the "coreutils"\n\
 package."

inherit debian-package
PV = "9.0.6"

LICENSE="BSD-4-Clause"
LIC_FILES_CHKSUM="file://usr.bin/hexdump/hexdump.h;endline=35;md5=148f6a2793c64631604a03a5d41a2cdc"

DEPENDS = "libhdate-native"

DEBIAN_PATCH_TYPE = "quilt"

inherit pythonnative update-alternatives

do_compile() {
	oe_runmake

	# Base on debian/rules
	currentyear=$(date +%Y)
	year=$(expr $currentyear + 5)
	while [ $year -ne $currentyear ] ; do
		year=$(expr $year - 1)
		PYTHONPATH=${STAGING_DIR_NATIVE}${PYTHON_SITEPACKAGES_DIR} \
		  ${PYTHON} ${S}/debian/calendarJudaic.py $year \
		  > ${S}/debian/calendars/calendar.judaic.$year
	done
	cd ${S}/debian/calendars
	[ -L calendar.judaic ] || ln -s calendar.judaic.$year calendar.judaic
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install

	# Base on debian/dirs
	install -d ${D}${sysconfdir}/calendar \
	           ${D}${sysconfdir}/cron.daily \
	           ${D}${sysconfdir}/default \
	           ${D}${datadir}/calendar \
	           ${D}${docdir} \
	           ${D}${datadir}/lintian/overrides
	install -m 755 ${S}/debian/cron.daily           ${D}${sysconfdir}/cron.daily/bsdmainutils
	install -m 644 ${S}/debian/bsdmainutils.default ${D}${sysconfdir}/default/bsdmainutils

	# Base on debian/install
	for i in ${S}/usr.bin/calendar/calendars/*; do
		if [ -f $i ]; then
			install -m 644 $i ${D}${datadir}/calendar/
		elif [ -d $i ]; then
			destdir=$(basename $i | cut -d. -f1)
			install -d ${D}${datadir}/calendar/$destdir
			install -m 644 $i/* ${D}${datadir}/calendar/$destdir/
		fi
	done
	install -m 644 ${S}/debian/calendars/calendar.* ${D}${datadir}/calendar/
	install -m 644 ${S}/debian/calendars/default    ${D}${sysconfdir}/calendar/
	install -m 644 ${S}/debian/lintian/bsdmainutils ${D}${datadir}/lintian/overrides/
	install -m 644 ${S}/debian/calendarJudaic.py    ${D}${docdir}/bsdmainutil
}

FILES_${PN} += " \
    ${datadir}/calendar \
    ${datadir}/lintian/overrides \
"

# Base on debian/postinst
inherit update-alternatives

ALTERNATIVE_${PN} = "write from"
ALTERNATIVE_LINK_NAME[write] = "${bindir}/write"
ALTERNATIVE_TARGET[write] = "${bindir}/bsd-write"
ALTERNATIVE_PRIORITY[write] = "100"
ALTERNATIVE_LINK_NAME[from] = "${bindir}/from"
ALTERNATIVE_TARGET[from] = "${bindir}/bsd-from"
ALTERNATIVE_PRIORITY[from] = "10"
