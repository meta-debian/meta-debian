#
# build zic binary from glibc source
#

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit debian-package native autotools
PV = "2.19"

BPN = "glibc"

# same as glibc
do_configure () {
	(cd ${S} && gnu-configize) || die "failure in running gnu-configize"
	find ${S} -name "configure" | xargs touch
	CPPFLAGS="" oe_runconf
}

# build only "zic" binary based on ${S}/timezone/Makefile
do_compile() {
	mkdir -p ${B}/timezone

	# compile objects
	${CC} -c ${S}/timezone/scheck.c -o ${B}/timezone/scheck.o
	${CC} -c ${S}/timezone/ialloc.c -o ${B}/timezone/ialloc.o

	# required by zic.c
	echo 'static char const TZVERSION[] = "2.19";' > \
		${B}/timezone/version.h

	# compile zic with libc headers & libraries
	# in the host environment, without -nostdinc
	# -I${B}/timezone: to find version.h
	# --include ${B}/config.h: to import REPORT_BUGS_TO and PKGVERSION
	# -O2: config.h doesn't permit compiling without optimization
	${CC} -c ${S}/timezone/zic.c \
		-I${B}/timezone \
		--include ${B}/config.h \
		-O2 \
		-o ${B}/timezone/zic.o

	# link
	${CC} \
		${B}/timezone/scheck.o \
		${B}/timezone/ialloc.o \
		${B}/timezone/zic.o \
		-o ${B}/timezone/zic
}

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${B}/timezone/zic ${D}${sbindir}
}
