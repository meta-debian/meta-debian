#
# base recipe: meta-selinux/recipes-security/selinux/policycoreutils_2.4.bb
# base branch: jethro
#

SUMMARY = "SELinux core policy utilities"
DESCRIPTION = "Security-enhanced Linux is a patch of the Linux® kernel and a number \
of utilities with enhanced security functionality designed to add \
mandatory access controls to Linux.  The Security-enhanced Linux \
kernel contains new architectural components originally developed to \
improve the security of the Flask operating system. These \
architectural components provide general support for the enforcement \
of many kinds of mandatory access control policies, including those \
based on the concepts of Type Enforcement®, Role-based Access Control, \
and Multi-level Security."
HOMEPAGE = "http://userspace.selinuxproject.org/"

PR = "r0"

inherit debian-package
PV = "2.3"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "libcap libselinux libsemanage libsepol dbus-glib"
DEPENDS_append_class-target += " audit libpam setools"

inherit pythonnative systemd

PROVIDES += "mcstrans"

export STAGING_INCDIR
export STAGING_LIBDIR
export BUILD_SYS
export HOST_SYS

# EXTRA_OEMAKE is typically: -e MAKEFLAGS=
# "MAKEFLAGS= " causes problems as ENV variables will not pass to subdirs, so
# we redefine EXTRA_OEMAKE here
EXTRA_OEMAKE = "-e"

AUDITH="`ls ${STAGING_INCDIR}/libaudit.h >/dev/null 2>&1 && echo /usr/include/libaudit.h `"
PAMH="${@bb.utils.contains('DISTRO_FEATURES', 'pam', \
      '`ls ${STAGING_INCDIR}/security/pam_appl.h >/dev/null 2>&1 && echo /usr/include/security/pam_appl.h `', \
      '',d)}"
EXTRA_OEMAKE_append_class-target = " PAMH=${PAMH} AUDITH=${AUDITH} INOTIFYH=n"
EXTRA_OEMAKE += "PREFIX=${D}"
EXTRA_OEMAKE += "INITDIR=${D}/etc/init.d"

PCU_NATIVE_CMDS = "setfiles semodule_package semodule semodule_link semodule_expand semodule_deps"

# Follow debian/rules
export LIBDIR="${D}${libdir}"
export PYTHONLIBDIR="${D}${libdir}/${PYTHON_DIR}"
export INITDIR="${D}${sysconfdir}/init.d"
export SYSCONFDIR="${D}${sysconfdir}/default"
export SYSTEMDDIR="${D}${systemd_unitdir}"

do_compile_prepend() {
	export PYTHON=python
	export PYLIBVER='python${PYTHON_BASEVERSION}'
	export PYTHON_CPPFLAGS="-I${STAGING_INCDIR}/$PYLIBVER"
	export PYTHON_LDFLAGS="${STAGING_LIBDIR}/lib$PYLIBVER.so"

	# Prevent using headers from host path
	find ${S} -name Makefile -exec sed -i \
		-e "s:-I\/usr\/include:-I${STAGING_INCDIR}:g" \
		-e "s:-I\/usr\/lib:-I${STAGING_LIBDIR}:g" {} \;
}

do_compile() {
	oe_runmake all \
		INCLUDEDIR='${STAGING_INCDIR}' \
		LIBDIR='${STAGING_LIBDIR}'
}

do_compile_class-native() {
	for PCU_CMD in ${PCU_NATIVE_CMDS}; do
		oe_runmake -C $PCU_CMD \
			INCLUDEDIR='${STAGING_INCDIR}' \
			LIBDIR='${STAGING_LIBDIR}'
	done
}

do_install() {
	oe_runmake install \
		DESTDIR="${D}" \
		PREFIX="${D}${prefix}" \
		INCLUDEDIR="${D}${includedir}" \
		SHLIBDIR="${D}${base_libdir}"

	install -d ${D}${libdir}/tmpfiles.d
	cp ${S}/debian/policycoreutils.mcstrans.tmpfile \
	   ${D}${libdir}/tmpfiles.d/mcstrans.conf

	# Fix symlink
	rm -f ${D}${sbindir}/load_policy
	ln -sf ../../sbin/load_policy ${D}${sbindir}/

	# Install init script
	test -d ${INITDIR} || install -d ${INITDIR}
	install -m 0755 ${S}/debian/policycoreutils.mcstrans.init \
	                ${INITDIR}/mcstrans
	install -m 0755 ${S}/debian/policycoreutils.restorecond.init \
                        ${INITDIR}/restorecond

	# Install pam files
	if ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'true', 'false', d)}; then
		test -d ${D}${sysconfdir}/pam.d || install -d ${D}${sysconfdir}/pam.d
		cp ${S}/debian/newrole.pam ${D}${sysconfdir}/pam.d/newrole
		cp ${S}/debian/run_init.pam ${D}${sysconfdir}/pam.d/run_init
	fi

	# Follow debian/policycoreutils.dirs
	install -d ${D}${localstatedir}/lib/selinux

	# Follow debian/policycoreutils.install
	install -m 0755 ${S}/debian/se_dpkg ${D}${sbindir}/
	install -d ${D}${docdir}/${DPN}/mcstrans-examples/
	cp -r ${S}/mcstrans/share/examples/* ${D}${docdir}/${DPN}/mcstrans-examples/

	# Follow debian/policycoreutils.links
	ln -sf se_dpkg ${D}${sbindir}/se_apt-get
	ln -sf se_dpkg ${D}${sbindir}/se_aptitude
	ln -sf se_dpkg ${D}${sbindir}/se_dpkg-reconfigure
	ln -sf se_dpkg ${D}${sbindir}/se_dselect
	ln -sf se_dpkg ${D}${sbindir}/se_synaptic
	ln -sf setsebool ${D}${datadir}/bash-completion/completions/getsebool

	# Remove unwanted files
	find ${D}${libdir}/${PYTHON_DIR} -name "*.pyc" -exec rm -f {} \;
}

do_install_class-native() {
	for PCU_CMD in ${PCU_NATIVE_CMDS} ; do
		oe_runmake -C $PCU_CMD install \
			DESTDIR="${D}" \
			PREFIX="${D}/${prefix}" \
			SBINDIR="${D}/${base_sbindir}"
	done
}

# Base on debian/policycoreutils.postinst
pkg_postinst_${PN} () {
	if [ ! -e $D${sysconfdir}/selinux/config ]; then
		test -d $D${sysconfdir}/selinux || mkdir -p $D${sysconfdir}/selinux
		cat >$D${sysconfdir}/selinux/config<<EOF
# This file controls the state of SELinux on the system.
# SELINUX= can take one of these three values:
# enforcing - SELinux security policy is enforced.
# permissive - SELinux prints warnings instead of enforcing.
# disabled - No SELinux policy is loaded.
SELINUX=permissive
# SELINUXTYPE= can take one of these two values:
# default - equivalent to the old strict and targeted policies
# mls     - Multi-Level Security (for military and educational use)
# src     - Custom policy built from source
SELINUXTYPE=default

# SETLOCALDEFS= Check local definition changes
SETLOCALDEFS=0
EOF
	fi
}

PACKAGE_BEFORE_PN += "python-sepolicy"

FILES_python-sepolicy = "${libdir}/${PYTHON_DIR}/*-packages/sepolicy*"
FILES_${PN} += " \
    ${libdir}/${PYTHON_DIR}/*-packages/seobject.py \
    ${libdir}/tmpfiles.d \
    ${systemd_unitdir} \
    ${datadir} \
"
FILES_${PN}-dbg += "${libdir}/${PYTHON_DIR}/*-packages/sepolicy/.debug"

RPROVIDES_${PN} += "mcstrans"
RDEPENDS_${PN}_class-target += "python-sepolicy"

# Init scripts from Debian require lsb-base
RDEPENDS_${PN}_class-target += "lsb-base"

BBCLASSEXTEND = "native"
