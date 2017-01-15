SUMMARY = "Highly configurable and low resource X11 Window manager"
DESCRIPTION = "\
	Fairly similar to blackbox, from which it is derived, but has been \
	extended with features such as pwm-style window tabs, configurable \
	key bindings, toolbar, and an iconbar. It also includes some cosmetic\
	fixes over blackbox"
HOMEPAGE = "http://fluxbox.org"

LICENSE = "MIT & CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=e90c7c0bee6fc368be0ba779e0eac053 \
	file://data/styles/bora_blue/theme.cfg;beginline=1;endline=2;md5=d7b703c84a3be84264906e7f87e3710b \	
"

DEPENDS = "virtual/libx11 libxt"
PR = "r0"
inherit autotools-brokensep pkgconfig

inherit debian-package
PV = "1.3.5"
DEBIAN_SECTION = "x11"
EXTRA_OECONF += "\
	--with-windowmenu=${sysconfdir}/X11/${DPN}/window.menu \
	--with-keys=${sysconfdir}/X11/${DPN}/keys \
	--with-apps=${sysconfdir}/X11/${DPN}/apps \
	--with-overlay=${sysconfdir}/X11/${DPN}/overlay \
	--with-init=${sysconfdir}/X11/${DPN}/init \
"
do_compile_append() {
	oe_runmake -C ${S}/debian/additional-themes
}

#install follow debian jessie
do_install_append() {
	install -d ${D}${datadir}/images/fluxbox
	cp -rv  ${S}/debian/additional-themes/stage/* \
		${D}${datadir}/fluxbox/styles/
	cp -rv  ${S}/debian/additional-themes/stage-images/* \
		${D}${datadir}/images/fluxbox/

	install -m 0644 ${S}/debian/system.fluxbox-menu \
			${D}${sysconfdir}/X11/${DPN}/
	install -m 0644 ${S}/debian/fluxbox.menu-user \
			${D}${sysconfdir}/X11/${DPN}/

	install -D -m 0644 ${S}/debian/fluxbox.menu-method \
			${D}${sysconfdir}/menu-methods/fluxbox
	install -D -m 0644 ${S}/debian/fluxbox.menu \
			${D}${datadir}/menu/fluxbox
	install -D -m 0644 ${S}/debian/fluxbox.desktop \
			${D}${datadir}/xsessions/fluxbox.desktop
	rm ${D}${datadir}/${DPN}/menu ${D}${bindir}/fluxbox-generate_menu
}
FILES_${PN} += "${datadir}/images ${datadir}/menu ${datadir}/xsessions"
