SUMMARY = "PulseAudio sound server"
DESCRIPTION = "PulseAudio, previously known as Polypaudio, is a sound server for POSIX and \
 WIN32 systems. It is a drop in replacement for the ESD sound server with \
 much better latency, mixing/re-sampling quality and overall architecture."
HOMEPAGE = "http://www.pulseaudio.org"

inherit debian-package
PV = "5.0"

LICENSE = "GPLv2+ & LGPLv2.1+ & MIT"
LIC_FILES_CHKSUM = "\
	file://GPL;md5=4325afd396febcb659c36b49533135d4 \
	file://LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
	file://LICENSE;md5=dae3286e7999e70ddc23636cd25c89bd \
	file://src/modules/reserve.c;beginline=3;endline=25;md5=0e23094760367d51b6609750e9b31fbb"
inherit autotools pkgconfig gettext useradd

# *.desktop rules wont be generated during configure and build will fail
# if using --disable-nls
USE_NLS = "yes"

DEPENDS += "intltool-native libsndfile json-c openssl libasyncns"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
                   bluez5 bluez4 gconf avahi \
                   "

PACKAGECONFIG[bluez4] = "--enable-bluez4,--disable-bluez4,bluez sbc"
PACKAGECONFIG[bluez5] = "--enable-bluez5,--disable-bluez5,bluez sbc"
PACKAGECONFIG[gtk] = "--enable-gtk3,--disable-gtk3,gtk+3"
PACKAGECONFIG[systemd] = "--enable-systemd-daemon --enable-systemd-login --enable-systemd-journal \
                          --with-systemduserunitdir=${systemd_user_unitdir}, \
                          --disable-systemd --disable-systemd-journal,systemd"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,virtual/libx11 libxtst libice libsm libxcb"
PACKAGECONFIG[avahi] = "--enable-avahi,--disable-avahi,avahi"
PACKAGECONFIG[jack] = "--enable-jack,--disable-jack,jack"
PACKAGECONFIG[lirc] = "--enable-lirc,--disable-lirc,lirc"
PACKAGECONFIG[webrtc] = "--enable-webrtc-aec,--disable-webrtc-aec,webrtc-audio-processing"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[udev] = "--enable-hal-compat,--disable-hal-compat,udev"
PACKAGECONFIG[gconf] = "--enable-gconf,--disable-gconf,gconf"
PACKAGECONFIG[manpages] = "--enable-manpages, --disable-manpages, "

do_install_append() {
	# Follow debian/pulseaudio-esound-compat.links
	ln -sf esdcompat ${D}${bindir}/esd

	# Follow debian/rules
	find ${D}${libdir} -name "*.la" -delete

	mkdir -p ${D}${datadir}/alsa/alsa.conf.d
	cp -a ${S}/debian/pulse.conf ${D}${datadir}/alsa/alsa.conf.d
	cp -a ${S}/debian/pulse-alsa.conf ${D}${datadir}/alsa

	install -d ${D}${datadir}/apport/package-hooks
	cp ${S}/debian/apport-hook.py \
	   ${D}${datadir}/apport/package-hooks/source_pulseaudio.py

	mkdir -p ${D}${datadir}/zsh/vendor-completions
	install -m 0644 ${S}/shell-completion/pulseaudio-zsh-completion.zsh \
	                ${D}${datadir}/zsh/vendor-completions/_pulseaudio
}

# Base on debian/pulseaudio.postinst
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r pulse-access; -r audio"
USERADD_PARAM_${PN} = "--system -G audio --no-create-home --home /var/run/pulse pulse"

PACKAGES =+ "\
	libpulse libpulse-dev libpulse-mainloop-glib libpulsedsp pulseaudio-esound-compat \
	pulseaudio-module-bluetooth pulseaudio-module-gconf pulseaudio-module-jack \
	pulseaudio-module-lirc pulseaudio-module-raop pulseaudio-module-x11 \
	pulseaudio-module-zeroconf pulseaudio-utils"

FILES_libpulse = "${sysconfdir}/pulse/client.conf \
                  ${libdir}/libpulse-simple${SOLIBS} \
                  ${libdir}/libpulse${SOLIBS} \
                  ${libdir}/pulseaudio/libpulsecommon-5.0.so \
                  "
FILES_libpulse-dev = "${includedir}/pulse/* \
                      ${libdir}/cmake/PulseAudio/*.cmake \
                      ${libdir}/*.so \
                      ${libdir}/pkgconfig \
                      ${datadir}/vala/* \
                      "
FILES_libpulse-mainloop-glib = "${libdir}/libpulse-mainloop-glib${SOLIBS} \
                                "
FILES_libpulsedsp = "${libdir}/pulseaudio/libpulsedsp.so \
                     "
FILES_pulseaudio-esound-compat = "${bindir}/esd \
                                  ${bindir}/esdcompat \
                                  ${libdir}/pulse-5.0/modules/libprotocol-esound.so \
                                  ${libdir}/pulse-5.0/modules/module-esound-compat* \
                                  ${libdir}/pulse-5.0/modules/module-esound-protocol* \
                                  "
FILES_pulseaudio-module-bluetooth = "${libdir}/pulse-5.0/modules/libbluez4-util.so \
                                     ${libdir}/pulse-5.0/modules/libbluez5-util.so \
                                     ${libdir}/pulse-5.0/modules/module-blue* \
                                     "
FILES_pulseaudio-module-gconf = "${libdir}/pulse-5.0/modules/module-gconf.so \
                                 ${libdir}/pulseaudio/pulse/gconf-helper \
                                 "
FILES_pulseaudio-module-jack = "${libdir}/pulse-5.0/modules/module-jack* \
                                "
FILES_pulseaudio-module-lirc = "${libdir}/pulse-5.0/modules/module-lirc.so \
                                "
FILES_pulseaudio-module-raop = "${libdir}/pulse-5.0/modules/libraop.so \
                                ${libdir}/pulse-5.0/modules/module-raop* \
                                "
FILES_pulseaudio-module-x11 = "${libdir}/pulse-5.0/modules/module-x11* \
                               "
FILES_pulseaudio-module-zeroconf = "${libdir}/pulse-5.0/modules/module-zeroconf* \
                                    ${libdir}/pulse-5.0/modules/libavahi-wrap.so \
                                    "
FILES_pulseaudio-utils = "${bindir}/pa* \
                          "
FILES_${PN} += "${libdir}/pulse-5.0/modules/* \
                ${libdir}/pulse-5.0/modules/module-esound-sink.so \
                ${datadir}/alsa/* \
                ${datadir}/apport/* \
                ${datadir}/zsh/*"
FILES_${PN}-dbg += "${libdir}/pulse-5.0/modules/.debug \
                    ${libdir}/pulseaudio/pulse/.debug"
# Avoid QA issues:
# 	do_package_qa: QA Issue: pulseaudio-esound-compat rdepends on libpulse-dev [dev-deps]
# 	do_package_qa: QA Issue: pulseaudio rdepends on libpulse-dev [dev-deps]
# 	do_package_qa: QA Issue: pulseaudio-module-x11 rdepends on libpulse-dev [dev-deps]
# 	do_package_qa: QA Issue: pulseaudio-module-raop rdepends on libpulse-dev [dev-deps]
# 	do_package_qa: QA Issue: pulseaudio-module-bluetooth rdepends on libpulse-dev [dev-deps]
# 	do_package_qa: QA Issue: pulseaudio-module-gconf rdepends on libpulse-dev [dev-deps]
# 	do_package_qa: QA Issue: pulseaudio-module-zeroconf rdepends on libpulse-dev [dev-deps]
# These packages don't depends on libpulse-dev.
INSANE_SKIP_${PN} += "dev-deps"
INSANE_SKIP_pulseaudio-module-x11 += "dev-deps"
INSANE_SKIP_pulseaudio-module-raop += "dev-deps"
INSANE_SKIP_pulseaudio-esound-compat += "dev-deps"
INSANE_SKIP_pulseaudio-module-bluetooth += "dev-deps"
INSANE_SKIP_pulseaudio-module-gconf += "dev-deps"
INSANE_SKIP_pulseaudio-module-zeroconf += "dev-deps"

DEBIANNAME_libpulse = "libpulse0"
RPROVIDES_libpulse += "libpulse0"
RDEPENDS_${PN} += "lsb-base udev libpulse pulseaudio-utils"
RDEPENDS_pulseaudio-utils += "libpulsedsp"
RPROVIDES_pulseaudio-esound-compat += "esound"
RDEPENDS_pulseaudio-module-bluetooth += "bluez"
RDEPENDS_pulseaudio-module-x11 += "pulseaudio-utils"
RDEPENDS_pulseaudio-module-zeroconf += "avahi-daemon"
RDEPENDS_libpulse-dev += "libpulse-mainloop-glib glib-2.0-dev libavahi-client-dev"
