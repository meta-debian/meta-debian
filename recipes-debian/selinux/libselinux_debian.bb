# base recipe: meta-selinux/recipes-security/selinux/libselinux_2.8.bb
# base branch: warrior

SUMMARY = "SELinux library and simple utilities"
DESCRIPTION = "libselinux provides an API for SELinux applications to get and set \
process and file security contexts and to obtain security policy \
decisions.  Required for any applications that use the SELinux API."
HOMEPAGE = "https://github.com/SELinuxProject"
SECTION = "base"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84b4d2c6ef954a2d4081e775a270d0d0"

inherit debian-package
require recipes-debian/sources/libselinux.inc

DEBIAN_QUILT_PATCHES = ""

SRC_URI += "\
        file://libselinux-drop-Wno-unused-but-set-variable.patch \
        file://libselinux-make-O_CLOEXEC-optional.patch \
        file://libselinux-make-SOCK_CLOEXEC-optional.patch \
        file://libselinux-define-FD_CLOEXEC-as-necessary.patch \
        file://0001-src-Makefile-fix-includedir-in-libselinux.pc.patch \
        file://0001-libselinux-Do-not-define-gettid-if-glibc-2.30-is-use.patch \
        "

inherit lib_package pythonnative

DEPENDS += "libsepol python libpcre swig-native"
DEPENDS_append_libc-musl = " fts"
RDEPENDS_${PN}-python += "python-core python-shell"

PACKAGES += "${PN}-python"
FILES_${PN}-python = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
FILES_${PN}-dbg += "${libdir}/python${PYTHON_BASEVERSION}/site-packages/selinux/.debug/*"

def get_policyconfigarch(d):
    import re
    target = d.getVar('TARGET_ARCH', True)
    p = re.compile('i.86')
    target = p.sub('i386',target)
    return "ARCH=%s" % (target)
EXTRA_OEMAKE += "${@get_policyconfigarch(d)}"

EXTRA_OEMAKE += "LDFLAGS='${LDFLAGS} -lpcre' LIBSEPOLA='${STAGING_LIBDIR}/libsepol.a'"
EXTRA_OEMAKE_append_libc-musl = " FTS_LDLIBS=-lfts"

do_compile() {
	oe_runmake all \
		INCLUDEDIR='${STAGING_INCDIR}' \
		LIBDIR='${STAGING_LIBDIR}'
}

do_compile_append() {
	oe_runmake pywrap -j1 \
		INCLUDEDIR='${STAGING_INCDIR}' \
		LIBDIR='${STAGING_LIBDIR}' \
		PYINC='-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}'
}

do_install() {
	oe_runmake install \
		DESTDIR="${D}" \
		PREFIX="${prefix}" \
		INCLUDEDIR="${includedir}" \
		LIBDIR="${libdir}" \
		SHLIBDIR="${base_libdir}" \
		SYSTEMDDIR="${systemd_unitdir}"
}

do_install_append() {
	oe_runmake install-pywrap swigify \
		PYTHONLIBDIR=${D}${libdir}/python${PYTHON_BASEVERSION}/site-packages
	if ! ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','true','false',d)}; then
		rm -rf ${D}${base_sbindir}
	fi
}

BBCLASSEXTEND = "native"