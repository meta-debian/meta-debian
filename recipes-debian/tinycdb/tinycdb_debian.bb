DESCRIPTION = "an utility to manipulate constant databases (cdb) \
tinycdb is a small, fast and reliable utility and subroutine \
library for creating and reading constant databases. The database \
structure is tuned for fast reading."

PR = "r0"

inherit debian-package autotools-brokensep
PV = "0.78"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=dca69c9caec3af9b850379632f912f81"

#EXTRA_OECONF += "includedir=${includedir}"
# Fix Makefile to set variable for compiling successfully
do_compile () {
	sed -i -e "s:/local::" ${S}/Makefile
	sed -i -e "s:^CC .*:CC = ${CC}:" ${S}/Makefile
        sed -i -e "s:^AR .*:AR = ${AR}:" ${S}/Makefile
        sed -i -e "s:^RANLIB .*:RANLIB = ${RANLIB}:" ${S}/Makefile
	sed -i -e 's:$(prefix)/man:${mandir}:' ${S}/Makefile
	sed -i -e "s:^DESTDIR=.*:DESTDIR=${D}:" ${S}/Makefile
	oe_runmake staticlib sharedlib cdb-shared nss
	cp -pf nss_cdb-Makefile cdb-Makefile
}

# Install files follow Debian
do_install () {
	oe_runmake install \
		'includedir=${includedir}' \
		'libdir=${libdir}' \
		'bindir=${bindir}'
	install -d ${D}${libdir}
	install -m 0644 ${S}/*.a ${D}${libdir}
	install -m 0755 libcdb.so.* ${D}${libdir}
	ln -sf libcdb.so.1 ${D}${libdir}/libcdb.so
}

# Split files follow Debian
PACKAGES =+ "libcdb"

FILES_libcdb += "${libdir}/*.so.*"
FILES_${PN}-doc += "${mandir}"

DEBIANNAME_${PN}-dev = "libcdb-dev"
RPROVIDES_${PN}-dev += "libcdb-dev"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

BBCLASSEXTEND = "native"

