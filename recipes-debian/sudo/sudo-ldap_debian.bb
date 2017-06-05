require sudo.inc

PR = "${INC_PR}.1"
#Add configuration options for sudo-ldap package
#Follow debian/rules
# --without-selinux: Don't use selinux support
EXTRA_OECONF += "\	
	--with-ldap 						\
	--with-pam 						\
	--prefix=${prefix} -v 					\
	--with-all-insults 					\
	--with-fqdn 						\
	--with-logging=syslog 					\
	--with-logfac=authpriv 					\
	--with-env-editor 					\
	--with-editor=${bindir}/editor 				\
	--with-timeout=15 					\
	--with-password-timeout=0 				\
	--with-passprompt="[sudo] password for %p: " 		\
	--disable-root-mailer 					\
	--disable-setresuid 					\
	--with-sendmail=${sbindir}/sendmail 			\
	--with-rundir=${localstatedir}${base_libdir}/sudo 	\
	--with-ldap-conf-file=${sysconfdir}/sudo-ldap.conf 	\
	--mandir=${mandir}	 				\
	--libexecdir=${libdir}/sudo 				\
	--without-selinux 					\
	--with-linux-audit 					\
"
DEPENDS += "openldap"

#sudo-ldap packages conflict with sudo package; follow debian/control
RCONFLICTS_${PN} = "sudo"
RREPLACES_${PN} = "sudo"

do_install_append () {
	# Create /etc/init.d, pam.d folders
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/pam.d
	
	#Create /lib/systemd/system folder
	install -d ${D}${systemd_system_unitdir}
	
	#install etc/init.d/sudo-ldap; etc/pam.d/sudo-ldap
	install -m 0755 ${S}/debian/sudo-ldap.sudo-ldap.init \
			${D}${sysconfdir}/init.d/sudo-ldap
	install -m 0644 ${S}/debian/sudo.pam ${D}${sysconfdir}/pam.d/sudo
	
	#install lib/systemd/system/sudo.service
	install -m 0644 ${S}/debian/sudo.service \
			${D}${systemd_system_unitdir}

	chmod 4755 ${D}${bindir}/sudo
	chmod 0440 ${D}${sysconfdir}/sudoers
	chmod 0755 ${D}${sysconfdir}/sudoers.d
}

FILES_${PN} += "\
	${libdir}/sudo/sesh ${includedir}/* \
	${libdir}/tmpfiles.d ${sysconfdir}/init.d/* \
	${sysconfdir}/pam.d/sudo ${systemd_system_unitdir}/sudo.service \
	${libdir}/sudo/*.so ${datadir}/locale/* \
"
FILES_${PN}-dbg += "${libdir}/sudo/.debug/*"
