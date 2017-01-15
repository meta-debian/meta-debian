SUMMARY = "Wrapper library for evdev devices"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libevdev/"

inherit autotools pkgconfig debian-package
PV = "1.3+dfsg"
PR = "r1"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=75aae0d38feea6fda97ca381cb9132eb \
                    file://libevdev/libevdev.h;endline=21;md5=7ff4f0b5113252c2f1a828e0bbad98d1"

# Follow configure in Debian rules
EXTRA_OECONF = "--disable-silent-rules"

# debian/patches/use-system-libevdev-for-tests.patch exists,
# but it is only for debian/tests/check, not for building package.
# This patch should not be applied in do_debian_patch
do_debian_patch_prepend() {
	if [ -f ${S}/debian/patches/use-system-libevdev-for-tests.patch ]; then
		rm ${S}/debian/patches/use-system-libevdev-for-tests.patch
	fi
	rmdir --ignore-fail-on-non-empty ${S}/debian/patches
}

# After removing use-system-libevdev-for-tests.patch,
# there is no debian patch and no debian series file
DEBIAN_QUILT_PATCHES = ""
