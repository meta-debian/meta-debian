#
# base-recipe: http://cgit.openembedded.org/meta-openembedded/tree/meta-oe/recipes-multimedia/cdrkit/cdrkit_1.1.11.bb
# base-branch: jethro
#
SUMMARY_wodim = "command line CD/DVD writing tool"
SUMMARY_genisoimage = "Creates ISO-9660 CD-ROM filesystem images"
SUMMARY_icedax = "Creates WAV files from audio CDs"

inherit debian-package
PV = "1.1.11"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b30d3b2750b668133fc17b401e1b98f8"

SRC_URI += "file://0001-do-not-create-a-run-test-to-determine-order-of-bitfi.patch"

inherit cmake

DEBIAN_PATCH_TYPE = "nopatch"

DEPENDS_class-target = "libcap file bzip2"
DEPENDS_class-native = "libcap-native file-native bzip2-replacement-native"

# Base on debian/rules
do_configure_append() {
	cd ${S}/3rd-party/zisofs_tools
	./configure --build=${BUILD_SYS} --host=${HOST_SYS}
}

do_compile_append() {
	oe_runmake -C ${S}/3rd-party/zisofs_tools
}

do_install_append() {
	# Follow debian/rules
	install -m 0755 ${S}/3rd-party/geteltorito/geteltorito.pl \
		${D}${bindir}/geteltorito
	install -m 0755 ${S}/3rd-party/zisofs_tools/mkzftree ${D}${bindir}/
	install -d ${D}${sysconfdir}
	cp -a ${S}/wodim/wodim.dfl ${D}${sysconfdir}/wodim.conf
	cp -a ${S}/netscsid/netscsid.dfl ${D}${sysconfdir}/netscsid.conf

	# Follow debian/icedax.links
	ln -sf icedax ${D}${bindir}/list_audio_tracks

	rm -rf ${D}${bindir}/isodebug
}

PACKAGES =+ "genisoimage icedax wodim"

FILES_genisoimage = "\
	${bindir}/devdump \
	${bindir}/dirsplit \
	${bindir}/genisoimage \
	${bindir}/geteltorito \
	${bindir}/isodump \
	${bindir}/isoinfo \
	${bindir}/isovfy \
	${bindir}/mkzftree \
	"
FILES_icedax = "\
	${bindir}/cdda2mp3 \
	${bindir}/cdda2ogg \
	${bindir}/icedax \
	${bindir}/list_audio_tracks \
	${bindir}/pitchplay \
	${bindir}/readmult \
"
FILES_wodim = "\
	${sysconfdir}/netscsid.conf \
	${sbindir}/netscsid \
	${bindir}/wodim \
	${bindir}/readom \
	${sysconfdir}/wodim.conf \
"

BBCLASSEXTEND = "native"
