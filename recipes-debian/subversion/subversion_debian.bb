SUMMARY = "Advanced version control system"
DESCRIPTION = "\
 Apache Subversion, also known as svn, is a centralised version control \
 system.  Version control systems allow many individuals (who may be \
 distributed geographically) to collaborate on a set of files (source \
 code, websites, etc).  Subversion began with a CVS paradigm and \
 supports all the major features of CVS, but has evolved to support \
 many features that CVS users often wish they had."
HOMEPAGE = "http://subversion.apache.org/"

PR = "r0"
inherit debian-package
PV = "1.8.10"

LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=1c2f0119e478700b5428e26386cff923 \
	file://tools/dist/_gnupg.py;endline=25;md5=565706acba7cfcf491084aa83855a156"

# svn-xcompile-configure.patch
#	remove the run test program while cross compiling
# remove-use-host-library_debian.patch:
#	fix QA issue host include and/or library paths were used
# remove-undef-bool_debian.patch:
#	fix error: bool undeclared (first use in this function) 
#	           in usr/lib/perl/5.20.2/CORE/handy.h.
#	Patching the generated code allows the bindings to build and the tests to work.
# fix-generate-perl-makefile_debian.patch:
#	We will generate perl Makefile with our configuration.
# fix-build-with-python_debian.patch:
#	Replace '(LINK)' by '(LINK_LIB)' to generate .so* files
# correct-apr-version_debian.patch:
# 	User apr version 1.x, this patch base on debian/apr-api
SRC_URI += "\
	file://svn-xcompile-configure.patch \
	file://remove-use-host-library_debian.patch \
	file://remove-undef-bool_debian.patch \
	file://fix-generate-perl-makefile_debian.patch \
	file://fix-build-with-python_debian.patch \
	file://correct-apr-version_debian.patch \
	"
# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

# Avoid a parallel build problem
PARALLEL_MAKE = ""

inherit autotools-brokensep gettext pythonnative perlnative cpan-base

DEPENDS += "swig-native db apr chrpath-native libsasl2"
MIN_SQLITE_VER="3.7.12"
EXTRA_OECONF += "\
	--with-apr=${STAGING_BINDIR_CROSS} \
	--with-apr-util=${STAGING_BINDIR_CROSS} \
	--enable-sqlite-compatibility-version=${MIN_SQLITE_VER} \
	--with-berkeley-db=db.h:${STAGING_INCDIR}:${STAGING_LIBDIR}:db \
	--with-sasl=${STAGING_DIR_HOST}${prefix} \
	--with-swig=${STAGING_DIR_NATIVE}${prefix_native} \
"
# Currently, we don't have ruby recipe,
# disable checking for ruby path to prevent using ruby from host system
CACHED_CONFIGUREVARS += "ac_cv_path_RUBY=no"

PACKAGECONFIG ??= "apache2 serf"
PACKAGECONFIG[serf] = "--with-serf=${STAGING_DIR_HOST}${prefix}, --without-serf, serf,"
PACKAGECONFIG[gnome-keyring] = "--with-gnome-keyring, --without-gnome-keyring,libgnome-keyring,"
PACKAGECONFIG[kwallet] = "--with-kwallet, --without-kwallet, dbus kde4libs,"
PACKAGECONFIG[apache2] = "\
	--with-apxs=${STAGING_BINDIR_NATIVE}/apxs \
	--disable-mod-activation \
	--with-apache-libexecdir=${libdir}/apache2/modules, \
	--without-apache-libexecdir,apache2,"

#export some variable from poky, to use for python command
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

# Env var which tells perl where the perl include files are
export PERL_INC = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}/CORE"
export PERL_LIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERL_ARCHLIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERLHOSTLIB = "${STAGING_LIBDIR_NATIVE}/perl-native/perl/${@get_perl_version(d)}/"

export LIBTOOL="${STAGING_BINDIR}/crossscripts/${HOST_SYS}-libtool"
EXTRA_OEMAKE += " 'LIBTOOL=${LIBTOOL}'"

do_configure() {
	perl_version=${PERLVERSION}
	short_perl_version=`echo ${perl_version%.*}`
	. ${STAGING_LIBDIR}/perl/config.sh
	sed -i -e "s:##EXTRA_CPANFLAGS##:${EXTRA_CPANFLAGS}:g" \
	       -e "s:##CC##:${cc}:g" \
	       -e "s:##LD##:${ld}:g" \
	       -e "s:##LDFLAGS##:${ldflags}:g" \
	       -e "s:##CCFLAGS##:${ccflags}:g" \
	       -e "s:##LDDLFLAGS##:${lddlflags}:g" \
	       -e "s:##INSTALLVENDORLIB##:${datadir}/perl5:g" \
	       -e "s:##INSTALLVENDORARCH##:${libdir}/perl5/$short_perl_version:g" \
	       -e "s:##INSTALLSITELIB##:${libdir}/perl5/$short_perl_version:g" \
	       -e "s:##INSTALLSITEARCH##:${libdir}/perl5/$short_perl_version:g" \
	       -e "s:##INSTALLSITEMAN3DIR##:${mandir}/man3:g" \
	       -e "s:##INSTALLMAN3DIR##:${mandir}/man3:g" \
	       -e "s:##INSTALLVENDORMAN3DIR##:${mandir}/man3:g" \
               ${S}/Makefile.in
	oe_runconf
}

CPPFLAGS += "-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}"
do_compile_append() {
	oe_runmake autogen-swig
	oe_runmake tools
	oe_runmake -j1 swig-pl MAN3EXT=3perl
	oe_runmake -C ${S}/subversion/bindings/swig/perl/native all \
		MAN3EXT=3perl OPTIMIZE="-g -Wall ${CFLAGS}" LD="${CC}"

	oe_runmake swig-py \
		PYTHON=python${PYTHON_BASEVERSION} \
		PYVER=${PYTHON_BASEVERSION} \
		swig_pydir=${PYTHON_SITEPACKAGES_DIR}/libsvn \
		swig_pydir_extra=${PYTHON_SITEPACKAGES_DIR}/libsvn	
}
do_install_append() {
	oe_runmake local-install install-tools DESTDIR="${D}" toolsdir=${bindir}
	oe_runmake install-swig-pl-lib DESTDIR="${D}"
	oe_runmake -C ${S}/subversion/bindings/swig/perl/native install \
		MAN3EXT=3perl DESTDIR="${D}"

	oe_runmake install-swig-py \
		PYTHON=python${PYTHON_BASEVERSION} \
		PYVER=${PYTHON_BASEVERSION} \
		swig_pydir=${PYTHON_SITEPACKAGES_DIR}/libsvn \
		swig_pydir_extra=${PYTHON_SITEPACKAGES_DIR}/libsvn \
		DESTDIR=${D}

	# Follow debian/subversion-tools.install
	install -m 0755 ${S}/debian/contrib/svn_apply_autoprops.py \
		${D}${bindir}/svn_apply_autoprops
	install -m 0755 ${S}/debian/contrib/svn-clean \
		${D}${bindir}/svn-clean
	install -m 0755 ${S}/debian/contrib/svn-fast-backup \
		${D}${bindir}/svn-fast-backup
	install -m 0755 ${S}/debian/contrib/svn_load_dirs/svn_load_dirs.pl \
		${D}${bindir}/svn_load_dirs
	install -D -m 0755 ${S}/debian/contrib/emacs/50psvn.el \
		${D}${sysconfdir}/emacs/site-start.d/50psvn.el
	install -D -m 0755 ${S}/debian/contrib/emacs/dsvn.el \
		${D}${datadir}/emacs/site-lisp/dsvn.el
	install -m 0755 ${S}/debian/contrib/emacs/psvn.el \
		${D}${datadir}/emacs/site-lisp/psvn.el
	install -D -m 0755 ${S}/debian/contrib/commit-email.pl \
		${D}${datadir}/subversion/hook-scripts/commit-email.pl

	# Follow debian/subversion-tools.install
	install -m 0755 ${S}/debian/bin/* ${D}${bindir}
	install -m 0755 ${S}/tools/server-side/svn-backup-dumps.py \
		${D}${bindir}/svn-backup-dumps

	# Follow debian/libapache2-mod-svn.install
	install -D -m 0644 ${S}/debian/dav_svn.conf \
		${D}${sysconfdir}/apache2/mods-available/dav_svn.conf
	install -m 0644 ${S}/debian/dav_svn.load \
		${D}${sysconfdir}/apache2/mods-available/dav_svn.load
	install -m 0644 ${S}/debian/authz_svn.load \
		${D}${sysconfdir}/apache2/mods-available/authz_svn.load

	# Follow debian/rules
	install -m 0755 ${S}/tools/backup/hot-backup.py \
		${D}${bindir}/svn-hot-backup
	install -m 0755 ${S}/tools/examples/svnshell.py \
		${D}${bindir}/svnshell
	install -D -m 644 ${S}/tools/client-side/bash_completion \
		${D}${sysconfdir}/bash_completion.d/subversion
	cp -r ${S}/tools/hook-scripts/* ${D}${datadir}/subversion/hook-scripts
	rm ${D}${datadir}/subversion/hook-scripts/*.in \
	   ${D}${datadir}/subversion/hook-scripts/*.rb
	rm -r ${D}${datadir}/subversion/hook-scripts/mailer/tests
	cd ${D}${datadir}/subversion/hook-scripts; \
		chmod 0755 commit-email.pl commit-access-control.pl \
			mailer/mailer.py verify-po.py svnperms.py
	cd -
	
	chrpath -d ${D}${libdir}/perl5/*/auto/SVN/_*/_*.so
	rm ${D}${bindir}/diff* ${D}${includedir}/subversion-1/svn-revision.txt \
	   ${D}${libdir}/apache2/modules/mod_dontdothat.so
}
PACKAGES =+ "libapache2-mod-svn libsvn-perl python-subversion libsvn subversion-tools"

# Provide libsvn-dev package, useful for satisfying runtime dependencies.
RPROVIDES_${PN}-dev = "libsvn-dev"

FILES_libapache2-mod-svn = "\
	${sysconfdir}/apache2/mods-available \
	${libdir}/apache2/modules/*"
FILES_libsvn-perl = "\
	${libdir}/libsvn_swig_perl-1${SOLIBS} \
	${libdir}/perl5/*/SVN \
	${libdir}/perl5/*/auto/SVN/*/*"
FILES_libsvn = "${libdir}/*${SOLIBS}"
FILES_python-subversion = "\
	${bindir}/svnshell \
	${PYTHON_SITEPACKAGES_DIR}/libsvn/* \
	${libdir}/libsvn_swig_py*${SOLIBS}"
FILES_subversion-tools = "\
	${sysconfdir}/emacs \
	${bindir}/fsfs-* \
	${bindir}/svn-* \
	${bindir}/svnraisetreeconflict \
	${bindir}/svn_apply_autoprops \
	${bindir}/svn_load_dirs \
	${bindir}/svnwrap \
	${datadir}/emacs \
	${datadir}/subversion"
FILES_${PN}-dbg += "\
	${libdir}/apache2/modules/.debug \
	${PYTHON_SITEPACKAGES_DIR}/libsvn/.debug \
	${libdir}/perl5/*/auto/SVN/*/.debug"

DEBIAN_NOAUTONAME_libsvn-perl = "1"
DEBIANNAME_${PN}-dev = "libsvn-dev"
DEBIANNAME_libsvn = "libsvn1"
