#
# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-selinux/tree/recipes-security/setools/setools_3.3.8.bb
# base branch: jethro
#

SUMMARY = "tools for Security Enhanced Linux policy analysis"
DESCRIPTION = "Security-enhanced Linux is a patch of the Linux kernel and a number \
of utilities with enhanced security functionality designed to add \
mandatory access controls to Linux. These are Tools for analysing \
security policy on SELinux systems."
HOMEPAGE = "http://oss.tresys.com/projects/setools"

PR = "r3"

inherit debian-package
PV = "3.3.8"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=26035c503c68ae1098177934ac0cc795 \
    file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.LGPL;md5=fbc093901857fcd118f065f900982c24 \
"

DEPENDS = "bison-native flex-native tcl-native python \
           libsepol libselinux libxml2 tcl \
           "

# setools-configure-ac_debian.patch:
# 	Do not find library in host machine.
# setools-cross-ar.patch:
# 	Use cross ar "$(AR)" instead of "ar".
# setools-Fix-python-setools-Makefile.am-for-cross_debian.patch:
# 	Use cross compiler when build python modules.
SRC_URI += "file://setools-configure-ac_debian.patch \
            file://setools-cross-ar.patch \
            file://setools-Fix-python-setools-Makefile.am-for-cross_debian.patch \
            "

inherit autotools-brokensep pythonnative pkgconfig

# Follow debian/rules
EXTRA_OECONF = "--enable-swig-python --enable-swig-tcl --disable-bwidget-check \
                --with-tcl=${STAGING_BINDIR_CROSS} \
                --with-tclinclude=${STAGING_INCDIR}/tcl8.6 \
                --disable-selinux-check \
"

# Change path of sepol and selinux to sysroot
EXTRA_OECONF += "--with-sepol-devel=${STAGING_LIBDIR}/.. \
                 --with-selinux-devel=${STAGING_LIBDIR}/.."

EXTRA_OECONF_append_class-native = " \
    --disable-gui \
    --with-tcl=${STAGING_LIBDIR_NATIVE}/tcl8.6 \
"

PACKAGECONFIG_class-target ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'tk gui', '', d)}"
PACKAGECONFIG[tk] = "--with-tk=${STAGING_BINDIR_CROSS} --with-tkinclude=${STAGING_INCDIR}/tcl8.6, --without-tk, tk8.6"
PACKAGECONFIG[gui] = "--enable-gui, --disable-gui, gtk+ libglade"

# need to export these variables for python-config to work
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

CFLAGS_append = " -fPIC"
CXXFLAGS_append = " -fPIC"

do_configure_prepend() {
	export ac_cv_policydb_version_max=26
	export PYTHON=python
	export PYLIBVER='python${PYTHON_BASEVERSION}'
	export PYTHON_CPPFLAGS="-I${STAGING_INCDIR}/${PYLIBVER}"
	export PYTHON_LDFLAGS="${STAGING_LIBDIR}/lib${PYLIBVER}.so"
	export PYTHON_SITE_PKG="${libdir}/${PYLIBVER}/site-packages"
	export REAL_TCL_BIN_DIR=${STAGING_BINDIR_NATIVE}
}

do_install_append() {
	# Follow debian/setools-gui.install
	mkdir -p ${D}${sysconfdir}/logwatch/conf/logfiles/ \
	         ${D}${sysconfdir}/logwatch/scripts/services/ \
	         ${D}${sysconfdir}/logwatch/conf/services/
	# seaudit will be built if gui is enabled,
	# we need check if files existed before install
	test -f ${S}/seaudit/seaudit-report-group.conf && \
	    cp ${S}/seaudit/seaudit-report-group.conf \
	         ${D}${sysconfdir}/logwatch/conf/logfiles/
	test -f ${S}/seaudit/seaudit-report-service && \
	    cp ${S}/seaudit/seaudit-report-service \
	         ${D}${sysconfdir}/logwatch/scripts/services/
	test -f ${S}/seaudit/seaudit-report-service.conf && \
	    cp ${S}/seaudit/seaudit-report-service.conf \
	         ${D}${sysconfdir}/logwatch/conf/services/

	test -f ${S}/debian/setools-gui.menu && \
	    mkdir -p ${D}${datadir}/menu && \
	    cp ${S}/debian/setools-gui.menu ${D}${datadir}/menu/setools-gui

	# Remove unnecessary files
	find ${D}${libdir} -name '*.pyc' -delete
	find ${D}${libdir} -name '*.pyo' -delete
	find ${D}${libdir} -name 'setools-1.0.egg-info' -delete

	# Fix permission follow debian/rules
	find ${D}${libdir}/setools/ -type f -name '*.tcl' -exec chmod -x {} \;
	find ${D}${libdir}/setools/ -type f -name '*.tcl' -exec chmod -x {} \;

	if [ -f ${D}${sysconfdir}/logwatch/scripts/services/seaudit-report-service ]; then
		chmod +x ${D}${sysconfdir}/logwatch/scripts/services/seaudit-report-service
	fi

	if [ -f ${D}${datadir}/setools/3.3/seaudit-report-service ]; then
		chmod +x ${D}${datadir}/setools/3.3/seaudit-report-service
	fi
}

PACKAGE_BEFORE_PN =+ "libapol libpoldiff libqpol libseaudit libsefs \
                      libsetools-tcl python-${PN} setools-gui \
                      "

FILES_libapol = "${libdir}/libapol${SOLIBS}"
FILES_libpoldiff = "${libdir}/libpoldiff${SOLIBS}"
FILES_libqpol = "${libdir}/libqpol${SOLIBS}"
FILES_libseaudit = "${libdir}/libseaudit${SOLIBS}"
FILES_libsefs = "${libdir}/libsefs${SOLIBS}"
FILES_libsetools-tcl = "${libdir}/setools/apol/* \
                        ${libdir}/setools/poldiff/* \
                        ${libdir}/setools/qpol/* \
                        ${libdir}/setools/seaudit/* \
                        ${libdir}/setools/sefs/* \
                        "
FILES_python-${PN} = "${libdir}/python*/*-packages/setools/*"
FILES_setools-gui = "${sysconfdir}/* \
                     ${bindir}/apol ${bindir}/seaudit-report ${bindir}/sediffx \
                     ${sbindir}/* \
                     ${libdir}/setools/apol_tcl/* \
                     ${datadir}/setools/*/apol* \
                     ${datadir}/setools/*/seaudit* \
                     ${datadir}/setools/*/sediff* \
                     ${datadir}/setools/*/domaintrans_help.txt \
                     ${datadir}/setools/*/dot_seaudit \
                     ${datadir}/setools/*/file_relabel_help.txt \
                     ${datadir}/setools/*/infoflow_help.txt \
                     ${datadir}/setools/*/types_relation_help.txt \
                     ${datadir}/menu/setools-gui \
                     "
FILES_${PN}-dbg += "${libdir}/python*/*-packages/setools/.debug \
                    ${libdir}/setools/*/.debug \
                    "

RDEPENDS_setools-gui += "libsetools-tcl"

BBCLASSEXTEND = "native"
