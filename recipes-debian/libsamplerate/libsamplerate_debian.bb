SUMMARY = "Audio sample rate conversion library"
DESCRIPTION = "\
libsamplerate (aka Secret Rabbit Code) is a library for audio rate conversion. \
libsamplerate currently provides three different sample rate conversion \
algorithms; zero order hold, linear interpolation and FIR filter interpolation \
(using filters derived from the mathematical SINC function). The first two \
algorithms (zero order hold and linear) are included for completeness and are \
not recommended for any application where high quality sample rate conversion \
is required. For the FIR/Sinc algorithm, three converters are provided; \
SRC_SINC_FASTEST, SRC_SINC_MEDIUM_QUALITY and SRC_SINC_BEST_QUALITY to allow a \
trade off between conversion speed and conversion quality \
"
HOMEPAGE = "http://www.mega-nerd.com/SRC/"
PR = "r0"
inherit debian-package
PV = "0.1.8"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
inherit autotools-brokensep pkgconfig

PACKAGES =+ "samplerate-programs"

DEBIANNAME_${PN} = "${PN}0"
DEBIANNAME_${PN}-dev = "${PN}0-dev"

FILES_samplerate-programs = "${bindir}/sndfile-resample"
RDEPENDS_${PN}-dev += "${PN}"
