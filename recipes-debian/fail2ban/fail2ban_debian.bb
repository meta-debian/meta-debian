# fail2ban receipe
SUMMARY = "Daemon to ban hosts that cause multiple authentication errors."
DESCRIPTION = "Fail2Ban scans log files like /var/log/auth.log and bans IP addresses having too \
many failed login attempts. It does this by updating system firewall rules to reject new \
connections from those IP addresses, for a configurable amount of time. Fail2Ban comes \
out-of-the-box ready to read many standard log files, such as those for sshd and Apache, \
and is easy to configure to read any log file you choose, for any error you choose."
HOMEPAGE = "http://www.fail2ban.org"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ecabc31e90311da843753ba772885d9f"

inherit debian-package
require recipes-debian/sources/fail2ban.inc
inherit update-rc.d setuptools
SRC_URI += " \
    file://fail2ban_setup.py \
"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "fail2ban"
INITSCRIPT_PARAMS = "defaults 25"

do_compile_prepend () {
    cp ${WORKDIR}/fail2ban_setup.py ${S}/setup.py
}
do_install_append () {
    install -d ${D}/${sysconfdir}/fail2ban
    install -d ${D}/${sysconfdir}/init.d
    install -m 0755 ${S}/files/debian-initd ${D}${sysconfdir}/init.d/fail2ban
}

FILES_${PN} += "/run"

INSANE_SKIP_${PN}_append = "already-stripped"

RDEPENDS_${PN} = "syslog-ng nftables iptables sqlite3 python python-logging python-shell python-tests lsb"
