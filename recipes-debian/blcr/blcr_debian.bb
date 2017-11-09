SUMMARY = "Userspace tools to Checkpoint and Restart Linux processes"
DESCRIPTION = "BLCR (Berkeley Lab Checkpoint/Restart) allows programs running on\n\
 Linux to be "checkpointed" (written entirely to a file), and then\n\
 later "restarted".\n\
 .\n\
 BLCR can checkpoint both single- and multithreaded (pthreads)\n\
 programs linked with the NPTL implementation of pthreads. BLCR is\n\
 also able to save and restore groups of related processes together\n\
 with the pipes that connect them."
HOMEPAGE = "https://ftg.lbl.gov/projects/CheckpointRestart/"

inherit debian-package
PV = "0.8.5"

LICENSE = "GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833 \
                    file://COPYING.LIB;md5=7fbc338309ac38fefcd64b04bb903e34 \
                    file://LICENSE.txt;md5=15154e6cb1c4844001e64615f4fa1bd7"

# Currently, BLCR version 0.8.5 only supported Linux 2.6.x and 3.x.y kernels
# We need disable some feature in configure to avoid error when use kernel 4.x
SRC_URI += "file://Fix-kernel-version_debian.patch \
            file://Dont-build-testdir-and-kbuild_debian.patch"

# Configure environment variables set before the cross-compiler, \
# otherwise will prompt cross_linuxthreads errors. 
export cross_linuxthreads = "0"
export cross_stack_direction = "-1"
export cross_signum = "64"

DEPENDS += "chrpath-native"
inherit autotools perlnative module

addtask make_scripts after do_patch before do_configure

EXTRA_OECONF += "--with-linux=${STAGING_KERNEL_BUILDDIR} \
                 --with-linux-src=${STAGING_KERNEL_DIR}"

do_configure_prepend() {
	if [ "${TARGET_ARCH}" = "powerpc" ]; then
		unset LDFLAGS
	fi
	sed -i -e "s|##KERNEL_VERSION##|`cat ${STAGING_KERNEL_BUILDDIR}/include/config/kernel.release`|g" \
		${S}/acinclude.m4
}

do_install() {
	oe_runmake install DESTDIR=${D}
	chrpath -d ${D}${bindir}/cr_checkpoint ${D}${bindir}/cr_restart
}
PACKAGES =+ "libcr"
FILES_libcr = "${libdir}/*${SOLIBS}"
FILES_${PN} += "${bindir}/*"
PKG_${PN} = "${PN}-util"
PKG_${PN}-dev = "libcr-dev"
PKG_libcr = "libcr0"
RPROVIDES_${PN} = "${PN}-util"
RPROVIDES_${PN}-dev = "libcr-dev"
