#
# base recipe: meta/recipes-multimedia/alsa/alsa-utils_1.0.27.2.bb
# base branch: daisy
#

PR = "r2"

inherit debian-package
PV = "1.0.28"

SUMMARY = "ALSA sound utilities"
HOMEPAGE = "http://www.alsa-project.org"
BUGTRACKER = "https://bugtrack.alsa-project.org/alsa-bug/login_page.php"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
	file://alsactl/utils.c;beginline=1;endline=20;md5=fe9526b055e246b5558809a5ae25c0b9 \
"

DEPENDS = "alsa-lib ncurses libsamplerate udev"

SRC_URI +=" \
	file://0001-alsactl-don-t-let-systemd-unit-restore-the-volume-wh.patch \
	file://alsa-utils-aplay-interrupt-signal-handling.patch \
"

PACKAGECONFIG ??= "udev"
PACKAGECONFIG[udev] = "--with-udev-rules-dir=`pkg-config --variable=udevdir udev`/rules.d,,udev"
PACKAGECONFIG[xmlto] = "--enable-xmlto, --disable-xmlto, xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"

# Follow Debian
EXTRA_OECONF += " \
	--with-asound-state-dir=${localstatedir}/lib/alsa \
	--with-alsactl-home-dir=${localstatedir}/run/alsa \
	--with-systemdsystemunitdir=${systemd_system_unitdir} \
	--disable-alsaconf \
"
# lazy hack. needs proper fixing in gettext.m4, see
# http://bugs.openembedded.org/show_bug.cgi?id=2348
# please close bug and remove this comment when properly fixed
#
EXTRA_OECONF_append_libc-uclibc = " --disable-nls"

inherit autotools gettext pkgconfig

# This are all packages that we need to make. Also, the now empty alsa-utils
# ipk depends on them.

ALSA_UTILS_PKGS = "\
             alsa-utils-alsamixer \
             alsa-utils-midi \
             alsa-utils-aplay \
             alsa-utils-amixer \
             alsa-utils-aconnect \
             alsa-utils-iecset \
             alsa-utils-speakertest \
             alsa-utils-aseqnet \
             alsa-utils-aseqdump \
             alsa-utils-alsactl \
             alsa-utils-alsaloop \
             alsa-utils-alsaucm \
            "

PACKAGES += "${ALSA_UTILS_PKGS}"
RDEPENDS_${PN} += "${ALSA_UTILS_PKGS}"

# init.d/alsa-utils require lsb-base
RDEPENDS_alsa-utils-alsactl += "lsb-base"

FILES_${PN} = "${datadir}/alsa/utils.sh"
FILES_alsa-utils-aplay       = "${bindir}/aplay ${bindir}/arecord"
FILES_alsa-utils-amixer      = "${bindir}/amixer"
FILES_alsa-utils-alsamixer   = "${bindir}/alsamixer"
FILES_alsa-utils-speakertest = "${bindir}/speaker-test ${datadir}/sounds/alsa/ ${datadir}/alsa/speaker-test/"
FILES_alsa-utils-midi        = "${bindir}/aplaymidi ${bindir}/arecordmidi ${bindir}/amidi"
FILES_alsa-utils-aconnect    = "${bindir}/aconnect"
FILES_alsa-utils-aseqnet     = "${bindir}/aseqnet"
FILES_alsa-utils-iecset      = "${bindir}/iecset"
FILES_alsa-utils-alsactl     = "${sbindir}/alsactl */udev/rules.d ${systemd_unitdir} \
				${localstatedir}/lib/alsa ${datadir}/alsa/init/ \
				${sysconfdir}/init.d/"
FILES_alsa-utils-aseqdump    = "${bindir}/aseqdump"
FILES_alsa-utils-alsaloop    = "${bindir}/alsaloop"
FILES_alsa-utils-alsaucm     = "${bindir}/alsaucm"

SUMMARY_alsa-utils-aplay        = "Play (and record) sound files using ALSA"
SUMMARY_alsa-utils-amixer       = "Command-line control for ALSA mixer and settings"
SUMMARY_alsa-utils-alsamixer    = "ncurses-based control for ALSA mixer and settings"
SUMMARY_alsa-utils-speakertest  = "ALSA surround speaker test utility"
SUMMARY_alsa-utils-midi         = "Miscellaneous MIDI utilities for ALSA"
SUMMARY_alsa-utils-aconnect     = "ALSA sequencer connection manager"
SUMMARY_alsa-utils-aseqnet      = "Network client/server for ALSA sequencer"
SUMMARY_alsa-utils-iecset       = "ALSA utility for setting/showing IEC958 (S/PDIF) status bits"
SUMMARY_alsa-utils-alsactl      = "Saves/restores ALSA-settings in /etc/asound.state"
SUMMARY_alsa-utils-aseqdump     = "Shows the events received at an ALSA sequencer port"
SUMMARY_alsa-utils-alsaloop     = "ALSA PCM loopback utility"
SUMMARY_alsa-utils-alsaucm      = "ALSA Use Case Manager"

ALLOW_EMPTY_alsa-utils = "1"

# Follow Debian
do_install_append() {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/init ${D}${sysconfdir}/init.d/alsa-utils

	# Follow ${S}/debian/links
	ln -s /dev/null ${D}${systemd_system_unitdir}/alsa-utils.service

	# Follow ${S}/debian/install
	install -m 0755 ${S}/debian/utils.sh ${D}${datadir}/alsa/
}
