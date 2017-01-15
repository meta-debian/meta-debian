#
# base recipe: meta/recipes-core/coreutils/coreutils_8.22.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "8.23"

LICENSE = "GPLv3+ & GFDL-1.3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://src/ls.c;beginline=5;endline=16;md5=38b79785ca88537b75871782a2a3c6b8 \
	file://doc/coreutils.info;beginline=7;endline=14;md5=f469e5a24d5c598b086a4f3532834660 \
"

DEPENDS = "gmp libcap libselinux"
DEPENDS_class-native = ""

DEBIAN_PATCH_TYPE = "dpatch"

inherit autotools gettext

SRC_URI += " \
	file://remove-usr-local-lib-from-m4.patch \
	file://dummy_help2man.patch \
	file://fix-for-dummy-man-usage.patch \
"

EXTRA_OECONF_class-native = "--without-gmp"
EXTRA_OECONF_class-target = "--enable-install-program=arch --libexecdir=${libdir}"

# acl is not a default feature
#
PACKAGECONFIG_class-target ??= "${@bb.utils.contains('DISTRO_FEATURES', 'acl', 'acl', '', d)}"
PACKAGECONFIG_class-native ??= ""

# with, without, depends, rdepends
#
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"

BIN_PROGS = "cat chgrp chmod chown cp date dd df dir echo false ln ls mkdir \
        mknod mv pwd readlink rm rmdir vdir sleep stty sync touch true uname \
        mktemp"

# Let aclocal use the relative path for the m4 file rather than the
# absolute since coreutils has a lot of m4 files, otherwise there might
# be an "Argument list too long" error when it is built in a long/deep
# directory.
acpaths = "-I ./m4"

# Deal with a separate builddir failure if src doesn't exist when creating version.c/version.h
do_compile_prepend () {
	mkdir -p ${B}/src
}

# Follow debian/rules
do_install_append() {
	# some things go in root rather than usr
	install -d ${D}${base_bindir}
	for f in ${BIN_PROGS}; do
		mv ${D}${bindir}/$f ${D}${base_bindir}/$f
	done

	# backward compatability
	ln -s md5sum ${D}${bindir}/md5sum.textutils
	ln -s md5sum.1 ${D}${mandir}/man1/md5sum.textutils.1

	# kill from procps is linux-specific
	rm -f ${D}${bindir}/kill ${D}${mandir}/man1/kill.1

	rm -f ${D}${bindir}/hostname ${D}${mandir}/man1/hostname.1
	rm -f ${D}${bindir}/uptime ${D}${mandir}/man1/uptime.1

	# the [ program doesn't have its own man page yet
	ln -s test.1 ${D}${mandir}/man1/[.1

	# gnu thinks chroot is in bin, debian thinks it's in sbin
	install -d ${D}${sbindir} ${D}${mandir}/man8
	mv ${D}${bindir}/chroot ${D}${sbindir}/chroot
	sed s/\"1\"/\"8\"/1 ${D}${mandir}/man1/chroot.1 > ${D}${mandir}/man8/chroot.8
	rm ${D}${mandir}/man1/chroot.1

    ln -s ${base_bindir}/touch ${D}${bindir}/touch
}

do_install_append_class-native(){
	# remove groups to fix conflict with shadow-native
	rm -f ${D}${STAGING_BINDIR_NATIVE}/groups
}

# Add update-alternatives definitions
inherit update-alternatives

base_bindir_progs = "cat chgrp chmod chown cp date dd df echo false ln ls mkdir mknod \
		mktemp mv pwd readlink rm rmdir sleep stty sync touch true uname"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "${base_bindir_progs}"
python __anonymous() {
        for prog in d.getVar('base_bindir_progs', True).split():
                d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_bindir', True), prog))
}

BBCLASSEXTEND = "native"
