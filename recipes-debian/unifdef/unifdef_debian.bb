SUMMARY = "Selectively remove #ifdef statements from sources"
DESCRIPTION = "Remove cpp '#ifdef' lines from files \
 The unifdef utility selectively processes conditional cpp(1) directives. \
 It removes from a file both the directives and any additional text that \
 they specify should be removed, while otherwise leaving the file alone."
HOMEPAGE = "http://dotat.at/prog/unifdef/"

inherit debian-package
PV = "2.10"
DPR = "-1.1"
DSC_URI = "${DEBIAN_MIRROR}/main/u/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=09957432c20fc52cbf380b7d6d3f6e14"

# source format is 3.0 but there is no patch
DEBIAN_QUILT_PATCHES = ""

LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=4da83e7128fb3e762bd4678e7e2f358d"

do_install() {
	oe_runmake install DESTDIR=${D} prefix=${prefix}
}

BBCLASSEXTEND = "native"
