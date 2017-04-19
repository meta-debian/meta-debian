#
# base recipe: meta/recipes-extended/xz/xz_5.1.3alpha.bb
# base branch: daisy
#
SUMMARY = "Utilities for managing LZMA compressed files"
HOMEPAGE = "http://tukaani.org/xz/"

inherit debian-package autotools gettext
PV = "5.1.1alpha+20120614"
DPN = "xz-utils"
PROVIDES = "xz"

DEPENDS += "gettext-native"
# The source includes bits of PD, GPLv2, GPLv3, LGPLv2.1+, but the only file
# which is GPLv3 is an m4 macro which isn't shipped in any of our packages,
# and the LGPL bits are under lib/, which appears to be used for libgnu, which
# appears to be used for DOS builds. So we're left with GPLv2+ and PD.
LICENSE = "GPLv2+ & GPLv3+ & LGPLv2.1+ & PD"
LICENSE_${PN} = "GPLv2+"
LICENSE_${PN}-dev = "GPLv2+"
LICENSE_${PN}-staticdev = "GPLv2+"
LICENSE_${PN}-doc = "GPLv2+"
LICENSE_${PN}-dbg = "GPLv2+"
LICENSE_liblzma = "PD"
LICENSE_xzdec = "GPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=c475b6c7dca236740ace4bba553e8e1c \
                    file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LGPLv2.1;md5=4fbd65380cdd255951079008b364516c \
                    file://lib/getopt.c;endline=23;md5=2069b0ee710572c03bb3114e4532cd84 "

BBCLASSEXTEND = "native nativesdk"

# Set license GPLv2+ for {PN}-locale* packages
python package_do_split_locales_append() {
    for l in sorted(locales):
        ln = legitimize_package_name(l)
        pkg = pn + '-locale-' + ln
        packages.append(pkg)
        d.setVar('LICENSE_' + pkg, "GPLv2+")
}

# generate build-aux/config.rpath so autoreconf can see it
do_configure_prepend() {
	cd ${S}
	./autogen.sh && cd -
}

do_compile_append () {
	oe_runmake -C ${B}/po all-yes
}

do_install_append () {
	install -d ${D}${base_libdir}
	#follow debian/rules
	mv ${D}${libdir}/liblzma.so.* ${D}${base_libdir}/
	dso=$(basename $(readlink ${D}${libdir}/liblzma.so))
	ln -s -f ../../lib/$dso ${D}${libdir}/liblzma.so

	#remove the unwanted files
	for file in lzcat lzcmp lzdiff lzegrep lzfgrep lzgrep lzless lzma lzmore unlzma; do
		rm ${D}${bindir}/$file
	done
	rm ${D}${libdir}/liblzma.la
	oe_runmake -C ${B}/po install-data-yes DESTDIR=${D}
}

PACKAGES =+ "liblzma xzdec"

FILES_liblzma = "${base_libdir}/liblzma*${SOLIBS}"
FILES_xzdec = "${bindir}/lzmadec ${bindir}/xzdec"

DEBIANNAME_${PN} = "${DPN}"
DEBIANNAME_${PN}-dev = "liblzma-dev"
DEBIANNAME_${PN}-doc = "liblzma-doc"
