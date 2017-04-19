#
# base recipe: meta-selinux/recipes-security/selinux/libselinux_2.4.bb
# base branch: jethro
#

DESCRIPTION = "SELinux runtime shared libraries"

HOMEPAGE =  "http://packages.debian.org/source/sid/libs/libselinux"

PR = "r2"


inherit debian-package lib_package pythonnative
PV = "2.3"

LICENSE = "PD & GPLv2"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=84b4d2c6ef954a2d4081e775a270d0d0 \
    file://utils/avcstat.c;endline=10;md5=bcadc03e47aaac313b251deb049fe801 \
"

DEPENDS += "libsepol libpcre swig-native python"

# EXTRA_OEMAKE is typically: -e MAKEFLAGS=
# "MAKEFLAGS= " causes problems as ENV variables will not pass to subdirs, so
# we redefine EXTRA_OEMAKE here
EXTRA_OEMAKE = "-e"

def get_policyconfigarch(d):
    import re
    target = d.getVar('TARGET_ARCH', True)
    p = re.compile('i.86')
    target = p.sub('i386',target)
    return "ARCH=%s" % (target)
EXTRA_OEMAKE += "${@get_policyconfigarch(d)}"
EXTRA_OEMAKE += "LDFLAGS='${LDFLAGS} -lpcre'"

do_compile() {
	oe_runmake all LIBDIR="${STAGING_LIBDIR}"

	oe_runmake pywrap -j1 \
	    INCLUDEDIR="${STAGING_INCDIR}" \
	    LIBDIR="${STAGING_LIBDIR}" \
	    PYLIBVER="python${PYTHON_BASEVERSION}" \
	    PYINC="-I${STAGING_INCDIR}/${PYTHON_DIR}" \
	    PYTHONLIBDIR="-L${STAGING_LIBDIR}/${PYTHON_DIR} -l${PYTHON_DIR}"
}

do_install() {
	oe_runmake install \
	    DESTDIR="${D}" \
	    PREFIX="${D}/${prefix}" \
	    INCLUDEDIR="${D}/${includedir}" \
	    LIBDIR="${D}/${libdir}" \
	    SHLIBDIR="${D}/${base_libdir}"

	oe_runmake install-pywrap \
	    DESTDIR="${D}" \
	    PYLIBVER="python${PYTHON_BASEVERSION}" \
	    PYLIBDIR="${D}/${libdir}/${PYTHON_DIR}"
	# Fix up the broken library symlink
	rm -f ${D}${libdir}/libselinux.so
	ln -s ../../lib/libselinux.so.1 ${D}${libdir}/libselinux.so

	# Remove unneeded directory to prevent QA warnings about install ${base_sbindir}
	if [ -d ${D}${base_sbindir} ]; then
		rmdir ${D}${base_sbindir}
	fi
}

# Add package follow debian
PACKAGES =+ "selinux-utils python-selinux"

FILES_selinux-utils += "${sbindir}/*"
FILES_python-selinux += "${PYTHON_SITEPACKAGES_DIR}/selinux/*"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/selinux/.debug"
FILES_${PN}-dev += "${libdir}/*"

# Correct .deb files
DEBIANAME_${PN} = "${PN}1"
PKG_${PN}-dev = "${PN}1-dev"

BBCLASSEXTEND = "native"
