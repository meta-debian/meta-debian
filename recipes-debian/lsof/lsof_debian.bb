#
# base recipe: meta/recipes-extended/lsof/lsof_4.89.bb
# base branch: master
# base commit: 29bba95a393349f27585f901d66c0cf900d6e353
#

DESCRIPTION = "Description: Utility to list open files \
Lsof is a Unix-specific diagnostic tool.  Its name stands \
for LiSt Open Files, and it does just that.  It lists \
information about any files that are open, by processes \
currently running on the system."
HOMEPAGE = "http://people.freebsd.org/~abe/"

PR = "r0"

inherit debian-package
PV = "4.86+dfsg"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://00README;beginline=642;endline=677;md5=36fe5cec79840589378d1deffb8fa37f"

do_configure() {
	export PLATFORM=linux
	export LSOF_AR="${AR} cr"
	export LSOF_RANLIB="${RANLIB}"
	export LSOF_CFGF="${CFLAGS} ${CPPFLAGS}"
	export LSOF_CFGL="${LDFLAGS}"
	if [ "x${GLIBCVERSION}" != "x" ];then
		LINUX_CLIB=`echo ${GLIBCVERSION} |sed -e 's,\.,,g'`
		LINUX_CLIB="-DGLIBCV=${LINUX_CLIB}"
		export LINUX_CLIB
	fi

	# LINUX_HASSELINUX is null by default in Debian.
	# This means selinux is enabled only if selinux.h exists
	# in include directories. Expressly set it to N here
	# so that lsof works in small systems without selinux,
	# instead of adding dependency on selinux.
	export LINUX_HASSELINUX=N

	yes | ./Configure -n $PLATFORM
}

export I = "${STAGING_INCDIR}"
export L = "${STAGING_INCDIR}"

EXTRA_OEMAKE = ""

do_compile () {
	oe_runmake 'CC=${CC}' 'DEBUG='
}

do_install () {
	install -d ${D}${bindir} ${D}${mandir}/man8
	install -m 4755 lsof ${D}${bindir}/lsof
	install -m 0644 lsof.8 ${D}${mandir}/man8/lsof.8
}
