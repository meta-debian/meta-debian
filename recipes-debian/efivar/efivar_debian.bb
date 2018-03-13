SUMMARY = "Tools to manage UEFI variables"
DESCRIPTION = "efivar provides a simple command line interface to the UEFI variable facility."
HOMEPAGE = "https://github.com/vathpela/efivar"

inherit debian-package
PV = "0.15"

LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=6626bb1e20189cfa95f2c508ba286393"

# Makefile calls ./makeguids to generate header file
# but makeguids is a cross compiled binary so it can't run on native environment.
# Call it from efivar-native instead.
SRC_URI_append_class-target = " file://run_native_makeguids.patch"

DEPENDS = "popt efivar-native"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake DESTDIR="${D}" install
}

do_install_append_class-native() {
	install -D -m 0755 ${B}/src/makeguids ${D}${bindir}/makeguids
}

PACKAGE_BEFORE_PN += "lib${DPN}"

FILES_lib${DPN} = "${libdir}/lib*${SOLIBS}"

DEBIANNAME_${PN}-dev = "lib${DPN}-dev"
RPROVIDES_${PN}-dev += "lib${DPN}-dev"

BBCLASSEXTEND = "native"
