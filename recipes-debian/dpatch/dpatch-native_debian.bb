SUMMARY= "patch maintenance system for Debian source packages"
DESCRIPTION = "dpatch is an easy to use patch system for Debian packages, somewhat\n\
similar to the dbs package, but much simpler to use.\n\
.\n\
It lets you store patches and other simple customization templates in\n\
debian/patches and otherwise does not require much reorganization of\n\
your source tree. To get the patches applied at build time you simply\n\
need to include a makefile snippet and then depend on the\n\
patch/unpatch target in the build or clean stage of debian/rules - or\n\
you can use the dpatch patching script directly.\n\
.\n\
It can easily apply patches only on specific architectures if needed."

PR = "r1"

inherit debian-package
PV = "2.0.35"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=876a2203d96d26da30e0bff0355d7f5b"

DEPENDS = "patchutils-native dpkg-native"

inherit native

do_configure() {
	# Remove hardcode path
	sed -i -e "/^prefix.*=.* \/usr/d" \
	       -e "/^sysconfdir.*=.* \/etc/d" ${S}/config.mk
}

do_compile() {
	oe_runmake
}

do_install() {
	# Install directory
	install -d ${D}${bindir} \
	           ${D}${mandir}/man7 ${D}${mandir}/man1

	oe_runmake install DESTDIR="${D}"

	# Replace hardcode path
	find ${D}${bindir}/ ${D}${datadir}/dpatch/ -name \* -type f \
		-exec sed -i -e "s@ /usr/bin@ ${bindir}@g" \
		             -e "s@ /usr/share@ ${datadir}@g" {} \;

	# Add execute permission to dpatch-run
	# so we can find it by "/usr/bin/env dpatch-run"
	chmod +x ${D}${datadir}/dpatch/dpatch-run
}
