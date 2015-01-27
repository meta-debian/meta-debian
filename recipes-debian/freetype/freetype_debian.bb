require recipes-graphics/freetype/${BPN}_2.5.2.bb

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = " \
file://Makefile;md5=429dda986c2be2d0e04f57e5380c7aca \
file://CMakeLists.txt;md5=c6055f248394b1507217a9e9c8c6d1e0 \
"

# .orig.tar.gz is doubly-compressed
addtask uncompress after do_unpack before do_debian_patch
do_uncompress() {
	cd ${S}
	PV_SRCPKG=$(head -n 1 ${S}/debian/changelog | \
				sed "s|.*(\([^()]*\)).*|\1|")
	PV_ORIG=$(echo $PV_SRCPKG | sed "s|-.*||")

	tar xvjf ${S}/freetype-$PV_ORIG.tar.bz2 -C ${S}
	mv ${S}/freetype-$PV_ORIG/* ${S}
	rm -r ${S}/freetype-$PV_ORIG
}

# patches exist in debian/patches-freetype (see debian/rules)
do_debian_patch() {
	cd ${S}
	QUILT_PATCHES=${S}/debian/patches-freetype quilt push -a
}
