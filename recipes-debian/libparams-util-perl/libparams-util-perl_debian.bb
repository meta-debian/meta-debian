SUMMARY = "Perl extension for simple stand-alone param checking functions"
DESCRIPTION = "Params::Util provides a basic set of importable functions that makes checking \
 parameters easier. The functions provided by Params::Util check in the most \
 strictly correct manner, and it should not be fooled by odd cases."
HOMEPAGE = "https://metacpan.org/release/Params-Util"

PR = "r0"
inherit debian-package
PV = "1.07"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=390;endline=397;md5=81eb41a1bb40f09453b25bf18f91ab2c \
                    file://LICENSE;md5=b41a38c91c3e99543225495ccf83895d"
inherit cpan
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

# set LD = CCLD to avoid issue:
# | unrecognized option '-Wl,-O1'
# | use the --help option for usage information
export LD="${CCLD}"
