SUMMARY = "Free Lossless Audio Codec"
DESCRIPTION = "FLAC stands for Free Lossless Audio Codec. Grossly oversimplified, FLAC is\n\
 similar to MP3, but lossless.  The FLAC project consists of:\n\
 .\n\
  * The stream format\n\
  * libFLAC, which implements a reference encoder, stream decoder, and file\n\
    decoder\n\
  * flac, which is a command-line wrapper around libFLAC to encode and decode\n\
    .flac files\n\
  * Input plugins for various music players (Winamp, XMMS, and more in the\n\
    works)"
HOMEPAGE = "http://xiph.org/flac/"

inherit debian-package
PV = "1.3.0"

LICENSE = "GFDL-1.2 & GPLv2+ & LGPLv2.1+ & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.FDL;md5=ad1419ecc56e060eccf8184a87c4285f \
                    file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LGPL;md5=fbc093901857fcd118f065f900982c24 \
                    file://COPYING.Xiph;md5=755582d124a03e3001afea59fc02b61b"

inherit autotools

# Base on debian/rules
OPTFLAGS = "--disable-asm-optimizations --disable-sse --disable-altivec"
OPTFLAGS_x86-64 = "--disable-altivec --enable-sse"
OPTFLAGS_powerpc64 = "--enable-altivec --disable-sse"

EXTRA_OECONF += "--disable-rpath \
                 --disable-xmms-plugin \
                 ${OPTFLAGS} \
                 --enable-static \
                 --disable-3dnow \
                 --with-ogg-libraries=${STAGING_LIBDIR} \
                 --with-ogg-includes=${STAGING_INCDIR}"

DEPENDS += "libogg"

PACKAGES =+ "libflac++-dev libflac++ libflac-dev libflac"

FILES_libflac++-dev = "${includedir}/FLAC++/*.h \
                       ${libdir}/libFLAC++.so \
                       ${libdir}/pkgconfig/flac++.pc \
                       ${datadir}/aclocal/libFLAC++.m4"
FILES_libflac++ = "${libdir}/libFLAC++${SOLIBS}"
FILES_libflac-dev = "${includedir}/FLAC/*.h \
                     ${libdir}/libFLAC.so \
                     ${libdir}/pkgconfig/flac.pc \
                     ${datadir}/aclocal/libFLAC.m4"
FILES_libflac = "${libdir}/libFLAC${SOLIBS}"

RPROVIDES_libflac++ += "libflac++6"
RPROVIDES_libflac += "libflac8"
RDEPENDS_libflac++ += "libstdc++ libflac"
RDEPENDS_libflac += "libogg"
