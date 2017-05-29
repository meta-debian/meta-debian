#
# base recipe: http://cgit.openembedded.org/openembedded-core/tree/meta/recipes-devtools/gcc/libgfortran_4.9.bb
# base branch: jethro
#

require gcc-4.9.inc
require gcc-configure-common.inc
require gcc-shared-source.inc
EXTRA_OECONF_PATHS = "\
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"

EXTRA_OECONF += "--disable-libquadmath-support"
do_configure () {
	target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
	cp -fpPR ${STAGING_INCDIR_NATIVE}/gcc-build-internal-$target/* ${B}

	for d in libgcc libgfortran; do
		rm -rf ${B}/$target/$d/
		mkdir -p ${B}/$target/$d/
		cd ${B}/$target/$d/
		chmod a+x ${S}/$d/configure
		${S}/$d/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
	done
	# Easiest way to stop bad RPATHs getting into the library since we have a
	# broken libtool here
	sed -i -e 's/hardcode_into_libs=yes/hardcode_into_libs=no/' ${B}/$target/libgfortran/libtool
}

do_compile () {
	# Fix error: Kind 16 not supported for type REAL
	sed -i -e "s|possible_real_kinds=.*|possible_real_kinds=\"4 8 10 \"|" \
		${S}/libgfortran/mk-kinds-h.sh
	target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
	cd ${B}/$target/libgfortran/
	oe_runmake MULTIBUILDTOP=${B}/$target/libgfortran/
}

do_install () {
	target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
	cd ${B}/$target/libgfortran/
	oe_runmake 'DESTDIR=${D}' MULTIBUILDTOP=${B}/$target/libgfortran/ install
	if [ -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude
	fi
	if [ -d ${D}${infodir} ]; then
		rmdir --ignore-fail-on-non-empty -p ${D}${infodir}
	fi
	chown -R root:root ${D}
}

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "gcc-runtime"

BBCLASSEXTEND = "nativesdk"

PACKAGES = "\
    ${PN}-dbg \
    libgfortran \
    libgfortran-dev \
    libgfortran-staticdev \
"
FILES_${PN} = "${libdir}/libgfortran.so.*"
FILES_${PN}-dev = "\
    ${libdir}/libgfortran*.so \
    ${libdir}/libgfortran.spec \
    ${libdir}/libgfortran.la \
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/libgfortranbegin.* \
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/libcaf_single.* \
"
INSANE_SKIP_${MLPREFIX}libgfortran-dev = "staticdev"

do_package_write_ipk[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_deb[depends] += "virtual/${MLPREFIX}libc:do_packagedata"
do_package_write_rpm[depends] += "virtual/${MLPREFIX}libc:do_packagedata"

python __anonymous () {
    f = d.getVar("FORTRAN", True)
    if "fortran" not in f:
        raise bb.parse.SkipPackage("libgfortran needs fortran support to be enabled in the compiler")
}
