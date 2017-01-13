#
# base recipe: meta/recipes-devtools/rpm/rpm_4.11.2.bb
# base branch: master
#

PR = "r2"

inherit debian-package
PV = "4.11.3"

SUMMARY = "The RPM package management system"
DESCRIPTION = "The RPM Package Manager (RPM) is a powerful command line driven \
package management system capable of installing, uninstalling, \
verifying, querying, and updating software packages. Each software \
package consists of an archive of files along with information about \
the package like its version, a description, etc."

SUMMARY_${PN}-dev = "Development files for manipulating RPM packages"

DESCRIPTION_${PN}-dev = "\
This package contains the RPM C library and header files. These \
development files will simplify the process of writing programs that \
manipulate RPM packages and databases. These files are intended to \
simplify the process of creating graphical package managers or any \
other tools that need an intimate knowledge of RPM packages in order \
to function."

SUMMARY_python-rpm = "Python bindings for apps which will manupulate RPM packages"
DESCRIPTION_python-rpm = "\
The rpm-python package contains a module that permits applications \
written in the Python programming language to use the interface \
supplied by the RPM Package Manager libraries."

HOMEPAGE = "http://www.rpm.org"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=f5259151d26ff18e78023450a5ac8d96"

# FIXME: fail when regenerate config.status and Makefile.in
# Makefile.in is already available, temporarily remove this step
# with no_recreate_Makefile_in.patch
SRC_URI += " \
file://no_recreate_Makefile_in.patch \
file://pythondeps.sh \
file://change-aclocal-version.patch \
file://remove-db3-from-configure.patch \
file://add_RPMSENSE_MISSINGOK_to_rpmmodule.patch \
file://support-suggests-tag.patch \
file://remove-dir-check.patch \
file://disable_shortcircuited.patch \
file://fix_libdir.patch \
file://rpm-scriptetexechelp.patch \
"

DEPENDS = "db libxml2 xz-utils file popt nss bzip2 elfutils patch attr \
               zlib acl gzip make binutils python"
DEPENDS_append_class-native = " file-replacement-native"
DEPENDS_class-nativesdk = "nativesdk-db nativesdk-libxml2 nativesdk-xz-utils nativesdk-file \
		nativesdk-popt nativesdk-nss nativesdk-bzip2 nativesdk-elfutils nativesdk-attr \
		nativesdk-zlib nativesdk-acl nativesdk-make nativesdk-python"

inherit autotools
inherit pythonnative
inherit pkgconfig
inherit gettext
# --without-selinux: Don't use selinux support
EXTRA_OECONF += "--host=${HOST_SYS} \
		--program-prefix= \
		--prefix=${prefix} \
		--exec-prefix=${prefix} \
		--bindir=${prefix}/bin \
		--sbindir=${prefix}/sbin \
		--sysconfdir=${sysconfdir} \
		--datadir=${prefix}/share \
		--includedir=${prefix}/include \
		--libdir=${prefix}/lib \
		--libexecdir=${prefix}/libexec \
		--localstatedir=${localstatedir} \
		--sharedstatedir=${prefix}/com \
		--mandir=${mandir} \
		--infodir=${infodir} \
		--disable-dependency-tracking \
		--with-acl \
		--without-lua \
		--without-cap \
		--enable-shared \
		--enable-python \
		--with-external-db \
		--without-selinux \
		"
LDFLAGS_append = " -Wl,-Bsymbolic-functions -ffunction-sections"
CCFLAGS_append = " -fPIC "
CXXFLAGS_append = " -fPIC "
CFLAGS_append = " -fPIC -DRPM_VENDOR_WINDRIVER -DRPM_VENDOR_POKY -DRPM_VENDOR_OE "

EXTRA_OEMAKE += " LIBTOOL=${HOST_SYS}-libtool"

do_configure_prepend() {
	rm -rf sqlite
	rm -f m4/libtool.m4
	rm -f m4/lt*.m4
	rm -rf db3/configure*
}

# Autoreconf breaks with
# gnu-configize: `configure.ac' or `configure.in' is required
# Works good enough without autoreconf
do_configure () {
        # configure cannot find sechash.h by default
        export CPPFLAGS="-I${STAGING_INCDIR_NATIVE} -I${STAGING_INCDIR_NATIVE}/nss3"
        # set some variables so python can see Python.h header
        export BUILD_SYS=${BUILD_SYS}
        export HOST_SYS=${HOST_SYS}
        export STAGING_INCDIR=${STAGING_INCDIR}
        export STAGING_LIBDIR=${STAGING_LIBDIR}
        oe_runconf
}

do_install_append_class-native() {
	mv ${D}/${base_bindir}/rpm ${D}/${bindir}/
	rmdir --ignore-fail-on-non-empty ${D}${base_bindir}
	# Correct links after moving rpm
	ln -sf rpm ${D}${bindir}/rpmquery
	ln -sf rpm ${D}${bindir}/rpmverify

	rm -f ${D}${prefix}/lib/*.la
	rm -f ${D}${prefix}/lib/rpm-plugins/*.la
	rm -f ${D}/${libdir}/python%{with_python_version}/site-packages/*.{a,la}
	rm -f ${D}/${libdir}/python%{with_python_version}/site-packages/rpm/*.{a,la}
	rm -fr ${D}/var
	install -d ${D}${prefix}/lib/rpm/bin
	ln -s ../debugedit ${D}${prefix}/lib/rpm/bin/debugedit
	ln -s ../rpmdeps ${D}${prefix}/lib/rpm/bin/rpmdeps-oecore
	install -m 0755 ${WORKDIR}/pythondeps.sh ${D}/${libdir}/rpm/pythondeps.sh
}

do_install_append_class-target() {
	mv ${D}${base_bindir}/rpm ${D}${bindir}/
	rmdir --ignore-fail-on-non-empty ${D}${base_bindir}
	ln -s ../lib/rpm/debugedit ${D}${bindir}/debugedit
	# Correct links after moving rpm
	ln -sf rpm ${D}${bindir}/rpmquery
	ln -sf rpm ${D}${bindir}/rpmverify
}

pkg_postinst_${PN}() {
	[ "x\$D" == "x" ] && ldconfig
	test -f ${localstatedir}/lib/rpm/Packages || rpm --initdb
	rm -f ${localstatedir}/lib/rpm/Filemd5s \
		${localstatedir}/lib/rpm/Filedigests \
		${localstatedir}/lib/rpm/Requireversion \
		${localstatedir}/lib/rpm/Provideversion
}
pkg_postrm_${PN}() {
	[ "x\$D" == "x" ] && ldconfig
}
# Don't use "python-${PN}",
# if we bitbake nativesdk-rpm, we will get nativesdk-python-nativesdk-rpm,
# so use "python-rpm" instead
PACKAGES =+ "debugedit librpm librpmbuild librpmio librpmsign rpm-common rpm2cpio python-rpm"
PROVIDES += "python-rpm"

FILES_debugedit = "${bindir}/debugedit ${libdir}/rpm/debugedit"
FILES_librpm = "${libdir}/librpm${SOLIBS}"
FILES_librpmbuild = "${libdir}/librpmbuild${SOLIBS}"
FILES_librpmio = "${libdir}/librpmio${SOLIBS}"
FILES_librpmsign = "${libdir}/librpmsign${SOLIBS}"
FILES_rpm-common = "${libdir}/rpm-plugins/* \
                    ${libdir}/rpm/platform/* \
                    ${libdir}/rpm/macro* \
                    ${libdir}/rpm/rpmrc"
FILES_rpm2cpio = "${bindir}/rpm2cpio"
FILES_python-rpm = "${libdir}/python${PYTHON_BASEVERSION}/*/rpm/*"
FILES_${PN}-dbg += "${libdir}/rpm-plugins/.debug \
                    ${libdir}/python${PYTHON_BASEVERSION}/*/rpm/.debug"

DEBIANNAME_${PN}-dev = "librpm-dev"
DEBIANNAME_${PN}-dbg = "librpm-dbg"

RDEPENDS_${PN} = "base-files run-postinsts perl \
                  rpm2cpio debugedit rpm-common"
RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN}_class-nativesdk = ""

# Follow debian/control
RDEPENDS_python-rpm = "python librpm librpmio librpmbuild librpmsign"
RDEPENDS_rpm2cpio = "rpm-common"
RDEPENDS_librpm = "librpmio"
RDEPENDS_librpmbuild = "librpm librpmio"
RDEPENDS_librpmsign = "librpm librpmio"
RDEPENDS_debugedit = "librpmio"

BBCLASSEXTEND = "native nativesdk"

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://rpm-native-test/run_native_test_rpm \
        file://rpm-native-test/run_checkdepend_command \
        file://rpm-native-test/run_get_info_command \
        file://rpm-native-test/run_help_command \
        file://rpm-native-test/run_usage_command \
        file://rpm-native-test/quilt-0.64-1.1.noarch.rpm.test \
"

DEBIAN_NATIVE_TESTS = "run_native_test_rpm"
TEST_DIR = "${B}/native-test"
