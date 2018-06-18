#
# base recipe: meta/recipes-extended/bc/bc_1.06.bb
# base branch: daisy
#

SUMMARY = "GNU bc arbitrary precision calculator language"
DESCRIPTION = "GNU bc is an interactive algebraic language with arbitrary precision which \
follows the POSIX 1003.2 draft standard, with several extensions including \
multi-character variable names, an `else' statement and full Boolean \
expressions.  GNU bc does not require the separate GNU dc program."
HOMEPAGE = "http://ftp.gnu.org/gnu/bc/"

inherit debian-package
PV = "1.07.1"
DPR = "-2"
DSC_URI = "${DEBIAN_MIRROR}/main/b/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=fc30a4f7d7314cb67599e9917bb31c52"

LICENSE = "GPLv3+ & LGPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://COPYING.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
	file://bc/bcdefs.h;endline=26;md5=83710f912db7e902d45016ab235c586f \
	file://dc/dc.h;endline=20;md5=be0fc95c3503cb8116eea6acb63a5922 \
	file://lib/number.c;endline=30;md5=8529a3cebb13aca3fb46658b89cb90c7 \
"

DEPENDS = "flex-native ed-native"

inherit autotools-brokensep texinfo update-alternatives

ALTERNATIVE_${PN} = "dc"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native nativesdk"
