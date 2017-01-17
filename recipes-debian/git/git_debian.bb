#
# base recipe: meta/recipes-devtools/git/git_2.5.0.bb
# base branch: master
# base commit: 9170c34a015c2847307ebd3758eb4f8e6b86b362
#

SUMMARY = "Distributed version control system"
HOMEPAGE = "http://git-scm.com"

PR = "r0"

inherit debian-package
PV = "2.1.4"

LICENSE = "GPLv2 & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=7c0d7ef03a7eb04ce795b0f60e68e7e1 \
    file://LGPL-2.1;md5=7c544716d354798a61bc76aac5d44d07 \
"

DEPENDS = "openssl curl zlib expat libpcre"

PROVIDES_append_class-native = " git-replacement-native"

inherit useradd
USERADD_PACKAGES = "${PN}-daemon-run ${PN}-daemon-sysvinit"
USERADD_PARAM_${PN}-daemon-run = " \
    --system --home /nonexistent --no-create-home gitlog; \
    --system --home /nonexistent --no-create-home gitdaemon \
"
USERADD_PARAM_${PN}-daemon-sysvinit = " \
    --system --home /nonexistent --no-create-home gitdaemon \
"

inherit autotools-brokensep perlnative

# Follow debian/rules
libexecdir = "${libdir}/git-core"
EXTRA_OEMAKE = " \
    NO_OPENSSL=1 \
    prefix=${prefix} \
    libexecdir=${libexecdir} \
    gitexecdir=${libexecdir} \
    mandir=${mandir} \
    htmldir=${docdir}/${DPN}/html \
    INSTALLDIRS=vendor \
    USE_SRV_RR=1 \
    USE_LIBPCRE=1 \
    SANE_TOOL_PATH= INSTALL=install TAR=tar \
    NO_CROSS_DIRECTORY_HARDLINKS=1 NO_INSTALL_HARDLINKS=1 \
    DEFAULT_PAGER=pager DEFAULT_EDITOR=editor \
    CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}' \
"

# Follow base recipe
EXTRA_OECONF = " \
    --with-perl=${STAGING_BINDIR_NATIVE}/perl-native/perl \
    ac_cv_snprintf_returns_bogus=no \
    ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
"
EXTRA_OEMAKE += " \
    'PERL_PATH=/usr/bin/env perl' \
"

# git store patch files in debian/diff not debian/patches
DEBIAN_QUILT_PATCHES = "${DEBIAN_UNPACK_DIR}/debian/diff"
do_debian_patch_prepend () {
	# Generate file series
	cd ${DEBIAN_QUILT_PATCHES}
	for i in $(ls *.diff *.patch); do
		echo $i >> ${DEBIAN_QUILT_PATCHES}/series
	done
	cd -
}

do_install () {
	#
	# Follow debian/rules
	#
	oe_runmake install DESTDIR='${D}'
	oe_runmake -C contrib/subtree install DESTDIR='${D}'
	oe_runmake -C contrib/mw-to-git install DESTDIR='${D}'

	# bash completion
	install -d -m 0755 ${D}${datadir}/bash-completion/completions
	install -m 0644 ${S}/contrib/completion/git-completion.bash \
			${D}${datadir}/bash-completion/completions/git
	ln -s git ${D}${datadir}/bash-completion/completions/gitk

	# bash prompt
	install -m 0644 ${S}/contrib/completion/git-prompt.sh \
			${D}${libdir}/git-core/git-sh-prompt
	install -d -m 0755 ${D}${sysconfdir}/bash_completion.d
	install -m 0644 ${S}/debian/git-prompt.completion \
			${D}${sysconfdir}/bash_completion.d/git-prompt

	# contrib hooks
	install -d -m 0755 ${D}${datadir}/git-core/contrib/hooks
	set -e; for i in ${S}/contrib/hooks/*; do
		test "$i" != "${S}/contrib/hooks/multimail" || continue
		install -m 0755 "$i" ${D}${datadir}/git-core/contrib/hooks/
	done
	# contrib
	install -d -m 0755 ${D}${docdir}/git
	cp -R ${S}/contrib ${D}${docdir}/git/
	find ${D}${docdir}/git/contrib -type f | xargs chmod 0644
	find ${D}${docdir}/git/contrib -type d | xargs chmod 0755
	# remove contrib hooks, they are now installed in
	# /usr/share/git-core/contrib, keep symlink for backward compatibility
	rm -rf ${D}${docdir}/git/contrib/hooks
	ln -s ../../../git-core/contrib/hooks ${D}${docdir}/git/contrib/
	find ${D}${docdir}/git/ -name .gitignore | xargs rm -f

	# git-daemon-run
	install -d -m 0755 ${D}${sysconfdir}/sv/git-daemon/log
	install -m 0755 ${S}/debian/git-daemon/run \
		${D}${sysconfdir}/sv/git-daemon/run
	install -m0755 ${S}/debian/git-daemon/log/run \
		${D}${sysconfdir}/sv/git-daemon/log/run

	# git-daemon-sysvinit
	install -d -m 0755 ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/git-daemon.init \
		${D}${sysconfdir}/init.d/git-daemon
	install -d -m 0755 ${D}${sysconfdir}/default
	install -m 0644 ${S}/debian/git-daemon.default \
		${D}${sysconfdir}/default/git-daemon
        install -d -m 0755 ${D}${datadir}/git-core/sysvinit
	>${D}${datadir}/git-core/sysvinit/sentinel
	chmod 0644 ${D}${datadir}/git-core/sysvinit/sentinel

	# git-el
	install -m 0644 -D ${S}/debian/git-el.emacsen-startup \
		${D}${sysconfdir}/emacs/site-start.d/50git-core.el
	install -m 0755 -D ${S}/debian/git-el.emacsen-install \
		${D}${libdir}/emacsen-common/packages/install/git
	install -m 0755 -D ${S}/debian/git-el.emacsen-remove \
		${D}${libdir}/emacsen-common/packages/remove/git
	install -d -m 0755 ${D}${datadir}/git-core/emacs
	install -m 0644 ${S}/contrib/emacs/*.el \
		${D}${datadir}/git-core/emacs/
	install -d -m 0755 ${D}${docdir}/git-el
	ln -s ../git/README.emacs \
		${D}${docdir}/git-el/README.Debian
	install -d -m 0755 ${D}${docdir}/git/contrib
	ln -s ../../../git-core/emacs \
		${D}${docdir}/git/contrib/emacs

	# gitweb
	install -d -m 0755 ${D}${libdir}/cgi-bin
	ln -s ../../share/gitweb/gitweb.cgi \
		${D}${libdir}/cgi-bin/gitweb.cgi
	install -m 0644 ${S}/debian/gitweb.conf ${D}${sysconfdir}/gitweb.conf
	install -d -m 0755 ${D}${sysconfdir}/apache2/conf-available
	install -m 0644 ${S}/debian/gitweb.apache2.conf \
		${D}${sysconfdir}/apache2/conf-available/gitweb.conf
	install -d -m 0755 ${D}${datadir}/gitweb
	ln -s gitweb.cgi ${D}${datadir}/gitweb/index.cgi

	rm ${D}${exec_prefix}/lib/perl-native/perl/*/Error.pm

	# man3 was installed in wrong location
	if [ -e ${D}${prefix}/man/man3 ]; then
		mv ${D}${prefix}/man/man3 ${D}${mandir}/
		rm -rf ${D}${prefix}/man
	fi
}

perl_native_fixup () {
	sed -i -e 's#${STAGING_BINDIR_NATIVE}/perl-native/#${bindir}/#' \
	       -e 's#${libdir}/perl-native/#${libdir}/#' \
	    ${@d.getVar("PERLTOOLS", True).replace(' /',d.getVar('D', True) + '/')}

	# ${libdir} is not applicable here, perl-native files are always
	# installed to /usr/lib on both 32/64 bits targets.
	mv ${D}${exec_prefix}/lib/perl-native/perl ${D}${libdir}
	rmdir -p ${D}${exec_prefix}/lib/perl-native || true
}

REL_GIT_EXEC_PATH = "${@os.path.relpath(libexecdir, bindir)}/git-core"
REL_GIT_TEMPLATE_DIR = "${@os.path.relpath(datadir, bindir)}/git-core/templates"

do_install_append_class-target () {
	perl_native_fixup
}

do_install_append_class-native() {
	create_wrapper ${D}${bindir}/git \
		GIT_EXEC_PATH='`dirname $''realpath`'/${REL_GIT_EXEC_PATH} \
		GIT_TEMPLATE_DIR='`dirname $''realpath`'/${REL_GIT_TEMPLATE_DIR}
}

do_install_append_class-nativesdk() {
	create_wrapper ${D}${bindir}/git \
		GIT_EXEC_PATH='`dirname $''realpath`'/${REL_GIT_EXEC_PATH} \
		GIT_TEMPLATE_DIR='`dirname $''realpath`'/${REL_GIT_TEMPLATE_DIR}
	perl_native_fixup
}

PERLTOOLS = " \
    ${libexecdir}/git-add--interactive \
    ${libexecdir}/git-difftool \
    ${libexecdir}/git-relink \
    ${libexecdir}/git-instaweb \
    ${libexecdir}/git-submodule \
    ${libexecdir}/git-am \
    ${libexecdir}/git-request-pull \
    ${libexecdir}/git-mw \
    ${libexecdir}/git-cvsserver \
    ${libexecdir}/git-cvsimport \
    ${libexecdir}/git-cvsexportcommit \
    ${libexecdir}/git-send-email \
    ${libexecdir}/git-remote-mediawiki \
    ${libexecdir}/git-svn \
    ${libexecdir}/git-archimport \
    ${bindir}/git-cvsserver \
    ${datadir}/gitweb/gitweb.cgi \
    ${datadir}/git-core/templates/hooks/prepare-commit-msg.sample \
    ${datadir}/git-core/templates/hooks/pre-rebase.sample \
"

PACKAGES =+ " \
    ${PN}-arch ${PN}-cvs ${PN}-daemon-run ${PN}-daemon-sysvinit \
    ${PN}-el ${PN}-email ${PN}-gui ${PN}-mediawiki ${PN}-svn gitk gitweb \
"

FILES_${PN}-arch = "${libexecdir}/git-archimport"
FILES_${PN}-cvs = " \
    ${bindir}/git-cvsserver \
    ${libexecdir}/git-cvs* \
"
FILES_${PN}-daemon-run = "${sysconfdir}/sv/git-daemon/*"
FILES_${PN}-daemon-sysvinit = " \
    ${sysconfdir}/default/git-daemon \
    ${sysconfdir}/init.d/git-daemon \
    ${datadir}/git-core/sysvinit \
"
FILES_${PN}-el = " \
    ${sysconfdir}/emacs \
    ${libdir}/emacsen-common \
    ${datadir}/git-core/emacs \
"
FILES_${PN}-email = "${libexecdir}/git-send-email"
FILES_${PN}-gui = " \
    ${libexecdir}/git-citool \
    ${libexecdir}/git-gui* \
    ${datadir}/git-gui \
"
FILES_${PN}-mediawiki = " \
    ${libexecdir}/git-mw \
    ${libexecdir}/git-remote-mediawiki \
    ${libdir}/perl/*/Git/Mediawiki.pm \
"
FILES_${PN}-svn = " \
    ${libexecdir}/git-svn \
    ${libdir}/perl/*/Git/SVN.pm \
    ${libdir}/perl/*/Git/SVN/* \
"
FILES_gitk = " \
    ${bindir}/gitk \
    ${datadir}/gitk \
"
FILES_gitweb = " \
    ${sysconfdir}/gitweb.conf \
    ${sysconfdir}/apache2 \
    ${libdir}/cgi-bin/gitweb.cgi \
"
FILES_${PN} += " \
    ${libdir}/perl/*/Git.pm \
    ${libdir}/perl/*/Git/I18N.pm \
    ${libdir}/perl/*/Git/IndexInfo.pm \
    ${datadir}/bash-completion \
    ${datadir}/git-core/contrib \
    ${datadir}/git-core/templates \
    ${datadir}/gitweb \
"

RDEPENDS_${PN}-arch += "${PN}"
RDEPENDS_${PN}-cvs += "${PN}"
RDEPENDS_${PN}-svn += "${PN}"
RDEPENDS_${PN}-mediawiki += "${PN}"
RDEPENDS_${PN}-email += "${PN}"
RDEPENDS_${PN}-daemon-run += "${PN}"
RDEPENDS_${PN}-daemon-sysvinit += "${PN}"
RDEPENDS_${PN}-gui += "${PN}"
RDEPENDS_gitk += "${PN}"
RDEPENDS_${PN}-el += "${PN}"
RDEPENDS_gitweb += "${PN}"

# perl modules used by Git.pm, I18N.pm and IndexInfo.pm
RDEPENDS_${PN} += " \
perl-module-exporter \
perl-module-carp \
perl-module-cwd \
perl-module-ipc-open2 \
perl-module-fcntl \
perl-module-time-local \
perl-module-file-temp \
perl-module-file-spec \
perl-module-posix \
"
RSUGGESTS_${PN} += " perl"

RCONFLICTS_${PN}-daemon-run = "${PN}-daemon-sysvinit"
RCONFLICTS_${PN}-daemon-sysvinit = "${PN}-daemon-run"

BBCLASSEXTEND = "native nativesdk"
