#
# base recipe: meta/recipes-extended/bzip2/bzip2_1.0.6.bb
# base recipe: daisy
#

PR = "r2"

inherit debian-package
PV = "1.0.6"

LICENSE = "bzip2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ddeb76cd34e791893c0f539fdab879bb"

SRC_URI += " \
file://Makefile-ptest.patch \
file://run-ptest \
"

PACKAGES =+ "libbz2 libbz2-dev libbz2-staticdev"

CFLAGS_append = " -fPIC -fpic -Winline -fno-strength-reduce -D_FILE_OFFSET_BITS=64"

inherit ptest

EXTRA_OEMAKE = "CC='${CC}' AR='${AR}' RANLIB='${RANLIB}' \
                CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}' \
                "

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake 'PREFIX=${D}' install

	# According to debian/rules
	if [ "${base_libdir}" != "/lib" ]; then
		install -d ${D}${libdir} ${D}${base_libdir}
		rm -f ${D}/lib/libbz2.so
		mv ${D}/lib/libbz2.so.* ${D}${base_libdir}/
		ln -sf ${@oe.path.relative("${libdir}","${base_libdir}")}/libbz2.so.1.0 ${D}${libdir}/libbz2.so
	fi

	ln -sf bzdiff ${D}/bin/bzcmp
	ln -sf bzgrep ${D}/bin/bzegrep
	ln -sf bzgrep ${D}/bin/bzfgrep
	ln -sf bzmore ${D}/bin/bzless

	# Fix path for nativesdk
	if [ "${base_bindir}" != "/bin" ]; then
		install -d ${D}${base_bindir}
		mv ${D}/bin/* ${D}${base_bindir}/
		rm -rf ${D}/bin
	fi

	install -d ${D}${datadir}
	mv ${D}/man ${D}${datadir}

	# According to debian/libbz2-dev.install
	mv ${D}/include ${D}${prefix}/
	mv ${D}/lib/libbz2.a ${D}${libdir}/

	# Remove empty folder
	rmdir --ignore-fail-on-non-empty ${D}/lib
}

do_install_append_class-native() {
	#install binaries to bzip2-native under sysroot for replacement-native
	install -d ${D}${STAGING_BINDIR_NATIVE}
	mv ${D}/${base_bindir} ${D}${STAGING_BINDIR_NATIVE}/${PN}/
}

do_install_ptest () {
	cp -f ${B}/Makefile ${D}${PTEST_PATH}/Makefile
	sed -i -e "s|^Makefile:|_Makefile:|" ${D}${PTEST_PATH}/Makefile
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "bunzip2 bzcat bzip2"
ALTERNATIVE_LINK_NAME[bunzip2] = "${base_bindir}/bunzip2"
ALTERNATIVE_LINK_NAME[bzcat] = "${base_bindir}/bzcat"
ALTERNATIVE_LINK_NAME[bzip2] = "${base_bindir}/bzip2"

FILES_libbz2 = "${libdir}/lib*${SOLIBS}"

FILES_libbz2-dev = "${includedir} ${libdir}/lib*${SOLIBSDEV}"
SECTION_libbz2-dev = "devel"
RDEPENDS_libbz2-dev = "libbz2 (= ${EXTENDPKGV})"

FILES_libbz2-staticdev = "${libdir}/*.a"
SECTION_libbz2-staticdev = "devel"
RDEPENDS_libbz2-staticdev = "libbz2-dev (= ${EXTENDPKGV})"

PROVIDES_append_class-native = " bzip2-replacement-native"
BBCLASSEXTEND = "native nativesdk"
