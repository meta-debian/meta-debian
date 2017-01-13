#
# base recipe: meta/recipes-core/zlib/zlib_1.2.8.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "1.2.8.dfsg"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = " \
file://zlib.h;beginline=4;endline=23;md5=fde612df1e5933c428b73844a0c494fd \
"

# prevent make install-libs from creating invalid cache
SRC_URI += "file://remove.ldconfig.call.patch"

# follow configure-stamp rule in debian/rules
do_configure() {
	./configure --shared --prefix=${prefix} --libdir=${libdir}
}

do_compile () {
	oe_runmake
}

do_install() {
	oe_runmake DESTDIR=${D} install
}

# move run-time libraries to ${libdir}
# "debian/rules install" also does the same things
do_install_append_class-target() {
	if [ ${base_libdir} != ${libdir} ]
	then
		mkdir -p ${D}/${base_libdir}
		mv ${D}/${libdir}/libz.so.* ${D}/${base_libdir}
		tmp=`readlink ${D}/${libdir}/libz.so`
		ln -sf ../../${base_libdir}/$tmp ${D}/${libdir}/libz.so
	fi
}

# In Debian, binary package name of zlib is "${PN}1g"
DEBIANNAME_${PN}-dbg       = "${PN}1g-dbg"
DEBIANNAME_${PN}-staticdev = "${PN}1g-staticdev"
DEBIANNAME_${PN}-dev       = "${PN}1g-dev"
DEBIANNAME_${PN}-doc       = "${PN}1g-doc"
DEBIANNAME_${PN}           = "${PN}1g"

BBCLASSEXTEND = "native nativesdk"

inherit ptest

SRC_URI += " \
file://Makefile-runtests.patch \
file://run-ptest \
"

do_compile_ptest() {
	oe_runmake static shared
}

do_install_ptest() {
	install ${B}/Makefile   ${D}${PTEST_PATH}
	install ${B}/example    ${D}${PTEST_PATH}
	install ${B}/minigzip   ${D}${PTEST_PATH}
	install ${B}/examplesh  ${D}${PTEST_PATH}
	install ${B}/minigzipsh ${D}${PTEST_PATH}
}
