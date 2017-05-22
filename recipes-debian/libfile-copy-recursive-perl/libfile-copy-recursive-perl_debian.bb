SUMMARY = "Perl extension for recursively copying files and directories"
DESCRIPTION = "File::Copy::Recursive module copies and moves directories recursively  \
(or single files, well... singley) to an optional depth and attempts  \
to preserve each file or directory's mode."
HOMEPAGE = "http://search.cpan.org/dist/File-Copy-Recursive/"

inherit debian-package
PV = "0.38"

LICENSE = "GPL-1.0+ | Artistic-1.0"
LIC_FILES_CHKSUM = "file://README;beginline=40;md5=c113500dbbf8f5d059394242b407295d"

DEBIAN_PATCH_TYPE = "nopatch"

inherit cpan

BBCLASSEXTEND = "native"
