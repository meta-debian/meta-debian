#
# base recipe: meta/recipes-devtools/gdb/gdb-cross-canadian.inc
# base branch: daisy
#

require gdb-common.inc

inherit cross-canadian
inherit python-dir

SUMMARY = "GNU debugger (cross-canadian gdb for ${TARGET_ARCH} target)"
PN = "gdb-cross-canadian-${TRANSLATED_TARGET_ARCH}"
PR = "${INC_PR}.0"

DEPENDS = "nativesdk-ncurses nativesdk-expat nativesdk-gettext nativesdk-readline nativesdk-python"
RDEPENDS_${PN} += "nativesdk-python-core nativesdk-python-lang nativesdk-python-re \
                   nativesdk-python-codecs nativesdk-python-netclient"

GDBPROPREFIX = "--program-prefix='${TARGET_PREFIX}'"

EXTRA_OECONF_append = "--with-python=${WORKDIR}/python"

SSTATE_DUPWHITELIST += "${STAGING_DATADIR}/gdb"

do_configure_prepend() {
	# on cross-canadian, TARGET_ARCH is not same as SDK_ARCH
	# libpython2.7.so cannot be found
	PYTHON_DEB_HOST_MULTIARCH="${@arch_to_multiarch(d.getVar('SDK_ARCH', True))}-${TARGET_OS}${GNU_SUFFIX}"
cat > ${WORKDIR}/python << EOF
#! /bin/sh
case "\$2" in
        --includes) echo "-I${STAGING_INCDIR}/${PYTHON_DIR}/" ;;
        --ldflags) echo "-L${STAGING_LIBDIR}/../${PYTHON_DEB_HOST_MULTIARCH} -Wl,-rpath-link,${STAGING_LIBDIR}/../${PYTHON_DEB_HOST_MULTIARCH} -Wl,-rpath,${libdir}/../${PYTHON_DEB_HOST_MULTIARCH} -lpthread -ldl -lutil -lm -lpython${PYTHON_BASEVERSION}" ;;
        --exec-prefix) echo "${exec_prefix}" ;;
        *) exit 1 ;;
esac
exit 0
EOF
        chmod +x ${WORKDIR}/python
}

# we don't want gdb to provide bfd/iberty/opcodes, which instead will override the
# right bits installed by binutils.
do_install_append() {
	rm -rf ${D}${exec_prefix}/lib
	cross_canadian_bindirlinks
}
