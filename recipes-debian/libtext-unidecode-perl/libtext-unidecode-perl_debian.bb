DESCRIPTION = "Text::Unidecode -- US-ASCII transliterations of Unicode text"

PR = "r0"

inherit debian-package
PV = "1.22"

LICENSE = "Artistic-1.0 | GPLv1+"
LIC_FILES_CHKSUM = " \
file://debian/copyright;md5=2f6ea07e452cfa24aa84dc9dbb353958 \
"

DEBIAN_QUILT_PATCHES = ""

inherit cpan
