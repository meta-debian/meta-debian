SUMMARY = "instrumentation system for Linux"
DESCRIPTION = "SystemTap provides infrastructure to simplify the gathering of \
information about the running Linux system."
HOMEPAGE = "http://sourceware.org/systemtap/"

inherit debian-package
PV = "2.6"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit autotools-brokensep gettext useradd pkgconfig

EXTRA_OECONF += "--enable-sqlite --disable-crash --disable-pie \
                 --enable-server --without-rpm --disable-silent-rules \
                 --enable-translator --disable-publican \
                 --libexecdir=${libdir}"

DEPENDS += "sqlite3 nspr nss elfutils"

do_install_append() {
	# Install vim files
	install -m 644 -D ${S}/vim/ftdetect/stp.vim \
	         ${D}${datadir}/vim/addons/ftdetect/stp.vim
	install -m 644 -D ${S}/vim/ftplugin/stp.vim \
	         ${D}${datadir}/vim/addons/ftplugin/stp.vim
	install -m 644 -D ${S}/vim/indent/stp.vim \
	         ${D}${datadir}/vim/addons/indent/stp.vim
	install -m 644 -D ${S}/vim/syntax/stp.vim \
	         ${D}${datadir}/vim/addons/syntax/stp.vim
	install -m 644 -D ${S}/debian/systemtap.yaml \
	         ${D}${datadir}/vim/registry/systemtap.yaml

	# Install emacs files
	install -m 644 -D ${S}/emacs/systemtap-init.el \
	         ${D}${datadir}/emacs/site-lisp/systemtap-common/systemtap-init.el
	install -m 644 -D ${S}/emacs/systemtap-mode.el \
	         ${D}${datadir}/emacs/site-lisp/systemtap-common/systemtap-mode.el

	# Install stap-prep
	install -D ${S}/stap-prep ${D}${bindir}/stap-prep

	# Follow debian/systemtap-runtime.install
	mv ${D}${bindir}/stapsh ${D}${libdir}/systemtap/

	install -d ${D}${libdir}/emacsen-common/packages/install
	install -d ${D}${libdir}/emacsen-common/packages/remove
	install -m 0755 ${S}/debian/systemtap-common.emacsen-install \
	         ${D}${libdir}/emacsen-common/packages/install/systemtap-common
	install -m 0755 ${S}/debian/systemtap-common.emacsen-remove \
	         ${D}${libdir}/emacsen-common/packages/remove/systemtap-common

	install -d ${D}${sysconfdir}/emacs/site-start.d
	install -m 0644 ${S}/debian/systemtap-common.emacsen-startup \
	         ${D}${sysconfdir}/emacs/site-start.d/50systemtap-common.el

	# Follow debian/systemtap-client.dirs debian/systemtap-server.dirs
	install -d ${D}${sysconfdir}/systemtap/ssl/client
	install -d ${D}${sysconfdir}/systemtap/ssl/server

	chmod 4754 ${D}${bindir}/staprun
	# Remove unwanted files
	rm -rf ${D}${bindir}/stap-report \
	       ${D}${bindir}/stapvirt
	# Remove /var/run to avoid QA error
	rm -rf ${D}/${localstatedir}/run
}

# Base on debian/systemtap-runtime.postinst
USERADD_PACKAGES = "${PN}-runtime"
GROUPADD_PARAM_${PN}-runtime = "-r stapdev; -r stapusr; -r stapsys"

pkg_postinst_${PN}-runtime() {
	# Fixup staprun binary for new group 'stapusr'.
	if [ -x $D${sbindir}/dpkg-statoverride ] && 
		! dpkg-statoverride --list $D${bindir}/staprun > /dev/null ; then
		dpkg-statoverride --update --add root stapusr 4754 $D${bindir}/staprun
	fi
}

PACKAGES =+ "${PN}-client ${PN}-common ${PN}-runtime ${PN}-server ${PN}-sdt-dev"

FILES_${PN}-client = "${libdir}/systemtap/stap-env"

FILES_${PN}-common = "${sysconfdir}/emacs/* \
                      ${datadir}/vim/* \
                      ${datadir}/emacs/* \
                      ${libdir}/emacsen-common/packages/* \
                      ${datadir}/systemtap/runtime/* \
                      ${datadir}/systemtap/tapset/* \
                      ${datadir}/vim/*"

FILES_${PN}-runtime = "${bindir}/stap-merge \
                       ${bindir}/staprun \
                       ${libdir}/systemtap/stap-authorize-cert \
                       ${libdir}/systemtap/stapio \
                       ${libdir}/systemtap/stapsh"

FILES_${PN}-sdt-dev = "${bindir}/dtrace \
                      ${includedir}/*"

FILES_${PN}-server = "${bindir}/stap-server \
                      ${libdir}/systemtap/*"

RDEPENDS_${PN} += "${PN}-common make"
RRECOMMENDS_${PN}-common = "${PN}"
RDEPENDS_${PN}-server = "libnss-tools unzip zip ${PN} ${PN}-client avahi-utils"
RDEPENDS_${PN}-client = "unzip zip dnsutils ${PN}-runtime avahi-utils"
RSUGGESTS_${PN}-client = "${PN}-server"
