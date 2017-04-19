#
# base recipe: meta/recipes-connectivity/openssh/openssh_6.7p1.bb
# base branch: master
# base commit: 3fb5191d4da52c6b352a23881c0ea63c2e348619
#

PR = "r3"

inherit debian-package
PV = "6.7p1"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENCE;md5=e326045657e842541d3f35aada442507"

DEPENDS = "zlib openssl"

# openssh-server.postinst is created base on ${S}/debian/openssh-server.postinst
SRC_URI += " \
    file://add-test-support-for-busybox.patch \
    file://run-ptest \
    file://openssh-server.postinst \
    file://sshd_config \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', '', 'file://pam_sshd-Remove-selinux-rule.patch', d)} \
"

inherit autotools-brokensep update-alternatives useradd systemd ptest

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home \
	--home-dir ${localstatedir}/run/sshd --shell /bin/false --user-group sshd"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ssh.service"

# LFS support:
CFLAGS += "-D__FILE_OFFSET_BITS=64"

# Configure follow debian/rules
EXTRA_OECONF = " \
	--sysconfdir=${sysconfdir}/ssh \
	--disable-strip \
	--with-4in6 \
	--with-privsep-path=${localstatedir}/run/sshd \
	--with-ssl-engine \
	--with-xauth=${bindir}/xauth \
	--with-default-path=${DEFAULT_PATH} \
	--with-superuser-path=${SUPERUSER_PATH} \
	--with-cflags='${cflags}' \
	--libexecdir=${libdir}/${BPN} \
"
cflags = "${CPPFLAGS} ${CFLAGS} -DLOGIN_PROGRAM=\"${base_bindir}/login\" -DLOGIN_NO_ENDOPT"
DEFAULT_PATH = "/usr/local/bin:/usr/bin:/bin:/usr/games"
SUPERUSER_PATH = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

PACKAGECONFIG ??= "tcp-wrappers \
	${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)} \
"
PACKAGECONFIG[tcp-wrappers] = "--with-tcp-wrappers,--without-tcp-wrappers,tcp-wrappers"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam"
PACKAGECONFIG[libedit] = "--with-libedit,--without-libedit,libedit"
PACKAGECONFIG[krb5] = "--with-kerberos5=${STAGING_LIBDIR}/..,--without-kerberos5,krb5"
PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux"

# passwd path is hardcoded in sshd
CACHED_CONFIGUREVARS += "ac_cv_path_PATH_PASSWD_PROG=${bindir}/passwd"

# This is a workaround for uclibc because including stdio.h
# pulls in pthreads.h and causes conflicts in function prototypes.
# This results in compilation failure, so unless this is fixed,
# disable pam for uclibc.
EXTRA_OECONF_append_libc-uclibc=" --without-pam"

do_configure_prepend(){
	export LD="${CC}"
	if [ ! -e acinclude.m4 -a -e aclocal.m4 ]; then
		cp aclocal.m4 acinclude.m4
	fi
}

do_install_append(){
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}" = "pam" ]; then
		install -d ${D}${sysconfdir}/pam.d
        install -m 0644 ${S}/debian/openssh-server.sshd.pam.in ${D}${sysconfdir}/pam.d/sshd
        FROM="^@IF_KEYINIT@"
        INTO=""
        sed -i 's/'"$FROM"'/'"$INTO"'/' ${D}${sysconfdir}/pam.d/sshd
		sed -i -e 's:^#\s*\(UsePAM\s*\).*:\1yes:' ${WORKDIR}/sshd_config
	fi

	# FIXME: Remove GSSAPIAuthentication and GSSAPIDelegateCredentials from ssh_config.
	# They are added by ${S}/debian/patches/debian-config.patch.
	# Currently, we don't support these options.
	sed -i '/^    GSSAPIAuthentication yes/d' ${D}${sysconfdir}/ssh/ssh_config
	sed -i '/^    GSSAPIDelegateCredentials no/d' ${D}${sysconfdir}/ssh/ssh_config

	install -m 0644 ${WORKDIR}/sshd_config ${D}${sysconfdir}/ssh/sshd_config

	# Remove version control tags to avoid unnecessary conffile
	# resolution steps for administrators.
	sed -i '/\$$OpenBSD:/d' \
		${D}${sysconfdir}/ssh/moduli \
		${D}${sysconfdir}/ssh/ssh_config

	# Follow debian/openssh-sftp-server.links
	ln -sf openssh/sftp-server ${D}${libdir}/sftp-server

	# Install additional files follow debian/openssh-client.install
	install -m 0755 ${S}/debian/ssh-argv0 ${D}${bindir}/
	install -m 0755 ${S}/contrib/ssh-copy-id ${D}${bindir}/

	# Install systemd service
	# NOTE: "inherit systemd" will remove ${systemd_unitdir} if DISTRO_FEATURES doesn't include systemd,
	# and remove ${sysconfdir}/init.d if DISTRO_FEATURES includes systemd but not sysvinit.
	install -d ${D}${systemd_unitdir}/system
	install -d ${D}${libdir}/tmpfiles.d
	install -m 0644 ${S}/debian/systemd/*.service ${D}${systemd_unitdir}/system/
	install -m 0644 ${S}/debian/systemd/*.socket ${D}${systemd_unitdir}/system/
	install -m 0644 ${S}/debian/systemd/sshd.conf ${D}${libdir}/tmpfiles.d/

	# Install init script
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/openssh-server.ssh.init ${D}${sysconfdir}/init.d/ssh

	# Install files from ${S}/debian
	install -d ${D}${sysconfdir}/default ${D}${sysconfdir}/init \
		${D}${sysconfdir}/network/if-up.d ${D}${sysconfdir}/ufw/applications.d
	install -m 0644 ${S}/debian/openssh-server.ssh.default ${D}${sysconfdir}/default/ssh
	install -m 0644 ${S}/debian/openssh-server.ssh.upstart ${D}${sysconfdir}/init/ssh.conf
	install -m 0755 ${S}/debian/openssh-server.if-up ${D}${sysconfdir}/network/if-up.d/openssh-server
	install -m 0644 ${S}/debian/openssh-server.ufw.profile \
			${D}${sysconfdir}/ufw/applications.d/openssh-server
}

do_install_ptest () {
	sed -i -e "s|^SFTPSERVER=.*|SFTPSERVER=${libdir}/${PN}/sftp-server|" regress/test-exec.sh
	cp -r regress ${D}${PTEST_PATH}
}

PACKAGES =+ "${PN}-client ${PN}-sftp-server"

FILES_${PN}-client = " \
	${sysconfdir}/ssh/ssh_config \
	${sysconfdir}/ssh/moduli \
	${bindir}/* \
	${libexecdir}/ssh-* \
"
FILES_${PN}-sftp-server = "${libexecdir}/sftp-server ${libdir}/sftp-server"
FILES_${PN} += " \
    ${sysconfdir}/ssh/sshd_config \
    ${libdir}/tmpfiles.d \
    /run \
    ${base_libdir}/systemd/system \
"

RPROVIDES_${PN} = "${PN}-server"
PKG_${PN} = "${PN}-server"

RDEPENDS_${PN}-ptest += "${PN} ${PN}-client ${PN}-sftp-server make"

# Follow debian/control
RDEPENDS_${PN} += " \
	${PN}-client \
	${PN}-sftp-server \
	lsb-base \
	${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam-modules', '', d)} \
"

RCONFLICTS_${PN} = "dropbear"
RCONFLICTS_${PN}-client = "dropbear"

CONFFILES_${PN} = "${sysconfdir}/ssh/sshd_config"
CONFFILES_${PN}-client = "${sysconfdir}/ssh/ssh_config"

# Follow debian/openssh-client.postinst
ALTERNATIVE_${PN}-client = "rsh rlogin rcp"
ALTERNATIVE_PRIORITY_${PN}-client = "20"
ALTERNATIVE_TARGET[rsh] = "${bindir}/ssh"
ALTERNATIVE_TARGET[rlogin] = "${bindir}/slogin"
ALTERNATIVE_TARGET[rcp] = "${bindir}/scp"

# Set pkg_postinst with content from openssh-server.postinst
python do_package_prepend() {
    workdir = d.getVar('WORKDIR', True)
    pn = d.getVar('PN', True)
    postinst_script = open('%s/openssh-server.postinst' % (workdir),'r').read()
    d.setVar('pkg_postinst_%s' % (pn),postinst_script)
}
