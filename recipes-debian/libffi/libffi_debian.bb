#
# Base recipe: meta/recipes-gnome/libffi/libffi_3.0.13.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "A portable foreign function interface library"
DESCRIPTION = "The `libffi' library provides a portable, high level programming interface to various calling \
conventions.  This allows a programmer to call any function specified by a call interface description at run \
time. FFI stands for Foreign Function Interface.  A foreign function interface is the popular name for the \
interface that allows code written in one language to call code written in another language.  The `libffi' \
library really only provides the lowest, machine dependent layer of a fully featured foreign function interface.  \
A layer must exist above `libffi' that handles type conversions for values passed between the two languages."

inherit autotools debian-package
PV = "3.1"
PR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3610bb17683a0089ed64055416b2ae1b"

SRC_URI += " \
file://fix-libffi.la-location.patch \
"

EXTRA_OECONF += "--disable-builddir"

FILES_${PN}-dev += "${libdir}/libffi-${PV}"

# Correct .deb file names
DEBIANNAME_${PN} = "libffi6"

BBCLASSEXTEND = "native nativesdk"
